package com.youku.soku.sort;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.config.ExtServerConfig;
import com.youku.soku.sort.major_term.MajorTermSearcher;
import com.youku.soku.sort.word_match.MatcherResult;
import com.youku.soku.sort.word_match.ProgramMatcher;
import com.youku.soku.util.HttpClientUtil;
import com.youku.soku.util.MyUtil;
import com.youku.soku.zhidaqu.v2.DictManager;
import com.youku.soku.zhidaqu.v2.Element;
import com.youku.soku.zhidaqu.v2.TokenType;

/**
 * soku搜索的逻辑
 */
public class SearcherSoku {

	
	static final int MAX_PROGRAMME_COUNT = 10;
	static Log logger = LogFactory.getLog(SearcherSoku.class);

	public static JSONObject search(Parameter p) throws Exception {

		// 搜索数据
		JSONObject result = null;
		try {
			result = Searcher_mix_site.INSTANCE.search(p);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (result == null) {
			return result;
		}

		// 检查直达区
		JSONObject ext = new JSONObject();
		// boolean matchProcess = true;
		TokenType type = cateToTokenType(p.cateid);
		MatcherResult fuzzyResult = ProgramMatcher.getSokuMatcher().match(StringUtils.trimToEmpty(p.keyword),type);
		String fuzzyKey = null;
		if (fuzzyResult != null) {
			
			fuzzyKey = fuzzyResult.getParamKeyAsString();
			
			logger.debug(p.keyword + ": 转换后的关键字为：" + fuzzyKey);
			
			if (p.ext) {
				
					// 调用Ext服务器来返回结果
					if (fuzzyKey != null && fuzzyKey.length() > 0) {
						StringBuffer serverURI = new StringBuffer("http://");
						serverURI.append(
								StringUtils.trimToEmpty(ExtServerConfig.getRandomServer())).append("/");
						serverURI.append(StringUtils.trimToEmpty(ExtServerConfig.getInstance().getString("NAMEARRPATH")));
						if(p.site>0){
							serverURI.append("?site=").append(p.site).append("&names=");
						}else {
							serverURI.append("?names=");
						}
						logger.debug("serverURI: " + serverURI);
						ext = new JSONObject(HttpClientUtil.getRemoteResult(serverURI.toString(), fuzzyKey));
						try {
							filter(fuzzyResult,ext,p.keyword);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				
			} else {

			}
			
			
		} else
			logger.debug(p.keyword + ": 没有相关的模糊匹配结果");

		result.put("ext", ext);

		// 检查大词
		try {
			JSONObject termObject = MajorTermSearcher.search(p.keyword);

			if (termObject != null) {
				if (termObject != null) {
					result.put("major_term", termObject);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return result;
	}
	
	private static void filter(MatcherResult fuzzyResult,JSONObject ext,String keyword) throws JSONException{
		
		//直接命中第几集或者某一期
		if (fuzzyResult.getLikeResult()!= null 
				&& fuzzyResult.getLikeResult().getProgram() != null){
			
			//直接命中第几集
			if (fuzzyResult.getLikeResult().getEpisode() != null){
				Element[] elements = DictManager.getElements(fuzzyResult.getLikeResult().getProgram());
				int i = 0;
				for (;i < elements.length ; i++){
					Element element =  elements[i];
					if (element.getType() == TokenType.TELEPLAY || element.getType() == TokenType.ANIME){
						break;
					}
				}
				JSONObject programJson = ext.optJSONObject(String.valueOf(i));
				if (programJson != null){
					programJson.put("currentEpisodeNumber", fuzzyResult.getLikeResult().getEpisode().getNormalValue());
				}
			}
			//命中综艺节目某天的节目
			else if (fuzzyResult.getLikeResult().getDate() != null){
				Element[] elements = DictManager.getElements(fuzzyResult.getLikeResult().getProgram());
				int i = 0;
				for (;i < elements.length ; i++){
					Element element =  elements[i];
					if (element.getType() == TokenType.VARIETY){
						break;
					}
				}
				
				JSONObject programJson = ext.optJSONObject(String.valueOf(i));
				if (programJson != null){
					programJson.put("currentEpisodeNumber", fuzzyResult.getLikeResult().getDate().getNormalValue());
				}
			}
			
		}
		
		//命中演员并且不止命中演员
		 if (fuzzyResult.getLikeResult()!= null  
				 && fuzzyResult.getLikeResult().getPerson() != null 
				 && !fuzzyResult.getLikeResult().getPerson().getValue().equals(keyword)){
				logger.debug("过滤演员："+ fuzzyResult.getLikeResult().getPerson().getValue());
				for(Iterator<String> keys = ext.keys();keys.hasNext();)
				{
					JSONObject programJson = ext.optJSONObject(keys.next());
					if (programJson != null){
						String type = programJson.optString("nameTypes");
						
						//只对电视剧和电影过滤演员
						if (type.equals(TokenType.VARIETY.name()) || type.equals(TokenType.PERSON.name())) {
							continue;
						}
						
						JSONArray details = programJson.optJSONArray(type);
						
						if (details != null && details.length() > 0){
							
							for (int i = 0 ;i < details.length() ;i ++){
								boolean findActor = false;
								JSONObject detail = details.optJSONObject(i);
								if (detail != null){
									JSONObject pro = detail.optJSONObject("programme");
									if (pro != null){
										JSONArray performer = pro.optJSONArray("performer");
										if(performer != null && performer.length() > 0){
											for (int j=0;j < performer.length() ;j++){
												JSONObject actor = performer.optJSONObject(j);
												if (fuzzyResult.getLikeResult().getPerson().getValue().equals(actor.optString("name"))){
													pro.put("highLightPerson", actor.optString("name"));
													findActor = true;
													break;
												}
											}
										}
									}
								}
								if (!findActor){
									details.put(i,new JSONObject());
								}
							}
						}
						
					}
				}
				
			}
		 
		//去重节目用的map
			Set<Integer> programSet = new HashSet<Integer>();
			
		 //只截取MAX_PROGRAMME_COUNT个,高亮，显示别名
		 int count = 0;
		 JSONArray siteArray = new JSONArray();
		 for(Iterator keys = ext.keys();keys.hasNext();)
			{
			 	String key = (String)keys.next();
				JSONObject programJson = ext.optJSONObject(key);
				if (programJson != null){
					String nameTypes = programJson.optString("nameTypes");
					
					JSONArray details = programJson.optJSONArray(nameTypes);
					String searchKey = programJson.optString("searchKey");
					
					if (nameTypes.equals(TokenType.PERSON.name()) ) {
						programJson.put("highLightName", highlight(searchKey));
						continue;
					}
					
					if (details != null && details.length() > 0){
						
						for (int i = 0 ;i < details.length() ;i ++){
							JSONObject detail = details.optJSONObject(i);
							if (detail != null && detail.length() > 0){
								if (count >= MAX_PROGRAMME_COUNT){
									details.put(i,new JSONObject());
								}
								else{
									JSONObject pro = detail.optJSONObject("programme");
									if (pro == null) 
										continue;
									
									String name = pro.optString("name");
									int programmeId = pro.optInt("programmeId");
									
									//根据节目ID去重
									if (programSet.contains(programmeId)  ){
										details.put(i,new JSONObject());
										continue;
									}
									programSet.add(programmeId);
									
									//增加数量
									count++;
									
									int index = Integer.parseInt(key);
									if (fuzzyResult.isLikeMatch(index)){
										logger.debug(name + " is like matched");
										if (name .equals(searchKey)){
											pro.put("highLightName", highlight(name));
										}
										else{
											pro.put("name", searchKey);
											pro.put("highLightName", highlight(searchKey));
											pro.put("aliasName", name);
										}
									}
									else{
										logger.debug(name + " 是前匹配命中，前匹配词："+fuzzyResult.getPrefixWord());
										if (MyUtil.isSimilar(fuzzyResult.getPrefixWord(),name)){
											pro.put("highLightName", highlight(name,fuzzyResult.getPrefixWord()));
										}
										else{
											//找别名
											String alias = pro.optString("alias");
											String[] arr = alias.split("\\|");
											for (String aliasName:arr){
												if(MyUtil.isSimilar(fuzzyResult.getPrefixWord(),aliasName)){
													pro.put("name", aliasName);
													pro.put("highLightName", highlight(aliasName,fuzzyResult.getPrefixWord()));
													break;
												}
											}
											
											pro.put("aliasName", name);
										}
									}
									
								}
							}
							
						}
					}
					
				}
			}
	}
	private static String highlight(String searchKey){
		return "<span class=\"highlight\">"+searchKey+"</span>";
	}
	
	//不区分大小写
	private static String highlight(String name,String searchKey){
		return name.replaceFirst("(?i)"+searchKey,"<span class=\"highlight\">$0</span>");
	}
	private static TokenType cateToTokenType(int cateId){
		switch(cateId){
			case 1:
				return TokenType.TELEPLAY;
			case 2:
				return TokenType.MOVIE;
			case 3:
				return TokenType.VARIETY;
			case 5:
				return TokenType.ANIME;
			case 6:
				return TokenType.PERSON;
			default:
				return null;
			
		}
	}
	
}