/**
 * 
 */
package com.youku.soku.library.load.douban;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.util.StringUtil;
import com.youku.soku.library.load.dao.ProgrammeDao;
import com.youku.soku.library.load.dao.ProgrammeDoubanDao;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.form.ProgrammeDoubanBo;
import com.youku.soku.library.load.util.Converter;
import com.youku.soku.library.load.util.SyncUtil;

/**
 * @author liuyunjian
 * 2011-4-1
 */
public class DoubanSearch {
	private static final Logger log = Logger.getLogger(DoubanSearch.class
			.getName());
	
	private static final int CATE_TELEPLAY=1;
	private static final int CATE_MOVIE=2;
	private static final int CATE_ANIME=5;
	
	public static void main(String[] args) {
		if(args.length<3){
			System.out.println("usage: log4j torque movie|anime|teleplay");
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
		
		DoubanSearch search = new DoubanSearch();
		search.start(args[2]);
	}
	
	public void start(String cate){
		Cost cost = new Cost();
		if("movie".equals(cate)){
			startSearch(CATE_MOVIE);//从豆瓣api中搜索所有电影节目
		}else if ("teleplay".equals(cate)) {
			startSearch(CATE_TELEPLAY);//所有电视剧节目
		}else if ("anime".equals(cate)) {
			startSearch(CATE_ANIME);//所有动漫节目
		}
		cost.updateEnd();
		log.info(cate+" "+cost);
		
		
//		startGet(3060445);
	}
	
//	public void startGet(int doubanId){
//		JSONObject jsonObject  = DoubanWget.getMovie(doubanId+"");
//		if(jsonObject!=null){
//			try {
//				System.out.println(jsonObject.toString(4));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	public void startSearch(int cate){
		//获取节目列表
		List<ProgrammeBo> programmeList = null;
		try {
			programmeList = ProgrammeDao.getInstance().getList(cate);
		} catch (TorqueException e) {
			log.error("get programme list error! cate:"+cate);
			return;
		}
		
//		System.out.println("cate:"+cate+" size:"+(programmeList==null?0:programmeList.size()));
		log.info("cate:"+cate+" size:"+(programmeList==null?0:programmeList.size()));
		
//		if(programmeList.size()>150){
//			programmeList = programmeList.subList(0, 20);
//		}
		
		//查找还没对应的节目
		List<ProgrammeBo> needSearchList = new ArrayList<ProgrammeBo>();
		boolean searched = false;
		for (ProgrammeBo programmeBo : programmeList) {
			searched = ProgrammeDoubanDao.getInstance().checkSearched(programmeBo.contentId);
			if(!searched){
				needSearchList.add(programmeBo);
			}
		}
		
		if(needSearchList.size()<=0){
			log.info("cate:"+cate+" have not any programme needed search douban.");
			return ;
		}else {
			log.info("cate:"+cate+" needed search num:"+needSearchList.size());
//			System.out.println("cate:"+cate+" needed search num:"+needSearchList.size());
		}
		
		for (ProgrammeBo programmeBo : needSearchList) {
			//查询中间层的豆瓣对应关系，如果有对应关系，直接如对应表
			JSONObject jsonObject = SyncUtil.buildProgrammeDoubanInfoByID(programmeBo.contentId);
			if(!JSONUtil.isEmpty(jsonObject)){
				JSONArray showArray = jsonObject.optJSONArray("results");
				if (!JSONUtil.isEmpty(showArray)) {
					JSONObject firstOne = showArray.optJSONObject(0);
					if(!JSONUtil.isEmpty(firstOne)){
						String doubanNum = firstOne.optString("douban_num");
						if(doubanNum!=null && doubanNum.length()>0) {
							int doubanId = StringUtil.parseInt(doubanNum, 0);
							if(doubanId!=0){
								ProgrammeDoubanBo pdBo = new ProgrammeDoubanBo();
								pdBo.pId = programmeBo.contentId;
								pdBo.pName = programmeBo.name;
								pdBo.doubanId = doubanId;
								pdBo.doubanName = "";
								pdBo.cate = programmeBo.cate;
								
								pdBo.status = 3;//表示是同步中间层
								ProgrammeDoubanDao.getInstance().insert(pdBo);
								continue;
							}
						}
					}
				}
			}//同步中间层imdb----结束------
			
			//搜索豆瓣结果
			//加入到待审核对应表中
			JSONArray searchItems = DoubanWget.searchMoviesArray(programmeBo.name);
			if(searchItems!=null && searchItems.length()>0) {
				//自动判断，根据版权库的信息和豆瓣的信息对比，符合一定的规则，指定为同一个节目
				JSONObject theSameOne = findSameOne(programmeBo,searchItems);
				if(theSameOne!=null){//如果找到了相同的一个数据，就直接插入对应表
					ProgrammeDoubanBo pdBo = Converter.convertDoubanProgramme(programmeBo,theSameOne);
					pdBo.status = 2;//表示是机器选中的
					ProgrammeDoubanDao.getInstance().insert(pdBo);
				}else {
					for (int i = 0; i < searchItems.length(); i++) {
						try {
							JSONObject item = searchItems.getJSONObject(i);
							ProgrammeDoubanBo pdBo = Converter.convertDoubanProgramme(programmeBo,item);
							if(pdBo!=null){
								ProgrammeDoubanDao.getInstance().insertMore(pdBo);
							}
						} catch (JSONException e) {
						}
					}
				}
			}
		}
		
	}

	
	
