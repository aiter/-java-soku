package com.youku.soku.manage.util;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.library.Utils;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.service.SiteService;

public class ManageUtil {
	public static int parseSite(String url) {
		int siteId = Constants.INTEGRATED_SITE_ID;
		String domain = Utils.parseDomain(url);
		if (!StringUtils.isBlank(domain)) {

			siteId = SiteService.getSiteDomainAndIds().get(domain);
		}
		
		return siteId;
	}
	
	//根据source判断剧集，节目是否是youku的版权剧
	public static boolean isYoukuRight(String source) {
		if(source != null && source.length() > 0 && source.charAt(0) == '1') {
			return true;
		} else {
			return false;
		}
	}
	
	public static String changeSourceOtherSite(String source) {
		if(source != null && source.length() == 3) {
			char[] sourceArray = source.toCharArray();
			sourceArray[1] = '1';
			return new String(sourceArray);
		} else {
			return "010";
		}
	}
	
	public static String changeSourceAutoSearch(String source) {
		if(source != null && source.length() == 3) {
			char[] sourceArray = source.toCharArray();
			sourceArray[2] = '1';
			return new String(sourceArray);
		} else {
			return "001";
		}
	}
	
	public static String string2Json(String s) { 
	    StringBuilder sb = new StringBuilder(s.length()+20); 
	    for (int i=0; i<s.length(); i++) { 
	        char c = s.charAt(i); 
	        switch (c) { 
	        case '\"': 
	            sb.append("\\\""); 
	            break; 	       
	        case '\b': 
	            sb.append("\\b"); 
	            break; 
	        case '\f': 
	            sb.append("\\f"); 
	            break; 
	        case '\n': 
	            sb.append("\\n"); 
	            break; 
	        case '\r': 
	            sb.append("\\r"); 
	            break; 
	        case '\t': 
	            sb.append("\\t"); 
	            break; 
	        default: 
	            sb.append(c); 
	        } 
	    } 
	    return sb.toString(); 
	 } 
	
	public static void main(String[] args) {
		System.out.println(changeSourceAutoSearch("000"));
	}
}
