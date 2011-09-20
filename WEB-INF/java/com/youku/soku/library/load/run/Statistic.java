/**
 * 
 */
package com.youku.soku.library.load.run;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.servlet.search_page.util.NovaMiddleResource;
import com.youku.soku.library.load.util.SyncUtil;

/**
 * 统计资料信息。中间层，豆瓣，Mtime
 * @author liuyunjian
 * 2011-5-5
 */
public class Statistic {
	private static Logger log = Logger.getLogger(Statistic.class.getName());
	private final static String SYNC_HOST = "10.103.12.71";
	private final static String SYNC_CALLER = "search_out";

	public static void main(String[] args) {
//		if(args.length<2){
//			System.out.println("usage: log4j torque");
//			System.exit(0);
//			return;
//		}
		// logger
//		String log4j = args[0];
//		System.out.println("初始化log4j: " + log4j);
//		DOMConfigurator.configure(log4j);

		int[] minMaxids = SyncUtil.getMinMaxIDs();
		System.out.println("min-max id:" + minMaxids[0] + "-" + minMaxids[1]);
		
		int length = 500;

		JSONObject showResultObject = null;
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("total", 0);
		map.put("shortdesc", 0);
		
		
		String category = "电影";
//		String category = "电视剧";
		String copyright = "";
//		String copyright = "authorized";
		
		for (int i = minMaxids[0]; i < minMaxids[1]; i += length) {
			showResultObject = buildProgrammeID(i, (i + length - 1),length,category);

			if (showResultObject == null || showResultObject.isNull("total")) {
				log.error("获取中间层数据,出错："+i+"-"+ (i + length - 1));
				continue;
			}

			int totalNum = showResultObject.optInt("total");
			if (totalNum > 0) {
				JSONArray showArray = showResultObject.optJSONArray("results");
				if (JSONUtil.isEmpty(showArray)) {
					log.error("获取中间层数据，结果为空");
				}
				
				
				
				for (int j = 0; j < showArray.length(); j++) {
					JSONObject itemJsonObject = showArray.optJSONObject(j);
					if(copyright!=null && copyright.length()>0){
						if(!copyright.equals(itemJsonObject.optString("copyright_status"))){
							continue;
						}
					}
					map.put("total", map.get("total")+1);
					
					
					for(Iterator<String> iter =itemJsonObject.keys();iter.hasNext();){
						String key = iter.next();
						Object tmp = null;
						int old = 0;
						tmp = map.get(key);
						if (tmp == null) {
							old = 0;
						}else {
							old = (Integer)tmp;
						}
						
						Object object = itemJsonObject.opt(key);
						if(object!=null){
							if("episode_collected".equals(key)){
								if(itemJsonObject.optInt(key)<=0){
									continue;
								}
							}
							if(object instanceof JSONArray && !JSONUtil.isEmpty((JSONArray)object)){
								old++;
							}else if (object instanceof String && ((String)object).length()>0) {
								if("showdesc".equals(key) && ((String)object).length()<100){
									map.put("shortdesc", map.get("shortdesc")+1);
								}
								old++;
							}else if (object instanceof Integer && ((Integer)object)>0) {
								old++;
							}
						}
						
						map.put(key, old);
					}
				}
				
			}
		}
		
		
		for (String key : map.keySet()) {
			System.out.println(key+":"+map.get(key));
		}
	}
	
	
	public static JSONObject buildProgrammeID(int start,int end,int length,String showcategory) {
		StringBuilder query = new StringBuilder("showid:");
		query.append(start).append("-").append(end)
		.append(" ").append(" showcategory:").append(showcategory);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("pn", "1");
		params.put("pl", Integer.toString(length));

		String fd_value = "performer director screenwriter executive_producer releasedate showalias language movie_genre tv_genre issuer "
						+" production area showdesc showlength show_thumburl show_vthumburl copyright_status episode_collected";
		params.put("fd", fd_value);
		params.put("ob", "showlastupdate:desc");
		params.put("cl", SYNC_CALLER);

		//如果中间层，获取失败{超时，内部出错}，重试3次，结果为空不算超时
		JSONObject jsonObject = NovaMiddleResource
		.search(SYNC_HOST,"show","show", query.toString(), params);
		if(jsonObject==null || jsonObject.isNull("total")){
			for (int i = 0; i < 3; i++) {
//				System.out.println("re-try:"+(i+1));
				jsonObject = NovaMiddleResource
				.search(SYNC_HOST,"show","show", query.toString(), params);
				if (jsonObject!=null && !jsonObject.isNull("total")) {
					return jsonObject;
				}
			}
		}
		return jsonObject;
	}
}
