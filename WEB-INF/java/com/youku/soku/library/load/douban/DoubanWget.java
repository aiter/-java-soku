/**
 * 
 */
package com.youku.soku.library.load.douban;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.struts2.components.If;
import org.apache.struts2.views.xslt.ArrayAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.util.Wget;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.form.ProgrammeDoubanBo;
import com.youku.soku.library.load.util.Converter;
import com.youku.soku.library.load.util.SyncUtil;

/**
 * 豆瓣接口-通用参数：http://www.douban.com/service/apidoc/reference/common
 * @author liuyunjian
 * 2011-4-2
 */
public class DoubanWget {
	private static final String apiKey = "0c0b0400963132b324210c01c2939819";//豆瓣public key
	private static final String secret = "54502c9f75245c5d";//豆瓣私钥
	
	private static Map<String, String> defaultParamMap = new HashMap<String, String>();
	static{
		defaultParamMap.put("alt", "json");//目前支持的返回值格式包括：atom(默认)；json
		defaultParamMap.put("apikey", apiKey);//豆瓣要求每个 API 的使用者申请一个 API Key，以防止 API 被滥用或恶意使用 
		defaultParamMap.put("start-index", "1");//下标从1开始
		defaultParamMap.put("max-results", "10");//除非特别说明，max-results的最大值为50，参数值超过50返回50个结果
	}
	
	private static final String movie_search_url = "http://api.douban.com/movie/subjects?q=";
	private static final String movie_url = "http://api.douban.com/movie/subject/";
	
	private static boolean needProxy = true;
	
