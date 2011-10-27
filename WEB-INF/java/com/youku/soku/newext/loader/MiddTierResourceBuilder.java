package com.youku.soku.newext.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.soku.library.load.util.SyncUtil;
import com.youku.soku.newext.util.middleResource.MiddleResourceUtil;

public class MiddTierResourceBuilder {
	
	private Log logger = LogFactory.getLog(this.getClass());
	
	private Map<Integer, JSONObject> showDetailMap;
	
	private Map<Integer, JSONArray> tidbitsAndTrailerMap;
	
	private Map<Integer, JSONArray> videoPointMap;
	
	private Map<Integer, JSONArray> videoGuestMap;
	
	private Map<Integer, JSONObject> personInfoMap;
	
	private Map<Integer, JSONArray> videoLanguageMap;

	public Map<Integer, JSONObject> getShowDetailMap() {
		return showDetailMap;
	}

	public Map<Integer, JSONArray> getTidbitsAndTrailerMap() {
		return tidbitsAndTrailerMap;
	}

	public Map<Integer, JSONArray> getVideoPointMap() {
		return videoPointMap;
	}

	public Map<Integer, JSONArray> getVideoGuestMap() {
		return videoGuestMap;
	}
	
	
	public Map<Integer, JSONObject> getPersonInfoMap() {
		if(personInfoMap == null) {
			initPersonMap();
		}
		return personInfoMap;
	}
	
	public Map<Integer, JSONArray> getVideoLanguageMap() {
		return videoLanguageMap;
	}

	public void init() {
		int[] minMaxids = SyncUtil.getMinMaxIDs();
		//int[] minMaxids = {99300, 99318};
		logger.info("min-max id:" + minMaxids[0] + "-" + minMaxids[1]);
		int length = 400;

		showDetailMap = new HashMap<Integer, JSONObject>();
		tidbitsAndTrailerMap = new HashMap<Integer, JSONArray>();
		videoPointMap = new HashMap<Integer, JSONArray>();
		videoGuestMap = new HashMap<Integer, JSONArray>();
		videoLanguageMap = new HashMap<Integer, JSONArray>();
		
		for (int i = minMaxids[0]; i < minMaxids[1]; i += length) {
			
			int pageLength = 1000;
			JSONArray showDetailJSONArray = MiddleResourceUtil.getProgrammeByIds(i, (i + length - 1),length);
			JSONArray tidbitAndTrailerJSONArray = MiddleResourceUtil.searchTidbitsAndTrailer(i, (i + length - 1),pageLength);
			JSONArray pointJSONArray = MiddleResourceUtil.searchVideoPoint(i, (i + length - 1),pageLength);
			JSONArray guestJSONArray = MiddleResourceUtil.searchVideoGuest(i, (i + length - 1),pageLength);
			JSONArray videoLanguageJSONArray = MiddleResourceUtil.searchVideoLanguage(i, i + length - 1, pageLength);
			
			if(showDetailJSONArray != null) {
				for(int j = 0; j < showDetailJSONArray.length(); j++) {
					JSONObject showDetail = showDetailJSONArray.optJSONObject(j);
					showDetailMap.put(showDetail.optInt("pk_odshow"), showDetail);
				}
			}
			
			if(tidbitAndTrailerJSONArray != null) {
				for(int j = 0; j < tidbitAndTrailerJSONArray.length(); j++) {
					JSONObject tidbitAndTrailerJSONObject = tidbitAndTrailerJSONArray.optJSONObject(j);
					int showId = tidbitAndTrailerJSONObject.optInt("show_id");
					JSONArray resultJSONArray = tidbitsAndTrailerMap.get(showId);
					if(resultJSONArray == null ) {
						resultJSONArray = new JSONArray();
						tidbitsAndTrailerMap.put(showId, resultJSONArray);
					}
					resultJSONArray.put(tidbitAndTrailerJSONObject);
				}
			}
			
			if(pointJSONArray != null) {
				for(int j = 0; j < pointJSONArray.length(); j++) {
					JSONObject pointJSONObject = pointJSONArray.optJSONObject(j);
					int showId = pointJSONObject.optInt("show_id");
					JSONArray resultJSONArray = videoPointMap.get(showId);
					if(resultJSONArray == null) {
						resultJSONArray = new JSONArray();
						videoPointMap.put(showId, resultJSONArray);
					}
					resultJSONArray.put(pointJSONObject);
				}
			}
			
			if(guestJSONArray != null) {
				for(int j = 0; j < guestJSONArray.length(); j++) {
					JSONObject guestJSONObject = guestJSONArray.optJSONObject(j);
					int showId = guestJSONObject.optInt("show_id");
					JSONArray resultJSONArray = videoGuestMap.get(showId);
					if(resultJSONArray == null) {
						resultJSONArray = new JSONArray();
						videoGuestMap.put(showId, resultJSONArray);
					}
					resultJSONArray.put(guestJSONObject);
				}
			}
			
			if(videoLanguageJSONArray != null) {
				for(int j = 0; j < videoLanguageJSONArray.length(); j++) {
					JSONObject videoLanguageJSONObject = videoLanguageJSONArray.optJSONObject(j);
					int showId = videoLanguageJSONObject.optInt("show_id");
					JSONArray resultJSONArray = videoLanguageMap.get(showId);
					if(resultJSONArray == null) {
						resultJSONArray = new JSONArray();
						videoLanguageMap.put(showId, resultJSONArray);
					}
					resultJSONArray.put(videoLanguageJSONObject);
				}
			}
		}
		
		//getAllSearchKeyword();
		initPersonMap();
	}
	
	
	
	private void initPersonMap() {
		int[] minMaxIds = MiddleResourceUtil.getPersonMinMaxIDs();
		//int[] minMaxIds = {10001, 20000};
		logger.info("min-max id:" + minMaxIds[0] + "-" + minMaxIds[1]);
		int length = 500;

		personInfoMap = new HashMap<Integer, JSONObject>();
		
		for (int i = minMaxIds[0]; i < minMaxIds[1]; i += length) {
			JSONArray personInfoJSONArray = MiddleResourceUtil.getPersonByIds(i, (i + length - 1),length);
			
			if(personInfoJSONArray != null) {
				for(int j = 0; j < personInfoJSONArray.length(); j++) {
					JSONObject personInfo = personInfoJSONArray.optJSONObject(j);
					personInfoMap.put(personInfo.optInt("personid"), personInfo);
				}
			}
		}
	}
	
//	private void getAllSearchKeyword() {
//		
//		try {
//			File file = new File("/tmp/all_searchkeyword.txt");
//			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//			for(int key : showDetailMap.keySet()) {
//				JSONObject showDetail = showDetailMap.get(key);
//				String showkeyword = showDetail.optString("showkeyword");
//				if(showkeyword.length() > 0) {
//					writer.write(showDetail.optInt("pk_odshow") + "\t" + showDetail.optString("showname") + "\t" + showkeyword  + "\n");
//				}
//			}
//
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	
	public static void main(String[] args) {
		MiddTierResourceBuilder builder = new MiddTierResourceBuilder();
		builder.initPersonMap();
		
		
		/*System.out.println(builder.getShowDetailMap().get(111829));
		System.out.println(builder.getTidbitsAndTrailerMap().get(111829));
		System.out.println(builder.getVideoPointMap().get(111829));
		System.out.println(builder.getVideoGuestMap().get(111829));*/
		System.out.println(builder.getPersonInfoMap().get(12456));
		System.out.println(Arrays.toString( MiddleResourceUtil.getPersonMinMaxIDs()));
	}

}
