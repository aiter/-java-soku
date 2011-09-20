package com.youku.top.paihangbang;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.soku.top.mapping.TopWords;
import com.youku.top.UrlUtils;
import com.youku.top.paihangbang.entity.TopWordsEntity;
import com.youku.top.util.TopVideoArea;
import com.youku.top.util.TopVideoType;
import com.youku.top.util.VideoType;

public class ContentParser {

	static Logger logger = Logger.getLogger(ContentParser.class);
	
	//从云剑的接口取得数据
	public static void parse(TopWordsEntity tw) {
		JSONObject jsonObject = null;
		if (tw.getTopwords().getProgrammeId() > 0) {
			String url = buildMiddUrl(tw.getTopwords().getProgrammeId(), tw
					.getTopwords().getCate());
			int blocked = 0;
			while (null == jsonObject && blocked < 3) {
				blocked++;
				jsonObject = Utils.requestGet(url);
				if (null == jsonObject)
					UrlUtils.sleep();
			}
		}
		boolean hasJson = true;
		if (null != jsonObject) {
			JSONArray jarr = jsonObject.optJSONArray("array");
			if (null != jarr) {
				JSONObject json = jarr.optJSONObject(0);
				if (null != json) {
					JSONObject programme = json.optJSONObject("programme");
					int year = 9999;
					if (null != programme) {
						//过滤没有url的
						String url = programme.optString("url");
						if (StringUtils.isBlank(url)
								|| url.trim().toLowerCase().equalsIgnoreCase(
										"null")) {
							logger.info("===No url," + tw.toString());
							tw.getTopwords().setProgrammeId(0);
							return;
						}
						//过滤没有海报的
						String haibao = programme.optString("vpic");
						if (StringUtils.isBlank(haibao)
								|| haibao.trim().toLowerCase().equalsIgnoreCase(
										"null")||haibao.contains("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E")) {
							logger.info("===No haibao," + tw.toString());
							tw.getTopwords().setProgrammeId(0);
							return;
						}
						
						year = DataFormat.parseInt(programme
								.optString("releaseyear"));
						if (year < 2004)
							year = 9999;
					} else {
						logger.info("===No programme," + tw.toString());
						tw.getTopwords().setProgrammeId(0);
						return;
					}
					JSONObject midd = json.optJSONObject("midd");
					if (null != midd) {
						JSONArray types_arr = midd.optJSONArray("genre");
						JSONArray area_arr = midd.optJSONArray("area");
						if (tw.getTopwords().getCate() == VideoType.movie
								.getValue()) {
							if (null != types_arr) {
								for (int i = 0; i < types_arr.length(); i++) {
									String mid_type_str = types_arr
											.optString(i);
									if (!StringUtils.isBlank(mid_type_str)) {
										String type_str = TopVideoType.midd2movie
												.get(mid_type_str);
										if (!StringUtils.isBlank(type_str)) {
											tw.getTypes().add(
													TopVideoType.MovieType
															.valueOf(type_str)
															.getValue());
										}
									}
								}
							}

							if (null != area_arr) {
								for (int i = 0; i < area_arr.length(); i++) {
									String mid_area_str = area_arr.optString(i);
									if (!StringUtils.isBlank(mid_area_str)) {
										String area_str = TopVideoArea.midd2movie
												.get(mid_area_str);
										if (!StringUtils.isBlank(area_str)) {
											tw.getAreas().add(
													TopVideoArea.MovieArea
															.valueOf(area_str)
															.getValue());
										}
									}
								}
							}
						} else if (tw.getTopwords().getCate() == VideoType.teleplay
								.getValue()) {
							if (null != types_arr) {
								for (int i = 0; i < types_arr.length(); i++) {
									String mid_type_str = types_arr
											.optString(i);
									if (!StringUtils.isBlank(mid_type_str)) {
										String type_str = TopVideoType.midd2teleplay
												.get(mid_type_str);
										if (!StringUtils.isBlank(type_str)) {
											tw.getTypes().add(
													TopVideoType.TeleplayType
															.valueOf(type_str)
															.getValue());
										}
									}
								}
							}

							if (null != area_arr) {
								for (int i = 0; i < area_arr.length(); i++) {
									String mid_area_str = area_arr.optString(i);
									if (!StringUtils.isBlank(mid_area_str)) {
										String area_str = TopVideoArea.midd2teleplay
												.get(mid_area_str);
										if (!StringUtils.isBlank(area_str)) {
											tw.getAreas().add(
													TopVideoArea.TeleplayArea
															.valueOf(area_str)
															.getValue());
										}
									}
								}
							}
						}
					}
					if (tw.getTopwords().getCate() == VideoType.movie
							.getValue()) {
						if (tw.getTypes().size() < 1) {
							tw.getTypes().add(
									TopVideoType.MovieType.其它.getValue());
						}
						if (tw.getAreas().size() < 1) {
							tw.getAreas().add(
									TopVideoArea.MovieArea.其它.getValue());
						}
					} else if (tw.getTopwords().getCate() == VideoType.teleplay
							.getValue()) {
						if (tw.getTypes().size() < 1) {
							tw.getTypes().add(
									TopVideoType.TeleplayType.其它.getValue());
						}
						if (tw.getAreas().size() < 1) {
							tw.getAreas().add(
									TopVideoArea.TeleplayArea.其它.getValue());
						}
					}
					tw.setYear(year);
				} else
					hasJson = false;
			} else
				hasJson = false;
		} else
			hasJson = false;
		if (!hasJson) {
			logger.error("没有取到中间层数据," + tw.toString());
			tw.getTopwords().setProgrammeId(0);
			return;
		}
	}

	public static void setNoneTypeAndArea(TopWordsEntity tw) {
		if (tw.getTopwords().getCate() == VideoType.variety.getValue()) {
			if (tw.getTypes().size() < 1) {
				tw.getTypes().add(0);
			}
			if (tw.getAreas().size() < 1) {
				tw.getAreas().add(0);
			}
		}
	}

	public static String buildMiddUrl(int programmeId, int cate) {
		StringBuilder url = new StringBuilder(
				"http://10.103.8.217/top/search?programmeId=");
		url.append(programmeId);
		url.append("&cate=");
		url.append(cate);
		return url.toString();
	}
	
	public static void main(String[] args){
		TopWordsEntity tw = new TopWordsEntity();
		tw.setTopwords(new TopWords());
		tw.getTopwords().setCate(2);
		tw.getTopwords().setProgrammeId(64302);
		parse(tw);
	}
}
