package com.youku.search.monitor.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.server.ChangeServer;
import com.youku.search.util.Constant;

/**
 * 
 * @author gaosong
 */
public class ConcurrentAccessMonitor {

	private final Log logger = LogFactory.getLog(Constant.LogCategory.ServerStateLog);
	
	public static final ConcurrentAccessMonitor I = new ConcurrentAccessMonitor();

	private final ExecutorService executor;

	private final AtomicBoolean start = new AtomicBoolean(false);
	
	private static final long PERIOD = 500L;
	
	private ConcurrentAccessMonitor() {
		executor = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * 异步记录当前并发访问数 <br>
	 */
	public void start(){
		// 得到之前的状态是否已经是stoped，如果是则设置为start
		boolean canStart = start.compareAndSet(false, true);
		if (canStart) {
			logger.info("--- 开始异步记录当前并发访问数");
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						// 每隔500毫秒记录一下并发量，直到start标记被置为false
						while(start.get()){
							ChangeServer.I.loggingAccessState();
							Thread.sleep(ConcurrentAccessMonitor.PERIOD);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		} else {
			logger.info("--- 不能异步记录当前并发访问数，可能正在记录，或者上次没有调用stop结束记录");
		}
	}
	
	public void stop() {
		start.getAndSet(false);
	}

}
