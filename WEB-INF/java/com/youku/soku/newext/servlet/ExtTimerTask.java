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
import com.youku.soku.newext.loader.UpdateRecommendation;

/**
 * 直达区数据定期加载
 */
public class ExtTimerTask {

	Log logger = LogFactory.getLog(getClass());

	long delay = 0 * 1000; //一调用，就执行一次，避免开始服务后，没有数据
	long period = 5 * 60 * 1000;

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
				run_();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			cost.updateEnd();
			logger.info("加载直达区结束, cost: " + cost.getCost());
		}

		void run_() {
			FileLoaderAndSaver loaderAndSaver = new FileLoaderAndSaver();
			if (loaderAndSaver.file == null) {
				logger.warn("FileLoaderAndSaver.file变量为 null");

			} else if (!loaderAndSaver.file.exists()) {
				logger.warn("直达区数据文件不存在, file: " + loaderAndSaver.file.getAbsolutePath());

			} else {

				if (loaderAndSaver.file.lastModified() <= last) {
					logger.info("直达区数据文件是一个旧文件, 上次加载时间: " + new Date(last));
					return;
				}
	
				logger.info("加载直达区新文件, 上次修改时间: " + new Date(last));
				
				loaderAndSaver.load();
				
				//由于直达区文件更新需要一定时间，这时候做过的增量更新会被覆盖。所以加载完成后再做一遍增量，将过去3个小时改变的节目在加载一遍
				Date startDate=null,nowDate=new Date();
				Calendar calendar=Calendar.getInstance();
				calendar.add(Calendar.HOUR, -24);
				last=calendar.getTime().getTime();
				startDate=new Date(last);	
				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				logger.info("更新开始时间："+sdf.format(startDate)+
						"结束时间："+sdf.format(nowDate));				
				 new UpdateAlias().doUpdate(startDate,nowDate);
				 //增量更新结束
				 
				 //重新加载相关节目
				 new UpdateRecommendation().doUpdate();
				
				last = loaderAndSaver.file.lastModified();
				logger.info("加载直达区新文件, 最新修改时间: " + new Date(last));
			}
		}
	}
}
