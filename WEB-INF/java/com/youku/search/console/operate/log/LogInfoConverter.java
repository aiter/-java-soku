package com.youku.search.console.operate.log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.console.vo.LogInfo;

public class LogInfoConverter {
	public static LogInfo convert(JSONObject json){
		LogInfo loginfo = new LogInfo();

        if (!json.isNull("keyword")) {
            try {
				loginfo.setKeyword(json.getJSONArray("keyword").getString(0));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        if (!json.isNull("query_type")) {
            try {
				loginfo.setType(json.getJSONArray("type").getString(0));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        if (!json.isNull("query_count")) {
            try {
				loginfo.setCounts(json.getJSONArray("query_count").optInt(0));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        if (!json.isNull("result")) {
            try {
				loginfo.setResults(json.getJSONArray("result").optInt(0));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        return loginfo;
    }
	
	public static List<LogInfo> convert(JSONArray jarr,int limit) {
		List<LogInfo> loginfos = new ArrayList<LogInfo>();
		LogInfo loginfo = null;
		for(int i=0;i<jarr.length()||i<limit;i++){
			if(0!=limit&&i>=limit) break;
			if(i==jarr.length()) break;
			try {
				loginfo = convert(jarr.getJSONObject(i));
				loginfos.add(loginfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return loginfos;
    }
}
