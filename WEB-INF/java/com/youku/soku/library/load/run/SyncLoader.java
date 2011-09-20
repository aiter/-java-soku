/**
 * 
 */
package com.youku.soku.library.load.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.struts2.views.xslt.ArrayAdapter;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.soku.library.load.dao.CategoryDao;
import com.youku.soku.library.load.dao.ProgrammeDao;
import com.youku.soku.library.load.dao.ProgrammeEpisodeDao;
import com.youku.soku.library.load.dao.ProgrammeSiteDao;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.form.ProgrammeEpisodeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;
import com.youku.soku.library.load.util.Converter;
import com.youku.soku.library.load.util.SyncUtil;

/**
 * 同步的入口
 * 
 * @author liuyunjian 2011-2-22
 */
public class SyncLoader {
	private static final Logger log = Logger.getLogger(SyncLoader.class
			.getName());

	public static void main(String[] args) {
		if(args.length<2){
			System.out.println("usage: log4j torque");
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

		//
		SyncLoader loader = new SyncLoader();
		
		Cost cost = new Cost();
		if(args.length>=3){
			if("all".equalsIgnoreCase(args[2])){
				System.out.println("all shows");
				loader.updateAllProgramme();
			}else if("videos".equalsIgnoreCase(args[2])){
				System.out.println("update videos");
				loader.updateVideos();
			}else if("shows".equalsIgnoreCase(args[2])){
				System.out.println("update shows");
				loader.updateProgramme();
				System.out.println("update not complete shows");
				loader.updateNotCompleteProgramme();
			} else if("showid".equalsIgnoreCase(args[2])){
				if(args.length>=4){
					System.out.println("update showid:"+args[3]);
					loader.updateProgramme(args[3]);
				}else {
					System.out.println("please input showid!!!!");
				}
			} 
		}else {
			loader.updateProgramme();
		}
		 
		cost.updateEnd();
		String infoStr = "all times:"+cost.getCost();
		System.out.println(infoStr);
		log.info(infoStr);
	}

	/**
	 * 将中间层的所有数据更新一遍
	 */
	public void updateAllProgramme() {
		int[] minMaxids = SyncUtil.getMinMaxIDs();
		log.info("min-max id:" + minMaxids[0] + "-" + minMaxids[1]);
		
		// DB查询分类
		Map<String, Integer> cateMap = getCateMap();
		if(cateMap==null){
			return;
		}
//		int length = 1000;
		int length = 500;

		// System.out.println(showResultObject);
		JSONObject showResultObject = null;
		for (int i = minMaxids[0]; i < minMaxids[1]; i += length) {
			showResultObject = SyncUtil.buildProgrammeID(i, (i + length - 1),length);
			// log.info("id:"+i+"-"+(i+length-1));

			if (showResultObject == null || showResultObject.isNull("total")) {
				log.error("获取中间层数据,出错："+i+"-"+ (i + length - 1));
				continue;
			}

			int totalNum = showResultObject.optInt("total");
			if (totalNum > 0) {
				JSONArray showArray = showResultObject.optJSONArray("results");
				if (JSONUtil.isEmpty(showArray)) {
					log.error("获取中间层数据，结果为空");
				}

				int updateCnt = doUpdateProgramme(cateMap,showArray,false);
			}
		}
	}

	/**
	 * 每次更新最近更改的1000条数据，这个需要频繁调用。
	 * 由于中间层，最多只能获取一种查询的前1000条数据。理论上这个调用时间要<中间层更新的时间，不然就会出现多余1000条的数据，无法更新。
	 * 由于这种不确定性，为了保证数据完全更新，过一段时间，就需要全检查一次所有数据。
	 * 可以根据，这1000条是否都有更新来，确定是否需要全部检查。（这1000条数据是最新更新时间倒排的），如果1000条都需要更新，那么就可以有更多
	 * 数据需要更新，如果<1000条，说明，所有更新的数据都包含在这1000条内，不需要全部更新。
	 */
	public void updateProgramme() {
		// 每次更新最近更改的1000条数据
		JSONObject showResultObject = SyncUtil.buildProgramme(1, 1000);
		// System.out.println(showResultObject);
		if (showResultObject == null || showResultObject.isNull("total")) {
			log.error("获取json出错");
			return;
		}

		int totalNum = showResultObject.optInt("total");
		if(totalNum<=0) {
			log.info("更新最新数据：hava not new data");
			return;
		}
		if (totalNum > 0) {
			JSONArray showArray = showResultObject.optJSONArray("results");
			if (JSONUtil.isEmpty(showArray)) {
				log.error("获取中间层数据，结果为空");
				return;
			}
			log.info("最新更新的-节目：number:"+showArray.length());
			
			// DB查询分类
			Map<String, Integer> cateMap = getCateMap();
			if(cateMap==null){
				return;
			}
			

			int updateCnt = doUpdateProgramme(cateMap,showArray,false);
//			if (log.isDebugEnabled()) {
//				log.info("updated count:" + updateCnt + "/"
//						+ showArray.length());
//			}
			if (updateCnt == totalNum) {
				// TODO 考虑全更新
			}
		}
	}
	
	public void updateNotCompleteProgramme() {
		// 每次更新最近更改的1000条数据
		JSONObject showResultObject = SyncUtil.buildNotCompleteProgramme(1, 500);
		// System.out.println(showResultObject);
		if (showResultObject == null || showResultObject.isNull("total")) {
			log.error("获取json出错");
			return;
		}

		int totalNum = showResultObject.optInt("total");
		if(totalNum<=0) {
			log.info("更新最新数据：hava not new data");
			return;
		}
		if (totalNum > 0) {
			JSONArray showArray = showResultObject.optJSONArray("results");
			if (JSONUtil.isEmpty(showArray)) {
				log.error("获取中间层数据，结果为空");
				return;
			}
			log.info("最新更新的-节目：number:"+showArray.length());
			
			// DB查询分类
			Map<String, Integer> cateMap = getCateMap();
			if(cateMap==null){
				return;
			}
			

			int updateCnt = doUpdateProgramme(cateMap,showArray,false);
//			if (log.isDebugEnabled()) {
//				log.info("updated count:" + updateCnt + "/"
//						+ showArray.length());
//			}
			if (updateCnt == totalNum) {
				// TODO 考虑全更新
			}
		}
	}
	
	public void updateProgramme(String showid) {
		// DB查询分类
		Map<String, Integer> cateMap = getCateMap();
		if(cateMap==null){
			return;
		}
		
		JSONArray showArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("show_id", showid);
			showArray.put(jsonObject);
			int updateCnt = doUpdateProgramme(cateMap,showArray,true);
		} catch (JSONException e) {
		}
		
		
	}
	
