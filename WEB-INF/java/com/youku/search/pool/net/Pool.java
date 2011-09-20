package com.youku.search.pool.net;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool.Config;
import org.apache.mina.common.IoSession;

import com.youku.search.pool.net.impl.InnerKeyedObjectPool;
import com.youku.search.pool.net.util.Cost;

public class Pool {

	private static Config poolConfig = createInnerPoolConfig();
	private static KeyedObjectPool poolImpl = create();
	private static Map<InetSocketAddress, Object> keys = new ConcurrentHashMap<InetSocketAddress, Object>();

	private static Log logger = LogFactory.getLog(Pool.class);

	private static final Timer statusReport = new Timer(true);
	private static final long reportPeriod = 1000 * 10;

	static {
		statusReport.schedule(new PoolStatusReportTask(), 0, reportPeriod);
	}

	private static Config createInnerPoolConfig() {
		Config config = new Config();

		config.testOnBorrow = true;
		config.testOnReturn = false;
		config.testWhileIdle = false;

		config.maxActive = -1;
		config.maxIdle = -1;
		config.maxTotal = -1;

		config.timeBetweenEvictionRunsMillis = -1;// 回收周期（毫秒数）
		config.timeBetweenEvictionRunsMillis = 1000 * 30 * 1;// 回收周期（毫秒数）
		config.numTestsPerEvictionRun = 100;
		config.minEvictableIdleTimeMillis = 1000 * 30 * 1;// 回收条件（闲置毫秒数）
		config.minIdle = 20;

		config.lifo = true;

		config.whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_GROW;

		return config;
	}

	private static KeyedObjectPool create() {
		return new InnerKeyedObjectPool(PoolableSessionFactory.I, poolConfig);

		// return new GenericKeyedObjectPool(PoolableSessionFactory.I,
		// poolConfig);
	}

	/**
	 * 在程序启动时预连接，以防止刚开始大并发请求过来时超时
	 */
	public static void preConnectAllSocket(InetSocketAddress[] addresses,
			int threadCount) {
		for (InetSocketAddress address : addresses) {
			List<IoSession> sessionList = new ArrayList<IoSession>(threadCount);
			IoSession session = null;
			try {
				for (int i = 0; i < threadCount; i++) {
					session = getConnection(address);
					if (null != session) {
						sessionList.add(session);
					}
				}
			} finally {
				for (IoSession ioSession : sessionList) {
					Pool.release(ioSession);
				}
			}
		}
	}
	
	/**
	 * 断开池中的所有连接，以结束程序。<br>
	 * 注意，此方法仅在测试中使用 <br>
	 * 
	 * @param addresses
	 * @param threadCount
	 */
	public static void closeAll(InetSocketAddress[] addresses,
			int threadCount) {
		if (Pool.getNumActive() > 0) {
			try {
				Thread.currentThread().sleep(1000*60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (InetSocketAddress address : addresses) {
			List<IoSession> sessionList = new ArrayList<IoSession>(threadCount);
			IoSession session = null;
			try {
				for (int i = 0; i < threadCount; i++) {
					session = getConnection(address);
					if (null != session) {
						sessionList.add(session);
					}
				}
			} finally {
				for (IoSession ioSession : sessionList) {
					Pool.closeAndRelease(ioSession);
				}
			}
		}
	}

	public static IoSession getConnection(InetSocketAddress address) {

		Cost cost = new Cost();
		IoSession session = doGetConnection(address);
		cost.updateEnd();

		Cost make = PoolableSessionFactory.makeCost.get();
		if (make == null) {
			make = new Cost(0, -1);
		}

		Cost validate = PoolableSessionFactory.validateCost.get();
		if (validate == null) {
			validate = new Cost(0, -1);
		}

		if (cost.getCost() > 1000 && logger.isInfoEnabled()) {
			ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

			logger.info("getConnection: " + cost + "; " + make + "; "
					+ validate + "; thread: " + threadMXBean.getThreadCount()
					+ "/" + threadMXBean.getPeakThreadCount());
		}

		return session;
	}

	private static IoSession doGetConnection(InetSocketAddress address) {

		IoSession session = null;
		try {
			session = (IoSession) poolImpl.borrowObject(address);
			keys.put(address, "");
		} catch (Exception e) {
			logger.error("从连接池中获取IoSession失败，address：" + address, e);
		}
		return session;
	}

	public static void closeAndRelease(IoSession session) {
		try {
			session.close();
			release(session);

		} catch (Throwable e) {
			logger.error("关闭IoSession失败，IoSesssion：" + session, e);
		}
	}

	public static void release(IoSession session) {

		Cost cost = new Cost();
		doRelease(session);
		cost.updateEnd();

		Cost destroy = PoolableSessionFactory.destroyCost.get();
		if (destroy == null) {
			destroy = new Cost(0, -1);
		}

		if (cost.getCost() > 1000 && logger.isInfoEnabled()) {
			ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

			logger.info("releaseConnection: " + cost + "; " + destroy
					+ "; thread: " + threadMXBean.getThreadCount() + "/"
					+ threadMXBean.getPeakThreadCount());
		}
	}

	private static void doRelease(IoSession session) {
		try {
			InetSocketAddress remote = (InetSocketAddress) session
					.getRemoteAddress();

			poolImpl.returnObject(remote, session);

		} catch (Throwable e) {
			logger.error("释放session发生异常！session: " + session, e);
		}
	}

	public static int getMaxTotal() {
		return poolConfig.maxTotal;
	}

	public static int getMaxActive() {
		return poolConfig.maxActive;
	}

	public static int getNumActive() {
		return poolImpl.getNumActive();
	}

	public static int getNumActive(InetSocketAddress address) {
		return poolImpl.getNumActive(address);
	}

	public static int getNumIdle() {
		return poolImpl.getNumIdle();
	}

	public static int getNumIdle(InetSocketAddress address) {
		return poolImpl.getNumIdle(address);
	}

	public static Set<InetSocketAddress> getRemoteSocketSet() {
		return keys.keySet();
	}

	static class PoolStatusReportTask extends TimerTask {

		private static int active_history = -1;
		private static int total_history = -1;
		private static int idle_history = -1;

		private Log logger = LogFactory.getLog(getClass());

		public PoolStatusReportTask() {
			logger.info("PoolStatusReportTask start, period: " + reportPeriod
					+ " ms");
		}

		@Override
		public void run() {
			if (!logger.isInfoEnabled()) {
				return;
			}

			int active = getNumActive();
			int idle = getNumIdle();
			int total = active + idle;
			int max_active = getMaxActive();
			int max_total = getMaxTotal();

			active_history = Math.max(active, active_history);
			idle_history = Math.max(idle, idle_history);
			total_history = Math.max(total, total_history);

			StringBuilder builder = new StringBuilder();

			builder.append("total: " + total + "/" + total_history + "; ");
			builder.append("active: " + active + "/" + active_history + "; ");
			builder.append("idle: " + idle + "/" + idle_history + "; ");
			builder.append("max active: " + max_active + "; ");
			builder.append("max total: " + max_total + "; ");

			//
			ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
			builder.append("thread count: " + threadMXBean.getThreadCount()
					+ "/" + threadMXBean.getPeakThreadCount());

			logger.info(builder.toString());
		}
	}
}
