/**
 * 
 */
package com.youku.soku.library.load.autoSearch.handle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.soku.library.Utils;
import com.youku.soku.library.load.autoSearch.SearchLoader;
import com.youku.soku.library.load.dao.EpisodeLogDao;
import com.youku.soku.library.load.dao.ProgrammeEpisodeDao;
import com.youku.soku.library.load.dao.SearchNumDao;
import com.youku.soku.library.load.form.ProgrammeEpisodeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;
import com.youku.soku.library.load.util.SearchUtil;
import com.youku.soku.library.load.util.SyncUtil;

/**
 * @author liuyunjian
 * 2011-3-1
 */
public class Handler {
	private static final Logger log = Logger.getLogger(Handler.class
			.getName());
	/*目前未保存的其他信息，主要来自中间层 */
	protected String [] alias;//别名
	protected String subName;//子名称
	private String seriesName;//系列名称
	private String [] keywords;//该节目的一些关键词
	protected List<String> searchKeys = new ArrayList<String>();
	
	protected List<ProgrammeEpisodeBo> peBoList;//该节目下的视频列表
	protected List<Integer> needSearchList;//该节目下的需要搜索的视频序号列表
	private int autoSearchNum;//自动发现，改节目入库的条数 
	
	//一集的所有搜索结果
	protected Map<Integer, List<JSONObject>> orderSearchMap = new HashMap<Integer, List<JSONObject>>();
	//一集最好的结果，没有就为空
	protected Map<Integer, JSONObject> bestMap = new HashMap<Integer, JSONObject>();
	
//	private int minSeconds; //最小时间
	private double avgSeconds; //已有视频的平均时间
	
	protected ProgrammeSiteBo psBo;
	

	public Handler(ProgrammeSiteBo psBo) {
		super();
		this.psBo = psBo;
	}
	
	public static Handler getHandler(ProgrammeSiteBo psBo){
		switch (psBo.programmeBo.cate) {
		case SearchLoader.CATE_MOVIE:
			return new MovieHandler(psBo);
		case SearchLoader.CATE_VARIETY:
			return new VarietyHandler(psBo);
		case SearchLoader.CATE_ANIME:
			return new AnimeHandler(psBo);
		case SearchLoader.CATE_TELEPLAY:
			return new Handler(psBo);
		default:
			return new Handler(psBo);
		}
	}
	
	/**
	 * 数据库，取出已有的视频列表。
	 * 取得总视频数（有的没有总数，根据不同的分类，设置一个总数）
	 * 如果已有视频列表，计算出这些视频的评价时间
	 * 算出需要自动搜索的列表
	 */
	public void buildSearchList(){
		peBoList = getEpisodeList(psBo.id);
		int total = getEpisodeTotal();
		avgSeconds = getAvgSeconds(peBoList);
		needSearchList = getNEpidodeList(total);
		
//		_printLog(total,peBoList,nPeBoList);
//		System.out.println(Arrays.toString(nPeBoList.toArray(new Integer[0])));
	}
	//指定单个链接
	public void buildSearchList(int orderId){
		if(needSearchList == null){
			synchronized (needSearchList) {
				if (needSearchList==null) {
					needSearchList = new ArrayList<Integer>(); 
				}
			}
		}
		needSearchList.add(orderId);
	}
	

	/**
	 * 通过已有的视频列表，算出平均时间
	 */
	private double getAvgSeconds(List<ProgrammeEpisodeBo> peBoList2) {
		if(peBoList2!=null && peBoList2.size()>0){
			double total = 0;
			int cnt = 0;
			for (ProgrammeEpisodeBo peBo : peBoList2) {
				if(peBo.seconds>0){
					total+=peBo.seconds;
					cnt++;
				}
			}
			return total/cnt;
		}
		return 0;
	}

