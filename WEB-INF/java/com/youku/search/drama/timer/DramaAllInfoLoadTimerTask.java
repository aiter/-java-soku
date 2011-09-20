package com.youku.search.drama.timer;

import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.drama.Drama;
import com.youku.search.drama.cache.DramaSaver;
import com.youku.search.drama.db.DramaLoader;
import com.youku.search.pool.net.util.Cost;

/**
 *这个任务是为了快速加载，memcache和heap共用同一个drama数据结构。
 */
public class DramaAllInfoLoadTimerTask extends TimerTask {

	Log logger = LogFactory.getLog(getClass());

	String desc = "加载Drama信息到ram、memcache";

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
		long freeM_1 = Runtime.getRuntime().freeMemory() / 1024 / 1024;

		// 1
		logger.info(desc + ": 开始查询数据库...");
		Cost loadCost = new Cost();

		List<Drama> list = DramaLoader.loadDrama(null, false);// 很重要：先不加载视频EpisodeVideo信息，以便序列化到memcache

		loadCost.updateEnd();
		logger.info(desc + ": 查询数据库结束，查询到的Drama数目：" + list.size() + "，cost: "
				+ loadCost.getCost());

		// 2
		logger.info(desc + ": 开始保存到memcache...");
		Cost saveMemCacheCost = new Cost();

		DramaSaver.saveDramaAndVersionMap(list);

		saveMemCacheCost.updateEnd();
		logger.info(desc + ": 保存到memcache结束，cost: "
				+ saveMemCacheCost.getCost());

		// 3
		logger.info(desc + ": 继续查询数据库，加载EpisodeVideo信息...");
		Cost loadEpisodeVideoCost = new Cost();

		DramaLoader.loadEpisodeVideo(list);

		loadEpisodeVideoCost.updateEnd();
		logger.info(desc + ": 加载EpisodeVideo信息结束，cost: "
				+ loadEpisodeVideoCost.getCost());

		// 4
		logger.info(desc + ": 开始保存到ram...");
		Cost saveMemCost = new Cost();

		DramaSaver.saveDramaAndEpisodeMap(list, true);

		saveMemCost.updateEnd();
		logger.info(desc + " 保存到ram结束，cost: " + +saveMemCost.getCost());

		// ok
		long total = loadCost.getCost() + saveMemCost.getCost()
				+ loadEpisodeVideoCost.getCost() + saveMemCacheCost.getCost();

		long freeM_2 = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		long memCost = freeM_1 - freeM_2;

		logger.info(desc + "完成！ 时间cost：" + total + ", ram cost：" + memCost
				+ "M");
	}
}
