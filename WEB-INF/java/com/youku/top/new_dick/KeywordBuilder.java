package com.youku.top.new_dick;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import com.youku.search.console.util.Wget;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.DickResult;
import com.youku.top.UrlUtils;
import com.youku.top.data_source.NewSokuLibraryDataSource;
import com.youku.top.data_source.SearchStatDataSource;
import com.youku.top.paihangbang.ContentParser;
import com.youku.top.util.MidProgrammeCate;
import com.youku.top.util.TopWordType.WordType;

public class KeywordBuilder {

	static Logger logger = Logger.getLogger(KeywordBuilder.class);
	public static JdbcTemplate searchStatDataSource = new JdbcTemplate(
			SearchStatDataSource.INSTANCE);
	public static JdbcTemplate newSokuLibraryDataSource = new JdbcTemplate(
			NewSokuLibraryDataSource.INSTANCE);

	private static JSONObject requestAndJson(String url) {
		try {
			byte[] bytes = Wget.get(url);
			return new JSONObject(new String(bytes));
		} catch (Exception e) {
			logger.error("url:" + url, e);
		}
		return null;
	}

	public static Map<String, Collection<DickResult>> getprogrammeSearchNamesFromMidd(
			Collection<DickResult> persons) {
		Map<String, Collection<DickResult>> dickmap = new HashMap<String, Collection<DickResult>>();
		Collection<DickResult> dicklist = null;
		JSONObject jobject = new JSONObject();
		int pageblock = 0;
		int start = 10000;
		int step = 1000;
		while (null != jobject && pageblock < 3) {
			String url = "http://10.103.12.71/show.show?q=showid%3A"
					+ (start + 1)
					+ "-"
					+ (start + step)
					+ "%20state%3Anormal&fc=&fd=showname%20series%20showkeyword%20showalias%20pk_odshow%20person%20releasedate%20copyright_status%20seriesid%20showcategory&pn=1&pl=1000&ob=showweek_vv%3Adesc&ft=json&cl=search_out";
			jobject = requestAndJson(url);
			int block = 0;
			while (null == jobject && block < 2) {
				block++;
				jobject = requestAndJson(url);
			}

			start = start + step;

			if (null == jobject) {
				pageblock += 1;
				logger.error("No JSON return , " + url);
				continue;
			}
			if (null != jobject) {
				logger.info(url);
				JSONArray jarr = jobject.optJSONArray("results");
				if (null != jarr && jarr.length() > 0) {
					pageblock = 0;
					JSONObject json = null;
					MidProgrammeCate wt = null;
					for (int i = 0; i < jarr.length(); i++) {
						json = jarr.optJSONObject(i);
						if (null != json) {
							String category = json.optString("showcategory");
							if (StringUtils.isBlank(category)) {
								logger.info("NULL category return , "
										+ json.toString());
								continue;
							}
							try {
								wt = MidProgrammeCate.valueOf(category.trim());
							} catch (Exception e) {
								wt = null;
							}
							if (null == wt) {
								logger.info("category:" + category
										+ " , json:  " + json.toString());
								continue;
							}
							dicklist = dickmap.get(wt.getValue());
							if (null == dicklist)
								dickmap.put(wt.getValue(),
										new HashSet<DickResult>());
							dicklist = dickmap.get(wt.getValue());

							int id = json.optInt("pk_odshow");
							int seriesid = json.optInt("seriesid");
							String name = json.optString("showname");
							String releasedate = json.optString("releasedate");
							String series_name = json.optString("series");
							JSONArray showalias = json
									.optJSONArray("showalias");
							JSONArray showkeyword = json
									.optJSONArray("showkeyword");
							JSONArray person = json.optJSONArray("person");
							String copyright_status = json
									.optString("copyright_status");
							boolean has_right = false;
							if (!StringUtils.isBlank(copyright_status)
									&& "authorized"
											.equalsIgnoreCase(copyright_status
													.trim().toLowerCase()))
								has_right = true;
							if (StringUtils.isBlank(releasedate))
								releasedate = "";

							// 非综艺时不需要系列id
							if (!wt.getValue().equalsIgnoreCase(
									MidProgrammeCate.综艺.getValue())) {
								seriesid = 0;
							}

							dicklist.add(new DickResult(name, has_right,
									releasedate, seriesid, id));

							// 综艺添加系列
							if (wt.getValue().equalsIgnoreCase(
									MidProgrammeCate.综艺.getValue())) {
								if (!StringUtils.isBlank(series_name))
									dicklist.add(new DickResult(series_name,
											has_right, "", seriesid, 0));
							}

							if (null != showalias) {
								String alias = null;
								for (int j = 0; j < showalias.length(); j++) {
									alias = showalias.optString(j);
									if (!StringUtils.isBlank(alias)) {
										dicklist.add(new DickResult(alias,
												has_right, releasedate,
												seriesid, id));
									}
								}
							}
							if (null != showkeyword) {
								String alias = null;
								for (int j = 0; j < showkeyword.length(); j++) {
									alias = showkeyword.optString(j);
									if (!StringUtils.isBlank(alias)) {
										dicklist.add(new DickResult(alias,
												has_right, releasedate,
												seriesid, id));
									}
								}
							}
							if (null != person) {
								JSONObject pobject = null;
								String p = null;
								String type = null;
								for (int j = 0; j < person.length(); j++) {
									pobject = person.optJSONObject(j);
									if (null != pobject) {
										type = pobject.optString("type");
										if (StringUtils.isBlank(type)
												|| (!type
														.equalsIgnoreCase("director")
														&& !type
																.equalsIgnoreCase("performer") && !type
														.equalsIgnoreCase("host")))
											continue;
										p = pobject.optString("name");
										if (!StringUtils.isBlank(p)
												&& !isNumeric(p.trim())) {
											persons.add(new DickResult(
													p.trim(), false, "9999", 0,
													0));
										}
									}
								}
							}
						}
					}

				} else
					pageblock += 1;

			}

		}
		return dickmap;
	}

