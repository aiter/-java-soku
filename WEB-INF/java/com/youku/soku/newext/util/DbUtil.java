package com.youku.soku.newext.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesPeer;
import com.youku.soku.library.load.SeriesSubjectPeer;

/**
 * 操作数据库类，从数据库中查找结果
 * @author User
 *
 */
public class DbUtil {
	private static Log logger=LogFactory.getLog(DbUtil.class);
	
	/**
	 * 根据series返回其对应的List<Programme>
	 * @param series
	 * @return
	 */
	public static List<Programme> getProgrammeListBySeries(Series series){
		List<Programme> returnList=new ArrayList<Programme>();
		if(series==null) return returnList;
		
		Criteria criteria=new Criteria();
		criteria.add(SeriesSubjectPeer.FK_SERIES_ID,series.getId());
		criteria.addJoin(SeriesSubjectPeer.PROGRAMME_ID,ProgrammePeer.ID,Criteria.INNER_JOIN);
		criteria.addAscendingOrderByColumn(SeriesSubjectPeer.ORDER_ID);
		criteria.addDescendingOrderByColumn(ProgrammePeer.UPDATE_TIME);
		List<Programme> result=null;
		try {
			result = ProgrammePeer.doSelect(criteria);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			logger.error("查询programme list 失败： series's id:"+series.getId());
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 根据programme返回其对应的List<ProgrammeSite>
	 * @param programme
	 * @return
	 */
	public static List<ProgrammeSite> getProgrammeSiteListByProgramme(Programme programme){
		List<ProgrammeSite> returnList=new ArrayList<ProgrammeSite>();
		if(programme==null) return returnList;
		
		Criteria criteria=new Criteria();
		criteria.add(ProgrammeSitePeer.FK_PROGRAMME_ID,programme.getId());
//		order by order_id asc, completed desc,  blocked 0, mid_empty asc,source asc
//      update_time desc   
		//TODO 2011.5.4 暂停综合
		criteria.addNotIn(ProgrammeSitePeer.SOURCE_SITE, new int[]{100});
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.ORDER_ID);
		criteria.addDescendingOrderByColumn(ProgrammeSitePeer.COMPLETED);
		criteria.add(ProgrammeSitePeer.BLOCKED,0);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.MID_EMPTY);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.SOURCE);
		criteria.addDescendingOrderByColumn(ProgrammeSitePeer.UPDATE_TIME);
		
		try {
			returnList= ProgrammeSitePeer.doSelect(criteria);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnList;
	}
	
