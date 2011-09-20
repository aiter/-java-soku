package com.youku.search.drama.timer;

import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DramaAllInfoLoadTimer {

	private Log logger = LogFactory.getLog(DramaAllInfoLoadTimer.class);
	private Timer timer = new Timer(true);
	private boolean started = false;

	public DramaAllInfoLoadTimer() {
	}

	public synchronized void start() {
		if (started) {
			logger.info("this timer is already started!");
			return;
		}

		// 1. 原来使用 DramaAllInfoLoadTimerTask，一步加载
		// 2. 现在独立加载
		start_oneStep();//2010.11.15 再用一步加载（memcached和内存）
//		start_twoSteps();

		started = true;
	}

	void start_oneStep() {
		final long minutes = 60;
		final long delay = 1000 * 10;
		final long period = 1000 * 60 * minutes;

		logger.info("加载Drama信息到ram、memcache任务已经部署，" + "周期：" + minutes
				+ " minute(s)");

		timer.schedule(new DramaAllInfoLoadTimerTask(), delay, period);
	}

	void start_twoSteps() {
		new DramaLoadTimer().start();
		new EpisodeVideoLoadTimer().start();
	}
}