	private static boolean hasEpisode(int fk_programme_id) {
		String sql = "select count(*) from programme_site where fk_programme_id=? and blocked=0 and episode_collected>0";
		try {
			int c = newSokuLibraryDataSource.queryForInt(sql,
					new Object[] { fk_programme_id },
					new int[] { Types.INTEGER });
			if (c > 0)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	private static Map<Integer, List<String>> getSeries(int fk_programme_id) {
		Map<Integer, List<String>> result = new HashMap<Integer, List<String>>();
		String sql = "select series.name as name,series.alias as alias,series.id as id from series,series_subject where series.id = series_subject.fk_series_id and  series_subject.programme_id = ? ";
		try {
			List list = newSokuLibraryDataSource.queryForList(sql,
					new Object[] { fk_programme_id },
					new int[] { Types.INTEGER });
			Iterator iterator = list.iterator();
			List<String> keys = null;
			Map map = null;
			int id = 0;
			String name = null;
			String alias = null;
			while (iterator.hasNext()) {
				map = (Map) iterator.next();
				name = String.valueOf(map.get("name"));
				alias = String.valueOf(map.get("alias"));
				id = DataFormat.parseInt(map.get("id"));
				keys = new ArrayList<String>();
				keys.add(name);
				if (!StringUtils.isBlank(alias)
						&& !alias.equalsIgnoreCase("null")) {
					keys.addAll(Utils.parseStr2Set(alias, "\\|"));
				}
				result.put(id, keys);
			}
			return result;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 只找有剧集的节目
	 * 
	 * @param cate
	 * @return
	 */
	public static Collection<DickResult> getprogrammeSearchNames(int cate,
			Collection<DickResult> persons) {
		return getprogrammeSearchNames(true, cate, persons);
	}

	private static JSONObject getMidd(String url) {
		JSONObject jsonObject = null;
		int blocked = 0;
		while (null == jsonObject && blocked < 3) {
			blocked++;
			jsonObject = Utils.requestGet(url);
			if (null == jsonObject)
				UrlUtils.sleep();
		}
		return jsonObject;
	}

	public static Collection<DickResult> getprogrammeSearchNames(
			boolean hasEpisodes, int cate, Collection<DickResult> persons) {
		Set<DickResult> drset = new HashSet<DickResult>();
		String sql = "select id,name,alias,source,content_id from programme where cate=? and state='normal' and blocked=0 ";
		try {
			List list = newSokuLibraryDataSource.queryForList(sql,
					new Object[] { cate }, new int[] { Types.INTEGER });
			Iterator iterator = list.iterator();
			Map map = null;
			int id = 0;
			String name = null;
			String alias = null;
			String source = null;
			List<String> keys = null;
			String url = null;
			JSONObject jsonObject = null;
			JSONArray performer = null;
			String releaseyear = null;
			JSONArray host = null;
			JSONArray director = null;
			JSONObject midd = null;
			JSONArray jarr = null;
			JSONObject series = null;
			int series_id;
			String series_name = null;
			JSONObject pobject = null;
			while (iterator.hasNext()) {

				map = (Map) iterator.next();
				id = DataFormat.parseInt(map.get("id"));
				url = ContentParser.buildMiddUrl(id, cate);
				// 是否版权
				boolean hasRight = false;
				source = String.valueOf(map.get("source"));
				if (source.trim().startsWith("1"))
					hasRight = true;

				jsonObject = getMidd(url);

				if (null != jsonObject) {
					jarr = jsonObject.optJSONArray("array");
					if (null != jarr) {
						JSONObject json = jarr.optJSONObject(0);
						if (null != json) {
							name = null;
							alias = null;
							performer = null;
							releaseyear = null;
							host = null;
							director = null;
							series_id = 0;
							series_name = null;
							JSONObject programme = json
									.optJSONObject("programme");
							boolean has_url = false;
							if (null != programme) {
								// 过滤没有url的
								String playdefault = programme.optString("url");
								if (!StringUtils.isBlank(playdefault)
										&& !playdefault.trim().toLowerCase()
												.equalsIgnoreCase("null")) {
									has_url = true;
								}
								name = programme.optString("name");
								alias = programme.optString("alias");
								performer = programme.optJSONArray("performer");
								releaseyear = programme
										.optString("releaseyear");

							}
							if (hasEpisodes && !has_url) {
								logger.info("没有剧集结果,programme id:" + id
										+ " , name:" + name + ",time:"
										+ new Date());
								continue;
							}

							midd = json.optJSONObject("midd");
							if (null != midd) {
								host = midd.optJSONArray("host");
								director = midd.optJSONArray("director");
							}

							if (cate == WordType.综艺.getValue()) {
								series = json.optJSONObject("series");
								if (null != series) {
									series_id = series.optInt("id");
									series_name = series.optString("name");
								}
							}

							keys = new ArrayList<String>();
							if (!StringUtils.isBlank(name))
								keys.add(name);
							if (!StringUtils.isBlank(alias)
									&& !alias.equalsIgnoreCase("null")) {
								keys.addAll(Utils.parseStr2Set(alias, "\\|"));
							}
							if (!StringUtils.isBlank(series_name)) {
								drset.add(new DickResult(series_name, hasRight,
										"", series_id, 0));
							}
							if (StringUtils.isBlank(releaseyear)
									|| releaseyear.trim().equalsIgnoreCase(
											"0000"))
								releaseyear = "";

							for (String n : keys) {
								if (StringUtils.isBlank(n))
									continue;
								drset.add(new DickResult(n, hasRight,
										releaseyear, series_id, id));
							}

							keys = new ArrayList<String>();

							if (null != performer) {
								for (int i = 0; i < performer.length(); i++) {
									pobject = performer.optJSONObject(i);
									if (null != pobject)
										keys.add(pobject.optString("name"));
								}
							}

							if (null != host) {
								for (int i = 0; i < host.length(); i++) {
									pobject = host.optJSONObject(i);
									if (null != pobject)
										keys.add(pobject.optString("name"));
								}
							}

							if (null != director) {
								for (int i = 0; i < director.length(); i++) {
									pobject = director.optJSONObject(i);
									if (null != pobject)
										keys.add(pobject.optString("name"));
								}
							}

							for (String n : keys) {
								if (StringUtils.isBlank(n))
									continue;
								persons.add(new DickResult(n, false, "9999"));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("sql:" + sql + ",cate:" + cate, e);
		}
		logger.info("programme:" + drset.size() + ",cate:" + cate
				+ ",hasEpisodes:" + hasEpisodes);
		return drset;
	}

	public static void sleep() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// public static int getQueryCont(String keyword, String table) {
	// try {
	// return searchStatDataSource.queryForInt("select sum(query_count) from "
	// + table + " where keyword=? and query_type='video' and source='youku'",
	// new Object[] { keyword}, new int[] { Types.VARCHAR });
	// } catch (Exception e) {
	// logger.debug(keyword,e);
	// }
	// return 0;
	// }

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))
					&& !isCNNumeric(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isCNNumeric(char c) {
		return charset.contains(c);
	}

	private static final Set<Character> charset = new HashSet<Character>();

	static {
		charset.add('零');
		charset.add('一');
		charset.add('二');
		charset.add('三');
		charset.add('四');
		charset.add('五');
		charset.add('六');
		charset.add('七');
		charset.add('八');
		charset.add('九');
		charset.add('十');
	}

	public static void main(String[] args) {
		System.out.println(isNumeric("十八"));
	}
}
