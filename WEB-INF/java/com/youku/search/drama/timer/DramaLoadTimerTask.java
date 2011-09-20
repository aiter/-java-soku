package com.youku.search.drama.timer;

import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.drama.Drama;
import com.youku.search.drama.cache.DramaSaver;
import com.youku.search.drama.db.DramaLoader;
import com.youku.search.pool.net.util.Cost;

public class DramaLoadTimerTask extends TimerTask {

	Log logger = LogFactory.getLog(getClass());

	String desc = "加载Drama信息到memcache";

	@Override
	public void run() {
		try {
			run_();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	void run_() {
		logger.info(desc + "...");

		Cost loadCost = new Cost();

		// 1
		List<Drama> list = DramaLoader.loadDrama(null, false);
		loadCost.updateEnd();
		logger.info(desc + ": 查询到的Drama数目：" + list.size() + ", load cost: "
				+ loadCost.getCost());

		// 2
		logger.info(desc + ": 开始保存到memcache...");

		Cost saveCost = new Cost();
		DramaSaver.saveDramaAndVersionMap(list);
		saveCost.updateEnd();

		logger.info(desc + ": 保存到memcache完成！ save cost: " + saveCost.getCost());

		//
		logger.info(desc + "完成！ 总cost："
				+ (loadCost.getCost() + saveCost.getCost()));
	}

}
