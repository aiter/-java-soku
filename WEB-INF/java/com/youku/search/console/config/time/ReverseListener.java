package com.youku.search.console.config.time;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.config.Config;
import com.youku.search.console.operate.juji.EpisodeMgt;
import com.youku.search.console.operate.juji.EpisodeVideoMgt;
import com.youku.search.console.operate.juji.JujiUtils;
import com.youku.search.console.operate.reverse.EpisodeVideoPrepare;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.teleplay.Video;
import com.youku.search.util.DataFormat;

public class ReverseListener  extends TimerTask{
	private static Log log = LogFactory.getLog(ReverseListener.class);
	private static boolean isRunning = false;
	
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			long s=System.currentTimeMillis();
			System.out.println(new Date()+"--start--根据file_id将当日锁定的剧集的相关剧集添加至episode_video,start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			
			try {
				List<Episode> episodes=EpisodeMgt.getInstance().getLastLockedEpisodes();
				EpisodeVideoPrepare evp=EpisodeVideoPrepare.getInstance();
				List<Video> videos=null;
				Video video=null;
				String url=null;
				for(Episode episode:episodes){
					video=JujiUtils.getInstance().getVideoByDB(DataFormat.parseInt(episode.getVideoId()));
					if(null==video||video.getVid()<1)continue;
					url="http://"+Config.getYoukuHost()+"/search?type=18&curpage=1&pagesize=100&order=1&orderfield=1&md5=1&keyword="+video.getFile_id();
					videos=evp.getVideoListByVid(url);
					for(Video v:videos){
						EpisodeVideoMgt.getInstance().addEpisodeVideo(episode.getId(), v);
						System.out.println("addEpisodeVideo:eid:"+episode.getId()+",vid:"+v.getVid());
						Thread.sleep(35);
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(new Date()+"--end--根据file_id将当日锁定的剧集的相关剧集添加至episode_video,end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}

}
