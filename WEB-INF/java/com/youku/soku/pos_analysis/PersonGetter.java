package com.youku.soku.pos_analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.soku.library.Utils;
import com.youku.top.UrlUtils;
import com.youku.top.paihangbang.entity.TypeWord;
import com.youku.top.util.TopWordType.WordType;

public class PersonGetter {
	private static Log logger = LogFactory.getLog(PersonGetter.class);

	private static PersonGetter instance = null;

	public static synchronized PersonGetter getInstance() {
		if (null != instance)
			return instance;
		else {
			instance = new PersonGetter();
			;
			return instance;
		}
	}

	public Set<String> getPersonData() {
		Set<String> persons = new HashSet<String>();
		int maxPersonId = getMaxPersonId();
		while (maxPersonId < 1) {
			getMaxPersonId();
			if (maxPersonId < 1)
				sleepLong();
		}
		logger.info("取得最大人物id,id:" + maxPersonId);
		if (maxPersonId > 0) {
			int start = 10001;
			int step = 500;
			String url = "";
			while (start <= maxPersonId) {
				url = UrlUtils.buildPersonUrl(start, start + step);
				logger.info("取得人物数据,url:" + url);
				start += step;
				parsePersonName(url, persons);
			}
		}
		return persons;
	}

	public void parsePersonName(String url, Set<String> persons) {
		int block = 0;
		JSONObject json = null;
		while (null == json && block < 2) {
			block++;
			json = Utils.requestGet(url);
			if (null == json)
				UrlUtils.sleep();
		}
		if (null != json) {
			org.json.JSONArray jarr = json.optJSONArray("results");
			if (null != jarr) {
				JSONObject jobject = null;
				String name = null;
				for (int i = 0; i < jarr.length(); i++) {
					jobject = jarr.optJSONObject(i);
					if (null != jobject) {
						name = jobject.optString("personname");
						if (!StringUtils.isBlank(name)) {
							persons.add(name.trim());
						}
					}
				}
			}
		}
	}

	public void getPersonTypeData(Map<String, List<TypeWord>> persons) {
		int maxPersonId = getMaxPersonId();
		while (maxPersonId < 1) {
			getMaxPersonId();
			if (maxPersonId < 1)
				sleepLong();
		}
		logger.info("取得最大人物id,id:" + maxPersonId);
		if (maxPersonId > 0) {
			int start = 10001;
			int step = 500;
			String url = "";
			while (start <= maxPersonId) {
				url = UrlUtils.buildPersonUrl(start, start + step);
				logger.info("取得人物数据,url:" + url);
				start += step;
				parsePersonNameAndPic(url, persons);
			}
		}
	}

	public void parsePersonNameAndPic(String url,
			Map<String, List<TypeWord>> persons) {
		int block = 0;
		JSONObject json = null;
		while (null == json && block < 2) {
			block++;
			json = Utils.requestGet(url);
			if (null == json)
				UrlUtils.sleep();
		}
		if (null != json) {
			org.json.JSONArray jarr = json.optJSONArray("results");
			if (null != jarr) {
				JSONObject jobject = null;
				String name = null;
				TypeWord tw = null;
				for (int i = 0; i < jarr.length(); i++) {
					jobject = jarr.optJSONObject(i);
					if (null != jobject) {
						name = jobject.optString("personname");
						if (!StringUtils.isBlank(name)) {
							name = name.trim();
							String pic = jobject.optString("thumburl");
							if (!StringUtils.isBlank(pic)) {
								String l = StringUtils.substringAfterLast(pic,
										".ykimg.com/");
								if (!StringUtils.isBlank(l)) {
									pic = "1" + l.substring(1);
								}
								if (null == persons.get(name)) {
									persons
											.put(name,
													new ArrayList<TypeWord>());
								}
								tw = new TypeWord(name, WordType.人物.getValue(),
										pic);
								if (!persons.get(name).contains(tw))
									persons.get(name).add(tw);
							}
						}
					}
				}
			}
		}
	}

	public int getMaxPersonId() {
		String url = UrlUtils.buildPersonUrlGetMax();
		int block = 0;
		JSONObject json = null;
		while (null == json && block < 2) {
			block++;
			json = Utils.requestGet(url);
			if (null == json)
				UrlUtils.sleep();
		}
		if (null != json) {
			org.json.JSONArray jarr = json.optJSONArray("results");
			if (null != jarr) {
				JSONObject jobject = jarr.optJSONObject(0);
				if (null != jobject) {
					return jobject.optInt("personid");
				}
			}
		}
		return 0;
	}

	public static void sleepLong() {
		try {
			Thread.sleep(1000L * 60 * 30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
