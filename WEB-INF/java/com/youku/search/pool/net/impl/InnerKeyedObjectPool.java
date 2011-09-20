package com.youku.search.pool.net.impl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool.Config;

import com.youku.search.pool.net.util.Cost;

public class InnerKeyedObjectPool implements KeyedObjectPool {

	Log logger = LogFactory.getLog(getClass());

	class Pair {
		public Object object;
		public long timestamp;

		public Pair() {
		}

		public Pair(Object object) {
			this.object = object;
			this.timestamp = System.currentTimeMillis();
		}
	}

	class DestroyIdleTask extends TimerTask {
		@Override
		public void run() {

			int destroyed = 0;
			Cost cost = new Cost();
			for (Map.Entry<Object, Queue<Pair>> entry : idle.entrySet()) {
				Object key = entry.getKey();
				Queue<Pair> queue = entry.getValue();

				for (int i = 0; i < config.numTestsPerEvictionRun
						&& idleNumbers.get(key).get() > config.minIdle; i++) {

					Pair pair = queue.poll();
					if (pair == null) {
						break;
					}

					idleNumber.decrementAndGet();
					idleNumbers.get(key).decrementAndGet();

					try {
						factory.destroyObject(key, pair.object);
					} catch (Throwable e) {
						logger.error("销毁对象发生异常", e);
					}

					destroyed++;
				}
			}
			cost.updateEnd();

			logger.info(destroyed + " object(s) were destroyed! " + cost);

			int idleObjects = 0;
			int keys = 0;
			for (Object key : idle.keySet()) {
				keys++;
				idleObjects += idle.get(key).size();
			}
			logger.info("keys: " + keys + ", activeNumber: "
					+ activeNumber.get() + ", active object(s): "
					+ active.size() + ", idleNumber: " + idleNumber.get()
					+ ", idle object(s): " + idleObjects);
		}
	}

	private Config config;
	private KeyedPoolableObjectFactory factory;

	private ConcurrentMap<Object, Queue<Pair>> idle = new ConcurrentHashMap<Object, Queue<Pair>>();
	private ConcurrentMap<Object, Object> active = new ConcurrentHashMap<Object, Object>();
	private final Object ACTIVE_VALUE = new Object();

	private AtomicInteger activeNumber = new AtomicInteger();
	private AtomicInteger idleNumber = new AtomicInteger();
	private ConcurrentMap<Object, AtomicInteger> activeNumbers = new ConcurrentHashMap<Object, AtomicInteger>();
	private ConcurrentMap<Object, AtomicInteger> idleNumbers = new ConcurrentHashMap<Object, AtomicInteger>();

	private final Lock lock = new ReentrantLock();

	private final Timer timer = new Timer(true);

	public InnerKeyedObjectPool(KeyedPoolableObjectFactory factory,
			Config config) {

		this.config = config;
		this.factory = factory;

		final long period = config.timeBetweenEvictionRunsMillis;
		if (period > 0) {
			logger.info("schedule DestroyIdleTask at period: " + period);
			timer.schedule(new DestroyIdleTask(), period, period);
		}
	}

