package com.youku.search.drama.timer;

import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.drama.Drama;
import com.youku.search.drama.cache.DramaSaver;
import com.youku.search.drama.db.DramaLoader;
import com.youku.search.pool.net.util.Cost;

public class EpisodeVideoLoadTimerTask extends TimerTask {

	Log logger = LogFactory.getLog(getClass());

	private List<Integer> dramaIds;
	private boolean loadEpisodeVideo = true;

	public EpisodeVideoLoadTimerTask(List<Integer> dramaIds) {
		this.dramaIds = dramaIds;
	}

	@Override
	public void run() {
		try {
			run_();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	void run_() {
		StringBuilder taskDesc = new StringBuilder();
		taskDesc.append("EpisodeVideo task(");
		taskDesc.append(dramaIds == null ? 0 : dramaIds.size());
		taskDesc.append(" drama id(s) & ");
		taskDesc.append(loadEpisodeVideo ? "" : "do NOT");
		taskDesc.append(" load episode video");
		taskDesc.append(")");

		long freeM_1 = Runtime.getRuntime().freeMemory() / 1024 / 1024;

		logger.info(taskDesc + " start...");

		// 1
		logger.info(taskDesc + " load from database...");
		Cost loadCost = new Cost();
		List<Drama> list = DramaLoader.loadDrama(dramaIds, loadEpisodeVideo);
		loadCost.updateEnd();

		logger.info(taskDesc + " find " + list.size() + " dramas, load cost: "
				+ loadCost.getCost());

		// 2
		logger.info(taskDesc + " save to ram...");
		Cost saveCost = new Cost();
		DramaSaver.saveDramaAndEpisodeMap(list, loadEpisodeVideo);
		saveCost.updateEnd();
		logger.info(taskDesc + " save to ram complete! save cost: "
				+ +saveCost.getCost());

		// 3
		long freeM_2 = Runtime.getRuntime().freeMemory() / 1024 / 1024;

		logger.info(taskDesc + " complete!" + " mem cost: "
				+ (freeM_1 - freeM_2) + "M, total cost: "
				+ (loadCost.getCost() + saveCost.getCost()));
	}
}
