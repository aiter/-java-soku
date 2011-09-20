package com.youku.search.console.config.time;

import java.util.Timer;
import java.util.TimerTask;

import com.youku.search.console.operate.juji.PlayVersionService;
import com.youku.search.console.operate.juji.TeleplayService;

public class LogoEpisodeCollectionFixTimer {
	
	private static boolean isRunning = false;
	
	public void start() {
		Timer timer = new Timer(true);

		final long delay = 5 * 1000;// 5秒
		final long period = 1000L * 60 * 60 * 2;// 2小时

		TimerTask task = new TimerTask() {
			public void run() {
				if (!isRunning) {
					isRunning = true;
					try {
						PlayVersionService.getInstance().updateVersionEpisodeCollecteds();
						PlayVersionService.getInstance().updateVersionEpisodeFixed();
						PlayVersionService.getInstance().updateVersionLogo();
						TeleplayService.getInstance().updateTeleplayVersionCount();
	
					} catch (Exception e) {
						e.printStackTrace();
					}
				isRunning = false;
			}
			}
		};

		timer.schedule(task, delay, period);
	}
}