	public void updateVideos() {
		// 每次更新最近更改的1000条数据
		JSONObject showResultObject = SyncUtil.buildEpisode(1, 1000);
		// System.out.println(showResultObject);
		if (showResultObject == null || showResultObject.isNull("total")) {
			log.error("获取json出错");
			return;
		}

		int totalNum = showResultObject.optInt("total");
		if(totalNum<=0) {
			log.info("更新最新数据：hava not new data");
			return;
		}
		if (totalNum > 0) {
			JSONArray showArray = showResultObject.optJSONArray("results");
			if (JSONUtil.isEmpty(showArray)) {
				log.error("获取中间层数据，结果为空");
				return;
			}
			
			log.info("最新更新的-视频：number:"+showArray.length());
			
			// DB查询分类
			Map<String, Integer> cateMap = getCateMap();
			if(cateMap==null){
				return;
			}
			

			int updateCnt = doUpdateProgramme(cateMap,showArray,true);
//			if (log.isDebugEnabled()) {
//				log.info("updated count:" + updateCnt + "/"
//						+ showArray.length());
//			}
//			String stageUpdOldTime = showArray.optJSONObject(showArray.length()-1).optString("show_collecttime");
//			String stageUpdNewTime = showArray.optJSONObject(0).optString("show_collecttime");
			//如果最后的一条，时间早于上次更新的最新的一条，说明更新完成，否则继续往前更新。
			if (updateCnt == totalNum) {
				// TODO 考虑全更新
			}
		}
	}
	
	
	
	// DB查询分类
	private Map<String,Integer> getCateMap() {
		
		Map<String, Integer> cateMap = null;
		try {
			cateMap = CategoryDao.getInstance().getCateMap();
			if (cateMap == null || cateMap.size() == 0) {
				log.info("分类表数据，结果为空");
			}
		} catch (TorqueException e) {
			log.error("分类表数据，DB出错");
		}
		return cateMap;
	}

