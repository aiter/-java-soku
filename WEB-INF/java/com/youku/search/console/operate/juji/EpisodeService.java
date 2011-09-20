package com.youku.search.console.operate.juji;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodePeer;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.teleplay.Video;
import com.youku.search.console.vo.EpisodeVO;

public class EpisodeService {
	static Log logger = LogFactory.getLog(EpisodeService.class);

	private EpisodeService() {
	}

	private static EpisodeService instance = null;

	public static synchronized EpisodeService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance = new EpisodeService();
			return instance;
		}
	}

	public boolean deleteEpisode(int tid, int pid, int eid, int i)
			throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
		criteria.add(EpisodePeer.FK_VERSION_ID, pid);
		criteria.add(EpisodePeer.ORDER_ID, i, SqlEnum.GREATER_THAN);
		List<Episode> el = EpisodePeer.doSelect(criteria);

		EpisodeVideoMgt.getInstance().executeSql(
				"delete from episode_video where fk_episode_id =" + eid,"delete");

		if (el == null || el.size() < 1) {
			criteria = new Criteria();
			criteria.add(EpisodePeer.ID, eid);
			List<Episode> eelist = EpisodePeer.doSelect(criteria);
			int k = 0;
			for (Episode ee : eelist) {
				if (ee.getVideoId() != null && ee.getVideoId() != ""
						&& ee.getVideoId().trim().length() > 0)
					k = k + 1;
			}
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set fixed=0,episode_count=episode_count-"
							+ k + " where id=" + pid);
			EpisodePeer.doDelete(criteria);

		} else {
			EpisodeMgt.getInstance().executeSql("update episode set isLock=0,source_name='',video_id='',encode_video_id='',logo='',seconds=0,file_id='' where id="
							+ eid);
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set episode_count=episode_count-1 where id="
							+ pid);
		}
		return true;
	}

	public boolean deleteEpisode(int eid) throws NoRowsException,
			TooManyRowsException, TorqueException {
		Episode e = EpisodePeer.retrieveByPK(eid);
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_TELEPLAY_ID, e.getFkTeleplayId());
		criteria.add(EpisodePeer.FK_VERSION_ID, e.getFkVersionId());
		criteria
				.add(EpisodePeer.ORDER_ID, e.getOrderId(), SqlEnum.GREATER_THAN);
		List<Episode> el = EpisodePeer.doSelect(criteria);
		criteria = new Criteria();
		criteria.add(EpisodePeer.ID, eid);
		List<Episode> eelist = EpisodePeer.doSelect(criteria);
		int k = 0;
		int subcate = 0;
		if (eelist != null && eelist.size() > 0) {
			if (null != eelist.get(0)) {
				PlayVersion pv = PlayVersionMgt.getInstance()
						.getPlayVersionByID(eelist.get(0).getFkVersionId());
				if (null != pv)
					subcate = pv.getSubcate();
			}
		}
		for (Episode ee : eelist) {
			if (ee.getVideoId() != null && ee.getVideoId() != ""
					&& ee.getVideoId().trim().length() > 0)
				k = k + 1;
		}
		if (el == null || el.size() < 1 || subcate == 2078) {
			EpisodePeer.doDelete(criteria);
		} else {
			EpisodeMgt
					.getInstance()
					.executeSql(
							"update episode set isLock=0, source_name='',video_id='',encode_video_id='',logo='',seconds=0,file_id='' where id="
									+ eid);
		}
		if (k > 0)
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set episode_count=episode_count-" + k
							+ " where id=" + e.getFkVersionId());
		EpisodeVideoMgt.getInstance().executeSql(
				"delete from episode_video where fk_episode_id =" + eid,"delete");
		return true;
	}
	
	public EpisodeVO getEpisodeVO(int tid,int pid,int count,int subcate) throws TorqueException{
		String tn=PlayNameMgt.getInstance().getTeleplayMainName(tid);
		String pn=PlayVersionMgt.getInstance().getVersionName(pid);
		String title = tn;
		if(!StringUtils.isBlank(pn)){
			title=tn+pn;
		}
		EpisodeVO epvo= VersionEpisodeSpiderService.getInstance().getEpisodeVO(tid, pid,title, count, subcate);
		return epvo;
	}
	
	public int updateSingleEpisode(int eid, String eurl, int tid, int pid,
			int i) throws Exception {
		Episode episode = null;
		if (eid > 0)
			episode = EpisodeMgt.getInstance().getEpisodeByID(eid);
		
		
		if ((null == eurl || eurl.trim().length() < 1) && eid > 0) {
			EpisodeMgt.getInstance().executeSql(
							"update episode set isLock=0,source_name='',video_id='',encode_video_id='',logo='',file_id='' where id="
									+ eid);
			if (episode != null && episode.getVideoId() != null
					&& episode.getVideoId().trim().length() > 0)
				PlayVersionMgt.getInstance().executeSql(
						"update play_version set fixed=0,episode_count=episode_count-1 where id="
								+ pid);
			return eid;
		}
		Video v = null;
		int vid = -1;
		vid = JujiUtils.urlAnalyze(eurl);
		v = JujiUtils.getInstance().getVideoByDB(vid);
		if (null == v||v.getVid()<1)
			return -1;
		Episode e = null;
		EpisodeVideoMgt evmgt = EpisodeVideoMgt.getInstance();
		if (eid < 1) {
			int p = 1;
			while (p < i) {
				Criteria criteria = new Criteria();
				criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
				criteria.add(EpisodePeer.FK_VERSION_ID, pid);
				criteria.add(EpisodePeer.ORDER_ID, p);
				List<Episode> el = EpisodePeer.doSelect(criteria);
				if (el == null || el.size() < 1) {
					e = new Episode();
					e.setFkTeleplayId(tid);
					e.setFkVersionId(pid);
					e.setName("第" + p + "集");
					e.setSourceName("");
					e.setVideoId("");
					e.setEncodeVideoId("");
					e.setLogo("");
					e.setOrderId(p);
					e.setFileId("");
					e.save();
					p = p + 1;
				} else {
					p = p + 1;
					continue;
				}
			}
			Criteria criteria = new Criteria();
			criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
			criteria.add(EpisodePeer.FK_VERSION_ID, pid);
			criteria.add(EpisodePeer.ORDER_ID, p);
			List<Episode> el = EpisodePeer.doSelect(criteria);
			if (el == null || el.size() < 1)
				e = new Episode();
			else
				e = el.get(0);
			e.setFkTeleplayId(tid);
			e.setFkVersionId(pid);
			e.setOrderId(i);
			e.setName("第" + i + "集");
			e.setSourceName(v.getTitle());
			e.setVideoId("" + vid);
			e.setEncodeVideoId(v.getEncodeVid());
			e.setLogo(v.getLogo());
			e.setSeconds(v.getSeconds());
			e.setIslock(1);
			e.setFileId(v.getFile_id());
			e.save();
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set episode_count=episode_count+1 where id="
							+ pid);
		} else {
			EpisodeMgt.getInstance().executeSql("update episode set isLock=1, source_name='"
					+ v.getTitle() + "',video_id='" + vid
					+ "',encode_video_id='" + v.getEncodeVid() + "',logo='"
					+ v.getLogo() + "',seconds="+v.getSeconds()+",file_id='"+v.getFile_id()+"' where id=" + eid);
			
			if (episode != null
					&& (episode.getVideoId() == null || episode.getVideoId()
							.trim().length() < 1))
				PlayVersionMgt.getInstance().executeSql(
						"update play_version set episode_count=episode_count+1 where id="
								+ pid);

		}
		int episodeid = eid;
		if (eid < 1)
			episodeid = e.getId();
		if (i == 1)
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set firstlogo='" + v.getLogo()
							+ "' where id=" + pid);
		
		// add
		evmgt.addEpisodeVideo(episodeid, v);
		return episodeid;
	}

	public boolean deleteEpisode(int tid,int pid,int eid,int i,int subcate) throws Exception{
		boolean f=false;
		if(subcate==2078){
			f=VarietyMgt.getInstance().deleteVariety(pid, eid);
		}else{
			f=deleteEpisode(tid, pid, eid, i);
		}
		return f;
	}
	
	public void updateSingleEpisode(int eid,int isLock) throws TorqueException{
		EpisodeMgt.getInstance().executeSql("update episode set isLock="+isLock+" where video_id!='' and id="+eid);
	}
}
