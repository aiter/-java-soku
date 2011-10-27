package com.youku.soku.newext.servlet;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.newext.loader.UpdateRecommendation;

/**
 * 直达区数据定期加载
 */
public class ExtUpdateRelatedShowTimerTask {

	Log logger = LogFactory.getLog(getClass());

	long delay = 60 * 1000 * 1; //先把节目加载完，在添加推荐节目
	long period = 60 * 60 * 1000 * 24;

	public void start() {
		Timer timer = new Timer(true);
		timer.schedule(new Task(), delay, period);
	}

	class Task extends TimerTask {

		long last = -1;

		@Override
		public void run() {
			logger.info("开始相关节目数据...");
			Cost cost = new Cost();
			try {
				new UpdateRecommendation().doUpdate();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			cost.updateEnd();
			logger.info("加载相关节目结束, cost: " + cost.getCost());
		}

		
	}
}
