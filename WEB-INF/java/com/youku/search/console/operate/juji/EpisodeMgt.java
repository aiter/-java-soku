package com.youku.search.console.operate.juji;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.workingdogs.village.Record;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodePeer;
import com.youku.search.console.teleplay.Video;
import com.youku.search.util.DataFormat;

public class EpisodeMgt {
	
	static Log logger = LogFactory.getLog(EpisodeMgt.class);
	
	private EpisodeMgt(){}
	private static EpisodeMgt instance = null;
	
	public static synchronized EpisodeMgt getInstance() {
		if(null!=instance)
			return instance;
		else{
			instance = new EpisodeMgt();
			return instance;
		} 
	}
	
	public void executeSql(String sql){
		try {
			EpisodePeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public String getFirstLogo(int version_id){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_VERSION_ID,version_id);
		criteria.add(EpisodePeer.LOGO,(Object)"",SqlEnum.GREATER_THAN);
		criteria.and(EpisodePeer.LOGO,new Object(),SqlEnum.ISNOTNULL);
		criteria.addAscendingOrderByColumn(EpisodePeer.ORDER_ID);
		try {
			List<Episode> episodes =  EpisodePeer.doSelect(criteria);
			if(null!=episodes){
				for(Episode episode:episodes){
					if(!StringUtils.isBlank(episode.getLogo()))
						return episode.getLogo();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	public int getEpisodeCollecteds(int version_id){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_VERSION_ID,version_id);
		criteria.add(EpisodePeer.VIDEO_ID,(Object)"",SqlEnum.GREATER_THAN);
		criteria.and(EpisodePeer.VIDEO_ID,new Object(),SqlEnum.ISNOTNULL);
		try {
			List<Episode> episodes =  EpisodePeer.doSelect(criteria);
			if(null!=episodes){
				return episodes.size();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}
	
	public int saveEpisodeReturnEid(int teleplay_id,int version_id,int i,Video v){
		Episode es = new Episode();
		es.setFkTeleplayId(teleplay_id);
		es.setName("第" + i + "集");
		es.setOrderId(i);
		es.setFkVersionId(version_id);
		if(null!=v){
			es.setSourceName(v.getTitle());
			es.setEncodeVideoId(v.getEncodeVid());
			es.setVideoId("" + v.getVid());
			es.setLogo(v.getLogo());
			es.setSeconds(v.getSeconds());
			es.setFileId(v.getFile_id());
		}
		try {
			es.save();
			return es.getId();
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}
	
	public List<Episode> getEpisodes(int tid,int pid,int greater_order_id){
		Criteria criteria = new Criteria();
		if(tid>0)criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
		if(pid>0)criteria.add(EpisodePeer.FK_VERSION_ID, pid);
		if(greater_order_id>0)criteria.add(EpisodePeer.ORDER_ID,greater_order_id,SqlEnum.GREATER_EQUAL);
		criteria.addAscendingOrderByColumn(EpisodePeer.ORDER_ID);

		List<Episode> le;
		try {
			le = EpisodePeer.doSelect(criteria);
			if(null!=le&&le.size()>0)
				return le;
		} catch (Exception e) {
			logger.error(criteria.toString(),e);
		}
		return null;
	}
	
	public List<Episode> getEpisodes(int tid,int pid,Object video_id){
		Criteria criteria = new Criteria();
		if(tid!=-1)criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
		criteria.add(EpisodePeer.FK_VERSION_ID, pid);
		if(null!=video_id)criteria.add(EpisodePeer.VIDEO_ID,video_id,SqlEnum.EQUAL);
		criteria.addAscendingOrderByColumn(EpisodePeer.ORDER_ID);
		List<Episode> le;
		try {
			le = EpisodePeer.doSelect(criteria);
			if(null!=le&&le.size()>0)
				return le;
		} catch (Exception e) {
			logger.error(criteria.toString(),e);
		}
		return null;
	}
	
	public int getMaxEpisodesOrder(int tid,int pid){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
		criteria.add(EpisodePeer.FK_VERSION_ID, pid);
		criteria.addDescendingOrderByColumn(EpisodePeer.ORDER_ID);
		criteria.setLimit(1);
		List<Episode> le;
		try {
			le = EpisodePeer.doSelect(criteria);
			if(null!=le&&le.size()>0) return le.get(0).getOrderId();
		} catch (Exception e) {
			logger.error(criteria.toString(),e);
		}
		return 0;
	}
	
	public List<Episode> getAllEpisode(){
		Criteria criteria = new Criteria();
		criteria.addAscendingOrderByColumn(EpisodePeer.ID);
		List<Episode> le;
		try {
			le = EpisodePeer.doSelect(criteria);
			if(null!=le&&le.size()>0)
				return le;
		} catch (Exception e) {
			logger.error(criteria.toString(),e);
		}
		return null;
	}
	
	public Episode getEpisode(int tid,int pid,int order_id){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
		criteria.add(EpisodePeer.FK_VERSION_ID, pid);
		criteria.add(EpisodePeer.ORDER_ID, order_id);
		List<Episode> le;
		try {
			le = EpisodePeer.doSelect(criteria);
			if(null!=le&&le.size()>0)
				return le.get(0);
		} catch (Exception e) {
			logger.error(criteria.toString(),e);
		}
		return null;
	}
	
	public Episode getEpisodeByID(int id){
		try {
			return EpisodePeer.retrieveByPK(id);
		} catch (Exception e) {
			logger.error(id,e);
		}
		return null;
	}
	
	public String getEpisodeUrl(int eid) {
		Episode e = getEpisodeByID(eid);
		if(null==e||StringUtils.isBlank(e.getEncodeVideoId())) return null;
		return "http://v.youku.com/v_show/id_"+ e.getEncodeVideoId().trim() + ".html";
	}
	
	public void deleteEpisodeByEid(int eid){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.ID, eid);
		try {
			EpisodePeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(eid,e);
		}
	}
	
	public void deleteEpisodeByVersionId(int pid ){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_VERSION_ID, pid);
		try {
			EpisodePeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(pid,e);
		}
	}
	
	public void deleteEpisodeByTeleplayId(int tid ){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
		try {
			EpisodePeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(tid,e);
		}
	}
	
	public void deleteEpisode(Episode episode){
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.ID, episode.getId());
		try {
			EpisodePeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(episode,e);
		}
	}
	
	public int getEpisodeCountByID(int versionid) {
		String sql = "select count(*) as count_num from episode where video_id!='' and fk_version_id="+versionid;
		try {
			List<Record> res = EpisodePeer.executeQuery(sql,"searchteleplay");
			if(null!=res&&res.size()>0)
				return res.get(0).getValue("count_num").asInt();
		} catch (Exception e) {
			logger.error(versionid,e);
		}
		return 0;
	}
	
	public List<Episode> getLastLockedEpisodes() {
		String sql = "select * from episode where isLock=1 and video_id!='' and update_time>=(interval -1 day+'"
				+ DataFormat.formatDate(new Date(),
						DataFormat.FMT_DATE_YYYYMMDD) + "') order by id";
		return getLastEditEpisodes(sql);
	}
	
	public List<Episode> getLastUploadEpisodes() {
		String sql = "select * from episode where video_id!='' and update_time<(interval -1 day+'"
				+ DataFormat.formatDate(new Date(),
						DataFormat.FMT_DATE_YYYYMMDD) + "') order by id";
		return getLastEditEpisodes(sql);
	}
	
	public List<Episode> getLastEditEpisodes() {
		String sql = "select a.* from episode a,teleplay b where a.fk_teleplay_id=b.id and b.is_valid=1 and a.video_id!='' and a.update_time>=(interval -1 day+'"
				+ DataFormat.formatDate(new Date(),
						DataFormat.FMT_DATE_YYYYMMDD) + "') order by a.id";
		return getLastEditEpisodes(sql);
	}
	
	public List<Episode> getLastEditEpisodes(String sql) {
		List<Episode> elist = new ArrayList<Episode>();
		Episode ep = null;
		try {
			List<Record> res = EpisodePeer.executeQuery(sql,"searchteleplay");
			if(null== res) return elist;
			for(Record r:res){
				ep = new Episode();
				ep.setId(r.getValue("id").asInt());
				ep.setFkTeleplayId(r.getValue("fk_teleplay_id").asInt());
				ep.setFkVersionId(r.getValue("fk_version_id").asInt());
				ep.setName(r.getValue("name").asString());
				ep.setOrderId(r.getValue("order_id").asInt());
				ep.setVideoId(r.getValue("video_id").asString());
				ep.setEncodeVideoId(r.getValue("encode_video_id").asString());
				ep.setSourceName(r.getValue("source_name").asString());
				ep.setSeconds(r.getValue("seconds").asDouble());
				elist.add(ep);
			}
		} catch (Exception e) {
			logger.error(sql,e);
		}
		return elist;
	}
	
	public List<Episode> getAllNoValidEpisodes() {
		String sql = "select a.* from episode a,teleplay b where a.video_id!='' and a.fk_teleplay_id=b.id and b.is_valid=1 order by a.id";
		
		return getLastEditEpisodes(sql);
	}
}
