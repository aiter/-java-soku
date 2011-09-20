package com.youku.search.pool.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.ScheduledExecutors;
import com.youku.search.pool.net.util.Cost;

/**
 * 实现一个基于ConcurrentLinkedQueue的简单对象池 <br>
 * 注意，此类还未经过测试 <br>
 * 
 * @author gaosong
 * @deprecated 未完成
 */
public abstract class SimplePool<E> {

	final Log logger = LogFactory.getLog(getClass());

	private final String poolName;

	/**
	 * 当前没有被借出的对象池
	 */
	private final ConcurrentLinkedQueue<E> idlePool = new ConcurrentLinkedQueue<E>();

	private final AtomicInteger idleNum = new AtomicInteger(0);

	/**
	 * 当前已经被借出的对象池
	 */
	private final ConcurrentMap<E, Object> activePool = new ConcurrentHashMap<E, Object>();
	private final Object ACTIVE_VALUE = new Object();

	private final AtomicInteger activeNum = new AtomicInteger(0);

	private final ScheduledExecutorService scheduledExecutor;

	private final int corePoolSize;
	
	private final int minPoolSize;

	private static final long DEFAULT_EVICTION_PERIOD = 1000 * 30;

	/**
	 * @param poolName
	 * @param corePoolSize
	 *            核心池大小，如果idleNum &gt; corePoolSize，监控线程会清除多余的item
	 * @param minPoolSize
	 *            最小池大小，如果idleNum &lt; minPoolSize，监控线程会增加item <br>
	 *            注意，minPoolSize必须小于corePoolSize <br>
	 * @param initItems 池初始化时放入的对象
	 */
	SimplePool(String poolName, int corePoolSize, int minPoolSize, List<E> initItems) {
		this(poolName, corePoolSize, minPoolSize, initItems, DEFAULT_EVICTION_PERIOD);
	}

	/**
	 * @param poolName
	 * @param corePoolSize
	 *            核心池大小，如果idleNum &gt; corePoolSize，监控线程会清除多余的item
	 * @param minPoolSize
	 *            最小池大小，如果idleNum &lt; minPoolSize，监控线程会增加item <br>
	 *            注意，minPoolSize必须小于corePoolSize <br>
	 * @param initItems 池初始化时放入的对象
	 * @param period
	 *            定期监控线程每次启动的时间间隔（单位毫秒）
	 */
	SimplePool(String poolName, int corePoolSize, int minPoolSize, List<E> initItems, long period) {
		if (null == poolName || poolName.length() == 0 || minPoolSize <= 0 || corePoolSize < minPoolSize) {
			throw new IllegalArgumentException("构造SimplePool时参数错误，poolName=" + poolName + 
					", corePoolSize=" + corePoolSize + ", minPoolSize=" + minPoolSize);
		}

		this.poolName = poolName;
		this.corePoolSize = corePoolSize;
		this.minPoolSize = minPoolSize;

		if (null != initItems && initItems.size() > 0) {
			this.idlePool.addAll(initItems);
			this.idleNum.addAndGet(initItems.size());
		}

		this.scheduledExecutor = ScheduledExecutors.common();
		this.scheduledExecutor.scheduleAtFixedRate(new MonitorPool(), period,
				period, TimeUnit.MILLISECONDS);
	}

	/**
	 * 监控线程，此线程被ScheduledExecutor定期执行，完成以下功能： <br>
	 * 1. 销毁大于corePoolSize的idle对象 <br>
	 * 2. log记录池状态 <br>
	 * 
	 * @author gaosong
	 */
	private class MonitorPool implements Runnable {
		
		final Log logger = LogFactory.getLog(getClass());
		
