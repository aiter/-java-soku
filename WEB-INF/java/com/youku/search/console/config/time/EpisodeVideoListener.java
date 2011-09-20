package com.youku.search.console.config.time;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.operate.juji.EpisodeVideoDateIn;

public class EpisodeVideoListener  extends TimerTask{
	private static Log log = LogFactory.getLog(EpisodeVideoListener.class);
	private static boolean isRunning = false;
	
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			long s=System.currentTimeMillis();
			System.out.println(new Date()+"--start--定时单个剧集搜索并入库start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			EpisodeVideoDateIn.getInstance().episodeVideoInsert();
			EpisodeVideoDateIn.getInstance().dailyEpisodeVideoInsert();
			System.out.println(new Date()+"--end--定时单个剧集搜索并入库end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}

}
