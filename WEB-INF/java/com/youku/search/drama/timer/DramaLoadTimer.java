package com.youku.search.drama.timer;

import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 加载数据到memcache
 */
public class DramaLoadTimer {

	private Log logger = LogFactory.getLog(DramaLoadTimer.class);
	private Timer timer = new Timer(true);
	private boolean started = false;

	public DramaLoadTimer() {
	}

	public synchronized void start() {
		if (started) {
			logger.info("this timer is already started!");
			return;
		}

		final long minutes = 60;
		logger.info("加载Drama信息到memcache任务已经部署，周期：" + minutes + " minute(s)");

		final long delay = 1000 * 10;
		final long period = 1000 * 60 * minutes;
		timer.schedule(new DramaLoadTimerTask(), delay, period);

		started = true;
	}

}