	public static void main(String[] args){
		try {
			Torque.init("E:/workspace/Ext/WebRoot/WEB-INF/local-conf/Torque.properties");
			
			String dateTime="2011-02-24 14:56:05";
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date tmpDate=sdf.parse(dateTime);
			Criteria criteria=new Criteria();
			criteria.add(ProgrammePeer.UPDATE_TIME,tmpDate,Criteria.LESS_EQUAL);
//			criteria.addJoin(SeriesSubjectPeer.PROGRAMME_ID,ProgrammePeer.ID,Criteria.INNER_JOIN);
			
			List result= ProgrammePeer.doSelect(criteria);
			
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("over!");
	}

	/**根据programmeSite 来获取相应的programmeEpisode
	 * @param programmeSite
	 * @return
	 */
	public static List<ProgrammeEpisode> getProgrammeEpisode (
			ProgrammeSite programmeSite,boolean reverse) {
		if(programmeSite==null) return null;
		Criteria criteria=new Criteria();
		criteria.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, programmeSite.getId());
		if (reverse)
			criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
		else{
			criteria.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
		}
		List<ProgrammeEpisode> resultList=null;
		try {
			resultList = ProgrammeEpisodePeer.doSelect(criteria);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			logger.warn("cann't get episode by programmeSite:"+programmeSite.getId());
			e.printStackTrace();
		}
		
		return resultList;
	}
	public static List<ProgrammeEpisode> getProgrammeEpisode (
			ProgrammeSite programmeSite,boolean reverse,int cate) {
		if(programmeSite==null) return null;
		Criteria criteria=new Criteria();
		criteria.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, programmeSite.getId());
		criteria.add(ProgrammeEpisodePeer.URL,(Object)"url is not NULL",Criteria.CUSTOM);
		criteria.add(ProgrammeEpisodePeer.URL,(Object)"length(url)>0",Criteria.CUSTOM);
		if(cate!=ChannelType.MOVIE.getValue()){
			criteria.add(ProgrammeEpisodePeer.ORDER_ID, 0, Criteria.GREATER_THAN);
		}
//		criteria.addGroupByColumn(ProgrammeEpisodePeer.ORDER_ID);
		if (reverse)
			if(cate==ChannelType.MOVIE.getValue()){
				criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
			}else {
				criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.ORDER_STAGE);
				criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);//避免stage=0的错误
			}
		else{
			if(cate==ChannelType.MOVIE.getValue()){
				criteria.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
			}else {
				criteria.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_STAGE);
				criteria.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);//避免stage=0的错误
			}
		}
		
		List<ProgrammeEpisode> resultList=null;
		try {
			resultList = ProgrammeEpisodePeer.doSelect(criteria);
			//2011.5.12自动发现和版权都有的情况下，先选择版权数据
			sortReultList(resultList,cate);
//			if(resultList!=null && resultList.size()>0){
//				logger.info(programmeSite.getId()+" episodes's size:"+resultList.size());
//			}
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			logger.warn("cann't get episode by programmeSite:"+programmeSite.getId());
			e.printStackTrace();
		}
		
		return resultList;
	}

	/**
	 * 2011.5.12自动发现和版权都有的情况下，先选择版权数据.
	 * @param resultList
	 */
	private static void sortReultList(List<ProgrammeEpisode> resultList,int cate) {
		if(resultList==null || resultList.size()==0){
			return;
		}
		Map<Integer, ProgrammeEpisode> map = new HashMap<Integer, ProgrammeEpisode>();
		
		ProgrammeEpisode episode = null;
		ProgrammeEpisode old = null;
		for(int i=0;i<resultList.size();i++){
			 episode = null;
			 episode = resultList.get(i);
			 old = null;
			 int orderId = episode.getOrderId();
			 if((cate==ChannelType.TELEPLAY.getValue() || cate == ChannelType.ANIME.getValue()) && episode.getOrderStage()>0){
				 orderId = episode.getOrderStage();
			 }
			 old = map.get(orderId);
			 if(old==null){
				 map.put(orderId, episode);
			 }else {
				 if(episode.getViewOrder()==1){//已有一个orderID，目前这个又是版权数据(view_order=1),就替换
					 map.put(orderId, episode);
				 }
			 }
		}
		
		if(resultList.size()>map.size()){//说明有重复的
			//logger.info(resultList.size()+" ***** episodes's size:"+map.size());
			for (Iterator iterator = resultList.iterator(); iterator.hasNext();) {
				ProgrammeEpisode programmeEpisode = (ProgrammeEpisode) iterator
						.next();
				if(!map.containsValue(programmeEpisode)){
					iterator.remove();
				}
			}
			logger.debug(resultList.size()+"********"+resultList.get(0).getFkProgrammeSiteId());
		}
	}

	public static Series getSeriesByProgramme(Programme programme) {
		Series series=new Series();
		
		if(programme==null) return null;
		Criteria criteria=new Criteria();
		criteria.add(SeriesSubjectPeer.PROGRAMME_ID,programme.getId());
		criteria.addJoin(SeriesSubjectPeer.FK_SERIES_ID, SeriesPeer.ID,Criteria.INNER_JOIN);
		try {
			List<Series> seriesList= SeriesPeer.doSelect(criteria);
			if(seriesList!=null && seriesList.size()>=1) series= seriesList.get(0);
		} catch (TorqueException e) {
			e.printStackTrace();
			series=null;
		}
		return series;
		
		
	}

	
	public static Programme getProgrammeByProgrammeSite(
			ProgrammeSite programmeSite) {
		if(programmeSite==null || programmeSite.getBlocked()==1) return null;
		
		Criteria criteria=new Criteria();
		criteria.add(ProgrammePeer.ID,programmeSite.getFkProgrammeId());
		try {
			List<Programme> programmeList=ProgrammePeer.doSelect(criteria);
			if(programmeList!=null && programmeList.size()>0) return programmeList.get(0);
			
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * 根据programmeSite 来获取其对应的Programme_episodes列表
	 */
		public static List<ProgrammeEpisode> getEpisodesByProgrammeSite(ProgrammeSite programmeSite) throws Exception {
//			order by order_id asc,stage_order desc, vied_order desc, hd desc, url not null source asc, update_time desc
			if( programmeSite==null) return null;
			Criteria criteria=new Criteria();
			criteria.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID,programmeSite.getId());
			criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
			criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.VIEW_ORDER);
			criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.HD);
			criteria.add(ProgrammeEpisodePeer.URL,new Object(), SqlEnum.ISNOTNULL);
			criteria.addAscendingOrderByColumn(ProgrammeEpisodePeer.SOURCE);
			criteria.addDescendingOrderByColumn(ProgrammeEpisodePeer.UPDATE_TIME);
			List<ProgrammeEpisode>  programmeEpisodeList=ProgrammeEpisodePeer.doSelect(criteria);
			return programmeEpisodeList;
			
		}
	

}
