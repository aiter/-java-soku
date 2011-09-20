/**
 * 
 */
package com.youku.soku.index.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.index.manager.db.SiteManager;

/**
 * @author 1verge
 *
 */
public class SiteTimer {

	Log logger = LogFactory.getLog(getClass());

	public void start() {
		Timer timer = new Timer(true);

		final long delay = 1 * 1000;// 1秒
		final long period = 1800 * 1000;// 1800秒

		TimerTask task = new TimerTask() {
			public void run() {
				try {
					logger.info("Site is reloading.....");
					SiteManager.getInstance().init();
					logger.info("Site load over.....");

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		};

		timer.schedule(task, delay, period);
	}

	
}