	//**************************** 比较youku节目和豆瓣节目是否相同********** start
	/**
	 * 从搜索结果中，找出一个相同的节目
	 * @param programmeBo
	 * @param searchItems
	 * @return
	 */
	private JSONObject findSameOne(ProgrammeBo programmeBo,
			JSONArray searchItems) {
		if(programmeBo==null || JSONUtil.isEmpty(searchItems)){
			return null;
		}
		
		JSONObject showJsonObject = SyncUtil.buildProgrammeDoubanInfoByID(programmeBo.contentId);
		if(JSONUtil.isEmpty(showJsonObject)){
			return null;
		}
		
		YoukuInfo youkuInfo = parseInfo(showJsonObject);
		
		for (int i = 0; i < searchItems.length(); i++) {
			JSONObject tmp = searchItems.optJSONObject(i);
			if(isSame(youkuInfo,tmp)){
				return tmp;
			}
		}
		return null;
	}

	/**
	 * @param showJsonObject
	 * @return
	 */
	private YoukuInfo parseInfo(JSONObject showJsonObject) {
		if(JSONUtil.isEmpty(showJsonObject)){
			return null;
		}
		YoukuInfo youkuInfo = new YoukuInfo();
		if(JSONUtil.isEmpty(showJsonObject, "results")){
			return null;
		}
		
		JSONObject firstJsonObject = showJsonObject.optJSONArray("results").optJSONObject(0);
		if(JSONUtil.isEmpty(firstJsonObject)){
			return null;
		}
		youkuInfo.name = firstJsonObject.optString("showname");
		youkuInfo.pubdate = firstJsonObject.optString("releasedate");
		youkuInfo.country = getArray(firstJsonObject.optJSONArray("area"));
		youkuInfo.alias = getArray(firstJsonObject.optJSONArray("showalias"));
		youkuInfo.director = getArray(firstJsonObject.optJSONArray("person"),"director");
		youkuInfo.performer = getArray(firstJsonObject.optJSONArray("person"),"performer");
		youkuInfo.writer = getArray(firstJsonObject.optJSONArray("person"),"writer");
		youkuInfo.dubbing = getArray(firstJsonObject.optJSONArray("person"),"dubbing");
		youkuInfo.imdb = firstJsonObject.optString("imdb_num");
		
		return youkuInfo;
	}

	/**
	 * @param optJSONArray
	 * @param string
	 * @return
	 */
	private String[] getArray(JSONArray optJSONArray, String string) {
		String [] tmp = getArray(optJSONArray);
		if(tmp!=null && tmp.length>0){
			List<String> list = new ArrayList<String>();
			for (String string2 : tmp) {
				
				if(string2.indexOf(":")>1){
					String [] infos = string2.split(":");
					if(infos.length==2  &&  string.equalsIgnoreCase(infos[1])){
						list.add(infos[0]);
					}
				}
			}
			if(list.size()>0){
				return list.toArray(new String[0]);
			}
		}
		return tmp;
	}

	/**
	 * @param optJSONArray
	 * @return
	 */
	private String[] getArray(JSONArray optJSONArray) {
		if(JSONUtil.isEmpty(optJSONArray)){
			return null;
		}
		String [] infos = new String[optJSONArray.length()];
		for (int i = 0; i < infos.length; i++) {
			infos[i]=optJSONArray.optString(i);
		}
		return infos;
	}

