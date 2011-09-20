/**
 * 
 */
package com.youku.soku.library.load.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.util.Cost;

/**
 * 定时加载跳转词。每台sort机器，搜索前，发现匹配的搜索词，直接跳转到详情页
 * @author liuyunjian
 * 2011-4-23
 */
public class ForwardWordTimer {
	private Log logger = LogFactory.getLog(getClass());

	public void start() {
		// 部属数据库加载任务
		Timer timer = new Timer(true);

		final long delay = 10 * 1000;// 10秒
		final long period = 1800 * 1000;// 30*60秒

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					// 从数据库加载数据
					logger.info("开始加载跳转词...(period: " + period + ")");
					Cost cost = new Cost();
					
					ForwardWord.getInstance().init();
					
					cost.updateEnd();
					logger.info("加载跳转词完毕; cost: " + cost.getCost());

				} catch (Exception e) {
					logger.error("加载跳转词发生异常", e);
				}
			}
		};

		logger.info("部署跳转词的任务: delay " + delay + "; period: " + period);
		timer.schedule(task, delay, period);
	}
	
}
