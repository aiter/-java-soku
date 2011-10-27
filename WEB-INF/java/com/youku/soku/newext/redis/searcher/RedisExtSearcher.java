package com.youku.soku.newext.redis.searcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.youku.soku.newext.searcher.ProgrammeSearcher;
import com.youku.soku.newext.util.ChannelType;
import com.youku.soku.newext.util.RedisCacheUtil;
import com.youku.soku.newext.util.StringUtil;

/**
 * @author tanxiuguang
 * create on Oct 16, 2011
 */
public class RedisExtSearcher {
	
	private static Log logger = LogFactory.getLog(RedisExtSearcher.class);
	
	public static JSONObject searchProgrammeId(int programmeId) {
		Jedis jedis = RedisClient.getInstance().getJedisPool().getResource();
		
		try {
			String jsonStr = jedis.get(programmeId + "");
			
			if(!StringUtils.isBlank(jsonStr)) {
				JSONObject jsonObj = new JSONObject(jsonStr);
				return jsonObj;
			}
		} catch(JSONException e) {
			logger.equals(e.getMessage());
		} finally {
			RedisClient.getInstance().getJedisPool().returnResource(jedis);
		}
		
		return null;
	}
	
	public static JSONObject searchNameArray(List<String> nameArr,String site) {
		
		Jedis jedis = RedisClient.getInstance().getJedisPool().getResource();
		
		JSONObject returnJson = null;
		try {
			returnJson = new JSONObject();
			if(nameArr==null || nameArr.size()<=0) return returnJson;
			for(int i=0;i<nameArr.size(); i++){
				String eleName=nameArr.get(i);
				if(eleName==null || StringUtils.trimToEmpty(eleName).length()<=0){
					returnJson.put(new Integer(i).toString(), new JSONObject());
					continue;
				}
				
				String[] eleNameArr=eleName.split("/");
				if(eleNameArr.length<=1){
//				查找所有分类
					logger.debug("根据 eleName查找所有分类："+eleName);
					JSONObject json=new JSONObject();
					json=getExtInfo(eleNameArr[0],site, jedis);
					returnJson.put(new Integer(i).toString(), json);
					continue;
					
				}else if(eleNameArr.length>=2){
					JSONObject programmeJson = getJSONFromRedis(jedis, eleNameArr[0], eleNameArr[1], site);

					programmeJson.put("nameTypes", eleNameArr[1]);
					programmeJson.put("searchKey",StringUtils.trimToEmpty(eleNameArr[0]));
					returnJson.put(i + "", programmeJson);
				}
				
			}
			returnJson.put("size", nameArr.size());
		} catch (JSONException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			RedisClient.getInstance().getJedisPool().returnResource(jedis);
		}
		
		return returnJson;
	}
	
	/**
	 * 对全部分类进行检索 
	 * @return
	 * @throws Exception
	 */
	private static JSONObject getExtInfo(String keyword, String site, Jedis jedis) throws Exception {
		
		JSONObject movieJson = getJSONFromRedis(jedis, keyword, ChannelType.MOVIE.name(), site);
		JSONObject teleplayJson = getJSONFromRedis(jedis, keyword, ChannelType.TELEPLAY.name(), site);
		JSONObject varietyJson = getJSONFromRedis(jedis, keyword, ChannelType.VARIETY.name(), site);
		JSONObject animeJson = getJSONFromRedis(jedis, keyword, ChannelType.ANIME.name(), site);
		JSONObject documentaryJson = getJSONFromRedis(jedis, keyword, ChannelType.DOCUMENTARY.name(), site);
        
		JSONObject jsonObject=new JSONObject();
		
		int jsonIdx = 0;
		jsonIdx += repackJson(jsonObject, movieJson, jsonIdx);
		jsonIdx += repackJson(jsonObject, teleplayJson, jsonIdx);
		jsonIdx += repackJson(jsonObject, varietyJson, jsonIdx);
		jsonIdx += repackJson(jsonObject, animeJson, jsonIdx);
		jsonIdx += repackJson(jsonObject, documentaryJson, jsonIdx);

		jsonObject.put("size", jsonIdx);
       return jsonObject;
	}
	
	private static JSONObject getJSONFromRedis(Jedis jedis, String keyword, String category, String site) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("get from redis key: "+ RedisCacheUtil.getStoreKey(keyword, category));
		}
		
		String extJsonStr = jedis.get(RedisCacheUtil.getStoreKey(keyword, category));
		
		if(extJsonStr == null) {
			return null;
		}
		
		try {
			JSONObject extJson = new JSONObject(extJsonStr);
			if(!StringUtils.isBlank(site)) {
				List<Integer> siteIdList = new ArrayList<Integer>(StringUtil.parseSite(site));
				int programmeSize = extJson.optInt("size");
				for(int i = 0; i < programmeSize; i++) {
					JSONObject channelJson=extJson.optJSONObject(new Integer(i).toString());
					if(channelJson==null) continue;
					String nameType=StringUtils.trimToEmpty(channelJson.optString("nameTypes"));
					
					JSONArray programmeArr=channelJson.optJSONArray(nameType);
					if(programmeArr==null || programmeArr.length()<=0) continue;
					
					for(int j = 0; j < programmeArr.length(); j++) {
						JSONObject oneProJson = programmeArr.optJSONObject(j);
						JSONObject programmeInfo = oneProJson.optJSONObject("programme");

						List<String> tmpSiteList = ProgrammeSearcher.getProSiteStrList(siteIdList);
						programmeInfo.put("sites",tmpSiteList);
						
						JSONObject proSiteJson = oneProJson.optJSONObject("programmeSite");
						JSONObject newProSiteJson = new JSONObject();
						for(int siteId : siteIdList) {
							newProSiteJson.put(siteId + "", proSiteJson.optJSONObject(siteId + ""));
						}
						oneProJson.put("programmeSite", newProSiteJson);
					}
				}
			}
			
			return extJson;
		} catch (JSONException e) {
			logger.error("JSON Format error", e);
		}
		
		return null;
	}
	
	private static int repackJson(JSONObject resultJson, JSONObject proJson, int startIdx) throws JSONException {
		
		if(proJson == null) {
			return 0;
		}
		
		int packedSize = 0;
		int programmeSize = proJson.optInt("size");
		for(int i = 0; i < programmeSize; i++) {
			JSONObject channelJson = proJson.optJSONObject(new Integer(i).toString());
			resultJson.put((i + startIdx) + "", channelJson);
			packedSize++;
		}
		
		return packedSize;
	}

}
