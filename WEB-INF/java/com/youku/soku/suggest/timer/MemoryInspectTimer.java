package com.youku.soku.suggest.timer;

import java.util.Timer;
import java.util.TimerTask;

public class MemoryInspectTimer {
	
	private static final long delay = 1000 * 30;
	
	private static final long period = 1000 * 30;
	
	public void start() {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				System.out.format(
						"total memeor: %1d, maxMemeory: %2d, freeMemeor: %3d",
						Runtime.getRuntime().totalMemory(), Runtime.getRuntime()
								.maxMemory(), Runtime.getRuntime().freeMemory());
			}
			
		};
		
		Timer timer = new Timer();
		timer.schedule(task, delay, period);
	}

}
