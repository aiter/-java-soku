package com.youku.search.console.operate.juji;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodePeer;
import com.youku.search.console.teleplay.VarietyQuery;
import com.youku.search.console.teleplay.Video;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;

public class VarietyMgt {
	static Log logger = LogFactory.getLog(VarietyMgt.class);

	private VarietyMgt() {
	}

	private static VarietyMgt instance = null;

	public static synchronized VarietyMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new VarietyMgt();
			return instance;
		}
	}
	
	public int updateSingleVariety(int eid, String eurl, int tid, int pid,
			int i,int dn) throws Exception {
		//System.out.println("eid="+eid+"---eurl="+eurl+"---tid="+tid+"---pid="+pid+"---i="+i+"---dn="+dn);
			Episode episode = null;
			if(eid > 0)
				episode=EpisodePeer.retrieveByPK(eid);
			if ((null == eurl || eurl.trim().length() < 1) && eid > 0) {
				EpisodeMgt.getInstance().executeSql(
								"update episode set source_name='',video_id='',encode_video_id='',logo='',file_id='' where id="
										+ eid);
				if(episode!=null&&episode.getVideoId()!=null&&episode.getVideoId().trim().length()>0)
				PlayVersionMgt.getInstance().executeSql(
						"update play_version set fixed=0,episode_count=episode_count-1 where id=" + pid);
				return eid;
			}
			Video v = null;
			int vid = -1;
			vid = JujiUtils.urlAnalyze(eurl);
			v = JujiUtils.getInstance().getVideoByDB(vid);
			if (null == v||v.getVid()<1)
				return -1;
			Episode e =null;
			EpisodeVideoMgt evmgt=EpisodeVideoMgt.getInstance();
			if (eid < 1) {
				Criteria criteria = new Criteria();
				criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
				criteria.add(EpisodePeer.FK_VERSION_ID, pid);
				criteria.add(EpisodePeer.ORDER_ID, dn);
				List<Episode> el = EpisodePeer.doSelect(criteria);
				if(el==null||el.size()<1)
					e = new Episode();
				else e=el.get(0);
				e.setFkTeleplayId(tid);
				e.setFkVersionId(pid);
				e.setOrderId(dn);
				e.setName(StringUtils.substring(""+dn, 4, 6));
				e.setSourceName(v.getTitle());
				e.setVideoId("" + vid);
				e.setEncodeVideoId(v.getEncodeVid());
				e.setLogo(v.getLogo());
				e.setSeconds(v.getSeconds());
				e.setFileId(v.getFile_id());
				e.save();
				PlayVersionMgt.getInstance().executeSql(
						"update play_version set episode_count=episode_count+1 where id=" + pid);
			} else {
				EpisodeMgt.getInstance().executeSql("update episode set source_name='"
						+ v.getTitle() + "',video_id='" + vid
						+ "',encode_video_id='" + v.getEncodeVid() + "',logo='"
						+ v.getLogo() + "',order_id="+dn+",seconds="+v.getSeconds()+",file_id='"+v.getFile_id()+"' where id=" + eid);
				if(episode!=null&&(episode.getVideoId()==null||episode.getVideoId().trim().length()<1))
					PlayVersionMgt.getInstance().executeSql(
							"update play_version set episode_count=episode_count+1 where id=" + pid);
					
			}
			int episodeid=eid;
			if(eid<1)
				episodeid=e.getId();
			if (i == 1)
				PlayVersionMgt.getInstance().executeSql(
						"update play_version set firstlogo='" + v.getLogo()
								+ "' where id=" + pid);
			//add
//			TeleplaySearch ts=new TeleplaySearch();
//			TeleName telename=ts.getTeleplayNameById(tid, conn);
			evmgt.addEpisodeVideo(episodeid, v);
//			String exclude=ExcludeMgt.getInstance().getExclude(tid, conn);
//			String[] excludes=null;
//			if(null!=exclude&&exclude.trim().length()>0)
//				excludes=exclude.split(";");
//			
//			evmgt.addVarietyVideo(conn, episodeid, telename.getName(), telename.getAlias(),excludes, DataFormat.parseUtilDate(""+dn, DataFormat.FMT_DATE_SPECIAL));
			return episodeid;
	}
	
	
	
	public boolean deleteVariety(int pid, int eid) throws Exception {
			EpisodeVideoMgt.getInstance().executeSql("delete from episode_video where fk_episode_id ="
							+ eid ,"delete");
			Criteria criteria = new Criteria();
			criteria.add(EpisodePeer.ID, eid);
			List<Episode> el=EpisodePeer.doSelect(criteria);
			if(el!=null&&el.size()>0&&el.get(0).getVideoId()!=null&&el.get(0).getVideoId().trim().length()>0)
				PlayVersionMgt.getInstance().executeSql(
							"update play_version set fixed=0,episode_count=episode_count-1 where id=" + pid);
			EpisodePeer.doDelete(criteria);
			return true;
	}
	
	public boolean varietyAnalyze(int tid, int pid, String pn, int count) {
		try {
			Set<String> tns = PlayNameMgt.getInstance().getTeleplayNameByIdReturnSet(tid);
			List<Video> lv = null;
			String exclude=ExcludeMgt.getInstance().getExclude(tid);
			Set<String> excludes=null;
			if(null!=exclude&&exclude.trim().length()>0)
				excludes=Utils.parseStr2Set(exclude, "\\|");
			VarietyQuery vq = new VarietyQuery();
			lv = vq.getVarietyOneYear(tns, excludes,DataFormat.parseInt(pn));
			Criteria criteria = null;
			List<Episode> eplist = null;
			int k = 0;
			String logo = null;
			EpisodeVideoMgt evmgt=EpisodeVideoMgt.getInstance();
			
			for (int i = 0; i < lv.size(); i++) {
				Video v = lv.get(i);
				criteria = new Criteria();
				criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
				criteria.add(EpisodePeer.FK_VERSION_ID, pid);
				criteria.add(EpisodePeer.ORDER_ID, v.getIndex());
				eplist = EpisodePeer.doSelect(criteria);
				Episode e = null;
				if (eplist != null && eplist.size() > 0) {
					e = eplist.get(0);
					if ((e.getVideoId() == null
							|| e.getVideoId().trim().length() < 1)&&!BlacklistMgt.isInBlacklist(pid, v.getVid())) {
							k = k + 1;
							e.setSourceName(v.getTitle());
							e.setVideoId("" + v.getVid());
							e.setEncodeVideoId(v.getEncodeVid());
							e.setLogo(v.getLogo());
							e.setSeconds(v.getSeconds());
							e.setFileId(v.getFile_id());
							evmgt.addEpisodeVideo(e.getId(), v);
					} else
						continue;
					EpisodePeer.doUpdate(e);
					EpisodeLogMgt.getInstance().save(PlayNameMgt.getInstance().getTeleplayMainName(tid)+e.getOrderId(), e.getId());
					
				} else {
					if(!BlacklistMgt.isInBlacklist(pid, v.getVid())){
						e = new Episode();
						e.setFkTeleplayId(tid);
						e.setFkVersionId(pid);
						e.setName(StringUtils.substring(v.getIndex(), 4, 6));
						e.setOrderId(DataFormat.parseInt(v.getIndex()));
						e.setSourceName(v.getTitle());
						e.setVideoId("" + v.getVid());
						e.setEncodeVideoId(v.getEncodeVid());
						e.setLogo(v.getLogo());
						e.setSeconds(v.getSeconds());
						e.setFileId(v.getFile_id());
						k = k + 1;
						e.save();
						evmgt.addEpisodeVideo(e.getId(), v);
						EpisodeLogMgt.getInstance().save(PlayNameMgt.getInstance().getTeleplayMainName(tid)+e.getOrderId(), e.getId());
					}else continue;
				}
//					evmgt.addVarietyVideo(conn, e.getId(), telename.getName(), telename.getAlias(),excludes,DataFormat.parseUtilDate(v.getIndex(), DataFormat.FMT_DATE_SPECIAL));
					
				if (null == logo)
					logo = e.getLogo();
			}
			String shql = " episode_count=episode_count+" + k;
			if (null != logo && logo.trim().length() > 0)
				shql = shql + ",firstlogo='" + logo + "'";
			if (lv.size() > 0 && shql != null && shql.trim().length() > 0)
				PlayVersionMgt.getInstance().executeSql("update play_version set "
						+ shql + " where id=" + pid);
			return true;
		} catch (Exception e) {
			logger.error(e);
			
		}
		return false;
	}
	
}
