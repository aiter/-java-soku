/**
 * 
 */
package com.youku.soku.library.load.util;

import com.youku.search.util.StringUtil;
import com.youku.soku.config.Config;

/**
 * 工具类,搜索数据
 * @author liuyunjian
 * 2011-3-1
 */
public class SearchUtil {
	

	/**
	 * @param keyword
	 * @param searchNum
	 * @return
	 */
	public static String buildSearchUrl(String keyword,  int searchNum) {
//		StringBuilder url = new StringBuilder("http://"+Config.getYoukuHost()+"/search?dup=0&curpage=1&pagesize=").append(searchNum).append("&keyword=").append(StringUtil.urlEncode(keyword,"utf-8"));
		StringBuilder url = new StringBuilder("http://"+"10.103.88.151"+"/search?dup=0&curpage=1&pagesize=").append(searchNum).append("&keyword=").append(StringUtil.urlEncode(keyword,"utf-8"));
		return url.toString();
	}
}
