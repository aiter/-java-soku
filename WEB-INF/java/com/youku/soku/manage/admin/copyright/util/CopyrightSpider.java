package com.youku.soku.manage.admin.copyright.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.youku.soku.manage.admin.copyright.load.CopyrightConf;
import com.youku.soku.manage.admin.copyright.load.CopyrightResult;

public class CopyrightSpider {
	private static String IMAGE_REGX = "<img src='(.+?)'";
	private static String TITLE_REGX = "class=\"crop\" title='(.+?)'";
	private static String URL_REGX = "</a>.+?href=\"(.+?)\".+?class=\"result-link\"";
	private static String TIME_REGX = "<span class=\"text\">(.+?)</span>";
	private static String KEYWORD_REGX = "";
	// 普通搜索结果信息
	private static String RESULT_AREA_REGX = "<ul id=\"results-list\" class=\"clearfix\">(.+?)</ul>";
	private static String CONTENT_AREA_REGX = "<li class=\"result(.+?)</li>";

	private static int PAGE_COUNT = 1;

	// test
	public static void main(String[] args) {
		CopyrightConf conf = new CopyrightConf();
		conf.setId(1);
		conf.setName("步步惊心");
		conf.setKeyword("步步惊心");
		conf.setTime(1);
		conf.setAuthoritySite("1,19");
		List<CopyrightResult> result = new CopyrightSpider().spider(conf);
		int i = 0;
		for (CopyrightResult crs : result) {
			i++;
			System.out.println(i + " " + crs.getSiteName() + " title:"
					+ crs.getTitle() + " time:" + crs.getTime() + " url:"
					+ crs.getUrl());
		}
	}

	// 根据配置信息搜索全部站点的节目信息
	public static List<CopyrightResult> spider(CopyrightConf conf) {
		List<CopyrightResult> result = new ArrayList<CopyrightResult>();
		// 搜索所有站点的节目版权信息
		Map<Integer, String> siteMap = getSpiderSiteMap();
		for (Integer siteKey : siteMap.keySet()) {
			int key = siteKey;
			// 过滤已授权的站点
			String sites = conf.getAuthoritySite();
			if (null == sites)
				sites = "";
			sites = "," + sites + ",";
			if (sites.contains("," + key + ","))
				continue;
			// 搜索指定站点的各个关键字
			String keyword = conf.getKeyword();
			if (null == keyword || keyword.isEmpty())
				continue;
			String[] keywords = keyword.split(",");
			for (String word : keywords) {
				int pid = conf.getId();
				// 默认搜索PAGE_COUNT页
				for (int i = 1; i < PAGE_COUNT + 1; i++) {
					String url = getUrlByConf(word, key, i, conf.getTime());
					List<CopyrightResult> siteWordList = spider(url,
							"gb2312", pid, word, key);
					result.addAll(siteWordList);
				}
			}
		}

		return result;
	}

	// 根据关键字 搜索指定节目的信息
	public static List<CopyrightResult> spider(String url, String charset, int pid,
			String key, int site) {
		List<CopyrightResult> result = new ArrayList<CopyrightResult>();
		String pageContent = getPageContent(url, charset);

		String resultContent = null;
		String content = null;
		// 获取站点的节目搜索结果信息
		resultContent = getContentByRegx(pageContent, RESULT_AREA_REGX);
		if (null == resultContent)
			return result;
		// 获取站点节目搜索的节目信息
		Pattern p = Pattern.compile(CONTENT_AREA_REGX);
		Matcher m = p.matcher(resultContent);
		while (m.find()) {
			content = m.group(1);
			CopyrightResult cr = generateCopyRight(content, pid, key, site);
			result.add(cr);
		}
		return result;
	}

