package com.youku.soku.sort.ext.recommend;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.util.Cost;

public class RecommendLoaderTimer {

	Log logger = LogFactory.getLog(getClass());

	public void start() {
		// 部属数据库加载任务
		Timer timer = new Timer(true);

		final long delay = 10 * 1000;// 10秒
		final long period = 1800 * 1000;// 1800秒

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					// 从数据库加载数据
					logger.info("开始加载信息...(period: " + period + ")");
					Cost cost = new Cost();

					RecommendLoader recommendLoader = new RecommendLoader();
					RecommendInfo recommendInfo = recommendLoader.load();
					if (recommendInfo != null) {
						RecommendInfoHolder.info = recommendInfo;
					}

					cost.updateEnd();
					logger.info("加载信息完毕; cost: " + cost.getCost()
							+ "; 加载到的推荐视频信息: " + recommendInfo);

				} catch (Exception e) {
					logger.error("加载信息发生异常", e);
				}
			}
		};

		logger.info("部署加载信息的任务: delay " + delay + "; period: " + period);
		timer.schedule(task, delay, period);
	}
}
