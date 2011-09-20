package com.youku.soku.newext.searcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.soku.sort.Parameter;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.TypeConstant;
import com.youku.soku.newext.util.ChannelType;

/**
 * 只搜索视频的接口。
 */
public class Searcher {

	static Log logger = LogFactory.getLog(Searcher.class);

	public static String search(Parameter parameter) throws Exception {

		// 检查直达区
		String extInfo = "";
		try {
			extInfo = getExtInfo(parameter);
		} catch (Exception e) {
			logger.error("查询直达区发生异常", e);
			ExtInfoHolder.removeCurrentThreadLocal();
		}

		return extInfo;
	}

	private static String getExtInfo(Parameter p) throws Exception {
//      直达区不对关键词进行处理，直接从map中取结果
		final String k = p.keyword;

		JSONArray  m_json = MovieSearcher.searchByName(k,p.site+"");
		JSONArray  teleplay_json= TeleplaySearcher.searchByName(k,p.site+"");
		JSONArray  variety_json= VarietySearcher.searchByName(k,p.site+"");
		JSONArray  anime_json=AnimeSearcher.searchByName(k,p.site+"");
		
		JSONObject people_json=PeopleSearcher.searchByName(k,p.site+"");
        
		List<Integer> channelTypeList=new ArrayList<Integer>();
		JSONObject jsonObject=new JSONObject();
		
		if (m_json != null) {
			channelTypeList.add(2);
			jsonObject.put("movie", m_json);
		}
		
		if (people_json != null) {
			channelTypeList.add(10);
			jsonObject.put("people", people_json);
		}
		
		if (teleplay_json != null) {
			channelTypeList.add(1);
			jsonObject.put("teleplay", teleplay_json);
		}
		
		if (variety_json != null) {
			channelTypeList.add(3);
			jsonObject.put("variety", variety_json);
		}
		
		if (anime_json != null) {
			channelTypeList.add(5);
			jsonObject.put("anime", anime_json);
		}
		
        if(channelTypeList.size()>0)
        	jsonObject.put("nameTypes", channelTypeList);

        String returnStr="";
        if (p.h) {
        	returnStr = jsonObject.toString(4);
		} else {
			returnStr = jsonObject.toString(4);
		}
		return   returnStr;
	}
	
	/**
	 * 对全部分类进行检索 
	 * @return
	 * @throws Exception
	 */
	private static JSONObject getExtInfo(String k,String site) throws Exception {
//      直达区不对关键词进行处理，直接从map中取结果

		JSONArray  m_json = MovieSearcher.searchByName(k,site);
		JSONArray  teleplay_json= TeleplaySearcher.searchByName(k,site);
		JSONArray  variety_json= VarietySearcher.searchByName(k,site);
		JSONArray  anime_json=AnimeSearcher.searchByName(k,site);
        
		List<Integer> channelTypeList=new ArrayList<Integer>();
		JSONObject jsonObject=new JSONObject();
		
		if (m_json != null) {
			channelTypeList.add(ChannelType.MOVIE.getValue());
			jsonObject.put(ChannelType.MOVIE.name(), m_json);
		}
		
		if (teleplay_json != null) {
			channelTypeList.add(ChannelType.TELEPLAY.getValue());
			jsonObject.put(ChannelType.TELEPLAY.name(), teleplay_json);
		}
		
		if (variety_json != null) {
			channelTypeList.add(ChannelType.VARIETY.getValue());
			jsonObject.put(ChannelType.VARIETY.name(), variety_json);
		}
		
		if (anime_json != null) {
			channelTypeList.add(ChannelType.ANIME.getValue());
			jsonObject.put(ChannelType.ANIME.name(), anime_json);
		}
		
        if(channelTypeList.size()>0)
        	jsonObject.put("nameTypes", channelTypeList);

       return jsonObject;
	}

	
	public static String searchTop(String programmeIds, int cate_id) throws Exception  {
		String[] programmeIdArr=programmeIds.split(",");
		String returnStr="";
		if(cate_id==TypeConstant.NameType.MOVIE){
			JSONObject tmpJson= MovieSearcher.search(programmeIdArr,cate_id);
			if(tmpJson!=null) returnStr=tmpJson.toString(4);
		}else if(cate_id==TypeConstant.NameType.TELEPLAY){
			JSONObject tmpJson= TeleplaySearcher.search(programmeIdArr,cate_id);
			if(tmpJson!=null) returnStr=tmpJson.toString(4);
		}else if(cate_id==TypeConstant.NameType.VARIETY){
			JSONObject tmpJson= VarietySearcher.search(programmeIdArr,cate_id);
			if(tmpJson!=null) returnStr=tmpJson.toString(4);
			
		}else if(cate_id==TypeConstant.NameType.ANIME){
			JSONObject tmpJson= AnimeSearcher.search(programmeIdArr,cate_id);
			if(tmpJson!=null) returnStr=tmpJson.toString(4);
		}
		
		return returnStr;
	}
	
	

