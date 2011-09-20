package com.youku.search.index.server;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.monitor.impl.ConcurrentAccessMonitor;
import com.youku.search.pool.net.util.SafeCost;
import com.youku.search.util.Constant;

/**
 * 切服管理类
 * 
 * @author gaosong
 */
public class ChangeServer {

	private final Log logger = LogFactory.getLog(Constant.LogCategory.ServerStateLog);
	
	public static ChangeServer I = new ChangeServer();

	/**
	 * 当前对外公开的AccessControl引用
	 */
	private final AtomicReference<AccessControl> currentAccessControlRef = new AtomicReference<AccessControl>();
	
	/**
	 * 老服的引用
	 */
	private AtomicReference<AccessControl> oldAccessControlRef = new AtomicReference<AccessControl>();
	
	private final SafeCost changeCost = new SafeCost();
	
	private final SafeCost waitDrainCost = new SafeCost();
	
	private final ReentrantLock changeLock = new ReentrantLock();
	
	private volatile ChangeServerState state = ChangeServerState.NON_CHANGE;
	
	private static enum ChangeServerState {
		NON_CHANGE, // 不在切服状态
		CHANGING, // 进入切服状态
	}
	
	private ChangeServer() {
	}
	
	public void init() {
		changeLock.lock();
		try {
			AccessControl sc = new AccessControl(CServerManager.getInitOnlineServers());
			currentAccessControlRef.getAndSet(sc);
		} finally {
			changeLock.unlock();
		}
	}
	
	/**
	 * 得到当前正在提供服务的计数器 <br>
	 * 
	 * @return
	 */
	public AccessControl getCurrentAccessControl() {
		return currentAccessControlRef.get();
	}
	
	/**
	 * 得到老服的计数器
	 * 
	 * @return
	 */
//	public AccessControl getOldAccessControl(){
//		return oldAccessControlRef.get();
//	}
	
	public long getChangeCost() {
		return changeCost.getCost();
	}
	
	public void loggingAccessState(){
		AccessControl currentAccessControl = currentAccessControlRef.get();
		AccessControl oldAccessControl = oldAccessControlRef.get();
		
		int currentConcurrentCount = currentAccessControl.getConcurrentAccessCount();
		int oldConcurrentCount = -1;
		if (null != oldAccessControl) {
			oldConcurrentCount = oldAccessControl.getConcurrentAccessCount();
		}
		
		logger.info("--- 当前服务的并发访问量=" + currentConcurrentCount + ", 老服的并发访问量=" + oldConcurrentCount);
	}
	
	/**
	 * 让当前线程wait，直到完成切服过程 <br>
	 * 如果在切服过程中发生异常，则会保持内部状态为CHANGING以拒绝以后所有的切服调用，直到forceChange()将CHANGING状态改变为止。<br>
	 * 
	 * @param maxWait 最大等待时间（单位毫秒），如果超过此时间，则说明切服超时，抛出TimeoutException
	 * @param onLineServers 要被换上的新服列表（只读）
	 * 
	 * @throws TimeoutException 等待老服切换完成时的超时
	 * @throws ChangeServerException 内部状态错误
	 * @throws InterruptedException 在wait过程中线程被中断
	 */
	public void change(long maxWait, Collection<CServerPair> onLineServers) throws TimeoutException, ChangeServerException, InterruptedException {
		logger.info("--- 开始切换服务引用 -> ChangeServer.change()...");
		if (state == ChangeServerState.CHANGING) {
			throw new ChangeServerException("当前状态已经处于切服状态，不能重入");
		}
		
		// 切换当前引用为新服
		changeLock.lock();
		try {
			// 为保证状态正确而进行的双重判断
			if (state == ChangeServerState.CHANGING) {
				throw new ChangeServerException("当前状态已经处于切服状态，不能重入");
			}
			
			AccessControl oldAccessControl = currentAccessControlRef.get();
			AccessControl changeAccess = new AccessControl(onLineServers);
			currentAccessControlRef.getAndSet(changeAccess);
			oldAccessControlRef.getAndSet(oldAccessControl);
			logger.info("--- 切换当前引用为新服 -> 完成");
			
			state = ChangeServerState.CHANGING;
			changeCost.reset();
			
			// 等待老服的并发计数归0
			waitDrainCost.reset();
			ConcurrentAccessMonitor.I.start();
			boolean timeouted = oldAccessControl.waitDrain(maxWait);
			if (!timeouted) {
				throw new TimeoutException("切服超时（等待老服的并发计数归0的过程中）,maxWait=" + maxWait);
			}
			waitDrainCost.updateEnd();
			logger.info("--- 等待老服的并发计数归0 -> 完成，等待时长=" + waitDrainCost.getCost() + "毫秒");
			
			state = ChangeServerState.NON_CHANGE;
			changeCost.updateEnd();
		} finally {
			changeLock.unlock();
			ConcurrentAccessMonitor.I.stop();
		}
	}
	