		@Override
		public void run() {
			try {
				final int idleItemNum = idleNum.get();
				if (idleItemNum < minPoolSize) {
					batchCreate();
				}
				else if (idleItemNum > corePoolSize) {
					batchDestroy(idleItemNum);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
			logger.info("------------ poolName-[" + poolName + "] 记录的activeNum="
					+ activeNum.get() + ", 真实的activeNum="
					+ activePool.size() + ", 记录的idleNum="
					+ idleNum.get() + ", 真实的idleNum=" + idlePool.size());
		}
		
		private void batchCreate() throws Exception{
			int createNum = 0;
			Cost cost = new Cost();
			
			// 每次循环都要看idleNum，只要不够就createItem
			for (int i = 0; i < (minPoolSize - idleNum.get()); i++) {
				E item = createItem();
				if (activePool.containsKey(item)) {
					logger.error("------------ poolName-[" + poolName + "] " +
							"createItem创建的新item在activePool中已存在，Duplicate Item：" + item);
					return;	// 不要continue，否则可能造成死循环
				}
				
				idlePool.offer(item);
				idleNum.incrementAndGet();
				createNum++;
			}
			cost.updateEnd();
			
			logger.info("------------ poolName-[" + poolName + "] " + createNum
					+ " object(s) were created! " + cost);
		}
		
		private void batchDestroy(final int idleItemNum) {
			int preDestroyNum = idleItemNum - corePoolSize;
			int destroyNum = 0;
			Cost cost = new Cost();
			
			// 并发情况下，当idleNum突然增大时，用i < preDestroyNum限制一次清理最多可以destroy多少
			// 并发情况下，当idleNum突然减小时，用idleNum.get() > corePoolSize控制及时结束清理
			for (int i = 0; i < preDestroyNum && idleNum.get() > corePoolSize; i++) {
				final E item = idlePool.poll();
				if (null == item) {
					break;	// idlePool池中没有item了
				}
				
				idleNum.decrementAndGet();
				destroyItem(item);
				destroyNum++;
			}
			cost.updateEnd();
			
			logger.info("------------ poolName-[" + poolName + "] " + destroyNum
					+ " object(s) were destroyed! " + cost);
		}

	}

	/**
	 * 创建新对象 <br>
	 * 注意，必须保证每个返回的对象是不同的实例 <br>
	 * 
	 * @return
	 * @throws Exception 创建失败，此异常应该被抛出到调用方 
	 */
	protected abstract E createItem() throws Exception;
	
	/**
	 * 销毁池中过期的对象，回收对象所使用的资源 <br>
	 * 注意，有引用此对象的地方需要解除引用关系，否则可能造成GC无法回收导致内存溢出 <br>
	 * 
	 * @param item
	 */
	protected abstract boolean destroyItem(E item);
	
	/**
	 * 激活对象，当createItem创建成功后回调此方法 <br>
	 * 如果返回false，SimplePool将调用destroyItem(item)销毁对象
	 * 
	 * @param item
	 */
	protected abstract boolean activateItem(E item);
	
	/**
	 * 确认对象是否能够被借出，当activateItem激活成功后回调此方法 <br>
	 * 如果返回false，SimplePool将调用destroyItem(item)销毁对象
	 * 
	 * @param item
	 * @return
	 */
	protected abstract boolean validateItem(E item);
	
	/**
	 * 钝化对象，当对象被还回idlePool前被回调 <br>
	 * 如果返回false，SimplePool将调用destroyItem(item)销毁对象
	 * 
	 * @param key
	 * @param obj
	 */
	protected abstract boolean passivateObject(E item);
	
	/**
	 * 从idlePool中返回一个缓存的item，如果没有则创建一个 <br>
	 * 这里的borrow()与ApacheCommonsPool的实现不同，如果借不到则会马上抛出异常，由外部去处理（可以脱离池管理） <br>
	 * 
	 * @return 缓存的或新的item
	 * @throws Exception 
	 */
	public E borrow() throws Exception {
		E item = idlePool.poll();
		if (null == item) {
			logger.error("------------ poolName-[" + poolName + "] idlePool中的缓存不足");
			item = createItem();
			
			Object activeObject = activePool.putIfAbsent(item, ACTIVE_VALUE);
			// 如果创建的新item和activePool中的item重复，则会导致当giveBack时还不回去
			if (null != activeObject) {
				throw new IllegalStateException("------------ poolName-[" + poolName + "] " +
						"createItem创建的新item在activePool中已存在，Duplicate Item：" + item);
			}
		} else {
			idleNum.decrementAndGet();
			Object activeObject = activePool.putIfAbsent(item, ACTIVE_VALUE);
			// 不应该走到这里
			if (null != activeObject) {
				throw new IllegalStateException("------------ poolName-[" + poolName + "] " +
						"idlePool中取出的item在activePool中已存在，Duplicate Item："+ item);
			}
		}
		
		if (!tryActivate(item)) {
			tryDestroy(item);
			throw new IllegalStateException("------------ poolName-[" + poolName + "] " + "内部错误，外部应该尝试脱离池管理");
		}
		
		if (!tryValidate(item)) {
			tryDestroy(item);
			throw new IllegalStateException("------------ poolName-[" + poolName + "] " + "内部错误，外部应该尝试脱离池管理");
		}
		
		activeNum.incrementAndGet();
		return item;
	}
	
	public void giveBack(E item) {
		boolean isSuccessRemove = activePool.remove(item, ACTIVE_VALUE);
		if (isSuccessRemove) {
			activeNum.decrementAndGet();
		} else {
			// 如果重复调用giveBack或者没有先borrow就直接giveBack，则直接destory
			logger.error("------------ poolName-[" + poolName + "] activePool中没有这个item：" + item);
			tryDestroy(item);
			return;
		}
		
		if (!tryPassivate(item)) {
			tryDestroy(item);
			return;
		}
		
		idlePool.offer(item);
		idleNum.incrementAndGet();
	}
	
	private boolean tryPassivate(E item){
		boolean passivateResult = false;
		Exception err = null;
		try {
			passivateResult = passivateObject(item);
		} catch (Exception e) {
			err = e;
			passivateResult = false;
		}
		
		if (!passivateResult) {
			if (null == err) {
				logger.error("------------ poolName-[" + poolName + "] " + "钝化对象失败，准备销毁此对象");
			} else {
				logger.error("------------ poolName-[" + poolName + "] " + "钝化对象失败，准备销毁此对象", err);
			}
		}
		
		return passivateResult;
	}
	
	private boolean tryActivate(E item){
		boolean activeResult = false;
		Exception err = null;
		try {
			activeResult = activateItem(item);
		} catch (Exception e) {
			err = e;
			activeResult = false;
		}
		
		if (!activeResult) {
			if (null == err) {
				logger.error("------------ poolName-[" + poolName + "] " + "激活对象失败，准备销毁此对象");
			} else {
				logger.error("------------ poolName-[" + poolName + "] " + "激活对象失败，准备销毁此对象", err);
			}
		}
		
		return activeResult;
	}
	
	private boolean tryDestroy(E item){
		boolean destroyResult = false;
		Exception err = null;
		try {
			destroyResult = destroyItem(item);
		} catch (Exception e) {
			err = e;
			destroyResult = false;
		}
		
		if (!destroyResult) {
			if (null == err) {
				logger.error("------------ poolName-[" + poolName + "] " + "销毁对象失败");
			} else {
				logger.error("------------ poolName-[" + poolName + "] " + "销毁对象失败", err);
			}
		}
		
		return destroyResult;
	}
	
	private boolean tryValidate(E item){
		boolean validateResult = false;
		Exception err = null;
		try {
			validateResult = validateItem(item);
		} catch (Exception e) {
			err = e;
			validateResult = false;
		}
		
		if (!validateResult) {
			if (null == err) {
				logger.error("------------ poolName-[" + poolName + "] " + "验证对象失败，准备销毁此对象");
			} else {
				logger.error("------------ poolName-[" + poolName + "] " + "验证对象失败，准备销毁此对象", err);
			}
		}
		
		return validateResult;
	}

	public int getIdleSize() {
		return idleNum.get();
	}

	public int getActiveSize() {
		return activeNum.get();
	}

	public String getPoolName() {
		return poolName;
	}

}
