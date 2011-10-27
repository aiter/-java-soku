package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.soku.newext.util.middleResource.NovaMiddleResource;

public class CmsLoader {
	
	private Logger logger = Logger.getLogger(this.getClass());

	private int getCmsId(String orderBy) {

		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("pn", "1");
		params.put("pl", "2");
		params.put("fd", "title desc cmsurl thumburl publishtime");
		params.put("ob", orderBy);
		params.put("cl", "search_result");

		JSONObject resource = NovaMiddleResource.search("cms", "cms", "", params);

		if (resource == null || resource.isNull("results")) {
			for (int i = 0; i < 5; i++) {
				resource = NovaMiddleResource.search("cms", "cms", "", params);
				if (resource != null && !resource.isNull("results")) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		JSONArray showArray = resource.optJSONArray("results");
		if (!JSONUtil.isEmpty(showArray)) {
			return showArray.optJSONObject(0).optInt("cmsid");
		}
		return 0;
	}

	private int[] getMinAndMaxIds() {

		int[] ids = new int[] { 1, 5000 };
		int minId = getCmsId("cmsid:asc");

		if (minId > 0) {
			ids[0] = minId;
		}

		int maxId = getCmsId("cmsid:desc");
		if (maxId > 0) {
			ids[1] = maxId;
		}

		return ids;
	}

	public Map<String, List<JSONObject>> getAllCms() throws Exception {
		Map<String, List<JSONObject>> resultMap = new HashMap<String, List<JSONObject>>();
		int[] minMaxIds = getMinAndMaxIds();	
		logger.debug("minMaxIds: " + Arrays.toString(minMaxIds));
		int length = 500;
		JSONArray cmsResultArray = null;
		for (int i = minMaxIds[0]; i < minMaxIds[1]; i += length) {
			cmsResultArray = loadCmsByPkCms(i, (i + length - 1),length);
			if(cmsResultArray != null) {
				for(int j = 0; j < cmsResultArray.length(); j++) {
					JSONObject cmsObj = cmsResultArray.getJSONObject(j);
					JSONArray cmsKeyword = cmsObj.optJSONArray("keywords");
					
					addJSONObjectToList(cmsObj.optString("title"), cmsObj, resultMap);
					if(cmsKeyword != null) {
						for(int k = 0; k < cmsKeyword.length(); k++) {
							addJSONObjectToList(cmsKeyword.optString(k), cmsObj, resultMap);
						}
					}
				}
			}
		}
		
		return resultMap;
	}
	
	private void addJSONObjectToList(String key, JSONObject cmsObj, Map<String, List<JSONObject>> resultMap) {
		List<JSONObject> cmsList = resultMap.get(key);
		if(cmsList == null) {
			cmsList = new ArrayList<JSONObject>();
		}
		
		boolean isDuplicate = false;
		for(JSONObject cms : cmsList) {
			if(cms.optInt("cmsid") == cmsObj.optInt("cmsid")) {
				isDuplicate = true;
			}
		}
		
		if(!isDuplicate) {
			cmsList.add(cmsObj);
		}
	}

	private JSONArray loadCmsByPkCms(int start, int end, int length) {
		String query = "cmsid:" + start + "-" + end + " type:topic state:published publishtime:{before1month}-";

		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("pn", "1");
		params.put("pl", "2");
		params.put("fd", "title desc cmsurl thumburl publishtime");
		params.put("ob", "publishtime:desc");
		params.put("cl", "search_result");

		JSONObject resource = NovaMiddleResource.search("cms", "cms", query, params);

		return JSONUtil.getProperty(resource, JSONArray.class, "results");
	}
}
