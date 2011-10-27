package com.youku.soku.newext.servlet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.newext.loader.FileLoaderAndSaver;
import com.youku.soku.newext.loader.UpdateAlias;

/**
 * 直达区数据定期加载
 */
public class ExtRelatedShowBuildTask {

	Log logger = LogFactory.getLog(getClass());

	long delay = 20 * 1000; //一调用，就执行一次，避免开始服务后，没有数据
	long period = 60 * 60 * 1000 * 12;

	public void start() {
		Timer timer = new Timer(true);
		timer.schedule(new Task(), delay, period);
	}

	class Task extends TimerTask {

		long last = -1;

		@Override
		public void run() {
			logger.info("开始加载直达区数据...");
			Cost cost = new Cost();
			try {
				
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			cost.updateEnd();
			logger.info("加载直达区结束, cost: " + cost.getCost());
		}

		
	}
}
