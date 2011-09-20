package com.youku.search.console.operate.juji;

import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.analyzer.WordProcessor;
import com.youku.search.console.teleplay.Video;
import com.youku.search.console.util.MD5;
import com.youku.search.util.DataFormat;
import com.youku.search.util.MyUtil;

public class JujiUtils {
	static Log logger = LogFactory.getLog(JujiUtils.class);

	private JujiUtils() {
	}

	private static JujiUtils instance = null;

	public static synchronized JujiUtils getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new JujiUtils();
			return instance;
		}
	}

	public Video getVideoByDB(int vid) {
		String hql = "select is_valid,public_type,title,thumb0,thumb4,seconds,file_id,source_type from t_video where pk_video="
				+ vid;
		Video v = new Video();
		try {
			List<Record> res = BasePeer.executeQuery(hql, "yoqoo");
			if (null != res && res.size() > 0) {
				int is_valid = 1;
				int public_type = 0;
				for (Record r : res) {
					is_valid = r.getValue("is_valid").asInt();
					if (1 != is_valid)
						return null;
					public_type = r.getValue("public_type").asInt();
					if (0 != public_type)
						return null;
					v.setEncodeVid(MyUtil.encodeVideoId(vid));
					v.setTitle(MyUtil.getString(r.getValue("title")
									.asString()));
					String logo = r.getValue("thumb0").asString();
					if (logo == null || logo.length() == 0
							|| logo.equals("DEFAULT")
							|| logo.equalsIgnoreCase("null"))
						logo = r.getValue("thumb4").asString();
					v.setLogo(logo);
					v.setSeconds(DataFormat.parseFloat(r.getValue("seconds").asString(),0));
					v.setFile_id(r.getValue("file_id").asString());
					v.setSource_type(r.getValue("source_type").asInt());
					v.setVid(vid);
				}
			}
		} catch (Exception e) {
			logger.error(vid, e);
		}
		return v;
	}

	public static String analyzer(String word) {
		if (null == word)
			return "";
		else {
			String[] arr = WordProcessor.analyzerPrepare(word);
			if (null != arr) {
				return trimNUll(arr[0]) + trimNUll(arr[1]);
			} else
				return "";
		}
	}

	public static String trimNUll(String word) {
		if (null == word)
			return "";
		else
			return word.trim();
	}
	
	public static int urlAnalyze(String url) {
		if (null == url || url.trim().length() < 1)
			return -1;
		String encodeid = StringUtils.substringBetween(url, "id_", ".html");
		if(null!=encodeid&&encodeid.trim().length()>0)
			return MyUtil.decodeVideoId(encodeid);
		else return -1;
	}
	
	public static int callApi(String encodevid) {
//		HttpClient httpClient = new HttpClient();
//		GetMethod getMethod = null;
//		long t = System.currentTimeMillis() / 1000;
//		String url = "http://api.youku.com/api_setSourceType?vid=" + encodevid
//				+ "&settype=1&ctime=" + t + "&token="
//				+ MD5.hash(t + "_VFEK4KBW6H");
//		getMethod = new GetMethod(url);
//		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//				new DefaultHttpMethodRetryHandler());
//		int statusCode;
//		String status = "";
//		try {
//			statusCode = httpClient.executeMethod(getMethod);
//			if (statusCode != HttpStatus.SC_OK) {
//				System.err.println("Method failed: "
//						+ getMethod.getStatusLine());
//			}
//			status = new String(getMethod.getResponseBody());
//			logger.info("result=" + status + "\t url=" + url);
//			return DataFormat.parseInt(status);
//		} catch (Exception e) {
//			logger.error(url,e);
//		} finally {
//			getMethod.releaseConnection();
//		}
//		return 2;
		return 3;
	}
	
	public static void sleep(int ms){
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
