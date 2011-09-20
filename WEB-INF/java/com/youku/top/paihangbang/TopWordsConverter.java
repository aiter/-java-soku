package com.youku.top.paihangbang;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.soku.top.mapping.TopWords;
import com.youku.top.paihangbang.entity.TopWordsEntity;

public class TopWordsConverter {
	
	public static JSONObject convert(TopWords tw){
		
		JSONObject json = new JSONObject();
		
		try {
			if(StringUtils.isBlank(tw.getKeyword()))
				return null;
			json.put("keyword",tw.getKeyword());
			json.put("query_count", tw.getQueryCount());
			
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static JSONArray convert(List<TopWordsEntity> twes){
		JSONArray jarr = new JSONArray();
		JSONObject json = null;
		for(TopWordsEntity twe:twes){
			
			if(null==twe.getTopwords())
				continue;
			
			json = convert(twe.getTopwords());
			
			if(null==json)
				continue;
			
			jarr.put(json);
		}
		return jarr;
	}
	
}
