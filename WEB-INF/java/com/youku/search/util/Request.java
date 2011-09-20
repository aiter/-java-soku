/**
 * 
 */
package com.youku.search.util;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;


/**
 * @author william
 *
 */
public class Request{
	public static String requestGet(String url){
		return requestGet(url,15000);
	}
	/**
	 * 短链接
	 */
	public static String requestGet(String url,int timeout){
		HttpClient client = new HttpClient();
		String result = null;
		GetMethod method = new GetMethod(url);
		
//		method.setRequestHeader("Accept","*/*");
//		method.setRequestHeader("Connection","close");
//		method.setRequestHeader("Accept-Language","zh-cn");
//		method.setRequestHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//		method.setRequestHeader("Cache-Control","no-cache");
		method.getParams().setSoTimeout(timeout);
	//	method.setFollowRedirects(true);
	//	method.setRequestHeader("Cookie",cookie);
		
		
//		method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		try {
			client.executeMethod(method);
			result = method.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			method.releaseConnection();
		}
		return result;
	}
	
	
}