	/**
	 * 0、查询分类 1、转换json对象 2、更新数据库
	 */
	private int doUpdateProgramme(Map<String,Integer> cateMap,JSONArray showArray,boolean needGetShow) {

		// 处理多个结果
		JSONObject showObject = null;
		JSONObject showVideosResult = null;
		JSONArray videosArray = null;
		int succCnt = 0;
		int updateCnt = 0;
		Cost saCost = new Cost();
		for (int i = 0; i < showArray.length(); i++) {
			showObject = showArray.optJSONObject(i);
			// 开始处理节目
			ProgrammeBo programmeBo = null;
			if(needGetShow){
				programmeBo = Converter.convertProgramme(showObject,
						cateMap,needGetShow);
			}else {
				programmeBo = Converter.convertProgramme(showObject,
						cateMap);
			}
			// if(log.isDebugEnabled()){
			// log.info("ProgrammeBo:"+programmeBo);
			// }
			if(programmeBo==null){
				continue;
			}
			
			ProgrammeDao programmeDao = ProgrammeDao.getInstance();
			boolean success = programmeDao.insert(programmeBo);
			if (success) {
				// System.out.println(showObject);
				// 开始处理节目站点
				ProgrammeSiteBo psBo = Converter
						.convertProgrammeSite(showObject);
				if (psBo != null) {
					psBo.fkProgrammeId = programmeBo.id;
					psBo.sourceSite = 14;
					psBo.source = programmeBo.source;
					psBo.blocked = "normal".equalsIgnoreCase(programmeBo.state)?0:1;
					// if(log.isDebugEnabled()){
					// log.info("ProgrammeSiteBo:"+psBo);
					// }
						System.out.println("ProgrammeSiteBo:"+psBo);
					success = ProgrammeSiteDao.getInstance().insert(psBo);
					if (success) {
						succCnt++;
						if (programmeBo.updated || psBo.updated) {
							updateCnt++;
//							System.out.println(showObject);
						}
						
						//节目-视频
						showVideosResult = SyncUtil.buildEpisode(programmeBo.contentId);
						if(showVideosResult==null ||showVideosResult.isNull("total")||JSONUtil.isEmpty(showVideosResult.optJSONArray("results"))){
							if(log.isDebugEnabled()){
								log.info("have not video:"+programmeBo.contentId);
							}
						}else {
							videosArray = showVideosResult.optJSONArray("results");
							doUpdateVideos(videosArray,psBo);
						}
					}
				} else {
					continue;
				}
			}
		}
		saCost.updateEnd();
//		if (log.isDebugEnabled()) {
			log.info("[programme] success/update/total:" + succCnt + "/"+ updateCnt + "/" + showArray.length()+" times:"+saCost.getCost());
//		}

		return updateCnt;
	}

	/**
	 * 节目视频 的数据添加或更新
	 * @param videosArray
	 * @param psBo
	 */
	private void doUpdateVideos(JSONArray videosArray, ProgrammeSiteBo psBo) {
		JSONObject videoObject = null;
		boolean success = false;
		int succCnt = 0;
		int updateCnt = 0;
//		Cost peCost = new Cost();
		
		//先获取一个站点节目的所有视频
		List<ProgrammeEpisodeBo> peList = null;
		try {
			peList = ProgrammeEpisodeDao.getInstance().getList(psBo.id,1);//viewOrder=1 为优酷中间层数据
		} catch (TorqueException e) {
			log.equals("get episode list error:"+psBo.id);
		}
		if(peList==null){
			peList = new ArrayList<ProgrammeEpisodeBo>();
		}
		
		for (int i = 0; i < videosArray.length(); i++) {
			videoObject = videosArray.optJSONObject(i);
			ProgrammeEpisodeBo peBo = Converter.convertProgrammeEpisode(videoObject);
			peBo.fkProgrammeSiteId = psBo.id;
			peBo.viewOrder = 1;
			peBo.source = psBo.source;
			
			
			success = ProgrammeEpisodeDao.getInstance().insert(peBo);
			if(success){
				succCnt++;
				if(peBo.updated){
					updateCnt++;
				}
			}
			
			//如果是已有的，就是更新，把原有的，而且更新过的移除，未移除的，说明中间层已删除，我们也应该删除
			for (Iterator peBos = peList.iterator(); peBos.hasNext();) {
				ProgrammeEpisodeBo programmeEpisodeBo = (ProgrammeEpisodeBo) peBos
						.next();
				if(programmeEpisodeBo.fkProgrammeSiteId == psBo.id && programmeEpisodeBo.viewOrder==1 && programmeEpisodeBo.orderId == peBo.orderId){
					if(success){
						peBos.remove();
					}
				}
			}
		}
		
		int delCnt=0;
		for (ProgrammeEpisodeBo peBo : peList) {
			boolean s = ProgrammeEpisodeDao.getInstance().delete(peBo.id);
			if(s){
				delCnt++;
			}
		}
		//如果还有未更新的，删除这些视频
		if(peList!=null && peList.size()>0){
			log.info("[videos] delete videos success/total size:"+delCnt+"/"+peList.size()+" fk_p_site_id:"+psBo.id);
		}
//		peCost.updateEnd();
//		System.out.println(videosArray.length()+":"+peCost);
		if (log.isDebugEnabled()) {
			if(succCnt!=videosArray.length() || updateCnt>0){
				log.info("[videos] success/update/total:" + succCnt + "/" + updateCnt + "/" + videosArray.length()+" fk_p_site_id:"+psBo.id);
			}
		}
	}

}
