package com.youku.soku.manage.admin.copyright;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Crawl {

	// 根据url获取页面文本信息
	// 无指定编码格式 默认为utf8
	public static String getContentByUrl(String urlStr) {
		String charset = "utf-8";
		return getContentByUrl(urlStr, charset);
	}

	// 指定编码格式获取页面信息
	public static String getContentByUrl(String urlStr, String charset) {
		StringBuffer sb = new StringBuffer();
		InputStream is = null;
		BufferedReader in = null;
		try {
			URL url = new URL(urlStr);
			is = url.openStream();
			in = new BufferedReader(new InputStreamReader(is, charset));
			String line ;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
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

	public static void main(String[] args) {
		System.out.print(getContentByUrl("http://www.renrenzhe.com"));
	}
}