	/**
	 * @param showObject
	 * @param show2Object
	 * @return
	 */
	private boolean isSame(YoukuInfo youkuInfo, JSONObject show2Object) {
		if(youkuInfo == null && show2Object == null){
			return true;
		}
		DoubanInfo doubanInfo = parseDoubanInfo(show2Object);
		if(doubanInfo==null){
			return false;
		}
		
		//youku的name必须和豆瓣的names中的一个完全相等
		if(youkuInfo.name == null || youkuInfo.name.length()==0 || doubanInfo.name==null || doubanInfo.name.length==0){
			return false;
		}
		boolean haveSame = false;
		for (String doubanName:doubanInfo.name) {
			if(youkuInfo.name.equalsIgnoreCase(doubanName)){
				haveSame = true;
			}
		}
		if(!haveSame){
			return false;
		}
		//有名称相同的情况下，如果导演有相同的，就很有可能是同一部剧了
		if(youkuInfo.imdb != null &&  doubanInfo.imdb!=null && doubanInfo.imdb.equals(youkuInfo.imdb)){
			return true;
		}
		
		if(haveSame(youkuInfo.director,doubanInfo.director)){
			return true;
		}
		
		if(haveSame(youkuInfo.writer,doubanInfo.writer)){
			return true;
		}
		
		if(haveSame(youkuInfo.performer,doubanInfo.cast)){
			return true;
		}
		
		if(haveSame(youkuInfo.dubbing,doubanInfo.dubbing)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	private boolean haveSame(String[] arr1, String[] arr2) {
		if(arr1 != null && arr1.length>0 && arr2!=null && arr2.length>0){
			for (String yDirector : arr1) {
				for (String dDirector : arr2) {
					if(yDirector.equalsIgnoreCase(dDirector)){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param show2Object
	 * @return
	 */
	private DoubanInfo parseDoubanInfo(JSONObject show2Object) {
		JSONArray attrs = show2Object.optJSONArray("db:attribute");
		if (JSONUtil.isEmpty(attrs)) {
			return null;
		}
		DoubanInfo doubanInfo = new DoubanInfo();
		
		doubanInfo.writer = getDoubanArray(attrs, "writer");
		doubanInfo.director = getDoubanArray(attrs, "director");
		doubanInfo.cast = getDoubanArray(attrs, "cast");
		doubanInfo.name = getDoubanArray(attrs, "aka");
		doubanInfo.pubdate = getDoubanArray(attrs, "pubdate");
		doubanInfo.country = getDoubanArray(attrs, "country");
		doubanInfo.dubbing = getDoubanArray(attrs, "cast");
		
		String [] imdb = getDoubanArray(attrs, "imdb");
		if(imdb!=null && imdb.length==1){
			//http://www.imdb.com/title/tt0078657/
			String tmp = imdb[0];
			String [] infos =tmp.split("/");
			if(infos!=null && infos.length>=5){
				doubanInfo.imdb = infos[4];
			}
			
		}
		return doubanInfo;
	}

	/**
	 * @param attrs
	 * @param string
	 * @return
	 */
	private String[] getDoubanArray(JSONArray attrs, String key) {
		if(JSONUtil.isEmpty(attrs)){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < attrs.length(); i++) {
			JSONObject tmp = attrs.optJSONObject(i);
			if(tmp!=null){
				if(key.equalsIgnoreCase(tmp.optString("@name"))){
					list.add(tmp.optString("$t"));
				}
			}
		}
		if(list.size()>0){
			return list.toArray(new String[0]);
		}
		
		return null;
	}

	public static class DoubanInfo {
		//豆瓣 "db:attribute": 属性是个数组 .
		//{
        //	"$t": "新海诚",
        //	"@name": "writer"
        //},
		String [] writer;
		String [] director;
		String [] cast;
		String [] name;//aka
		String [] pubdate;
		String [] country;
		String [] dubbing;//动漫的话，主要是配音，
		String imdb;// tt1446486
	}
	
	public static class YoukuInfo {
		//"person": 是个数组
		String name;//"showname": "单身男女",
		String  pubdate;//"releasedate": "2011-03-31",
		String [] alias;
		String [] country;//"area": ["香港"],
		String [] director;
		String [] performer;// "吴彦祖:performer",
		String [] writer;//"杜琪峰:scenarist",
		String [] dubbing;//动漫的话，主要是配音
		String imdb;// tt1446486
	}
	
	//**************************** 比较youku节目和豆瓣节目是否相同********** End
}