	public Object borrowObject(Object key) throws Exception,
			NoSuchElementException, IllegalStateException {

		final Queue<Pair> mappedQueue;

		lock.lock();
		try {
			final Queue<Pair> oldQueue = idle.get(key);
			if (oldQueue != null) {
				mappedQueue = oldQueue;
			} else {
				final Queue<Pair> newQueue = new ConcurrentLinkedQueue<Pair>();
				mappedQueue = newQueue;

				idle.put(key, newQueue);
				activeNumbers.put(key, new AtomicInteger());
				idleNumbers.put(key, new AtomicInteger());
			}
		} finally {
			lock.unlock();
		}

		Pair pair = mappedQueue.poll();
		if (pair == null) {
			Object object = newObject(key);
			active.put(object, ACTIVE_VALUE);
			activeNumber.incrementAndGet();
			activeNumbers.get(key).incrementAndGet();
			return object;
		}

		// 取得一个缓存对象
		idleNumber.decrementAndGet();
		idleNumbers.get(key).decrementAndGet();
		boolean activeError = false;
		try {
			factory.activateObject(key, pair.object);
		} catch (Exception e) {
			activeError = true;
			logger.error("激活对象失败，准备销毁此对象", e);
			try {
				factory.destroyObject(key, pair.object);
			} catch (Exception e2) {
				logger.error("激活对象失败，销毁此对象也失败", e2);
			}
		}

		if (activeError) { // 激活对象失败
			return borrowObject(key);// 递归调用
		}

		// 激活对象成功
		if (factory.validateObject(key, pair.object)) {
			// 对象有效
			active.put(pair.object, ACTIVE_VALUE);
			activeNumber.incrementAndGet();
			activeNumbers.get(key).incrementAndGet();
			return pair.object;
		} else {
			// 对象无效
			try {
				factory.destroyObject(key, pair.object);
			} catch (Exception e) {
				logger.error("销毁对象失败", e);
			}

			return borrowObject(key);
		}
	}

	private Object newObject(Object key) {

		Throwable lastCause = null;
		for (int i = 0; i < 3; i++) {
			try {
				return factory.makeObject(key);
			} catch (Throwable t) {
				lastCause = t;
			}
		}

		throw new RuntimeException("创建新对象失败", lastCause);
	}

	public void returnObject(Object key, Object obj) throws Exception {

		final Queue<Pair> queue = idle.get(key);
		if (queue == null) {
			logger.error("没有这么一个key: " + key);
			return;
		}

		final Object activeValue = active.remove(obj);
		if (ACTIVE_VALUE != activeValue) {
			logger.error("没有这么一个object: " + activeValue + ", key: " + key);
			return;
		}

		activeNumber.decrementAndGet();
		activeNumbers.get(key).decrementAndGet();

		Throwable passivateError = null;
		try {
			factory.passivateObject(key, obj);
		} catch (Throwable t) {
			passivateError = t;
			logger.error("passivate Object失败，准备销毁此对象", t);

			try {
				factory.destroyObject(key, obj);
			} catch (Exception e2) {
				logger.error("passivate Object失败，销毁此对象也失败", e2);
			}
		}

		// passivateObject 成功
		if (passivateError == null) {
			boolean offerSuccess = true;
			Throwable offerError = null;
			try {
				offerSuccess = queue.offer(new Pair(obj));
			} catch (Throwable t) {
				offerSuccess = false;
				offerError = t;
			}

			if (offerSuccess) {
				idleNumber.incrementAndGet();
				idleNumbers.get(key).incrementAndGet();
			} else {
				if (offerError == null) {
					logger.error("对象入idle队列失败！");
				} else {
					logger.error("对象入idle队列失败！", offerError);
				}
			}

		} else {
			logger.error("归还对象失败: " + passivateError.getMessage(),
					passivateError);
		}
	}

	public int getNumActive(Object key) throws UnsupportedOperationException {
		try {
			return activeNumbers.get(key).get();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		}
	}

	public int getNumActive() throws UnsupportedOperationException {
		return activeNumber.get();
	}

	public int getNumIdle(Object key) throws UnsupportedOperationException {
		try {
			return idleNumbers.get(key).get();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		}
	}

	public int getNumIdle() throws UnsupportedOperationException {
		return idleNumber.get();
	}

	public void addObject(Object key) throws Exception, IllegalStateException,
			UnsupportedOperationException {
	}

	public void clear() throws Exception, UnsupportedOperationException {
	}

	public void clear(Object key) throws Exception,
			UnsupportedOperationException {
	}

	public void close() throws Exception {
	}

	public void invalidateObject(Object key, Object obj) throws Exception {
	}

	public void setFactory(KeyedPoolableObjectFactory factory)
			throws IllegalStateException, UnsupportedOperationException {
		this.factory = factory;
	}

	public void destroyIdle() {
	}
}
