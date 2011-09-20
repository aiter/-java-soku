/**
 * 
 */
package com.youku.soku.library.load.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONObject;

import com.youku.search.sort.servlet.search_page.util.MiscUtils;
import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.EpisodeLogPeer;
import com.youku.soku.library.load.form.ProgrammeSiteBo;

/**
 * 操作视频的日志记录
 * @author liuyunjian
 * 2011-3-2
 */
public class EpisodeLogDao {

	private static final Logger log = Logger.getLogger(EpisodeLogDao.class.getName());
	private EpisodeLogDao(){}
	private static EpisodeLogDao instance = null;
	public static synchronized EpisodeLogDao getInstance(){
		if(null==instance){
			instance = new EpisodeLogDao();
		}
		return instance;
	}
	
	
	public boolean insert(JSONObject resultJson, Integer orderId,
			Integer stageOrder, ProgrammeSiteBo psBo) {
		if(resultJson==null || psBo==null ||psBo.id<=0){
			log.error("insert DB data[EpisodeLog] error:"+resultJson);
			return false;
		}
		EpisodeLog episodeLog = null;
		
		try {
			episodeLog = isExist(psBo.id,orderId);
		} catch (TorqueException e) {
				log.error("select DB error:"+episodeLog+e.getMessage());
			return false;
		}
		
		try {
			return doInsert(episodeLog,resultJson, orderId,
					stageOrder, psBo);
		} catch (TorqueException e) {
				log.error("insert DB error:"+episodeLog+e.getMessage());
			return false;
		}
	}
	
	/**
	 * 插入or更新
	 * @param episodeLog
	 * @param programmeBo
	 * @return
	 */
	private boolean doInsert(EpisodeLog episodeLog, JSONObject resultJson, Integer orderId,
			Integer orderStage, ProgrammeSiteBo psBo) throws TorqueException {
		if(episodeLog==null||episodeLog.getId()==0){
			episodeLog  = new EpisodeLog();
			episodeLog.setCreateTime(new Date());
			episodeLog.setType(1);//添加
			episodeLog.setNew(true);
		}else {//数据库已经存在这条数据
			episodeLog.setNew(false);
		}
		episodeLog.setCate(psBo.programmeBo.cate);
		episodeLog.setFkProgrammeSiteId(psBo.id);
		episodeLog.setTitle(resultJson.optString("title"));
		episodeLog.setOrderId(orderId);
		episodeLog.setOrderStage(orderStage);
		episodeLog.setLogo(resultJson.optString("logo"));
		double seconds = resultJson.optDouble("seconds");
		if((int)episodeLog.getSeconds()!=(int)seconds){
			episodeLog.setSeconds(seconds);
		}
		episodeLog.setHd(MiscUtils.hd(resultJson.optString("ftype"))?1:0);
		episodeLog.setUrl("http://v.youku.com/v_show/id_"+resultJson.optString("encodeVid")+".html");
		episodeLog.setSource(psBo.source); //TODO 是否处理
		
		if(episodeLog.isModified()){
			episodeLog.setUpdateTime(new Date());
		}
		try {
			episodeLog.save();
		} catch (Exception e) {
			throw new TorqueException(e.getMessage(), e);
		}
		
		return episodeLog.getId()>0;
	}

	/*
	 * 检测DB中是否存在指定siteId,orderId的记录
	 */
	private EpisodeLog isExist(int siteId,int orderId) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(EpisodeLogPeer.FK_PROGRAMME_SITE_ID,siteId);
		criteria.add(EpisodeLogPeer.ORDER_ID,orderId);
		List<EpisodeLog> list = EpisodeLogPeer.doSelect(criteria);
		EpisodeLog pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}
}