	public static String getPageContent(String urlStr, String charset) {
		StringBuffer sb = new StringBuffer();
		InputStream is = null;
		BufferedReader in = null;
		try {
			URL url = new URL(urlStr);
			is = url.openStream();
			in = new BufferedReader(new InputStreamReader(is, charset));
			String line;
			while ((line = in.readLine()) != null)
				sb.append(line);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != in)
					in.close();
				if (null != is)
					is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static CopyrightResult generateCopyRight(String content, int pid,
			String key, int site) {
		CopyrightResult result = new CopyrightResult();
		String image = getContentByRegx(content, IMAGE_REGX);
		String title = getContentByRegx(content, TITLE_REGX);
		String time = getContentByRegx(content, TIME_REGX);
		String url = getContentByRegx(content, URL_REGX);

		result.setPid(pid);
		result.setKeyword(key);
		result.setImage(image);
		result.setTitle(title);
		result.setTime(time);
		result.setUrl(url);
		result.setSite(site);
		return result;
	}

	public static String getContentByRegx(String content, String regx) {
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(content);
		while (m.find())
			return m.group(1);
		return null;
	}

	public static String getUrlByConf(String keyWord, Integer keySite, int page,
			int time) {
		String url = "http://video.baidu.com/v?word=";
		Map<Integer, String> siteMap = getSpiderSiteMap();
		if (siteMap.size() == 0)
			return url;
		if (null == keyWord || keyWord.isEmpty())
			return url;
		// 获取word 由keyword和site组成
		String word = null;
		String siteUrl = siteMap.get(keySite);
		try {
			word = URLEncoder.encode(keyWord, "gb2312");
			if (null != siteUrl && !siteUrl.isEmpty())
				word += "+" + URLEncoder.encode(siteUrl, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url += word;
		if (time > 0)
			url += "&du=" + time;
		if (page > 0)
			url += "&pn=" + 10 * page;
		return url;
	}

	public static Map<Integer, String> getSpiderSiteMap() {

		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();

		siteMap.put(1, "site:tudou.com");
		siteMap.put(2, "site:qiyi.com");
		siteMap.put(3, "site:sohu.com");
		siteMap.put(4, "site:qq.com");
		siteMap.put(5, "site:sina.com.cn");
		siteMap.put(6, "site:letv.com");
		siteMap.put(7, "site:ku6.com");
		siteMap.put(8, "site:56.com");
		siteMap.put(9, "site:pptv.com");
		siteMap.put(10, "site:ppstream.com");
		siteMap.put(11, "site:xunlei.com");
		siteMap.put(12, "site:baofeng.com");
		siteMap.put(13, "site:funshion.com");
		siteMap.put(14, "site:uusee.com");
		siteMap.put(15, "site:pipi.cn");
		siteMap.put(16, "site:baomihua.com");
		siteMap.put(17, "site:cntv.cn");
		siteMap.put(18, "site:baidu.com");
		siteMap.put(19, "site:joy.cn");

		return siteMap;
	}

	public static Map<Integer, String> getSpiderSiteNameMap() {

		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();

		siteMap.put(1, "土豆网");
		siteMap.put(2, "奇艺网");
		siteMap.put(3, "搜狐网");
		siteMap.put(4, "腾讯网");
		siteMap.put(5, "新浪网");
		siteMap.put(6, "乐视网");
		siteMap.put(7, "酷6网");
		siteMap.put(8, "我乐网");
		siteMap.put(9, "PPTV");
		siteMap.put(10, "PPS");
		siteMap.put(11, "迅雷看看");
		siteMap.put(12, "暴风影音");
		siteMap.put(13, "风行网");
		siteMap.put(14, "悠视网");
		siteMap.put(15, "皮皮网");
		siteMap.put(16, "爆米花");
		siteMap.put(17, "CNTV");
		siteMap.put(18, "百度贴吧");
		siteMap.put(19, "激动网");

		return siteMap;
	}

	// the regx init : get set method

	public String getIMAGE_REGX() {
		return IMAGE_REGX;
	}

	public void setIMAGE_REGX(String iMAGE_REGX) {
		IMAGE_REGX = iMAGE_REGX;
	}

	public String getTITLE_REGX() {
		return TITLE_REGX;
	}

	public void setTITLE_REGX(String tITLE_REGX) {
		TITLE_REGX = tITLE_REGX;
	}

	public String getURL_REGX() {
		return URL_REGX;
	}

	public void setURL_REGX(String uRL_REGX) {
		URL_REGX = uRL_REGX;
	}

	public String getTIME_REGX() {
		return TIME_REGX;
	}

	public void setTIME_REGX(String tIME_REGX) {
		TIME_REGX = tIME_REGX;
	}

	public String getKEYWORD_REGX() {
		return KEYWORD_REGX;
	}

	public void setKEYWORD_REGX(String kEYWORD_REGX) {
		KEYWORD_REGX = kEYWORD_REGX;
	}

	public String getRESULT_AREA_REGX() {
		return RESULT_AREA_REGX;
	}

	public void setRESULT_AREA_REGX(String rESULT_AREA_REGX) {
		RESULT_AREA_REGX = rESULT_AREA_REGX;
	}

	public String getCONTENT_AREA_REGX() {
		return CONTENT_AREA_REGX;
	}

	public void setCONTENT_AREA_REGX(String cONTENT_AREA_REGX) {
		CONTENT_AREA_REGX = cONTENT_AREA_REGX;
	}
}
