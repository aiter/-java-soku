package com.youku.top.recomend;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.youku.soku.library.Utils;
import com.youku.top.util.TopWordType.WordType;

public class Converter {

	public static String buildSubjectUrl(int i, int j) {
		StringBuilder url = new StringBuilder(
				"http://10.103.12.72/show.show?q=showid%3A");
		url.append(i);
		url.append("-");
		url.append(j);
		url
				.append("%20state%3Anormal%20copyright_status%3Aauthorized%20blocked%3A0%20allowfilter%3A1&fc=&fd=showweek_all_vv%20showcategory%20blocked%20anime_genre%20showname%20movie_genre%20tv_genre%20variety_genre%20mv_genre%20paid&pn=1&pl=1000&ob=showweek_vv%3Adesc&ft=json&cl=search_out");
		return url.toString();
	}

	private static boolean hasNormalResult(JSONObject json) {
		if (null == json)
			return false;
		if (json == JSONObject.NULL)
			return false;
		if (json.optInt("total") < 1)
			return false;
		if (json.isNull("results"))
			return false;
		return true;
	}

	public static List<Entity> convert(String url) {
		List<Entity> result = new ArrayList<Entity>();
		JSONObject json = Utils.requestGet(url);
		Entity entity = null;
		if (hasNormalResult(json)) {
			org.json.JSONArray jarr;
			try {
				jarr = json.getJSONArray("results");
				if (null != jarr) {
					JSONObject jo = null;
					for (int i = 0; i < jarr.length(); i++) {
						jo = jarr.getJSONObject(i);
						entity = new Entity();
						entity.type = getSubjectType(jo);
						if (entity.type != 1 && entity.type != 2
								&& entity.type != 3)
							continue;
						entity.keyword = TreeBuilder.stopWordsFilter(jo
								.optString("showname"));
						entity.searchTimes = jo.optInt("showweek_all_vv");
						entity.keyword_py = com.youku.search.hanyupinyin.Converter
								.convert(entity.getKeyword(), true);
						entity.valueNum = entity.searchTimes;
						if (1 == jo.optInt("paid")) {
							Constance.fufeis.add(entity);
							continue;
						}
						if (!StringUtils.isBlank(entity.keyword)) {
							result.add(entity);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static List<Entity> convert() {
		List<Entity> result = new ArrayList<Entity>();
		int block = 0;
		int i = 12000;
		int step = 1000;
		List<Entity> entitys = null;
		while (true) {
			if (block > 20) {
				System.out.println("终止id:" + i);
				break;
			}
			entitys = convert(buildSubjectUrl(i, i + step));
			i = i + step;
			if (null == entitys || entitys.size() < 1) {
				block = block + 1;
				continue;
			} else
				block = 0;
			result.addAll(entitys);
		}
		return result;
	}

	private static int getSubjectType(JSONObject json) {
		String cateName = null;
		try {
			cateName = json.getString("showcategory");
			WordType wt = WordType.valueOf(cateName);
			if (null != wt)
				return wt.getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