	/**
	 * 
	 * @param nameArr
	 * @param site
	 * @param size 目前只有人物直达区用到size
	 * @return
	 * @throws Exception
	 */
	public static JSONObject searchNameArray(String[] nameArr,String site) throws Exception{
		return searchNameArray(nameArr, site,0);
	}
	public static JSONObject searchNameArray(String[] nameArr,String site,int size) throws Exception{
		JSONObject returnJson=new JSONObject();
		if(nameArr==null || nameArr.length<=0) return returnJson;
		for(int i=0;i<nameArr.length; i++){
			String eleName=nameArr[i];
			if(eleName==null || StringUtils.trimToEmpty(eleName).length()<=0){
				returnJson.put(new Integer(i).toString(), new JSONObject());
				continue;
			}
			
			String[] eleNameArr=eleName.split("/");
			if(eleNameArr.length<=1){
//				查找所有分类
				logger.debug("根据 eleName查找所有分类："+eleName);
				JSONObject json=new JSONObject();
				json=getExtInfo(eleNameArr[0],site);
				returnJson.put(new Integer(i).toString(), json);
				continue;
				
			}else if(eleNameArr.length>=2){
				logger.debug("根据 eleName："+eleNameArr[0]+" ,分类："+eleNameArr[1]+" 查找");
				if(StringUtils.trimToEmpty(eleNameArr[1]).equals(ChannelType.MOVIE.name())){
					JSONObject movieJson=new JSONObject();
					
//					List<Integer> nameTypesList=new ArrayList<Integer>();
//					nameTypesList.add(ChannelType.MOVIE.getValue());
					movieJson.put("nameTypes", ChannelType.MOVIE.name());
					
					movieJson.put("searchKey",StringUtils.trimToEmpty(eleNameArr[0]));
					
					JSONArray searchResult=MovieSearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),site);
					movieJson.put(ChannelType.MOVIE.name(),searchResult);
					
					if(searchResult!=null) 
						returnJson.put(new Integer(i).toString(), movieJson);
					continue;
				}else if(StringUtils.trimToEmpty(eleNameArr[1]).equals(ChannelType.TELEPLAY.name())){
					JSONObject teleplayJson=new JSONObject();
					
//					List<Integer> nameTypesList=new ArrayList<Integer>();
//					nameTypesList.add(ChannelType.TELEPLAY.getValue());
					teleplayJson.put("nameTypes", ChannelType.TELEPLAY.name());
					
					teleplayJson.put("searchKey",StringUtils.trimToEmpty(eleNameArr[0]));
					
					JSONArray searchResult=TeleplaySearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),site);
					
					teleplayJson.put(ChannelType.TELEPLAY.name(),searchResult);
					if(searchResult!=null) 
						returnJson.put(new Integer(i).toString(), teleplayJson);
					continue;
				}else if(StringUtils.trimToEmpty(eleNameArr[1]).equals(ChannelType.VARIETY.name())){
					JSONObject varietyJson=new JSONObject();
					
//					List<Integer> nameTypesList=new ArrayList<Integer>();
//					nameTypesList.add(ChannelType.VARIETY.getValue());
					varietyJson.put("nameTypes", ChannelType.VARIETY.name());
					
					varietyJson.put("searchKey",StringUtils.trimToEmpty(eleNameArr[0]));
					
					JSONArray searchResult=VarietySearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),site);
					varietyJson.put(ChannelType.VARIETY.name(),searchResult);
					
					if(searchResult!=null)
						returnJson.put(new Integer(i).toString(), varietyJson);
					continue;
				}else if(StringUtils.trimToEmpty(eleNameArr[1]).equals(ChannelType.ANIME.name())){
					JSONObject animeJson=new JSONObject();
					
					List<Integer> nameTypesList=new ArrayList<Integer>();
					nameTypesList.add(ChannelType.ANIME.getValue());
					animeJson.put("nameTypes",ChannelType.ANIME.name());
					
					animeJson.put("searchKey",StringUtils.trimToEmpty(eleNameArr[0]));
					
					JSONArray searchResult=AnimeSearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),site);
					animeJson.put(ChannelType.ANIME.name(),searchResult);
					
					if(searchResult!=null) 
						returnJson.put(new Integer(i).toString(), animeJson);
					continue;
				}else if(StringUtils.trimToEmpty(eleNameArr[1]).equals(ChannelType.PERSON.name())){
					
					JSONObject personJson=new JSONObject();
					
					List<Integer> nameTypesList=new ArrayList<Integer>();
					nameTypesList.add(ChannelType.PERSON.getValue());
					personJson.put("nameTypes",ChannelType.PERSON.name());
					
					personJson.put("searchKey", StringUtils.trimToEmpty(eleNameArr[0]));
					
					JSONObject searchResult= null;
					if(eleNameArr.length==3){
						if(size>0){
							searchResult = PeopleSearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),size,ChannelType.valueOf(eleNameArr[2]),site);
						}else {
							searchResult = PeopleSearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),ChannelType.valueOf(eleNameArr[2]),site);
						}
					}
					else{
						if(size>0){
							searchResult = PeopleSearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),size,null,site);
						}else {
							searchResult = PeopleSearcher.searchByName(StringUtils.trimToEmpty(eleNameArr[0]),site);
						}
					}
					personJson.put(ChannelType.PERSON.name(),searchResult);
					
					personJson.put("name",StringUtils.trimToEmpty(eleNameArr[0]));
					
					if(searchResult!=null) 
						returnJson.put(new Integer(i).toString(), personJson);
					continue;
					
				}
				
			}
//			else{
//				logger.error("eleName格式出错：eleName"+eleName);
//				returnJson.put(new Integer(i).toString(), new JSONObject());
//				continue;
//			}
//			
			
			
			
		}
		
		returnJson.put("size",nameArr.length );
		
		
		
		return returnJson;
	}
}