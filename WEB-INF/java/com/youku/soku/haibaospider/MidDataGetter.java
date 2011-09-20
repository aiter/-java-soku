package com.youku.soku.haibaospider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;

public class MidDataGetter {

	private static Logger logger = Logger.getLogger(MidDataGetter.class);
	private static MidDataGetter instance = null;

	private MidDataGetter() {
		super();
	}

	public static synchronized MidDataGetter getInstance() {
		if (null == instance)
			instance = new MidDataGetter();
		return instance;
	}

	public Map<String, List<MidVO>> teleplayGetter() {
		Map<String, List<MidVO>> map = new HashMap<String, List<MidVO>>();
		String baseurl_prefix = "http://10.103.12.72/show.show?q=showcategory%3A%E7%94%B5%E8%A7%86%E5%89%A7%20state%3Anormal%20showid%3A";
		String baseurl_suffix = "&fc=&fd=showname%20douban_num%20imdb_num&pl=1000&ob=&ft=json&cl=search_out&pn=1";
		JSONObject json = null;
		int step = 1000;
		int start = 12232;
		while (start < 113000) {
			json = getJsonObjectFromUrl(baseurl_prefix + start + "-"
					+ (start + step) + baseurl_suffix, "utf-8");
			if (null != json) {
				Jsonobject2List(json, map);
			}
			start = start + step;
		}
		return map;
	}

	public Map<String, List<MidVO>> movieGetter() {
		Map<String, List<MidVO>> map = new HashMap<String, List<MidVO>>();
		String baseurl_prefix = "http://10.103.12.72/show.show?q=showcategory%3A%E7%94%B5%E5%BD%B1%20state%3Anormal%20showid%3A";
		String baseurl_suffix = "&fc=&fd=showname%20douban_num%20imdb_num&pl=1000&ob=&ft=json&cl=search_out&pn=1";
		JSONObject json = null;
		int step = 1000;
		int start = 10001;
		while (start <= 114000) {
			json = getJsonObjectFromUrl(baseurl_prefix + start + "-"
					+ (start + step) + baseurl_suffix, "utf-8");
			if (null != json) {
				Jsonobject2List(json, map);
			}
			start = start + step;
		}
		return map;
	}

	public Map<String, List<MidVO>> movieGetter1() {
		Map<String, List<MidVO>> map = new HashMap<String, List<MidVO>>();
		String baseurl_prefix = "http://10.103.12.72/show.show?q=showcategory%3A%E7%94%B5%E5%BD%B1%20state%3Anormal%20showid%3A";
		String baseurl_suffix = "&fc=&fd=showname%20douban_num%20imdb_num&pl=1000&ob=&ft=json&cl=search_out&pn=1";
		JSONObject json = null;
		int step = 1000;
		int start = 10001;
		while (start <= 40001) {
			json = getJsonObjectFromUrl(baseurl_prefix + start + "-"
					+ (start + step) + baseurl_suffix, "utf-8");
			if (null != json) {
				Jsonobject2List(json, map);
			}
			start = start + step;
		}
		return map;
	}

	public Map<String, List<MidVO>> movieGetter2() {
		Map<String, List<MidVO>> map = new HashMap<String, List<MidVO>>();
		String baseurl_prefix = "http://10.103.12.72/show.show?q=showcategory%3A%E7%94%B5%E5%BD%B1%20state%3Anormal%20showid%3A";
		String baseurl_suffix = "&fc=&fd=showname%20douban_num%20imdb_num&pl=1000&ob=&ft=json&cl=search_out&pn=1";
		JSONObject json = null;
		int step = 1000;
		int start = 40001;
		while (start <= 70001) {
			json = getJsonObjectFromUrl(baseurl_prefix + start + "-"
					+ (start + step) + baseurl_suffix, "utf-8");
			if (null != json) {
				Jsonobject2List(json, map);
			}
			start = start + step;
		}
		return map;
	}

	public Map<String, List<MidVO>> movieGetter3() {
		Map<String, List<MidVO>> map = new HashMap<String, List<MidVO>>();
		String baseurl_prefix = "http://10.103.12.72/show.show?q=showcategory%3A%E7%94%B5%E5%BD%B1%20state%3Anormal%20showid%3A";
		String baseurl_suffix = "&fc=&fd=showname%20douban_num%20imdb_num&pl=1000&ob=&ft=json&cl=search_out&pn=1";
		JSONObject json = null;
		int step = 1000;
		int start = 70001;
		while (start <= 114000) {
			json = getJsonObjectFromUrl(baseurl_prefix + start + "-"
					+ (start + step) + baseurl_suffix, "utf-8");
			if (null != json) {
				Jsonobject2List(json, map);
			}
			start = start + step;
		}
		return map;
	}

	public void Jsonobject2List(JSONObject json, Map<String, List<MidVO>> map) {
		JSONArray jarr = json.optJSONArray("results");
		JSONObject jobject = null;
		MidVO mid = null;
		if (null != jarr) {
			for (int i = 0; i < jarr.length(); i++) {
				jobject = jarr.optJSONObject(i);
				if (null != jobject) {
					mid = new MidVO();
					mid.id = jobject.optInt("pk_odshow");
					if (0 == mid.id)
						continue;
					mid.title = jobject.optString("showname");
					if (StringUtils.isBlank(mid.title))
						continue;
					mid.title = mid.title.trim();
					mid.dbid = DataFormat.parseInt(jobject.optString(
							"douban_num").trim());
					mid.imdb = jobject.optString("imdb_num");
					if (StringUtils.isBlank(mid.imdb)
							|| mid.imdb.trim().equalsIgnoreCase("none"))
						mid.imdb = "";
					else
						mid.imdb = mid.imdb.trim();
					if (null == map.get(mid.title)) {
						map.put(mid.title, new ArrayList<MidVO>());
					}
					map.get(mid.title).add(mid);
				}
			}
		}
	}

	public JSONObject getJsonObjectFromUrl(String url, String charset) {
		int block = 0;
		JSONObject json = null;
		String res = SpiderUtils.getInstance()
				.getHttpResponseHtml(url, charset);
		while (null == json && block < 2) {
			block += 1;
			res = SpiderUtils.getInstance().getHttpResponseHtml(url, charset);
			if (null != res) {
				try {
					json = new JSONObject(res);
				} catch (JSONException e) {
					logger.error(url, e);
				}
			}
			if (null == json && block < 2)
				SpiderUtils.sleep();
		}
		return json;
	}
}
