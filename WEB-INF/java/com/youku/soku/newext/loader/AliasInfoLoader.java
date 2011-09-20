package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.library.load.Series;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.TypeConstant.ProgrammeState;
import com.youku.soku.newext.util.ChannelType;
import com.youku.soku.newext.util.DbUtil;
import com.youku.soku.newext.util.ProgrammeSiteType;
import com.youku.soku.newext.util.comparator.DownComparator;
import com.youku.soku.newext.util.comparator.EpisodeOrderComparator;
import com.youku.soku.newext.util.comparator.SiteComparator;
import com.youku.soku.newext.util.middleResource.MiddleResourceUtil;

/**
 * 父类加载器
 */
public class AliasInfoLoader {

	private static Log logger = LogFactory.getLog(AliasInfoLoader.class);

	final int MAX_COUNT = Integer.MAX_VALUE;
	final int limit = 1000;

	public int load(AliasInfo aliasInfo) {
		logger.info("开始加载 （遍历programme 表，加载5个map） 信息 ...");
		Criteria criteria = new Criteria();
		criteria.add(ProgrammePeer.STATE, ProgrammeState.NORMAL);
		criteria.add(ProgrammePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammePeer.ID);
        

//  yanjg
//		int[] ints={113208,113770,113771,113809,113814,113815,114764,65183,109483,126598,122604,64430};
//		criteria.addIn(ProgrammePeer.ID, ints);

		criteria.setLimit(limit);
		int validCount = 0;
		for (int offset = 0; validCount < MAX_COUNT; offset += limit) {
			criteria.setOffset(offset);
			List<Programme> list = null;
			try {
				list = ProgrammePeer.doSelect(criteria);
			} catch (TorqueException e) {
				logger.error("获取programme list失败");
				e.printStackTrace();
				return -1;
			}

			if (list == null || list.size() <= 0)
				break;

			for (Programme programme : list) {
				aliasInfo.id_programme.put(programme.getId(), programme);
//				programme_series
				addProgrammeSeries(programme,aliasInfo);
				
//				middMap
				addMiddMap(programme,aliasInfo);
				
//				programme_programmeSite && programmeSite_programme
				addProProgrammeSite(programme,aliasInfo);

				validCount++;
				

			}

			if (list.size() <= 0) {
				break;
			}
			
			logger.info("counts:"+validCount);

		}
		

		logger
				.info("加载 AliasInfo 5个map结束；遍历programme表结束");

		/*// 加载programmeSite_programme 对应关系
		logger.info("开始加载programmeSite_programme  信息...");
		criteria.clear();
		criteria.add(ProgrammeSitePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.ID);
        
// yanjg
//		criteria.addIn(ProgrammeSitePeer.FK_PROGRAMME_ID, ints);

		criteria.setLimit(limit);
		validCount = 0;
		for (int offset = 0; validCount < MAX_COUNT; offset += limit) {
			criteria.setOffset(offset);
			List<ProgrammeSite> list = null;
			try {
				list = ProgrammeSitePeer.doSelect(criteria);
			} catch (TorqueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("获取 programmeSite list 失败");
				return -1;
			}
			if (list == null || list.size() <= 0)
				return -1;
			for (ProgrammeSite programmeSite : list) {
				Programme programme = DbUtil
						.getProgrammeByProgrammeSite(programmeSite);
				if (programme != null) {
					aliasInfo.programmeSite_programme.put(programmeSite,
							programme);
				} else {
					continue;
				}

				validCount++;
			}

			if (list.size() < limit) {
				break;
			}


		}
		// info.loadRelation();
		logger.info("加载AliasInfo.programmeSite_programme  信息结束");*/

//		// 加载中间层资源map
//		loadMiddResource(aliasInfo);

		return validCount;
	}


	

