package com.youku.soku.manage.timer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.manage.service.IndexPicGeneratorService;

public class IndexPicGeneratorTimer {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private static int counter = 0;
	public void start() {
		Timer timer = new Timer();
		
		final long delay = 10 * 1000L;
		final long period = 60 * 1000 * 60L;
		
		TimerTask task = new TimerTask() {
			public void run() {
				
				Calendar calendar = Calendar.getInstance();  
			       
				if(calendar.get(Calendar.HOUR_OF_DAY) == 9 || counter == 0){
	
					logger.info("首页图片更新  start");
					long start = System.currentTimeMillis();
					IndexPicGeneratorService generator = new IndexPicGeneratorService();		
					generator.generatorPic();
					long end = System.currentTimeMillis();
					
					logger.info("首页图片更新  end cost: " + (end - start));
					counter++;
				}
			
				
			
			}
		};
		
		timer.schedule(task, delay, period);
	}

}
