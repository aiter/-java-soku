package com.youku.search.sort.major_term.timer;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.major_term.MajorTermSearcher;
import com.youku.search.sort.major_term.loader.Constant;
import com.youku.search.sort.major_term.loader.MajorTermLoader;

/**
 * 大词数据加载,目前是：每台sort加载文件到内存中。
 */
public class MajorTermLoaderTimerTask {

	Log logger = LogFactory.getLog(getClass());

	long delay = 0 * 1000; //一调用，就执行一次，避免开始服务后，没有大词数据
	long period = 5 * 60 * 1000;

	public void start() {
		Timer timer = new Timer(true);
		timer.schedule(new Task(), delay, period);
	}

	class Task extends TimerTask {

		long last = -1;

		@Override
		public void run() {
			logger.info("开始加载大词数据...");
			Cost cost = new Cost();
			try {
				run_();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			cost.updateEnd();
			logger.info("加载大词数据结束, cost: " + cost.getCost());
		}

		void run_() {
			File file = new File(Constant.dir, Constant.file);
			if (!file.exists()) {
				logger.info("大词数据文件不存在, file: " + file.getAbsolutePath());
				return;
			}

			if (file.lastModified() <= last) {
				logger.info("大词数据文件是一个旧文件, 上次加载时间: " + new Date(last));
				return;
			}

			JSONObject jsonObject = MajorTermLoader.load();
			if (jsonObject == null || jsonObject.length() <= 0) {
				logger.info("大词数据加载结果: null/empty, 不需要更新线上大词数据");
			} else {
				logger.info("大词数据加载结果: " + jsonObject.length());
				MajorTermSearcher.update(jsonObject);
			}
		}
	}
}
