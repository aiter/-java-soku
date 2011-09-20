package com.youku.search.console.operate.juji;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.teleplay.QueryResult;
import com.youku.search.console.teleplay.TeleplayQuery;
import com.youku.search.console.teleplay.VarietyQuery;
import com.youku.search.console.teleplay.Video;
import com.youku.search.console.vo.TeleCate;

public class VersionEpisodeService {
	
	static Log logger = LogFactory.getLog(VersionEpisodeService.class);

	private VersionEpisodeService() {
	}

	private static VersionEpisodeService instance = null;

	public static synchronized VersionEpisodeService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new VersionEpisodeService();
			return instance;
		}
	}
	public void saveAllVersionAndVariety(Set<String> names,Collection<String> exclude,int tid,TeleCate tc) throws Exception{
		VarietyQuery query = new VarietyQuery();
		List<Video> videos = query.getAllVariety(names, exclude);
		if(null==videos||videos.size()<1)
			return;
		PlayVersion pv=null;
		String temp="a";
		String year="b";
		int j=0;
		Video v = null;
		int eid = 0;
		for(int i=0;i<videos.size();i++){
			year=videos.get(i).getIndex().substring(0, 4);
			if(!temp.equalsIgnoreCase(year)){
				//添加版本信息
				pv = new PlayVersion();
				pv.setFkTeleplayId(tid);
				pv.setName(year);
				pv.setViewName(year);
				pv.setAlias("");
				pv.setOrderId(j);
				pv.setFixed(0);
				pv.setCate(tc.getCate());
				pv.setSubcate(tc.getSubcate());
				pv.save();
				temp=year;
				j=j+1;
			}
			//添加视频剧集
			v = videos.get(i);
			eid = EpisodeMgt.getInstance().saveEpisodeReturnEid(tid, pv.getId(), i+1, v);
			if(0!=eid&&null!=v){
				EpisodeVideoMgt.getInstance().addEpisodeVideo(eid, v);
			}
			}
	}

	public void saveAllVersionAndEpisode(Set<String> names,int tid,TeleCate tc) throws Exception{
		TeleplayQuery tq = new TeleplayQuery();
		QueryResult qr = tq.queryAllVersionMovies(names);
		if(null!=qr){
			List<Video> lv=null;
			PlayVersion pv=null;
			Video v = null;
			if(0==qr.getVersion()){
				//添加一个版本名为null的默认版本
				pv = new PlayVersion();
				pv.setFkTeleplayId(tid);
				pv.setName("");
				pv.setViewName("");
				pv.setAlias("");
				pv.setOrderId(0);
				pv.setFixed(0);
				pv.save();
			}else{
				//依次添加版本
				for (int j = 0; j < qr.getVersion(); j++) {
					lv = qr.getContent().get(j);
					//添加版本信息
					pv = new PlayVersion();
					pv.setFkTeleplayId(tid);
					pv.setName(qr.getVersionNames().get(j));
					pv.setViewName(qr.getVersionNames().get(j));
					pv.setAlias("");
					pv.setOrderId(j);
					pv.setFixed(0);
					pv.setCate(tc.getCate());
					pv.setSubcate(tc.getSubcate());
					pv.save();
					if(null!=lv&&lv.size()>0){
						int eid = 0;
						//添加视频剧集
						for (int i = 0; i < lv.size(); i++) {
							v = lv.get(i);
							eid = EpisodeMgt.getInstance().saveEpisodeReturnEid(tid, pv.getId(), i+1, v);
							if(0!=eid&&null!=v){
								EpisodeVideoMgt.getInstance().addEpisodeVideo(eid, v);
							}
						}
					}
				}
			}
		}
	}
	
	
	
//	public void saveAllVersionAndEpisode(Set<String> names,int tid,TeleCate tc) throws Exception{
//		TeleplayQuery tq = new TeleplayQuery();
//		
//		QueryResult qr = tq.queryAllVersionMovies(names);
//		if (qr != null) {
//			PlayVersion pv=null;
//			List<Video> lv=null;
//			if(qr.getVersion()==0){
//				pv = new PlayVersion();
//				pv.setFkTeleplayId(tid);
//				pv.setName("");
//				pv.setViewName("");
//				pv.setAlias("");
//				pv.setOrderId(0);
//				pv.setFixed(0);
//				pv.save();
//			}
//			for (int j = 0; j < qr.getVersion(); j++) {
//				pv = new PlayVersion();
//				pv.setFkTeleplayId(tid);
//				pv.setName(qr.getVersionNames().get(j));
//				pv.setViewName(qr.getVersionNames().get(j));
//				pv.setAlias("");
//				pv.setOrderId(j);
//				pv.setFixed(0);
//				pv.setCate(tc.getCate());
//				pv.setSubcate(tc.getSubcate());
//				pv.save();
//								
//				lv = qr.getContent().get(j);
//				String logo = null;
//				if (null != lv) {
//					Episode es=null;
//					for (int i = 0; i < lv.size(); i++) {
//						es = new Episode();
//						es.setFkTeleplayId(tid);
//						es.setName("第" + (i + 1) + "集");
//						es.setFkVersionId(pv.getId());
//						try {
//							es.setSourceName(lv.get(i).getTitle());
//							es.setOrderId(i + 1);
//							es.setEncodeVideoId(lv.get(i).getEncodeVid());
//							es.setVideoId("" + lv.get(i).getVid());
//							es.setLogo(lv.get(i).getLogo());
//							es.setSeconds(lv.get(i).getSeconds());
//							es.setFileId(lv.get(i).getFile_id());
//							es.save();
//							
//							EpisodeVideoMgt.getInstance().addEpisodeVideo(es);
//						} catch (Exception e) {
//							es.setSourceName("");
//							es.setOrderId(i + 1);
//							es.setEncodeVideoId("");
//							es.setVideoId("");
//							es.setLogo("");
//							es.save();
//							System.out.println("第" + (i + 1) + "集空缺");
//						}
//					}
//					if (null == logo
//							&& es != null
//							&& (es.getVideoId() != null || es.getVideoId()
//									.trim().length() > 0))
//						logo = es.getLogo();
//				}
//				String firstlogo = "";
//				if (null != logo && logo.trim().length() > 0)
//					firstlogo = logo;
//				String shql = "";
//				if (null!=lv&&lv.size() > 0)
//					PlayVersionMgt.getInstance().executeSql(
//							"update play_version set " + shql
//									+ " firstlogo='" + firstlogo
//									+ "',episode_count=" + lv.size()
//									+ " where id=" + pv.getId());
//			}
//			if (qr.getVersion() > 1)
//				TeleplayMgt.getInstance().executeSql(
//						"update teleplay set version_count="
//								+ qr.getVersion() + " where id="
//								+ tid);
//		}
//	}
}
