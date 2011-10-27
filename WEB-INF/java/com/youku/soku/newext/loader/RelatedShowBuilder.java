package com.youku.soku.newext.loader;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.soku.library.load.util.SyncUtil;

public class RelatedShowBuilder {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String RELATED_SHOW_HOST = "http://10.103.20.101/api_getShowRelated?pid=XMjAwMA==&pl=6&showid=";
	
	
	private JSONArray getRelatedShow(int showId) throws Exception {
		//byte[] bytes = Wget.get(RELATED_SHOW_HOST + showId, 3000);
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(RELATED_SHOW_HOST + showId);
		method.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows 98)");
		client.executeMethod(method);
		JSONObject jsonObject = new JSONObject(method.getResponseBodyAsString());
		
		//logger.info(jsonObject);
		return jsonObject.optJSONArray("results");
	}
	
	public Map<Integer, String> build() {
		
		Map<Integer, String> relatedShowMap = new HashMap<Integer, String>();
		
		int[] minMaxids = SyncUtil.getMinMaxIDs();	
		
		//int[] minMaxids = {10000, 10100};
		for(int i = minMaxids[0]; i < minMaxids[1]; i++) {
			try {
				JSONArray relatedShow = getRelatedShow(i);
				if(relatedShow != null) {
					relatedShowMap.put(i, relatedShow.toString());
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		return relatedShowMap;
	}

}
