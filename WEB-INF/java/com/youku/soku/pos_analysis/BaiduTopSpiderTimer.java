package com.youku.soku.pos_analysis;

import java.util.Timer;
import java.util.TimerTask;

public class BaiduTopSpiderTimer{
	
private static boolean isRunning = false;
	
	public void start() {
		Timer timer = new Timer(true);

		final long delay = 1000L * 1;
		final long period = 1000L * 60 * 60 ;// 1小时

		TimerTask task = new TimerTask() {
			public void run() {
				if (!isRunning) {
					isRunning = true;
					try {
						BaiduTopSpider.getInstance().baiduTopSpiderSave();
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
