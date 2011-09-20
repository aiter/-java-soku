package com.youku.soku.sort.knowledge;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.sort.search.AbstractSearch;
import com.youku.search.store.pic.MemCachedPic;
import com.youku.search.util.Request;
import com.youku.soku.config.Config;
import com.youku.soku.index.query.VideoQueryManager.TimeLength;
import com.youku.soku.knowledge.KnowledgeData;
import com.youku.soku.knowledge.data.KnowledgeDataHolder;
import com.youku.soku.sort.Parameter;

public class KnowledgeSearch {



	private static Logger logger = Logger.getLogger(KnowledgeSearch.class);
	
	private static final int RECOMMEND_SIZE = 10;
	
	private static final int SEARCH_SIZE = 25;

	public static JSONObject search(Parameter param) throws Exception {
		
		String yokuHost = Config.getYoukuHost();

		long startTime = System.currentTimeMillis();
		//高级搜索  只搜标题
		StringBuilder buffer = new StringBuilder();
		String keyword = URLEncoder.encode(param.keyword, "utf-8");
		buffer.append("http://").append(yokuHost);
		buffer.append("/search?type=24&keyword=").append(keyword);
		
		
		buffer.append("&curpage=").append(param.page);
		
		buffer.append("&pagesize=").append(param.pagesize);
		
	//	.append("&relnum=").append(10);
		buffer.append("&hl=true");
		
		TimeLength timeLength = new TimeLength(param.time_length);
		buffer.append("&timeless=");
		buffer.append((int) (timeLength.getLess() / 60));
		buffer.append("&");

		buffer.append("timemore=");
		buffer.append((int) (timeLength.getMore() / 60));
		buffer.append("&");

		buffer.append("limit_date=");
		buffer.append(param.limit_date == 0 ? "" : param.limit_date);
		buffer.append("&");
		
		buffer.append("orderfield=null&");
		buffer.append("order=1&");

		if (param.hd == 1) {
			buffer.append("ftype=1");
		}
		String eduVideos = Request.requestGet(buffer.toString(), 500);
		
		if (eduVideos != null) {
			JSONObject result = new JSONObject(eduVideos);
			JSONObject eduObj = result.optJSONObject("items");
			if (eduObj != null) {
				Iterator it = eduObj.keys();

				List<String> vids = new ArrayList<String>();
				while (it.hasNext()) {
					String key = (String) it.next();
					JSONObject item = eduObj.optJSONObject(key);
					vids.add(item.optString("vid"));
					//list.add(item);
				}
				Map<String,Object> tmps = MemCachedPic.cacheGetByVid(vids.toArray(new String[]{}));
				
				
				it = eduObj.keys();
				while (it.hasNext()) {
					String key = (String) it.next();
					JSONObject item = eduObj.optJSONObject(key);
					String vid = item.optString("vid");
					String pvid = MemCachedPic.cacheKey(vid);
					
					if(tmps.get(pvid) != null) {
						item.put("pics", new JSONArray((String)tmps.get(pvid)));
					}
					
					item.put("url", getVideoUrl(item.optString("encodeVid")));
				}
			}
			
			try {
				//data.printKnowledgeTree(0, KnowledgeDataHolder.getCurrentThreadLocal());
				KnowledgeData dataMap = KnowledgeDataHolder.getCurrentThreadLocal();
				logger.info(dataMap.getChildColumn("武术"));
				logger.info(keyword);
				List<String> relatedColumn = dataMap.getChildColumn(param.keyword);
				JSONArray columnArr = new JSONArray();
				for(String c : relatedColumn) {
					columnArr.put(c);
				}
				result.put("r", columnArr);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				KnowledgeDataHolder.removeCurrentThreadLocal();
			}
			logger.info(result);
			return result;
		}
		long endTime = System.currentTimeMillis();
		logger.info("RecommendSearcher cost: " + (endTime - startTime));
		return null;
	}
	
	
	
	private static String getVideoUrl(String encodeVid) {
		return "http://v.youku.com/v_show/id_" + encodeVid + ".html";
	}



}