	public static void addMiddMap(Programme programme, AliasInfo aliasInfo) {
		if(programme==null) return;
		String fields = "showname  showalias   tv_genre movie_genre  variety_genre anime_genre  " +
				" releasedate releaseyear  update_notice  reputation "
			+ " director performer host area  showdesc avg_rating  show_thumburl  show_vthumburl " +
			" completed  episode_collected  firstepisode_videourl language showdesc station tv_station  streamtypes sortmode paid";
		JSONObject middleResourceJson = MiddleResourceUtil
			.getProgrammeById(programme.getContentId(), fields);
		if (middleResourceJson != null) {
			if(logger.isDebugEnabled()){
				logger.info("添加中间层资源："+programme.getName());
			}
			aliasInfo.middMap.put(programme.getContentId(),middleResourceJson.toString());
		}else {
			logger.info("添加中间层资源-失败-show_id："+programme.getContentId()+" programme_id:"+programme.getId());
		}
		
		
	}


	public static void addProProgrammeSite(Programme programme, AliasInfo aliasInfo) {
		if(programme==null) return;
		
		List<ProgrammeSite> list = DbUtil.getProgrammeSiteListByProgramme(programme);
		
		String middStr=aliasInfo.middMap.get(programme.getContentId());
		int programmeSortmode = 0;
		int programmePaid = 0;
		if(middStr!=null && middStr.length()>0) {
			try {
				JSONObject middJson=new JSONObject(middStr);
				programmeSortmode = middJson.optInt("sortmode", programmeSortmode);
				programmePaid = middJson.optInt("paid", programmePaid);
			} catch (JSONException e) {
			}
		}
		programme.setPaid(programmePaid);
		if(programmePaid==1){//所有收费节目都不加载
			return;
		}
			
        if(list!=null && list.size()>0){
        	for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				ProgrammeSite programmeSite = (ProgrammeSite) iterator.next();
				programmeSite.setSortmode(programmeSortmode);
				//添加站点节目-->视频列表
        		//TODO 可以根据条件，使视频是倒排还是正排***************************************
				
        		List<ProgrammeEpisode> programmeEpisodeList = null;
        		//只有电影加载order_id=0的视频列表
        			programmeEpisodeList = DbUtil.getProgrammeEpisode(programmeSite,programmeSortmode==1,programme.getCate());
		
				if(programmeEpisodeList==null || programmeEpisodeList.size()==0 ){
					//如果没有视频列表。移除这个站点
					iterator.remove();
					if(logger.isDebugEnabled()){
					logger.info("programmeEpisode 不存在： programme's id:"
							+ programme.getId() + "  programmeSite's id:"
							+ programmeSite.getId());
					}
					continue;
				}
				
				programmeSite.setEpisodeCollected(programmeEpisodeList.size());//设置站点收录的集数
				if(programme.getEpisodeTotal()>0){/** total为0的都未完成。收录集数大于total的，收录完成   */
					programmeSite.setCompleted(programmeSite.getEpisodeCollected()>=programme.getEpisodeTotal()?1:0);
				}else {
					programmeSite.setCompleted(0);
				}
				
				
				//根据搜索是否完成，对视频列表排序
//				if(programmeSite.getCompleted()>0){
//					Collections.sort(programmeEpisodeList, new EpisodeOrderComparator());
//				}else {
//					Collections.sort(programmeEpisodeList, new DownComparator<ProgrammeEpisode>(new EpisodeOrderComparator()));
//				}
				
				aliasInfo.programmeSite_episode.put(programmeSite, programmeEpisodeList);
			}
        	
        	
        	
        	//去掉无视频链接的节目站点后
        	if(!list.isEmpty()) {
	        	/*for (ProgrammeSite programmeSite : list) {
	        		//添加站点节目-->节目对应关系
	        		aliasInfo.programmeSite_programme.put(programmeSite, programme);
				}*/
        		
        		//检查是否只有综合站点和另一个站点,是的话，直接移除综合站点
            	if(list.size()==2){
            		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
        				ProgrammeSite programmeSite = (ProgrammeSite) iterator.next();
        				if(programmeSite.getSourceSite()==ProgrammeSiteType.综合.getValue()){
        					iterator.remove();
        				}
            		}
            	}
	        	
	        	//将站点排序
	        	Collections.sort(list, new SiteComparator());
	        	
	        	//设置节目的默认播放链接
	        	List<ProgrammeEpisode> programmeEpisodeList = aliasInfo.programmeSite_episode.get(list.get(0));
	        	if(programmeEpisodeList!=null && programmeEpisodeList.size()>0){
	        		programme.setPlayUrl(programmeEpisodeList.get(0).getUrl());
	        	}
	        	
	        	//添加节目-->节目站点对应关系。
	        	aliasInfo.programme_programmeSite.put(programme, list);
        	}
        }
		
	}


	private List<ProgrammeSite> sortList(List<ProgrammeSite> list) {
		// TODO Auto-generated method stub
		if(list==null || list.size()<=0) return null;
		List<ProgrammeSite> resultList=new ArrayList<ProgrammeSite>();
		for(ProgrammeSite tmpSite: list){
			if(tmpSite.getSourceSite()==ProgrammeSiteType.优酷网.getValue()){
				resultList.add(0,tmpSite);
			}else{
				resultList.add(tmpSite);
			}
		}
		
		
		return resultList;
		
		
		
	}


	public static  void addProgrammeSeries(Programme programme, AliasInfo aliasInfo) {
		if(programme==null) return;
		
		Series series = DbUtil.getSeriesByProgramme(programme);
        if(series!=null && series.getId()>0) 
			aliasInfo.programme_series.put(programme, series);
		
		
	}

	private void loadMiddResource(AliasInfo aliasInfo) {
		// TODO Auto-generated method stub
		logger.info("开始加载AliasInfo的中间层map ");

		Criteria criteria = new Criteria();
		criteria.add(ProgrammePeer.STATE, ProgrammeState.NORMAL);
		criteria.add(ProgrammePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammePeer.ID);
       
		criteria.setLimit(limit);
		int validCount = 0;
		for (int offset = 0; validCount < MAX_COUNT; offset += limit) {
			criteria.setOffset(offset);
			List<Programme> list = null;
			try {
				list = ProgrammePeer.doSelect(criteria);
			} catch (TorqueException e) {
				logger.error("获取programme list失败");
				e.printStackTrace();
				return;
			}

			if (list == null || list.size() <= 0)
				break;

			for (Programme programme : list) {

				String fields = "showname  showalias  tv_genre movie_genre  variety_genre anime_genre  releasedate "
						+ " director performer host area  showdesc";
				JSONObject middleResourceJson = MiddleResourceUtil
						.getProgrammeById(programme.getContentId(), fields);
				if (middleResourceJson != null) {
					
					aliasInfo.middMap.put(programme.getContentId(),
							middleResourceJson.toString());
				}
				
				
			}

			if (list.size() < limit) {
				break;
			}

		}

		logger.info("AliasInfo的中间层map 加载结束");

	}

	/**
	 * 填充 programme_programmeSite programme_series
	 * 
	 * @param programme
	 * @param aliasInfo
	 */
	public void addProgramme(Programme programme, AliasInfo aliasInfo) {
		if (programme == null)
			return;

		Criteria criteria = new Criteria();
		// order by order_id asc, completed desc, blocked 0, mid_empty
		// asc,source asc
		// update_time desc
		criteria.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programme.getId());
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.ORDER_ID);
		criteria.addDescendingOrderByColumn(ProgrammeSitePeer.COMPLETED);
		criteria.add(ProgrammeSitePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.MID_EMPTY);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.SOURCE);
		criteria.addDescendingOrderByColumn(ProgrammeSitePeer.UPDATE_TIME);

		List<ProgrammeSite> list = null;
		try {
			list = ProgrammeSitePeer.doSelect(criteria);
		} catch (TorqueException e) {
			logger.error("获取 programme site list 失败：programme's id:"
					+ programme.getId());
			e.printStackTrace();
			return;
		}

		aliasInfo.programme_programmeSite.put(programme, list);

		Series series = DbUtil.getSeriesByProgramme(programme);
        
		if(series!=null) 
			aliasInfo.programme_series.put(programme, series);

	}

}
