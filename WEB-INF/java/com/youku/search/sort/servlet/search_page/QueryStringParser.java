package com.youku.search.sort.servlet.search_page;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.config.LegacyPropertiesConfigurationProvider;

import com.youku.search.sort.servlet.util.WebUtils;

public class QueryStringParser {

	static Log logger = LogFactory.getLog(QueryStringParser.class);

	public static LinkedHashMap<String, String> parse(
			HttpServletRequest request, String prefix) {

		prefix = prefix == null ? "" : prefix.trim();

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		// parameter
		for (Enumeration<String> i = request.getParameterNames(); i
				.hasMoreElements();) {
			String key = i.nextElement();
			String value = request.getParameter(key);
			map.put(key, value);
		}

		if (!map.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("解析结果: " + map);
			}
			return map;
		}

		// path
		String uri = request.getRequestURI();
		if (uri == null || (!prefix.isEmpty() && !uri.startsWith(prefix))) {
			if (logger.isDebugEnabled()) {
				logger.debug("解析结果: " + map);
			}
			return map;
		}

		String query = uri;
		if (!prefix.isEmpty()) {
			query = uri.substring(prefix.length());
		}

		if (query.endsWith(".html")) {
			query = query.substring(0, query.lastIndexOf(".html"));
		} else if (query.endsWith(".htm")) {
			query = query.substring(0, query.lastIndexOf(".htm"));
		}

		map = parse(query);

		if (logger.isDebugEnabled()) {
			logger.debug("解析结果: " + map);
		}
		
		return map;
	}

	public static LinkedHashMap<String, String> parse(String query) {
		return parse(query, "UTF-8");
	}

	public static LinkedHashMap<String, String> parse(String query,
			String encoding) {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		if (query != null) {
			query = query.replaceAll("/+", "_");
			Map<String, String> splitMap = split(query);
			for (Entry<String, String> entry : splitMap.entrySet()) {
				String key = WebUtils.urlDecode(entry.getKey());
				String value = WebUtils.urlDecode(entry.getValue());

				map.put(key, value);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("query: " + query + "; map: " + map);
		}

		return map;
	}

	public static LinkedHashMap<String, String> split(String query) {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (query == null) {
			return map;
		}

		List<String> list = new ArrayList<String>();
		while (query.startsWith("_")) {
			query = query.substring(1);
		}

		String[] tokens = query.split("_");
		//TODO 特殊处理第一个参数，如果有第二个参数，必须是制定列表中的@ParameterYouku
		final int len = tokens.length;
		if(len<=2 || !WebParamHelper.ParameterYouku.q.name().equals(tokens[0])){
			for (String token : tokens) {
				list.add(token);
			}
		}else {
			int qIdx = -1;
			int qEnd = -1;
			for (int i = 0; i < len; i++) {
				if(qIdx == -1 && WebParamHelper.ParameterYouku.q.name().equals(tokens[i])){
					qIdx = i+1;
					continue;
				}
				
				if(qEnd == -1){
					try {
						WebParamHelper.ParameterYouku.valueOf(tokens[i]);//如果不在参数列表中，就处理异常
						qEnd = i;
						String keyword = "";
						keyword = buildKeyword(tokens,qIdx,qEnd-qIdx);
						list.add(WebParamHelper.ParameterYouku.q.name());
						list.add(keyword);
						
						list.add(tokens[i]);
						continue;
					} catch (Exception e) {
						if(i==(len-1)){
							String keyword = "";
							keyword = buildKeyword(tokens,qIdx,len-qIdx);
							list.add(WebParamHelper.ParameterYouku.q.name());
							list.add(keyword);
						}else {
							continue;
						}
					}
				}
				
				if(qIdx>=0 && qEnd>=0){
					list.add(tokens[i]);
				}
			}
		}

		if (list.size() % 2 == 1) {
			list.add("");
		}

		for (int i = 0; i < list.size(); i = i + 2) {
			map.put(list.get(i), list.get(i + 1));
		}

		return map;
	}

	/**
	 * @param tokens
	 * @param qIdx
	 * @param length
	 * @return
	 */
	private static String buildKeyword(String[] tokens, int qIdx, int length) {
		String[] infos = new String[length];
		System.arraycopy(tokens, qIdx, infos, 0, length);
		StringBuilder sbBuilder = new StringBuilder();
		for (String info : infos) {
			sbBuilder.append(info).append("_");
		}
		sbBuilder.deleteCharAt(sbBuilder.length()-1);
		return sbBuilder.toString();
	}

	public static void main(String[] args) throws Exception {
		String query;
		Map<String, String> map;

		query = "q_%E5%90%AC%E5%93%A6__a_te/st_1_a_";
//		query = "q_lm810308_2009@taobao";
//		query = "q_lm810308_2009@taobao/aaa_orderby__a";
//		query = "q_让子弹飞_source_top";
		query = "q_%E6%BC%82%E4%BA%AE/orderby_1///lengthtype_1%2F_a__b_c_m___x";
		System.out.println(WebUtils.urlDecode(query));
		map = parse(query);
		System.out.println(map);

		System.out.println(URLEncoder.encode("http://&?a.html", "UTF-8"));
		System.out.println(WebParamHelper.ParameterYouku.valueOf("q"));
//		System.out.println("test__1".split("_").length);
	}
}
