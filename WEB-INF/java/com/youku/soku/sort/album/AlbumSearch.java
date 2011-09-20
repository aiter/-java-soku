package com.youku.soku.sort.album;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.soku.sort.Parameter;
import com.youku.search.sort.search.AbstractSearch;
import com.youku.search.store.pic.MemCachedPic;
import com.youku.search.util.Request;
import com.youku.soku.config.Config;
import com.youku.soku.knowledge.KnowledgeData;
import com.youku.soku.knowledge.data.KnowledgeDataHolder;

public class AlbumSearch {


	private static Logger logger = Logger.getLogger(AlbumSearch.class);
	

	public static JSONObject search(Parameter param) throws Exception {
		
		String yokuHost = Config.getYoukuHost();

		long startTime = System.currentTimeMillis();

		StringBuilder buffer = new StringBuilder();
		buffer.append("http://").append(yokuHost).append("/search?type=2&keyword=").append(URLEncoder.encode(param.keyword, "utf-8"))
				.append("&curpage=").append(param.page).append("&pagesize=").append(param.pagesize).append("&cateid=").append(param.cateid)
				.append("&relnum=").append(10);
		String albums = Request.requestGet(buffer.toString(), 500);
		
		if(albums != null) {
			JSONObject result = new JSONObject(albums);
			return result;
		}
		
		long endTime = System.currentTimeMillis();
		logger.info("RecommendSearcher cost: " + (endTime - startTime));
		return null;
	}
	


}
