package com.youku.search.console.operate.juji;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.operate.RegexpBuilder;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodeVideo;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.teleplay.TeleplayQuery;
import com.youku.search.console.teleplay.VarietyQuery;
import com.youku.search.console.teleplay.Video;
import com.youku.search.util.DataFormat;
import com.youku.search.util.MyUtil;
import com.youku.soku.library.Utils;

public class EpisodeVideoDateIn {

static Log logger = LogFactory.getLog(EpisodeVideoDateIn.class);
	
	private EpisodeVideoDateIn(){}
	private static EpisodeVideoDateIn instance = null;
	
	public static synchronized EpisodeVideoDateIn getInstance() {
		if(null!=instance)
			return instance;
		else {
			instance = new EpisodeVideoDateIn();
			return instance;
		}
	}
	
	public void dateInsert(int eid, Video v){
//		if (v.getVid() > maxvideoid) {
			EpisodeVideoMgt evmgt = EpisodeVideoMgt.getInstance();
			int s = evmgt.isExistReturnStatusNotify(eid, v.getVid());
			if(1==s)
				return;
//			int status = JujiUtils.callApi(MyUtil.encodeVideoId(v.getVid()));
//			if (status != 1&&status!=3)
//				status = 2;
			if(0==s)
				evmgt.addEpisodeVideo(eid, v, 3);
//		}
	}

	public List<Video> getVidsFromTeleplay(Set<String> teleplaynames, Set<String> playversionnames,
			int order,int date) {
		TeleplayQuery query = new TeleplayQuery();
		query.setSort("createtime");
		String regexp = RegexpBuilder.build(teleplaynames, playversionnames, order);
		List<Video> videos = query.getOneMovie(teleplaynames, playversionnames,
				order,regexp, 0, date);
		List<Video> vidlist = new ArrayList<Video>();
		for (Video v : videos) {
			if (v != null) {
//				if (v.getVid() <= maxvideoid)
//					continue;
				vidlist.add(v);
			}
		}
		return vidlist;
	}

	public List<Video> getVidsFromVariety(Set<String> varietyName, Collection<String> excludes,
			int order,int date) {
		VarietyQuery vq = new VarietyQuery();
		List<Video> videos = vq.getOneVariety(varietyName, excludes,
				DataFormat.parseUtilDate(""+order,
						DataFormat.FMT_DATE_SPECIAL), 0, date);
		List<Video> vidlist = new ArrayList<Video>();
		for (Video v : videos) {
			if (v != null) {
//				if (v.getVid() <= maxvideoid)
//					continue;
				vidlist.add(v);
			}
		}
		return vidlist;
	}

	public void dailyEpisodeVideoInsert() {
		List<Episode> elist = null;
		try {
			elist = EpisodeMgt.getInstance().getLastEditEpisodes();
			episodeVideoSearch(elist, 0);
			logger.info("每日剧集搜索(当天修改)搜索完成,当日修改的episode.size:"+elist.size());
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	
	public void episodeVideoInsert() {
		List<Episode> elist = null;
		try {
			elist = EpisodeMgt.getInstance().getAllNoValidEpisodes();
			episodeVideoSearch(elist, 3);
			logger.info("每日剧集搜索搜索完成,episode.size:"+elist.size());
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public void episodeVideoSearch(List<Episode> elist,int date){
		Set<String> tns = null;
		Set<String> pns = null;
		List<Video> vidlist = null;
		PlayVersion pv = null;
		int temp = -1;
		int versionid = 0;
		for (Episode e : elist) {
			try {
				if (StringUtils.isBlank(e.getVideoId()))
					continue;
				versionid = e.getFkVersionId();
				if (temp != versionid) {
					pv = PlayVersionMgt.getInstance().getPlayVersionByID(e.getFkVersionId());
					temp = versionid;
				}
				if (null == pv)
					continue;
				
				tns = PlayNameMgt.getInstance().getTeleplayNameByIdReturnSet(e.getFkTeleplayId());
				if(null==tns||tns.size()<1) continue;
				
				pns = PlayVersionMgt.getInstance().getVersionNameByIdReturnSet(e.getFkVersionId());
				
				
				if (pv.getSubcate() != 2078) {
					
					logger.info("date:"+date+",剧集搜索-episodeID:" + e.getId()
							+ "\tteleplayName:" + tns.toString()+"\tplayName:"+(null!=pns?pns.toString():"")+"\tepisode_order:"+e.getOrderId());
					
					vidlist = getVidsFromTeleplay(tns, pns, e.getOrderId(),date);
				} else {
					String exclude = ExcludeMgt.getInstance().getExclude(
							e.getFkTeleplayId());
					Set<String> excludes = null;
					if (null != exclude && exclude.trim().length() > 0)
						excludes = Utils.parseStr2Set(exclude, "\\|");
					logger.info("date:"+date+",剧集搜索-episodeID:" + e.getId()
							+ "\tteleplayName:" + (null!=pns?pns.toString():"")+"\tunexeculde:"+(null!=exclude?exclude.toString():"")+"\tepisode_order:"+e.getOrderId());
					
					vidlist = getVidsFromVariety(tns, excludes,e.getOrderId(),date);
				}
				for (Video v : vidlist) {
					dateInsert(e.getId(), v);
				}
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}

	public void callApiList() {
		try {
			List<EpisodeVideo> evl = EpisodeVideoMgt.getInstance().getEpisodeVideo(1,false);
			Video v=null;
			int source_type;
			for (EpisodeVideo ev : evl) {
				v = JujiUtils.getInstance().getVideoByDB(ev.getVideoId());
				if(null==v||v.getVid()<1) continue;
				source_type = v.getSource_type();
				try {
					if((1&source_type)==1){
						EpisodeVideoMgt.getInstance().updateEpisodeVide(ev.getId(),1);
					}else{
//						int status = JujiUtils.callApi(MyUtil.encodeVideoId(ev.getVideoId()));
//						EpisodeVideoMgt.getInstance().updateEpisodeVide(ev.getId(),status);
					}
					Thread.sleep(100);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
