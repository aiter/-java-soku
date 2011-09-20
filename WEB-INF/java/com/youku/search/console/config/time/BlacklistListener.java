package com.youku.search.console.config.time;

import java.sql.Connection;
import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.juji.EpisodeLogMgt;
import com.youku.search.console.operate.juji.EpisodeVideoMgt;
import com.youku.search.console.operate.juji.EpisodeVideoRepeatMgt;
import com.youku.search.console.operate.juji.FeedBackMgt;
import com.youku.search.console.operate.juji.PlayVersionMgt;
import com.youku.search.console.operate.TeleplaySpideMgt;

public class BlacklistListener  extends TimerTask{
	private static Log log = LogFactory.getLog(BlacklistListener.class);
	private static boolean isRunning = false;
	
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			long s=System.currentTimeMillis();
			System.out.println(new Date()+"--start--删除blacklist,feedback,episode_log,teleplay_spide,episode_video中数据start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
//			TeleplayExecute te=new TeleplayExecute();
			
			Connection conn = null;
			try {
				conn = DataConn.getTeleplayConn();
//				te.deleteBlacklist(conn);
				
				TeleplaySpideMgt.getInstance().deleteWhoNotExist();
				
				FeedBackMgt feedmgt=FeedBackMgt.getInstance();
				feedmgt.deleteFeedback();
				feedmgt.deleteUpWeekFeedback();
				
				EpisodeLogMgt.getInstance().updateEpisodeLogWhoNoEid();
				
				feedmgt.mergeFeedback(conn);
				
				EpisodeVideoMgt.getInstance().executeSql("delete from episode_video where fk_episode_id not in (select id from episode)","delete");
				
				PlayVersionMgt.getInstance().executeSql(
						"update play_version set fixed=0 where episode_count<total_count");
				PlayVersionMgt.getInstance().executeSql(
						"update play_version set fixed=1 where episode_count=total_count and total_count>0");
				
				EpisodeVideoRepeatMgt.getInstance().deleteevs();
			} catch(Exception e){
				e.printStackTrace();
			}
			finally {
				DataConn.releaseConn(conn);
			}
			System.out.println(new Date()+"--end--删除blacklist,feedback,episode_log,teleplay_spide,episode_video中数据end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}

}
