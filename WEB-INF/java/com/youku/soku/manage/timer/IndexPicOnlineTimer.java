package com.youku.soku.manage.timer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.search.sort.MemCache;
import com.youku.soku.manage.service.IndexPicGeneratorService;
import com.youku.soku.manage.service.SokuIndexPicService;

public class IndexPicOnlineTimer {
	
	private Logger logger = Logger.getLogger(this.getClass());
	public void start() {
		Timer timer = new Timer();
		
		final long delay = 15 * 1000L;
		final long period = 10 * 1000 * 60L;
		
		TimerTask task = new TimerTask() {
			public void run() {
				logger.info("首页图片存到memcached start");
				long start = System.currentTimeMillis();
				String jsonStr = SokuIndexPicService.generateJSONResult();
				MemCache.cacheSet("index_pic", jsonStr, 36000);
				long end = System.currentTimeMillis();
				logger.info("首页图片存到memcached  end cost: " + (end - start));
			
			}
		};
		
		timer.schedule(task, delay, period);
	}

}
