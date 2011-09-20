package com.youku.soku.newext.util.middleResource;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.sort.json.util.JSONUtil;

public class NovaMiddleResourceUtil {

	public static JSONArray recommendVideos() {

		String query = "state:normal cms_dsid:6705";
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", "4");

		String fdValue = "videoid title seconds createtime thumburl userid username total_vv total_comment total_up total_down";
		params.put("fd", fdValue);

		params.put("ob", "cms_dsseq:asc");
		params.put("cl", "search_result");

		JSONObject resource = NovaMiddleResource.search("video", "cms", query,
				params);

		return JSONUtil.getProperty(resource, JSONArray.class, "results");
	}

	/**
	 * 页面右侧的cms内容
	 */
	public static JSONArray cms(String keyword) {

		if (keyword == null || keyword.isEmpty()) {
			return null;
		}

		String query = "keywords:" + keyword
				+ " type:topic state:published publishtime:{before1month}-";

		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("pn", "1");
		params.put("pl", "2");
		params.put("fd", "title desc cmsurl thumburl publishtime");
		params.put("ob", "publishtime:desc");
		params.put("cl", "search_result");

		JSONObject resource = NovaMiddleResource.search("cms", "cms", query,
				params);

		return JSONUtil.getProperty(resource, JSONArray.class, "results");
	}

