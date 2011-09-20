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
import com.youku.search.console.pojo.EpisodeLog;
import com.youku.search.console.pojo.EpisodeLogPeer;
import com.youku.search.console.pojo.TeleplayPeer;
import com.youku.search.console.vo.EpisodeLogVO;
import com.youku.search.util.DataFormat;

public class EpisodeLogMgt {
	static Log logger = LogFactory.getLog(EpisodeLogMgt.class);
	
	private static EpisodeLogMgt instance =null;
	public static synchronized EpisodeLogMgt getInstance(){
		if(null!=instance)
			return instance;
		instance = new EpisodeLogMgt();
		return instance;
	}
	
	public void save(String keyword,int episode_id){
		if(keyword==null||keyword.trim().length()<1)return;
		EpisodeLog episodelog=new EpisodeLog();
		episodelog.setKeyword(keyword);
		episodelog.setFkEpisodeId(episode_id);
		episodelog.setCreatetime(DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD));
		episodelog.setStatus(0);
		try {
			episodelog.save();
		} catch (Exception e) {
			logger.error("keyword:"+keyword+",eid:"+episode_id,e);
		}
	}
	
	public void executeSql(String sql){
		try {
			EpisodeLogPeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public void delete(int episode_id){
		executeSql("delete from  episode_log where status=0 and fk_episode_id="+episode_id);
	}
	
	public void update(int id,int status){
		executeSql("update episode_log set status="+status+" where id="+id);
	}
	
	public List<EpisodeLogVO> search(String keyword,int status,int page,int pagesize){
		List<EpisodeLogVO> fbvos=new ArrayList<EpisodeLogVO>();
		EpisodeLogVO fbvo=null;
		Criteria criteria = new Criteria();
		try {
			if(!StringUtils.isBlank(keyword))
				criteria.add(EpisodeLogPeer.KEYWORD,(Object)("%"+keyword+"%"),SqlEnum.LIKE);
			criteria.add(EpisodeLogPeer.STATUS,status);
			criteria.addAscendingOrderByColumn(EpisodeLogPeer.ID);
			criteria.setOffset((page - 1) * pagesize);
			criteria.setLimit(pagesize);
			List<EpisodeLog> els = EpisodeLogPeer.doSelect(criteria);
			if(null!=els){
				for(EpisodeLog el:els){
					fbvo=new EpisodeLogVO();
					fbvo.setId(el.getId());
					fbvo.setFkEpisodeId(el.getFkEpisodeId());
					fbvo.setKeyword(el.getKeyword());
					fbvo.setStatus(status);
					fbvo.setCreatetime(el.getCreatetime());
					fbvo.setUrl(EpisodeMgt.getInstance().getEpisodeUrl(el.getFkEpisodeId()));
					fbvos.add(fbvo);
				}
			}
		} catch (Exception e) {
			logger.error(criteria,e);
		}
		return fbvos;
	}
	
	public int searchEpisodeLogSizeReturnNum(String sql) {
		try {
			List<Record> res = TeleplayPeer.executeQuery(sql,"searchteleplay");
			if(null!=res&&res.size()>0)
				return res.get(0).getValue("num").asInt();
		} catch (Exception e) {
			logger.error(sql,e);
		}
		return 0;
	}
	public int searchSize(String keyword,int status){
		String whereHql = "";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql = " and keyword like '%"
					+ keyword + "%'";
		String sql="select count(*) as num from episode_log where status="+status+whereHql;
		return searchEpisodeLogSizeReturnNum(sql);
	}
	
	public EpisodeLogVO search(int id){
		EpisodeLogVO fbvo=null;
		Criteria criteria = new Criteria();
		try {
			criteria.add(EpisodeLogPeer.ID,id);
			List<EpisodeLog> els = EpisodeLogPeer.doSelect(criteria);
			if(null!=els&&els.size()>0){
				EpisodeLog el = els.get(0);
				fbvo=new EpisodeLogVO();
				fbvo.setId(el.getId());
				fbvo.setFkEpisodeId(el.getFkEpisodeId());
				fbvo.setKeyword(el.getKeyword());
				fbvo.setStatus(el.getStatus());
				fbvo.setCreatetime(el.getCreatetime());
				fbvo.setUrl(EpisodeMgt.getInstance().getEpisodeUrl(el.getFkEpisodeId()));
			}
		} catch (Exception e) {
			logger.error(id,e);
		}
		return fbvo;
	}
	
	public void updateEpisodeLogWhoNoEid(){
			executeSql("update episode_log set status=2 where fk_episode_id not in (select id from episode where video_id!='')");
	}
	
	public void deleteByEpisodeID(int eid){
		executeSql("update episode_log set status=2 where fk_episode_id ="+eid);
	}
}
