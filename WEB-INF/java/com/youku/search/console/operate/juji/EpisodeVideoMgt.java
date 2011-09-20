package com.youku.search.console.operate.juji;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.config.Config;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.reverse.EpisodeVideoPrepare;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodeVideo;
import com.youku.search.console.pojo.EpisodeVideoPeer;
import com.youku.search.console.teleplay.Video;
import com.youku.search.util.DataFormat;

public class EpisodeVideoMgt {
	static Log logger = LogFactory.getLog(EpisodeVideoMgt.class);

	private EpisodeVideoMgt() {
	}

	private static EpisodeVideoMgt instance = null;

	public static synchronized EpisodeVideoMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new EpisodeVideoMgt();
			return instance;
		}
	}

	public void executeSql(String sql,String operate) {
		try {
			int num = EpisodeVideoPeer.executeStatement(sql, "searchteleplay");
			if(num>0){
				EpisodeVideoUpdateInfoMgt.getInstance().insert(operate, 0, num);
				LogInfoWriter.episodeVideo_log.info("operate:"+operate+",num:"+num);
			}
		} catch (Exception e) {
			logger.error(sql, e);
		}
	}

	public void deleteEpisodeVideo(int eid) {
		Criteria criteria = new Criteria();
		criteria.add(EpisodeVideoPeer.FK_EPISODE_ID, eid);
		try {
			int num = selectNum(criteria);
			EpisodeVideoPeer.doDelete(criteria);
			if(num>0){
				LogInfoWriter.episodeVideo_log.info("operate:delete,num:"+num);
				EpisodeVideoUpdateInfoMgt.getInstance().insert("delete", 0, num);
			}
		} catch (Exception e) {
			logger.error(eid, e);
		}
	}

	public boolean addEpisodeVideo(int eid, Video v) {
		int status = isExistReturnStatusNotify(eid, v.getVid());
		if (status>0){
				return false;
		}else {
//			int s = JujiUtils.callApi(v.getEncodeVid());
			addEpisodeVideo(eid, v, 3);
		}
		return true;
	}

	public void addEpisodeVideo(int eid, Video v, int status_notify) {
		EpisodeVideo epv = new EpisodeVideo();
		epv.setFkEpisodeId(eid);
		epv.setVideoId(v.getVid());
		epv.setSourceName(v.getTitle());
		epv.setLogo(v.getLogo());
		epv.setSeconds(v.getSeconds());
		epv.setEncodeVideoId(v.getEncodeVid());
		epv.setStatusNotify(status_notify);
		epv.setFileId(v.getFile_id());
		try {
			epv.save();
			EpisodeVideoUpdateInfoMgt.getInstance().insert("insert", status_notify, 1);
			LogInfoWriter.episodeVideo_log.info("operate:insert,id:"+epv.getId()+",eid:"+eid+",vid:"+v.getVid()+",status:"+status_notify);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public int isExistReturnStatusNotify(int eid, int vid) {
		Criteria criteria = new Criteria();
		criteria.add(EpisodeVideoPeer.FK_EPISODE_ID, eid);
		criteria.add(EpisodeVideoPeer.VIDEO_ID, vid);
		List<EpisodeVideo> epvideos = null;
		try {
			epvideos = EpisodeVideoPeer.doSelect(criteria);
		} catch (Exception e) {
			logger.error(e);
		}
		if (null != epvideos && epvideos.size() > 0)
			return epvideos.get(0).getStatusNotify();
		return 0;
	}

	public void addEpisodeVideo(int eid, Video v, int shield, int status_notify) {
		if (shield == 0 && status_notify == 0)
			addEpisodeVideo(eid, v);
		if (status_notify != 0) {
			if (0 == isExistReturnStatusNotify(eid, v.getVid()))
				addEpisodeVideo(eid, v, status_notify);
		}
	}

	public void deleteEpisodeVideo(int eid, int vid) {
		Criteria criteria = new Criteria();
		criteria.add(EpisodeVideoPeer.FK_EPISODE_ID, eid);
		criteria.add(EpisodeVideoPeer.VIDEO_ID, vid);
		try {
			int num = selectNum(criteria);
			EpisodeVideoPeer.doDelete(criteria);
			if(num>0){
				EpisodeVideoUpdateInfoMgt.getInstance().insert("delete", 0, num);
				LogInfoWriter.episodeVideo_log.info("operate:delete,num:"+num);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public int selectNum(Criteria criteria){
		try {
			return EpisodeVideoPeer.doSelect(criteria).size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void deleteEpisodeVideoByFileid(int eid, int vid) {
		Video video = JujiUtils.getInstance().getVideoByDB(vid);
		if (null == video || video.getVid() < 1)
			return;
		String url = "http://"+Config.getYoukuHost()+"/search?type=18&curpage=1&pagesize=100&order=1&orderfield=1&md5=1&keyword="
				+ video.getFile_id();
		List<Video> videos = null;
		try {
			videos = EpisodeVideoPrepare.getInstance().getVideoListByVid(url);
			if (null == videos || videos.size() < 1)
				return;
			for (Video v : videos) {
				deleteEpisodeVideo(eid, v.getVid());
				logger.info("deleteEpisodeVideo:eid:" + eid + ",vid:"
						+ v.getVid());
			}
		} catch (Exception e) {
		}

	}

	public void addEpisodeVideo(Episode e) {
		int vid = DataFormat.parseInt(e.getVideoId());
		int status = isExistReturnStatusNotify(e.getId(),vid);
		if (status > 0)
			return;
		else {
//			int s = JujiUtils.callApi(e.getEncodeVideoId());
			Video v = JujiUtils.getInstance().getVideoByDB(vid);
			addEpisodeVideo(e.getId(), v, 3);
		}
	}

	public List<EpisodeVideo> getEpisodeVideo(int status, boolean isEqual)
			throws TorqueException {
		Criteria criteria = new Criteria();
		if (isEqual)
			criteria.add(EpisodeVideoPeer.STATUS_NOTIFY, (Object) status,
					Criteria.EQUAL);
		else
			criteria.add(EpisodeVideoPeer.STATUS_NOTIFY, (Object) status,
					Criteria.NOT_EQUAL);
		return EpisodeVideoPeer.doSelect(criteria);
	}

	public void updateEpisodeVide(int id, int status) throws TorqueException {
		executeSql("update episode_video set status_notify=" + status
				+ " where id=" + id,"update status");
	}
}