	/**
	 * 页面右侧的推荐版权内容
	 * 
	 * @param showids
	 *            逗号分割的showid
	 */
	public static JSONArray recommendShows(String showids) {

		final int COUNT = 5;

		if (showids == null) {
			return null;
		}

		String showid = null;
		String[] showid_array = showids.split(",");
		for (int i = 0; i < showid_array.length; i++) {
			showid = showid_array[i].trim();
			if (!showid.isEmpty()) {
				break;
			}
		}

		if (showid == null || showid.isEmpty()) {
			return null;
		}

		// show信息
		String query = "showid:" + showid;

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("fd",
				"showcategory area person movie_genre tv_genre variety_genre");

		JSONObject result = NovaMiddleResource.search("show", "show", query,
				params);
		JSONArray results = JSONUtil.getProperty(result, JSONArray.class,
				"results");

		if (JSONUtil.isEmpty(results)) {
			return null;
		}

		JSONObject show = results.optJSONObject(0);
		if (JSONUtil.isEmpty(show)) {
			return null;
		}

		String area = JSONUtil.join(show.optJSONArray("area"), ",");
		String showcategory = show.optString("showcategory");

		// 按人取
		TreeSet<String> persons = new TreeSet<String>();
		TreeSet<String> scenarist = new TreeSet<String>();// 编剧
		TreeSet<String> director = new TreeSet<String>();// 导演
		TreeSet<String> performer = new TreeSet<String>();// 演员
		TreeSet<String> host = new TreeSet<String>();// 主持人

		if (show.optJSONArray("person") != null) {
			JSONArray personArray = show.optJSONArray("person");
			for (int i = 0; i < personArray.length(); i++) {
				String person = personArray.optString(i);
				String[] person_info = person.split(":");

				String person_name = person_info[0];
				String person_type = person_info.length < 2 ? ""
						: person_info[1];

				if (person_type.equals("scenarist") && scenarist.size() < 1) {
					scenarist.add(person_name);
				}
				if (person_type.equals("director") && director.size() < 1) {
					director.add(person_name);
				}
				if (person_type.equals("performer") && performer.size() < 3) {
					performer.add(person_name);
				}
				if (person_type.equals("host") && host.size() < 2) {
					host.add(person_name);
				}
			}
		}

		persons.addAll(scenarist);
		persons.addAll(director);
		persons.addAll(performer);
		persons.addAll(host);

		final String fd = "showname performer releasedate show_thumburl streamtypes paid showday_vv";

		JSONArray shows = new JSONArray();// 保存最后的结果
		if (!persons.isEmpty()) {
			query = "-showid:" + showid
					+ " state:normal allowfilter:1 hasvideotype:正片";
			if (area.length() > 0) {
				query += " area:" + area;
			}
			if (showcategory.length() > 0) {
				query += " showcategory:" + showcategory;
			}

			query += " person:";
//					+ StringUtils.collectionToDelimitedString(persons, ",");

			params = new LinkedHashMap<String, String>();
			params.put("fd", fd);
			params.put("pl", "7");
			params.put("ob", "showday_vv:desc");

			result = NovaMiddleResource.search("show", "show", query, params);
			results = JSONUtil.getProperty(result, JSONArray.class, "results");

			if (!JSONUtil.isEmpty(results)) {
				shows = results;
			}
		}

		// 按分类取
		query = "";
		JSONArray movie_genre = show.optJSONArray("movie_genre");
		if (!JSONUtil.isEmpty(movie_genre)) {
			query += " movie_genre:" + JSONUtil.join(movie_genre, ",", 3);
		}

		JSONArray tv_genre = show.optJSONArray("tv_genre");
		if (!JSONUtil.isEmpty(tv_genre)) {
			query += " tv_genre:" + JSONUtil.join(tv_genre, ",", 3);
		}

		JSONArray variety_genre = show.optJSONArray("variety_genre");
		if (!JSONUtil.isEmpty(variety_genre)) {
			query += " variety_genre:" + JSONUtil.join(variety_genre, ",", 3);
		}

		if (query.length() > 0) {
			query += " -showid:" + showid
					+ " state:normal allowfilter:1 hasvideotype:正片";

			if (area.length() > 0) {
				query += " area:" + area;
			}
			if (showcategory.length() > 0) {
				query += " showcategory:" + showcategory;
			}

			params = new LinkedHashMap<String, String>();
			params.put("fd", fd);
			params.put("pl", "6");
			params.put("ob", "showday_vv:desc");

			result = NovaMiddleResource.search("show", "show", query, params);
			results = JSONUtil.getProperty(result, JSONArray.class, "results");

			if (!JSONUtil.isEmpty(results)) {
				if (JSONUtil.isEmpty(shows)) {
					shows = results;
				} else {
					for (int i = 0; i < results.length(); i++) {
						shows.put(results.opt(i));
					}
				}
			}
		}

		// 按人和分类取完后，去重
		shows = removeCopies(shows);

		// 少于COUNT个节目，补足
		if (shows.length() < COUNT) {
			query = "-showid:" + showid
					+ " state:normal allowfilter:1 hasvideotype:正片";

			if (showcategory.length() > 0) {
				query += " showcategory:" + showcategory;
			}

			params = new LinkedHashMap<String, String>();
			params.put("fd", fd);
			params.put("pl", String.valueOf((COUNT - shows.length()) * 2));
			params.put("ob", "showday_vv:desc");

			result = NovaMiddleResource.search("show", "show", query, params);
			results = JSONUtil.getProperty(result, JSONArray.class, "results");

			if (!JSONUtil.isEmpty(results)) {
				if (JSONUtil.isEmpty(shows)) {
					shows = results;
				} else {
					for (int i = 0; i < results.length(); i++) {
						shows.put(results.opt(i));
					}
				}
			}

			// 补足后，去重
			shows = removeCopies(shows);
		}

		if (shows.length() > COUNT) {
			JSONArray shows_COUNT = new JSONArray();
			for (int i = 0; i < shows.length() && i < COUNT; i++) {
				shows_COUNT.put(shows.opt(i));
			}

			shows = shows_COUNT;
		}

		return shows;
	}

	/**
	 * 去重
	 */
	private static JSONArray removeCopies(JSONArray shows) {

		Set<String> showidSet = new HashSet<String>();
		JSONArray shows_new = new JSONArray();

		for (int i = 0; i < shows.length(); i++) {
			JSONObject item = shows.optJSONObject(i);
			if (item == null) {
				continue;
			}

			String pk_odshow = item.optString("pk_odshow");
			if (!showidSet.contains(pk_odshow)) {
				showidSet.add(pk_odshow);
				shows_new.put(item);
			}
		}

		return shows_new;
	}

	public static void main(String[] args) {
		System.out.println(recommendShows("c2c376c0ac2d11df97c0"));
	}

}
