package com.youku.soku.manage.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.Request;
import com.youku.soku.manage.common.Constants;


public class SiteService {
	
	//static JdbcTemplate template = new JdbcTemplate(SimpleDataSource.INSTANCE);
	private static Logger log = Logger.getLogger(SiteService.class);
	
	private static Map<Integer, String> siteMap = null;
	private static Map<Integer, String> getSiteMap() {
		if(null==siteMap || siteMap.size() == 0) {
			if(null == siteMap)
				siteMap = new LinkedHashMap<Integer, String>();
			try {
				String result = Request.requestGet("http://10.12.0.23:8080/spider/getCommonSpiderSites.jsp", 500);
				JSONArray jsArr = new JSONArray(result);
				for(int i = 0; i < jsArr.length(); i++) {
					JSONObject jsObj = jsArr.getJSONObject(i);
					siteMap.put(jsObj.optInt("id"), jsObj.optString("name"));
				}
			} catch (JSONException e) {
				log.info(e.getMessage(), e);
			}
		}
		return siteMap;
	}
	
	public static String getSiteName(int id) {
			
		if(id == Constants.INTEGRATED_SITE_ID) {
			return Constants.INTEGRATED_SITE;
		}
		
		if(id == 0) {
			return Constants.INTEGRATED_SITE;
		}
		
		if(getEpisodeDetailMap().get(id) != null) {
			return getEpisodeDetailMap().get(id);
		} else {
			return "";
		}
		
	}
	
	public static Map<Integer, String> getSitesMap() {
		
		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();
		
		siteMap.put(Constants.INTEGRATED_SITE_ID, Constants.INTEGRATED_SITE);
	
		for(Integer id : getSiteMap().keySet()) {
			siteMap.put(id, getSiteMap().get(id));
		}
		
		return siteMap;
	}
	
	public static Map<Integer, String> getNoIntegrateSitesMap() {
		/*Connection conn = null;
		List<Site> list;
		try {
			conn = Torque.getConnection("so");
			list = SitePeer.doSelect(new Criteria());
		} catch(TorqueException e) {
			throw e;
		} finally {
			Torque.closeConnection(conn);
		}*/
		
		/*String sql = "SELECT id, name FROM site ORDER BY id DESC";
		List rows = template.queryForList(sql);*/
		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();
		
	
		for(Integer id : getSiteMap().keySet()) {
			siteMap.put(id, getSiteMap().get(id));
		}
		
		return siteMap;
	}
	
	public static Map<Integer, String> getSitesFilterMap() {

		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();
		siteMap.put(-1, "所有");
		siteMap.put(Constants.INTEGRATED_SITE_ID, Constants.INTEGRATED_SITE);
	
		List<String> topSites = Arrays.asList(Constants.TOP_SITES);
		for(Integer id : getSiteMap().keySet()) {
			
			if(topSites.contains(getSiteMap().get(id))){
				siteMap.put(id, getSiteMap().get(id));
			}			
		}
		return siteMap;
	}
	
	public static Map<Integer, String> getEpisodeDetailMap() {

		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();
	
		siteMap.put(14, "优酷网");
		siteMap.put(1, "土豆网");
		siteMap.put(2, "56网");
		siteMap.put(3, "新浪网");
		siteMap.put(6, "搜狐");
		siteMap.put(15, "CNTV");
		siteMap.put(9, "激动网");
		siteMap.put(17, "乐视网");
		siteMap.put(19, "奇艺网");
		siteMap.put(27, "QQ");
		siteMap.put(31, "PPTV");
		
		return siteMap;
	}
	
	public static Map<Integer, String> getEpisodeSpiderMap() {

		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();
	
		siteMap.put(1, "土豆网");
		siteMap.put(2, "56网");
		siteMap.put(3, "新浪网");
		siteMap.put(6, "搜狐");
		siteMap.put(15, "CNTV");
		siteMap.put(9, "激动网");
		siteMap.put(17, "乐视网");
		siteMap.put(19, "奇艺网");
		siteMap.put(27, "QQ");
		siteMap.put(31, "PPTV");
		
		return siteMap;
	}
	
	public static Map<String, Integer> getSiteDomainAndIds() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			String res = Request
					.requestGet("http://10.12.0.23:8080/spider/getDomain.jsp");
			if (StringUtils.isBlank(res))
				return map;
			JSONArray jsonarr = new JSONArray(res);
			JSONObject json = null;
			int site_id = 0;
			String url = null;
			for (int i = 0; i < jsonarr.length(); i++) {
				json = jsonarr.optJSONObject(i);
				if (null == json)
					continue;
				site_id = json.optInt("site_id");
				url = json.optString("url");
				if (0 == site_id || StringUtils.isBlank(url))
					continue;
				map.put(url, site_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
