/**
 * 
 */
package com.youku.soku.library.load.autoSearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.library.load.autoSearch.handle.Handler;
import com.youku.soku.library.load.dao.ProgrammeDao;
import com.youku.soku.library.load.dao.ProgrammeSiteDao;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;
import com.youku.soku.library.load.util.SourceUtil;

/**
 * @author liuyunjian
 * 2011-3-1
 */
public class SearchLoader {
	private static final Logger log = Logger.getLogger(SearchLoader.class
			.getName());
	public static final int CATE_TELEPLAY=1;
	public static final int CATE_MOVIE=2;
	public static final int CATE_VARIETY=3;
	public static final int CATE_ANIME=5;
	
	private static final int SITE_YOUKU=14;
	
	public static void main(String[] args) {
		if(args.length<3){
			System.out.println("usage: log4j torque teleplay/anime/movie/variety");
			System.exit(0);
			return;
		}
		// logger
		String log4j = args[0];
		System.out.println("初始化log4j: " + log4j);
		DOMConfigurator.configure(log4j);

		// torque
		String torque = args[1];
		System.out.println("初始化torque: " + torque);
		try {
			Torque.init(torque);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		
		// config
//		String config = args[2];
//		System.out.println("初始化config: " + config);
//		try {
//			Config.init(config);
//		} catch (ConfigurationException e) {
//			e.printStackTrace();
//		}
		
		SearchLoader  loader = new SearchLoader();
		if(args.length>=3){
			if("teleplay".equalsIgnoreCase(args[2])){
				System.out.println("auto search-->teleplay");
				loader.searchTeleplay();
			}else if("anime".equalsIgnoreCase(args[2])){
				System.out.println("auto search-->anime");
				loader.searchAnime();
			}else if("movie".equalsIgnoreCase(args[2])){
				System.out.println("auto search-->movie");
				loader.searchMovie();
			}else if("variety".equalsIgnoreCase(args[2])){
				System.out.println("auto search-->variety");
				loader.searchVariety();
			}/*else { //Test
				ProgrammeSiteBo programmeSiteBo = new ProgrammeSiteBo();
				
				ProgrammeBo programmeBo = new ProgrammeBo();
				programmeBo.contentId=106128;
				programmeSiteBo.programmeBo = programmeBo;
				
				ProgrammeDao.getInstance().updateAudit(programmeSiteBo.programmeBo);
			}*/
		}
	}

	/**
	 * 自动发现-电视剧接口 
	 */
	public void searchTeleplay(){
		Cost dbCost = new Cost();
		 List<ProgrammeSiteBo> psBoList = needSearchList(CATE_TELEPLAY);
		 log.info("not completed size:"+(psBoList==null?0:psBoList.size()));
		 dbCost.updateEnd();
		 if(psBoList==null ||psBoList.isEmpty()){
			 log.info("没有需要自动发现的站点-节目");
			 return;
		 }
		 System.out.println(dbCost);
		 
		 handleProgrammeSites(psBoList);
	}

	
	/**
	 * 自动发现-电影 
	 */
	public void searchMovie(){
		Cost dbCost = new Cost();
		 List<ProgrammeSiteBo> psBoList = needSearchList(CATE_MOVIE);
		 log.info("not completed size:"+(psBoList==null?0:psBoList.size()));
		 dbCost.updateEnd();
		 if(psBoList==null ||psBoList.isEmpty()){
			 log.info("没有需要自动发现的站点-节目");
			 return;
		 }
		 System.out.println(dbCost);
		 
		 handleProgrammeSites(psBoList);
	}
	
	/**
	 * 自动发现-综艺
	 */
	public void searchVariety(){
		Cost dbCost = new Cost();
		 List<ProgrammeSiteBo> psBoList = needSearchList(CATE_VARIETY);
		 log.info("not completed size:"+(psBoList==null?0:psBoList.size()));
		 dbCost.updateEnd();
		 if(psBoList==null ||psBoList.isEmpty()){
			 log.info("没有需要自动发现的站点-节目");
			 return;
		 }
		 System.out.println(dbCost);
		 
		 handleProgrammeSites(psBoList);
	}
	
	/**
	 * 通过一个站点-节目的ID和序号，自动发现相关的节目，并插入数据库
	 * @param pSiteId
	 * @param orderId
	 */
	public void searchEpsoideOrder(int pSiteId,int orderId){
//		Cost dbCost = new Cost();
////		 List<ProgrammeSiteBo> psBoList = needSearchList(CATE_VARIETY);
////		 log.info("not completed size:"+(psBoList==null?0:psBoList.size()));
//		 dbCost.updateEnd();
//		 if(psBoList==null ||psBoList.isEmpty()){
//			 log.info("没有需要自动发现的站点-节目");
//			 return;
//		 }
//		 System.out.println(dbCost);
		 
//		 handleProgrammeSites(psBoList);
		//TODO
		ProgrammeSiteBo programmeSiteBo = ProgrammeSiteDao.getInstance().getProgrammeSiteBo(pSiteId);
		ProgrammeBo programmeBo = ProgrammeDao.getInstance().getProgramme(programmeSiteBo.fkProgrammeId);
		programmeSiteBo.programmeBo = programmeBo;
		
		 int  successNum = handleProgramme(programmeSiteBo,orderId);
	}
	
	/**
	 * @param psBoList
	 */
	private void handleProgrammeSites(List<ProgrammeSiteBo> psBoList) {
		//对每个节目，进行搜索、筛选结果、插入数据库
		 for (ProgrammeSiteBo programmeSiteBo : psBoList) {
			 if(programmeSiteBo.source.indexOf("1")==0){
				 continue;
			 }
			 int  successNum = handleProgramme(programmeSiteBo);
			 if(log.isDebugEnabled()){
				 log.info("site-id:"+programmeSiteBo.id+" insert episode_log's size:"+successNum);
			 }
			 if(successNum>0){
				 ProgrammeDao.getInstance().updateAudit(programmeSiteBo.programmeBo);
			 }
		 }
	}

	/**
	 * 自动发现-综艺
	 */
	public void searchAnime(){
		Cost dbCost = new Cost();
		 List<ProgrammeSiteBo> psBoList = needSearchList(CATE_ANIME);
		 log.info("not completed size:"+(psBoList==null?0:psBoList.size()));
		 dbCost.updateEnd();
		 if(psBoList==null ||psBoList.isEmpty()){
			 log.info("没有需要自动发现的站点-节目");
			 return;
		 }
		 System.out.println(dbCost);
		 
		 handleProgrammeSites(psBoList);
	}
	
	
	/**
	 * 对每个节目，进行搜索、筛选结果、插入数据库<br/>
	 * 1、确定需要搜索的节目列表<br/>
	 * 2、通过中间层，获取节目更详细的信息，准备搜索词<br/>
	 * 3、搜索，并筛选最好的结果<br/>
	 * 4、如果有结果，结果插入数据库
	 * @param programmeSiteBo
	 * @return
	 */
	private int handleProgramme(ProgrammeSiteBo programmeSiteBo) {
		Handler handler = Handler.getHandler(programmeSiteBo);
		handler.buildSearchList();
		handler.buildSearchWords();
		handler.buildSearchResult();
		handler.buildDbData();
		return handler.buildNum();
	}
	
	private int handleProgramme(ProgrammeSiteBo programmeSiteBo,int orderId) {
		Handler handler = Handler.getHandler(programmeSiteBo);
		handler.buildSearchList(orderId);
		handler.buildSearchWords();
		handler.buildSearchResult();
		handler.buildDbData();
		return handler.buildNum();
	}

	/**
	 * 需要自动发现的站点-节目列表。
	 * @return
	 */
	private List<ProgrammeSiteBo> needSearchList(int cate) {
		List<ProgrammeBo> programmeList = null;
		try {
			//从DB中获取电视节目
			programmeList = ProgrammeDao.getInstance().getList(cate);
		} catch (TorqueException e) {
			log.error("get programme list ERROR");
		}
		if(programmeList==null || programmeList.isEmpty()){
			log.error("programme list isEmpty");
			return null;
		}
		
		//从DB中获取，每个站点-节目信息
		List<ProgrammeSiteBo> psList = new ArrayList<ProgrammeSiteBo>(programmeList.size());
		for (ProgrammeBo pBo : programmeList) {
			psList.add(ProgrammeSiteDao.getInstance().getProgrammeSiteBo(SITE_YOUKU,pBo));
		}
		
//		if(!psList.isEmpty()) {
//			System.out.println(psList.size());
//			System.out.println(psList.get(0));
//		}
		
		//判断这个站点-节目信息是否收录节目-视频完整
		for (Iterator iterator = psList.iterator(); iterator.hasNext();) {
			ProgrammeSiteBo psTmpBo = (ProgrammeSiteBo) iterator.next();
			boolean completed = checkCompleted(psTmpBo);
			if(completed){
				iterator.remove();
			}
		}
//		if(!psList.isEmpty()) {
//			System.out.println(psList.size());
//			System.out.println(psList.get(0));
//		}
		
		return psList;
	}

	/**
	 * @param psTmpBo
	 * @return
	 */
	private boolean checkCompleted(ProgrammeSiteBo psTmpBo) {
		if(psTmpBo!=null && psTmpBo.programmeBo!=null){
			return psTmpBo.completed==1 && psTmpBo.episodeCollected==psTmpBo.programmeBo.episodeTotal;
		}
		return false;
	}
}
