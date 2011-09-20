package com.youku.search.console.operate.juji;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.vo.SingleVersion;

public class PlayVersionService {
	
	static Log logger = LogFactory.getLog(PlayVersionService.class);

	private PlayVersionService() {
	}

	private static PlayVersionService instance = null;

	public static synchronized PlayVersionService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new PlayVersionService();
			return instance;
		}
	}
	
	public void updateVersionLogo(){
		List<PlayVersion> playVersions = PlayVersionMgt.getInstance().getPlayVersionByLogo();
		if(null==playVersions||playVersions.size()<1) return;
		String logo = null;
		for(PlayVersion pv:playVersions){
			logo = EpisodeMgt.getInstance().getFirstLogo(pv.getId());
			if(StringUtils.isBlank(logo))
				continue;
			PlayVersionMgt.getInstance().executeSql("update play_version set firstlogo='"+logo+"' where id="+pv.getId() );
		}
	}
	
	public void updateVersionEpisodeCollecteds(){
		List<PlayVersion> playVersions = PlayVersionMgt.getInstance().getAllPlayVersion();
		if(null==playVersions||playVersions.size()<1) return;
		int count = 0;
		for(PlayVersion pv:playVersions){
			count = EpisodeMgt.getInstance().getEpisodeCollecteds(pv.getId());
			PlayVersionMgt.getInstance().executeSql("update play_version set episode_count="+count+" where id="+pv.getId() );
		}
	}
	
	public void updateVersionEpisodeFixed(){
		PlayVersionMgt.getInstance().executeSql("update play_version set fixed=1 where total_count!=0 and total_count=episode_count");
		PlayVersionMgt.getInstance().executeSql("update play_version set fixed=0 where total_count!=0 and total_count!=episode_count");
	}
	
	public boolean updateVersion(SingleVersion sv) {

		boolean f = PlayVersionMgt.getInstance().updateVersion(sv);

		if (f && sv.getTotal_Count() > 0) {
			if (sv.getSubcate() != 2078) {
				List<Episode> episodes = EpisodeMgt.getInstance().getEpisodes(
						-1, sv.getPid(), sv.getTotal_Count());
				if (null != episodes) {
					for (Episode episode : episodes) {
						EpisodeVideoMgt.getInstance().deleteEpisodeVideo(
								episode.getId());
						EpisodeMgt.getInstance().deleteEpisode(episode);
					}
				}
			}
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set episode_count="
							+ EpisodeMgt.getInstance().getEpisodeCountByID(
									sv.getPid()) + " where id=" + sv.getPid());
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set fixed=1 where total_count=episode_count and id="
							+ sv.getPid());
		}
		return f;
	}

	public boolean deleteVersionByID(int tid, int pid) throws TorqueException {
		BlacklistMgt.getInstance().executeSql(
				"delete from blacklist where fk_version_id =" + pid);

		// TODO
		BasePeer
				.executeStatement(
						"delete from feedback where status=0 and fk_version_id ="
								+ pid, "searchteleplay");

		PlayVersionMgt.getInstance().deletePlayVersionByID(pid);

		EpisodeMgt.getInstance().deleteEpisodeByVersionId(pid);

		TeleplayMgt.getInstance().subVersionCount(tid);

		// TODO
		BasePeer
				.executeStatement(
						"delete from episode_log where fk_episode_id in (select id from episode where fk_version_id="
								+ pid + ")", "searchteleplay");

		EpisodeVideoMgt evmgt = EpisodeVideoMgt.getInstance();
		List<Episode> episodes = EpisodeMgt.getInstance().getEpisodes(-1, pid,
				-1);
		if (null != episodes) {
			for (Episode episode : episodes) {
				evmgt.deleteEpisodeVideo(episode.getId());
			}
		}
		return true;
	}
	
	public boolean addVersion(SingleVersion sv){
		boolean f=false;
		f = com.youku.search.console.operate.juji.PlayVersionMgt.getInstance().addVersion(sv);
		if(f)
			com.youku.search.console.operate.juji.TeleplayMgt.getInstance().addVersionCount(sv.getFkTeleplayId());
		return f;
	}
}
