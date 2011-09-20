package com.youku.search.drama.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 加载数据到heap
 */
public class EpisodeVideoLoadTimer {

	private Log logger = LogFactory.getLog(EpisodeVideoLoadTimer.class);
	private Timer timer = new Timer(true);
	private boolean started = false;

	public EpisodeVideoLoadTimer() {
	}

	public synchronized void start() {
		if (started) {
			logger.info("this timer is already started!");
			return;
		}

		final long minutes = 60;
		final long delay = 1000 * 10;
		final long period = 1000 * 60 * minutes;

		logger.info("剧集信息定时加载已经部署，周期：" + minutes + " minute(s)");

		TimerTask task = new EpisodeVideoLoadTimerTask(null);
		timer.schedule(task, delay, period);

		started = true;
	}

}
