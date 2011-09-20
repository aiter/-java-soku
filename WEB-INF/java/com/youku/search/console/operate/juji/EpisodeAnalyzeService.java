package com.youku.search.console.operate.juji;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;

import com.youku.search.console.operate.Channel;
import com.youku.search.console.operate.RegexpBuilder;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodePeer;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.teleplay.TeleplayQuery;
import com.youku.search.console.teleplay.VarietyQuery;
import com.youku.search.console.teleplay.Video;
import com.youku.search.console.vo.EpisodeLogVO;
import com.youku.search.console.vo.EpisodeSingleVO;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;

public class EpisodeAnalyzeService {
	
	static Log logger = LogFactory.getLog(EpisodeAnalyzeService.class);

	private EpisodeAnalyzeService() {
	}

	private static EpisodeAnalyzeService instance = null;

	public static synchronized EpisodeAnalyzeService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance = new EpisodeAnalyzeService();
			return instance;
		}
	}
	
	public boolean episodeAnalyze(int tid, int pid,int total_count) throws Exception {
		try {
			EpisodeMgt emgt=EpisodeMgt.getInstance();
			EpisodeVideoMgt evmgt = EpisodeVideoMgt.getInstance();
			TeleplayQuery tq = new TeleplayQuery();
			Set<String> tns = PlayNameMgt.getInstance().getTeleplayNameByIdReturnSet(tid);
			if(null==tns||tns.size()<1)return true;
			Set<String> pns = PlayVersionMgt.getInstance().getVersionNameByIdReturnSet(pid);
			List<Video> lv = null;
			boolean f = true;
			int k = 0;
			String logo = null;
			Video v=null;
			Episode e = null;
			PlayVersion pv=PlayVersionMgt.getInstance().getPlayVersionByID(pid);
			if(null!=pv)
				logo=pv.getFirstlogo();
			List<Episode> episodes=emgt.getEpisodes(tid, pid, (Object)(""));
			if(null!=episodes&&episodes.size()>0){
				String regexp = null;
				for(Episode ep:episodes){
					if(null==ep.getVideoId()||ep.getVideoId().trim().length() < 1){
						regexp = RegexpBuilder.build(tns, pns, ep.getOrderId());
						lv=tq.getOneMovie(tns,pns,ep.getOrderId(),regexp, 5, 0);
						if(null!=lv&&lv.size()>0){
							for(int q=0;q<lv.size();q++){
								v=lv.get(q);
								if (null != v&&!BlacklistMgt.isInBlacklist(pid, v.getVid())){
									System.out.println("versionID:" + pid + "\tteleplayName:" + tns
											+ "\tversionName:" + pns+"\tepisodeName:"+ep.getName()+"\tvid:"+v.getVid());
									break;
								}
							}
						}
						else v=null;
						if (null != v&&!BlacklistMgt.isInBlacklist(pid, v.getVid())) {
							k = k + 1;
							
							ep.setSourceName(v.getTitle());
							ep.setVideoId("" + v.getVid());
							ep.setEncodeVideoId(v.getEncodeVid());
							ep.setLogo(v.getLogo());
							ep.setSeconds(v.getSeconds());
							ep.setFileId(v.getFile_id());
							ep.setFileId(v.getFile_id());
							EpisodePeer.doUpdate(ep);
							
							evmgt.addEpisodeVideo(ep.getId(), v);
							
							EpisodeLogMgt.getInstance().save(
									TeleplayService.getInstance().getfullWords(ep.getId(), false), ep.getId());
							
							if ((null == logo||logo.trim().length()<1)
									&& ep != null
									&& (ep.getVideoId() != null || ep.getVideoId().trim()
											.length() > 0))
								logo = ep.getLogo();
						}else{
							f=false;
						}
					}
				}
			}
			
			int maxorder=EpisodeMgt.getInstance().getMaxEpisodesOrder(tid, pid);
			int tempmax=maxorder+1;
			if(total_count==0||maxorder<total_count){
				if(total_count>0)total_count=total_count+1;
				lv=tq.getMovies(tns,pns, tempmax, total_count);
				System.out.println("versionID:" + pid + "\tteleplayName:" + tns
						+ "\tversionName:" + pns+"\ttempmax:"+tempmax+"\tsize:"+lv.size());
				for(Video video:lv){
					e = new Episode();
					e.setFkTeleplayId(tid);
					e.setFkVersionId(pid);
					e.setName("第" + tempmax + "集");
					e.setOrderId(tempmax);
					if (null != video&&!BlacklistMgt.isInBlacklist(pid, video.getVid())) {
						e.setSourceName(video.getTitle());
						e.setVideoId("" + video.getVid());
						e.setEncodeVideoId(video.getEncodeVid());
						e.setLogo(video.getLogo());
						e.setSeconds(video.getSeconds());
						e.setFileId(video.getFile_id());
						k = k + 1;
						
					} else {
						f = false;
						e.setSourceName("");
						e.setVideoId("");
						e.setEncodeVideoId("");
						e.setLogo("");
						e.setSeconds(0);
					}
					e.save();
					tempmax=tempmax+1;
					if (null != video&&!BlacklistMgt.isInBlacklist(pid, video.getVid())) {
						evmgt.addEpisodeVideo(e.getId(), video);
						EpisodeLogMgt.getInstance().save(
								TeleplayService.getInstance().getfullWords(e.getId(), true), e.getId());
					}
					if ((null == logo||logo.trim().length()<1)
							&& e != null
							&& (e.getVideoId() != null || e.getVideoId().trim()
									.length() > 0))
						logo = e.getLogo();
				}
			}
			String shql1 = " episode_count=episode_count+" + k;
			if (null != logo && logo.trim().length() > 0)
				shql1 = shql1 + ",firstlogo='" + logo + "'";
			String shql = "";
			if (!f) {
				shql = shql1 + " ,fixed=0";
			} else
				shql = shql1;
			if (shql != null && shql.trim().length() > 0)
				PlayVersionMgt.getInstance().executeSql("update play_version set "
						+ shql + " where id=" + pid);
			return true;
		} catch (TorqueException e) {
			System.out
					.println("[ERROR] update episode  date error  in function episodeAnalyze");
			e.printStackTrace();
			return false;
		} 
	}

	public boolean add2blacklist(int eid, int pid, int dn)
			throws Exception {
		Episode e = EpisodePeer.retrieveByPK(eid);
		if(null!=e&&DataFormat.parseInt(e.getVideoId())==0)return false;
		BlacklistMgt.getInstance().add2Blacklist(e.getFkVersionId(), DataFormat.parseInt(e.getVideoId()));
		// add
		EpisodeVideoMgt.getInstance().deleteEpisodeVideoByFileid(eid, DataFormat.parseInt(e.getVideoId()));

		FeedBackMgt.getInstance().deleteFeedbackByepisodeID(eid, 1, Channel.SYSUSER);

		EpisodeLogMgt.getInstance().deleteByEpisodeID(eid);

		episodeSaveNext(e, dn);
		return true;
	}

	private void episodeSaveNext(Episode e, int dn)
			throws TorqueException {
		List<Video> vl = null;
		Video v = null;
		Set<String> tns = PlayNameMgt.getInstance().getTeleplayNameByIdReturnSet(e.getFkTeleplayId());
		if (dn != -1) {
			String exclude = ExcludeMgt.getInstance().getExclude(e.getFkTeleplayId());
			Set<String> excludes = null;
			if (null != exclude && exclude.trim().length() > 0)
				excludes = Utils.parseStr2Set(exclude, "\\|");
			VarietyQuery vq = new VarietyQuery();
			vl = vq.getOneVariety(tns,excludes, DataFormat.parseUtilDate("" + dn,
							DataFormat.FMT_DATE_SPECIAL), 10, 0);
		} else {
			TeleplayQuery query = new TeleplayQuery();
			Set<String> pns = PlayVersionMgt.getInstance().getVersionNameByIdReturnSet(e.getFkVersionId());
			String regexp = RegexpBuilder.build(tns, pns, e.getOrderId());
			vl = query.getOneMovie(tns,pns,e.getOrderId(),regexp);
		}
		for (Video video : vl) {
			if (null == video)
				continue;
			if (!BlacklistMgt.isInBlacklist(e.getFkVersionId(), video.getVid())) {
				v = video;
				break;
			}
		}
		if (null != v) {
			EpisodeMgt.getInstance().executeSql("update episode set isLock=0, source_name='"
					+ v.getTitle() + "',video_id='" + v.getVid()
					+ "',encode_video_id='" + v.getEncodeVid() + "',logo='"
					+ v.getLogo() + "',seconds=" + v.getSeconds()+",file_id='"+v.getFile_id()+"'"
					+ " where id=" + e.getId());

			EpisodeVideoMgt evmgt = EpisodeVideoMgt.getInstance();
			evmgt.addEpisodeVideo(e.getId(), v);

		} else {
			PlayVersionMgt.getInstance().executeSql(
							"update play_version set episode_count=episode_count-1 where episode_count>0 and id="
									+ e.getFkVersionId());
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set fixed=0 where id="
							+ e.getFkVersionId());
			EpisodeMgt.getInstance().executeSql(
							"update episode set isLock=0, source_name='',video_id='',encode_video_id='',logo='',seconds=0,file_id='' where id="
									+ e.getId());
		}

	}

	public boolean updateSingleEpisode(EpisodeSingleVO evo)
			throws Exception {
		Episode e = EpisodePeer.retrieveByPK(evo.getId());
		if ((null == evo.getUrl() || evo.getUrl().length() < 1)
				&& evo.getId() > 0) {
			EpisodeMgt.getInstance().executeSql(
							"update episode set isLock=0, source_name='',video_id='',encode_video_id='',logo='',file_id='' where id="
									+ evo.getId());
			PlayVersionMgt.getInstance().executeSql(
					"update play_version set fixed=0,episode_count=episode_count-1 where id="
							+ e.getFkVersionId());
			return true;
		}
		Video v = null;
		int vid = -1;
		vid = JujiUtils.urlAnalyze(evo.getUrl());
		v = JujiUtils.getInstance().getVideoByDB(vid);
		if (null == v||v.getVid()<1)
			return false;
		EpisodeVideoMgt evmgt = EpisodeVideoMgt.getInstance();
		EpisodeMgt.getInstance().executeSql("update episode set isLock=1, source_name='"
				+ v.getTitle() + "',video_id='" + vid + "',encode_video_id='"
				+ v.getEncodeVid() + "',logo='" + v.getLogo() + "',seconds="+v.getSeconds()+",file_id='"+v.getFile_id()+"' where id="
				+ evo.getId());
		evmgt.addEpisodeVideo(evo.getId(), v);
		return true;
	}

	public boolean updateSingleEpisode(EpisodeLogVO evo)
			throws Exception {
		EpisodeSingleVO esvo = new EpisodeSingleVO();
		esvo.setId(evo.getFkEpisodeId());
		esvo.setUrl(evo.getUrl());
		Episode e = EpisodeMgt.getInstance().getEpisodeByID(evo.getFkEpisodeId());
		if (null == e)
			return false;
		esvo.setName("" + e.getOrderId());
		return updateSingleEpisode(esvo);
	}
}
