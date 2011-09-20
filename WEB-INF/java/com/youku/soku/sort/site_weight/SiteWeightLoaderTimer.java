package com.youku.soku.sort.site_weight;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.manage.entity.SiteWeight;
import com.youku.soku.manage.service.SiteWeightService;

public class SiteWeightLoaderTimer {

	Log logger = LogFactory.getLog(getClass());
	SiteWeightHolder holder;

	private long delay = 10 * 1000;
	private long period = 600 * 1000;

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			logger.info("开始加载站点权重数据, period: " + period);

			Cost cost = new Cost();

			load(false);

			cost.updateEnd();
			logger.info("加载站点权重数据完毕, cost: " + cost.getCost() + "; period: "
					+ period);
		}
	};

	public SiteWeightLoaderTimer(SiteWeightHolder holder) {
		this.holder = holder;
	}

	public void load(boolean startTimer) {

		try {
			LinkedHashMap<String, SiteWeight> map = new LinkedHashMap<String, SiteWeight>();

			List<SiteWeight> weights = SiteWeightService.getAllSiteWeight();
			if (weights != null) {
				for (SiteWeight w : weights) {
					map.put(String.valueOf(w.getFkSiteId()), w);
				}
			}

			logger.info("加载到站点权重数据: " + map.size() + "条, " + "cache_key: "
					+ SiteWeightHolder.getKey(map));

			if (startTimer) {
				startTimer();
			}

			holder.update(map);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void startTimer() {
		logger.info("部署站点权重数据加载任务, delay: " + delay + "; period: " + period);
		Timer timer = new Timer(true);
		timer.schedule(task, delay, period);
	}
}
