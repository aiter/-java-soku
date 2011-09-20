package com.youku.search.console.operate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.operate.juji.EpisodeMgt;
import com.youku.search.console.operate.juji.EpisodeVideoDateIn;
import com.youku.search.console.pojo.Episode;

public class Main {
	static Log logger = LogFactory.getLog(EpisodeVideoDateIn.class);
	
	public void episodeVideoInsert() {
		
		List<Episode> elist = null;
		try {
			elist = EpisodeMgt.getInstance().getAllNoValidEpisodes();
			int size = elist.size();
			for(int i=0;i<size;i=i+50000){
				final List<Episode> el1 = elist.subList(i, (i+50000)>=size?size:(i+50000));
				new Thread(new Runnable() {
					public void run() {
						EpisodeVideoDateIn.getInstance().episodeVideoSearch(el1, 10);
					}
				}).start();
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