	public static void main(String[] args) {
//		JSONObject resultJson = searchMovies("空中决战 法国版");
		JSONObject resultJson = searchMovies("元素猎人");
		
		if(resultJson==null){
			return;
		}
		
		JSONObject showJsonObject = SyncUtil.buildProgrammeDoubanInfoByID(10328);
//		JSONObject doubanJsonObject = DoubanWget.getMovie(1395091+"");
		try {
			System.out.println(resultJson.toString(4));
			System.out.println(showJsonObject.toString(4));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray items = resultJson.optJSONArray("entry");
		for (int i = 0; i < items.length(); i++) {
			try {
				JSONObject jsonObject = items.getJSONObject(i);
				ProgrammeDoubanBo pdBo = Converter.convertDoubanProgramme(new ProgrammeBo(), jsonObject);
//				String id = getAttrBySearchItem(jsonObject,"id");
				System.out.println(pdBo.doubanId);
				System.out.println(pdBo.doubanName);
//				System.out.println(jsonObject.opt("id"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
//		System.out.println(items.length());
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("12123").append("|");
		
		System.out.println(sBuilder.substring(0,sBuilder.length()-1));
	}
	
	public static JSONArray searchMoviesArray(String title){
		JSONObject resultJson = searchMovies(title);
		if(resultJson!=null){
			JSONArray itmes = resultJson.optJSONArray("entry");
			return itmes;
		}
		return null;
	}
	public static JSONObject searchMovies(String title){
		
		byte[] bytes = null;
	     
	     try {
	    	String url = buildUrl(movie_search_url+title,defaultParamMap);
			bytes = get(url);
//			System.out.println(new String(bytes));
			JSONObject jsonObject = new JSONObject(new String(bytes));
//			System.out.println(jsonObject.toString(4));
			return jsonObject;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public static JSONObject getMovie(String movieId){
		
		byte[] bytes = null;
	     
	     try {
	    	 Map<String, String> tmp = new HashMap<String, String>();
	    	 for (String key : defaultParamMap.keySet()) {
				if("max-results".equals(key)||"start-index".equals(key)){
					continue;
				}
				tmp.put(key, defaultParamMap.get(key));
			}
	    	String url = buildUrl(movie_url+movieId+"?_=_",defaultParamMap);
//	    	System.out.println(url);
			bytes = get(url);
			JSONObject jsonObject = new JSONObject(new String(bytes));
//			System.out.println(jsonObject.toString(4));
			return jsonObject;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public static byte[] get(String url) throws Exception {

		
		Map<String, String> headers = null;
		int readTimeout = 2000;
		if (headers == null || headers.isEmpty()) {
			headers = new HashMap<String, String>();
			headers.put("User-Agent", "soku.com");
			headers.put("Referer", url);
		}

		URL urlObject = new URL(url);

		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
		byte[] buffer = new byte[1024];

		try {
			//豆瓣(访问外网)需要代理
			/**
			 *  适用于10.103.0.0/16网段的代理：
			 *	服务器IP：10.103.8.219
			 *	端口：8181
			 */
			URLConnection connection = null;
			if(needProxy){
				InetSocketAddress addr = new InetSocketAddress("10.103.8.219",8181);
	            // Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr); // Socket 代理
	            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
	            connection = urlObject.openConnection(proxy);
			}else {
				connection = urlObject.openConnection();
			}

			connection.setReadTimeout(readTimeout);

			for (String key : headers.keySet()) {
				connection.addRequestProperty(key, headers.get(key));
			}

			inputStream = connection.getInputStream();
			outputStream = new ByteArrayOutputStream();

			while (true) {
				int length = inputStream.read(buffer);
				if (length < 1) {
					break;
				}

				outputStream.write(buffer, 0, length);
			}

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return outputStream.toByteArray();
	}
	
	public static JSONObject getMovieReviews(String movieId){
		
		byte[] bytes = null;
	     
	     try {
	    	 Map<String, String> tmp = new HashMap<String, String>();
	    	 for (String key : defaultParamMap.keySet()) {
				if("max-results".equals(key)){
					continue;
				}
				tmp.put(key, defaultParamMap.get(key));
			}
	    	 tmp.put("max-results", "5");
	    	String url = buildUrl(movie_url+movieId+"/reviews?_=_",tmp);
//	    	System.out.println(url);
			bytes = get(url);
			JSONObject jsonObject = new JSONObject(new String(bytes));
//			System.out.println(jsonObject.toString(4));
			return jsonObject;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public static JSONObject getReview(String link){
		
		byte[] bytes = null;
	     
	     try {
	    	 Map<String, String> tmp = new HashMap<String, String>();
	    	 for (String key : defaultParamMap.keySet()) {
				if("max-results".equals(key)||"start-index".equals(key)){
					continue;
				}
				tmp.put(key, defaultParamMap.get(key));
			}
	    	String url = buildUrl(link+"?_=_",defaultParamMap);
//	    	System.out.println(url);
			bytes = get(url);
			JSONObject jsonObject = new JSONObject(new String(bytes));
//			System.out.println(jsonObject.toString(4));
			return jsonObject;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	

	/**
	 * @param string
	 * @param defaultParamMap2
	 * @return
	 */
	private static String buildUrl(String url, Map<String, String> defaultParamMap2) {
		if(defaultParamMap2!=null && defaultParamMap2.size()>0){
			StringBuilder sBuilder = new StringBuilder(url);
			for (Iterator keys = defaultParamMap2.keySet().iterator(); keys.hasNext();) {
				String key = (String) keys.next();
				sBuilder.append("&").append(key).append("=").append(defaultParamMap2.get(key));
			}
			
			return sBuilder.toString();
		}else {
		return url;
		}
	}
	
	/**
	 * http://api.douban.com/movie/subject/4724739
	 */
	public static int getDoubanId(String url){
		if(url==null || url.length()==0){
			return 0;
		}
		String tmp = url.substring(url.lastIndexOf("/")+1);
		
		try {
			return Integer.parseInt(tmp);//检验是否是数字
		} catch (Exception e) {
		}
		
		return 0;
	}
	
	
	/**
	 * "title": {"$t": "單身男女"},
	 */
	public static String getAttrBySearchItem(JSONObject jsonObject,String attr){
		if(jsonObject==null || jsonObject.length()==0){
			return "";
		}
		
		if(!JSONUtil.isEmpty(jsonObject, attr)){
			JSONObject idJsonObject = jsonObject.optJSONObject(attr);
			if(!JSONUtil.isEmpty(idJsonObject)){
				return idJsonObject.optString("$t");
			}
		}
		
		return "";
	}
	
	private static String getAttrBySearchName(JSONObject jsonObject,String attr){
		if(jsonObject==null || jsonObject.length()==0){
			return "";
		}
		
		if(!JSONUtil.isEmpty(jsonObject, "@name")){
			String name = jsonObject.optString("@name");
			if(attr.equals(name)){
				return jsonObject.optString("$t");
			}
		}
		
		return "";
	}
	
	public static String getTitle(JSONObject jsonObject){
		if(jsonObject==null || jsonObject.length()==0){
			return "";
		}
		
		String title = getAttrBySearchItem(jsonObject, "title");
		title = title.trim();
		Object attArrayObject = getObject(jsonObject, "db:attribute");
		if(attArrayObject instanceof JSONArray){
			JSONArray array = (JSONArray)attArrayObject;
			JSONObject tmp = null;
			String name = null;
			List<String> names = new ArrayList<String>();
			for (int i = 0; i < array.length(); i++) {
				tmp = array.optJSONObject(i);
				name = getAttrBySearchName(tmp, "aka");
				if(name!=null && name.trim().length()>0){
					names.add(name.trim());
				}
			}
			
			if(names.size()>0 && names.contains(title)){
				names.remove(title);
			}

			names.add(0,title.trim());
			
			StringBuilder sbBuilder = new StringBuilder();
			for (String string : names) {
				sbBuilder.append(string).append("  ");
			}
			
			if(sbBuilder.length()>2){
				return sbBuilder.substring(0, sbBuilder.length()-2);
			}
		}
		
		return title;
	}
	
	public static Object getObject(JSONObject jsonObject,String attr){
		if(jsonObject==null || jsonObject.length()==0){
			return null;
		}
		
		if(!JSONUtil.isEmpty(jsonObject, attr)){
			return jsonObject.opt(attr);
		}
		
		return null;
	}
}
