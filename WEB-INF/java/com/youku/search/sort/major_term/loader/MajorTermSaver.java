package com.youku.search.sort.major_term.loader;

import static com.youku.search.util.StringUtil.filterNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.util.StringUtil;
import com.youku.search.util.Wget;
import com.youku.soku.manage.entity.MajorTerm;
import com.youku.soku.manage.service.MajorTermService;

/**
 * 站内有版权的大词搜索。
 * 
 * 2010-10-25 modified liuyunjian
 */
public class MajorTermSaver {
	static class Category{
		private static Map<Integer, String> categoriesMap = new HashMap<Integer, String>();
		
		//目前，直接写入数据，完整应该从DB取，保证数据一致
		static{
			categoriesMap.put(1, "电视剧");
			categoriesMap.put(2, "电影");
			categoriesMap.put(3, "综艺");
			categoriesMap.put(4, "音乐");
			categoriesMap.put(5, "动漫");
			categoriesMap.put(6, "人物");
			categoriesMap.put(7, "体育");
			categoriesMap.put(8, "教育");
		}
		
		public static String getCategory(int cateId,String lable) {
			/**页面显示如：{关键词}的相关电视剧  如果什么都没有，就返回 “视频” 组成 :{关键词}的相关视频  */
			if(cateId==0){
				return (lable==null||lable.isEmpty())?"视频":lable;
			}
			String category = categoriesMap.get(cateId);
			if(category ==  null){
				logger.error("category ID："+cateId+" can't get name");
				return "视频";
			}
			return category;
		}
	}

	static Log logger = LogFactory.getLog(MajorTermSaver.class);

	public static void main(String[] args) throws Exception {

		// usage
		System.out.println("usage: log4j torque ip/ ");

		// logger
		String log4j = args[0];
		System.out.println("初始化log4j: " + log4j);
		DOMConfigurator.configure(log4j);

		// torque
		String torque = args[1];
		System.out.println("初始化torque: " + torque);
		Torque.init(torque);
		
		//是否是测试机
		String testServerIp = null;
		if(args.length>=3 && args[2]!=null && !args[2].isEmpty() 
				&& java.util.regex.Pattern.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", args[2])){
			testServerIp = args[2];
		}

		// ///////////////////////////////////////

		
		logger.info("开始查询大词数据...");

		final Cost dbMajorTermCost = new Cost();
		List<MajorTerm> list = MajorTermService.getMajorTerm();
		if (list == null || list.isEmpty()) {
			logger.info("查询到的大词数据为null或者为empty");
			return;
		}
		dbMajorTermCost.updateEnd();

		logger.info("查询大词数据结束,cost:"+dbMajorTermCost.getCost()+" 找到数据: " + list.size() + ", 开始抓取数据...");

		JSONObject json = new JSONObject();
		for (MajorTerm majorTerm : list) {

			String keyword = filterNull(majorTerm.getKeyword()).trim();   //大词
			int cate_id = majorTerm.getCateId();                          //分类ID
			String lable = majorTerm.getLabel();                          //cate_id=0时，使用lable显示
			//2010.10.29 html内容的，站内暂时不处理
//			String html_text = filterNull(majorTerm.getHtmlText()).trim();//html内容
			String url = filterNull(majorTerm.getUrlText()).trim();       //展示url
			String jsonUrl = filterNull(majorTerm.getDestUrl()).trim();	  //数据获取url
			
			if (keyword.isEmpty() || url.isEmpty() || jsonUrl.isEmpty()){
//				|| (html_text.isEmpty() && jsonUrl.isEmpty())) {
				logger.info("查询到的大词："+keyword+" 为空或destUrl为空");
				continue;
			}
			
			//http://www.soku.com/channel/subject.jsp?type=&area=%E9%9F%A9%E5%9B%BD&year=&letter=&start=0&limit=5&channel=teleplay&json=json
			//如果是测试机，那么将www.soku.com替换为测试机ip:10.101.8.112
			if(testServerIp!=null){
				jsonUrl  = jsonUrl.replace("www.soku.com", testServerIp);
			}
			
			//替换参数limit=5为limit=8
			jsonUrl = jsonUrl.replace("limit=5", "limit=8");
			
			//有版权的数据，加入right=1参数
			jsonUrl +="&right=1";
			
			
//			//2010.10.28  如果大词是使用html，站内直接抛弃这种情况，以后可以考虑将html内容转换为json，便于站内统一使用
//			if(!html_text.isEmpty()){
//				continue;
//			}
			

			JSONArray jsonData = null;
//			if (html_text.isEmpty() && !jsonUrl.isEmpty()) {
			if (!jsonUrl.isEmpty()) {
				try {
					byte[] bytes = Wget.get(jsonUrl);

					String charset = "UTF-8";
					String response = new String(bytes, charset);
					response = filterNull(response).trim();

					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("librarys");
					if (jsonArray.length() > 0) {
						jsonData = jsonArray;
						
						//2010.10.27 添加访问地址
						int len = jsonData.length();
						JSONObject eleObject = null;
						String name = null;
						String viewUrl = null;
						for (int i = 0; i < len; i++) {
							eleObject = jsonData.optJSONObject(i);
							if(eleObject!=null){
								viewUrl = eleObject.optString("url");
								if(viewUrl == null || viewUrl.isEmpty()){
									name = eleObject.optString("name");
									eleObject.put("url","http://www.soku.com/search_video/q_"+StringUtil.urlEncode(name, charset, name));
								}
							}
							eleObject = null;
							name = null;
							viewUrl = null;
						}
					}
					
					logger.info(keyword+":"+jsonArray.length() +":librarys, url: " + jsonUrl);
				} catch (Exception e) {
					logger.error("抓取、解析url内容发生异常, url: " + jsonUrl, e);
				}

			}

			if (jsonData == null) {
//				if (html_text.isEmpty() && jsonData == null) {
				continue;
			}

			// data is OK
			JSONObject keywordObject = json.optJSONObject(keyword);
			if (keywordObject == null) {
				keywordObject = new JSONObject();
				json.put(keyword, keywordObject);
			}

			String cate_id_string = Integer.toString(cate_id);
			JSONObject cateObject = keywordObject.optJSONObject(cate_id_string);
			if (cateObject == null) {
				cateObject = new JSONObject();
				keywordObject.put(cate_id_string, cateObject);
			}

			//TODO  添加类型名称  电影/电视剧/...
			cateObject.put("url", url);
			cateObject.put("keyword", keyword);
			cateObject.put("cate_name", Category.getCategory(cate_id,lable));
//			if (!html_text.isEmpty()) {
//				cateObject.put("html", html_text);
//				JSONArray html2JsonArray = convertHtml(html_text);
//				cateObject.put("json", html2JsonArray);
//			} else {
				cateObject.put("json", jsonData);
//			}
		}

		if (json.length() < 1) {
			logger.info("抓取数据结束, 没有找到有效的数据, 不需要写入本地文件");
			System.exit(1);
		}

		logger.info("抓取数据结束, 有效的数据: " + json.length() + "/" + list.size()
				+ ", 开始写入本地文件...");

		File dir = new File(Constant.dir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, Constant.file);
		OutputStreamWriter stream = new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8");
		stream.write(json.toString(4));
		stream.close();
		logger.info("写入本地文件结束");
	}

//	/**
//	 * @param html_text
//	 * @return
//	 */
//	private static JSONArray convertHtml(String html_text) {
//		
//		return null;
//	}
}