	/**
	 * @param total
	 * @param peBoList2
	 * @param nPeBoList2
	 */
	private void _printLog(int total, List<ProgrammeEpisodeBo> peBoList2,
			List<Integer> nPeBoList2) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("total:").append(total)
		.append(" have[");
		if(peBoList2!=null){
			for (ProgrammeEpisodeBo peBo : peBoList2) {
				sBuilder.append(peBo.orderId).append("-").append(peBo.orderStage).append(",");
			}
			sBuilder.subSequence(0, sBuilder.length()-1);
		}
		
		sBuilder.append("] search");
		if(nPeBoList2!=null){
			sBuilder.append(Arrays.toString(needSearchList.toArray(new Integer[0])));
		}else {
			sBuilder.append("[]");
		}
		
		System.out.println(sBuilder);
	}

	/**
	 *	电影-0.1.2...N
	 *  电视剧-0.1.2...N
	 *  综艺-0.1.2...N
	 *  动漫-0.1.2...N
	 */
	protected int getEpisodeTotal() {
		return psBo.programmeBo.episodeTotal;
	}
	
	protected int getMinSeconds() {
		int minSeconds = 0;
		switch (psBo.programmeBo.cate) {
		case 1://电视剧
			if(avgSeconds>0){
				minSeconds = (int)(avgSeconds * 0.8);
			}else {
				minSeconds = 40*60;
			}
			break;
		case 2://电影
			minSeconds = 60*60;
			break;
		case 3://综艺
			if(avgSeconds>0){
				minSeconds = (int)(avgSeconds * 0.8);
			}else {
				minSeconds = 20*60;
			}
			break;
		case 4://音乐
			minSeconds = 3*60;
			break;
		case 5://动漫
			if(avgSeconds>0){
				minSeconds = (int)(avgSeconds * 0.8);
			}else {
				minSeconds = 20*60;
			}
			break;
		default:
			break;
		}
		
		return minSeconds;
	}
	
	protected boolean matchName(String title, int order,String version) {
		String s = Utils.parse2Str(searchKeys);
		// String v = Utils.parse2Str(p.versions);
		StringBuilder keyword = new StringBuilder();

		if (!StringUtils.isBlank(s)) {
			keyword.append("(").append(s).append(")");
		} else {
			return false;
		}

		keyword.append("[ ]*");

		if (!StringUtils.isBlank(version)) {
			keyword.append("(").append(version).append("|").append(Utils.analyzer(version)).append(")");
		}

		keyword.append("[ ]*[第]*[ ]*[0]*" + order + "[ ]*[集话回]*");

		String t = Utils.formatTeleplayName(title);
		
		if(StringUtils.isBlank(t)) return false;
		t = Utils.stopWordsFilter(t);
		if(StringUtils.isBlank(t)) return false;
		
		if (t.matches(keyword.toString()))
			return true;
		if (Utils.analyzer(t).matches(keyword.toString()))
			return true;
		return false;
	}

	/**
	 * 通过已有的列表和提供的总列表，排出需要搜索的列表。<br/>
	 * 搜索次数超出范围的，也不自动搜索
	 */
	private List<Integer> getNEpidodeList(int total) {
		List<Integer> nPeList = new ArrayList<Integer>();
		int searchNum = 0;
		for (int i = 1; i <= total; i++) {
			searchNum = SearchNumDao.getInstance().getSearchNum(psBo.id,i);
			if(searchNum<2){//TODO 需要根据频率，调整这个值
				nPeList.add(i);
			}else if (searchNum<10) {
				SearchNumDao.getInstance().insert(i, psBo);
			}else {//如果搜索2次了，还是没有结果，就暂停
				SearchNumDao.getInstance().update(i, psBo,0);
			}
		}
		
		removeExistId(nPeList);
		
		return nPeList;
	}

	/**
	 * 电视剧/动漫，使用orderstage 
	 */
	protected void removeExistId(List<Integer> nPeList) {
		if(peBoList!=null){
			for (ProgrammeEpisodeBo pe : peBoList) {
				nPeList.remove(new Integer(pe.orderStage));
			}
		}
	}

	/**
	 * 节目已有的视频列表
	 */
	private List<ProgrammeEpisodeBo> getEpisodeList(int psId) {
		List<ProgrammeEpisodeBo> peList = null;
		try {
			peList = ProgrammeEpisodeDao.getInstance().getList(psId);
		} catch (TorqueException e) {
		}
		return peList;
	}

	/**
	 * 
	 */
	public void buildSearchWords() {
		//初始化
		searchKeys.add(psBo.programmeBo.name);
//		System.err.println("search-key："+psBo.programmeBo.name);
		
		//从中间层取节目的别名、子名称等信息
		JSONObject showResultObject = SyncUtil.buildProgrammeKeywordsByID(psBo.programmeBo.contentId);
		if (showResultObject == null || showResultObject.isNull("total")) {
			return;
		}

		int totalNum = showResultObject.optInt("total");
		if(totalNum<=0) {
			return;
		}
		if (totalNum > 0) {
			JSONArray showArray = showResultObject.optJSONArray("results");
			if (JSONUtil.isEmpty(showArray)) {
				return;
			}
			
			//取返回结果的第一个节目进行转换
			JSONObject tmp = showArray.optJSONObject(0);
			if(tmp!=null){
				JSONArray jsonArray = tmp.optJSONArray("showalias");
				alias = json2Array(jsonArray);
				subName = tmp.optString("showsubtitle");
				seriesName = tmp.optString("series");
				keywords = json2Array(tmp.optJSONArray("showkeyword"));
				
				//将别名也加入搜索词
				if(alias!=null && alias.length>0){
					for (String ali : alias) {
						searchKeys.add(ali);
					}
				}
//				_printKeywordLog();
			}
		}
	}

	/**
	 * 
	 */
	private void _printKeywordLog() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("name:"+psBo.programmeBo.name)
		.append(" alias:").append(Arrays.toString(alias))
		.append(" subname:").append(subName)
		.append(" series:").append(seriesName)
		.append(" keywords:").append(Arrays.toString(keywords));
		
		System.out.println(sBuilder);
	}

	/**
	 * @param jsonArray
	 * @return
	 */
	private String[] json2Array(JSONArray jsonArray) {
		if(jsonArray==null || jsonArray.length()==0){
			return null;
		}
		String [] tmp = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			tmp[i]=jsonArray.optString(i);
		}
		return tmp;
	}

	/**
	 * 默认搜索 1、名称+序号；2别名+序号<br/>
	 * 为每一集选出最好的一集
	 */
	public void buildSearchResult() {
		int searchNum = 10;
		if(needSearchList!=null){
			for (String keyword : searchKeys) {
				for (Integer orderId : needSearchList) {
					getSearchResult(keyword+getStageOrder(orderId),searchNum,orderId);
				}
			}
//			System.out.println("aaaaaaaaaaa");
			selectBest();
		}
	}
	
	protected String getStageOrder(int orderId){
		return getStageOrder(orderId,false);
	}
	
	protected String getStageOrder(int orderId,boolean haveYear){
		return orderId+"";
	}

	/**
	 * 对所有集选择最好的结果
	 */
	private void selectBest() {
		List<JSONObject> allResults = null;
		for (Integer orderId : orderSearchMap.keySet()) {
			allResults = orderSearchMap.get(orderId);
//			System.out.println("test:"+(allResults==null?"":allResults.size()));
			if(allResults!=null && allResults.size()>0){
				selectBestOne(orderId,allResults);
			}
		}
	}

	/**
	 * 对每集选择最好的结果
	 * @param orderId
	 * @param allResults
	 */
	private void selectBestOne(Integer orderId, List<JSONObject> allResults) {
		List<JSONObject> items = comItems(allResults);
		//TODO 选择最好的结果
		/**名称
		时长相似，根据原有的时长过滤（电视剧、动漫、综艺）
		分类相同
		发布时间（考虑，后面的集数，时间要晚于前面集数的时间）
		高清
		*/
		int size0 = items.size();
		//基本条件，必须满足{最短多少时间}
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			JSONObject jsonObject = (JSONObject) iterator.next();
			if(jsonObject.optDouble("seconds")<getMinSeconds()){
				iterator.remove();
			}
		}
		int size1 = items.size();
