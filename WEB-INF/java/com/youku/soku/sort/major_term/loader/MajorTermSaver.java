package com.youku.soku.sort.major_term.loader;

import static com.youku.search.util.StringUtil.filterNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.util.Wget;
import com.youku.soku.manage.entity.MajorTerm;
import com.youku.soku.manage.service.MajorTermService;

public class MajorTermSaver {

	static Log logger = LogFactory.getLog(MajorTermSaver.class);

	public static void main(String[] args) throws Exception {

		// usage
		System.out.println("usage: log4j torque ");

		// logger
		String log4j = args[0];
		System.out.println("初始化log4j: " + log4j);
		DOMConfigurator.configure(log4j);

		// torque
		String torque = args[1];
		System.out.println("初始化torque: " + torque);
		Torque.init(torque);

		// ///////////////////////////////////////

		logger.info("开始查询大词数据...");

		List<MajorTerm> list = MajorTermService.getMajorTerm();
		if (list == null || list.isEmpty()) {
			logger.info("查询到的大词数据为null或者为empty");
			return;
		}

		logger.info("查询大词数据结束, 找到数据: " + list.size() + ", 开始抓取数据...");

		JSONObject json = new JSONObject();
		for (MajorTerm majorTerm : list) {

			String keyword = filterNull(majorTerm.getKeyword()).trim();
			int cate_id = majorTerm.getCateId();
			String html_text = filterNull(majorTerm.getHtmlText()).trim();
			String url = filterNull(majorTerm.getUrlText()).trim();
			String jsonUrl = filterNull(majorTerm.getDestUrl()).trim();

			if (keyword.isEmpty() || url.isEmpty()
					|| (html_text.isEmpty() && jsonUrl.isEmpty())) {
				continue;
			}

			JSONArray jsonData = null;
			if (html_text.isEmpty() && !jsonUrl.isEmpty()) {
				try {
					byte[] bytes = Wget.get(jsonUrl);

					String charset = "UTF-8";
					String response = new String(bytes, charset);
					response = filterNull(response).trim();

					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("librarys");
					if (jsonArray.length() > 0) {
						jsonData = jsonArray;
					}
				} catch (Exception e) {
					logger.error("抓取、解析url内容发生异常, url: " + jsonUrl, e);
				}

			}

			if (html_text.isEmpty() && jsonData == null) {
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

			if (!html_text.isEmpty()) {
				cateObject.put("url", url);
				cateObject.put("html", html_text);
			} else {
				cateObject.put("url", url);
				cateObject.put("json", jsonData);
			}
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
}
