package com.youku.soku.manage.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.manage.deadlink.BaseUrlCheck;
import com.youku.soku.manage.deadlink.OtherSiteUrlCheck;
import com.youku.soku.manage.deadlink.YoukuUrlCheck;

public class DeadLinkCheckTimer {
	
	private Logger logger = Logger.getLogger(this.getClass());
	public void start() {
		Timer timer = new Timer();
		
		Calendar calendar = Calendar.getInstance();  
        calendar.set(Calendar.HOUR_OF_DAY, 2);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        Date date = calendar.getTime();  
        
        final long delay = 1 * 1000L;
		final long period = 2 * 60 * 1000 * 60L;
		
		TimerTask task = new TimerTask() {
			public void run() {
				logger.info("死链检查  start");
				long start = System.currentTimeMillis();
				BaseUrlCheck otherSiteCheck = new OtherSiteUrlCheck();
				otherSiteCheck.checkAll();
				BaseUrlCheck youkuCheck = new YoukuUrlCheck();
				youkuCheck.checkAll();
				
				long end = System.currentTimeMillis();
				
				logger.info("死链检查  end cost: " + (end - start));
			}
		};
		
		//timer.schedule(task, date);
		timer.schedule(task, delay, period);
	}

}