//		System.out.println(psBo.programmeBo.name+" "+orderId +" filter min seconds:"+size0+"-->"+size1);
		//名称过滤
//		for (JSONObject jsonObject : items) {
//			System.out.println(psBo.programmeBo.name+" "+orderId +jsonObject);
//			System.out.println("isMatch:"+matchName(jsonObject.optString("title"), orderId, null));	
//		}
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			JSONObject jsonObject = (JSONObject) iterator.next();
//			System.out.println(psBo.programmeBo.name+" "+orderId +jsonObject);
			if(!matchName(jsonObject.optString("title"), orderId, null)){
				iterator.remove();
			}
		}
		if(log.isDebugEnabled()){
			log.info(psBo.programmeBo.contentId+":"+Arrays.toString(searchKeys.toArray(new String[0]))+getStageOrder(orderId)+" 结果（搜索结果-->过滤时间-->过滤名称）："+size0+"-->"+size1+"-->"+items.size());
		}
		
		//基本时间和名称满足后，就选择第一个为暂时的最好结果，
		JSONObject bestOne = null;
		if(items.size()>0){
			bestOne = items.get(0);
		}else {
			return;
		}
		
		bestMap.put(orderId, bestOne);
		
		//如果结果还是多余一个，那么开始过去其他条件
		if(items.size()>1){
			//TODO  暂时不处理
		}
	}

	/**
	 * @param allResults
	 * @return
	 */
	private List<JSONObject> comItems(List<JSONObject> allResults) {
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject items = null;
		for (JSONObject jsonObject : allResults) {
			items = jsonObject.optJSONObject("items");
			for (int i = 0; i < items.length(); i++) {
				list.add(items.optJSONObject(i+""));
			}
		}
		return list;
	}

	/**
	 * @param string
	 * @param searchNum
	 */
	private void getSearchResult(String keyword, int searchNum,int orderId) {
		String url = null;
		JSONObject json = null;
		
		url = SearchUtil.buildSearchUrl(keyword,searchNum);
		json = Utils.requestGet(url);
		
		if(json!=null && json.optInt("total")>0){//将有结果的数据暂时保存，供筛选
			List<JSONObject> results = orderSearchMap.get(orderId);
			if(results==null){
				results = new ArrayList<JSONObject>();
				orderSearchMap.put(orderId, results);
			}
			results.add(json);
		}
		
		/*System.out.println(psBo.id+"::total:"+getEpisodeTotal()+":last-orderid:"+((peBoList==null||peBoList.isEmpty())?0:peBoList.get(peBoList.size()-1).orderId)
				+":stage:"+((peBoList==null||peBoList.isEmpty())?0:peBoList.get(peBoList.size()-1).orderStage)+":orderid:"+orderId+"::"+keyword+json);
		*/
//		System.out.println(":orderid:"+orderId+"::"+keyword+json);
	}

	/**
	 * 1、记录插入的日志数据，操作日志表
	 * 2、没有结果的，插入搜索次数表
	 */
	public void buildDbData() {
		JSONObject resultJson = null;
		int orderStage = 0;
		boolean success = false;
		for (Integer orderId : needSearchList) {
			resultJson = bestMap.get(orderId);
			if(resultJson==null){
				//记录搜索次数
				SearchNumDao.getInstance().insert(orderId, psBo);
//				System.out.println("DB--No-results--->:"+psBo.programmeBo.name+"【"+orderId+"】");
			}else {
				success = false;
				//插入数据；插入日志
				orderStage = 0;
				try {
					orderStage = Integer.parseInt(getStageOrder(orderId,true));
				} catch (Exception e) {
				}
				
				success = EpisodeLogDao.getInstance().insert(resultJson,orderId,orderStage,psBo);
				if(success){
					autoSearchNum ++;
				}
				//记录搜索次数
				SearchNumDao.getInstance().insert(orderId, psBo);
				
//				System.out.println("DB----->:"+psBo.programmeBo.name+"【"+orderId+"】");
			}
		}
	}
	
	public int buildNum(){
		return autoSearchNum;
	}
}