	/**
	 * 并发访问的控制类 <br>
	 * 
	 * @author gaosong
	 */
	public static class AccessControl {
//		private final AtomicLong accessTotalCount = new AtomicLong(0L); // 总访问次数
		private final AtomicInteger concurrentAccessCount = new AtomicInteger(0); // 当前并发量
		
		private final ReentrantLock lock = new ReentrantLock();
		private final Condition drain = lock.newCondition();
		
		private final Collection<CServerPair> onLineServerPairList;
		
		private final List<CServer> onLineServerList;
		
		private static final int HEALTH_CHECK_TIMES = 3;
		
		/**
		 * 访问许可信号
		 */
		private final Semaphore permits = new Semaphore(FALL_PERIMITS);
		
		/**
		 * 最大失败许可次数
		 */
		public static final int FALL_PERIMITS = 20;
		
		public AccessControl(Collection<CServerPair> onLineServerPairList) {
			this.onLineServerPairList = onLineServerPairList;
			this.onLineServerList = CServerManager.transformServerPairList(onLineServerPairList);
		}
		
		/**
		 * 开始一次请求 <br>
		 * 
		 */
		public void startRequest() {
			concurrentAccessCount.incrementAndGet();
//			accessTotalCount.incrementAndGet();
		}
		
		/**
		 * 结束一次请求 <br>
		 * 并发访问量减1，当减到<=0时通知ChangeServerMonitor线程结束切服过程 <br>
		 * 控制访问失败量，如果到达最大失败许可次数，则进行一下健康检查并报警 <br>
		 * 
		 * @return
		 */
		public void endRequest(boolean isMiss) {
			int count = concurrentAccessCount.decrementAndGet();
			
			lock.lock();
			try {
				if (count <= 0) {
					drain.signal();
				}
				
				// 如果本次请求成功，并且可用许可数<20，则访问许可+1
				if (!isMiss) {
					if (permits.availablePermits() < FALL_PERIMITS) {
						permits.release();
					}
				}
				// 如果本次请求失败，则访问许可减3；如果已经减到0，则开始进行健康检查
				else {
					boolean isAcquired = permits.tryAcquire(3);
					if (!isAcquired) {
						ServerHealthCheck.I.asyncHealthCheck(onLineServerList, HEALTH_CHECK_TIMES);
					}
				}
			} finally {
				lock.unlock();
			}
		}

		/**
		 * 等待并发访问量归0，如果当前并发访问量<=0，则会立即返回true
		 * 
		 * @param maxWait
		 *            超时时长（单位毫秒）
		 * @return 如果等待超时则返回false，否则返回true
		 * @throws InterruptedException
		 */
		boolean waitDrain(long maxWait) throws InterruptedException {
			boolean result = false;
			if (concurrentAccessCount.get() <= 0) {
				return true;
			}
			
			lock.lock();
			try {
				result = drain.await(maxWait, TimeUnit.MILLISECONDS);
			} finally {
				lock.unlock();
			}
			
			return result;
		}
		
		/**
		 * 得到总访问量
		 * 
		 * @return
		 */
//		public long getAccessTotalCount() {
//			return accessTotalCount.get();
//		}
		
		/**
		 * 得到当前的并发访问量
		 * 
		 * @return
		 */
		public int getConcurrentAccessCount() {
			return concurrentAccessCount.get();
		}
		
		public InetSocketAddress[] getOnLineServerSockets(){
			return CServerManager.getOnlineServerSockets(onLineServerPairList);
		}
		
		public Collection<CServerPair> getOnLineServerPairList(){
			return onLineServerPairList;
		}
	}
}
