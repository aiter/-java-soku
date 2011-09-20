package com.youku.common.correct;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class CorrectorManager {
	protected static Log logger = LogFactory.getLog(CorrectorManager.class);
	
	private static Corrector corrector = null;
	
	public static void reload(){
		logger.info("开始重新读取纠错词表");
		
		Corrector newCorrector = new Corrector();
		newCorrector.initWords();
		corrector = newCorrector;
		
		logger.info("结束重新读取纠错词表");
	}
	
	public static Corrector getCorrector(){
		if (corrector == null) {
			startTimer();
			return new Corrector();
		}
		return corrector;
	}
	
	
	private static boolean hasRunning = false;
	private static Object lock = new Object();
	
	public static void startTimer(){
		synchronized(lock){
			if (!hasRunning)
			{
				Timer timer = new Timer(true);
		
				final long delay = 2 * 1000;// 2秒
				final long period = 20 * 60 * 1000;// 20分钟
		
				TimerTask task = new TimerTask() {
					public void run() {
						reload();
					}
				};
				
				timer.schedule(task, delay, period);
				
				hasRunning = true;
			}
		}
	}
	
}



