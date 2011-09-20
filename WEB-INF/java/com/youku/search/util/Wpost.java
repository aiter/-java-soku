package com.youku.search.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wpost {

	public static final int READ_TIMEOUT = 20 * 1000;

	/**
	 * 以post方式提交一个web请求
	 */
	public static byte[] post(String url, Map<String, String> params)
			throws Exception {
		return post(url, params, READ_TIMEOUT);
	}

	public static byte[] post(String url, Map<String, String> params,
			int readTimeout) throws Exception {

		URL urlObject = new URL(url);

		OutputStream outputStream = null;
		InputStream inputStream = null;

		try {
			URLConnection connection = urlObject.openConnection();
			connection.setReadTimeout(readTimeout);

			connection.setDoOutput(true);
			outputStream = connection.getOutputStream();

			StringBuilder builder = new StringBuilder();
			List<String> keys = new ArrayList<String>(params.keySet());
			for (int i = 0; i < keys.size(); i++) {
				String key = keys.get(i);
				String value = params.get(key);

				builder.append(URLEncoder.encode(key, "UTF-8"));
				builder.append("=");
				builder.append(URLEncoder.encode(value, "UTF-8"));
				if (i < keys.size() - 1) {
					builder.append("&");
				}
			}

			outputStream.write(builder.toString().getBytes("UTF-8"));

			inputStream = connection.getInputStream();
			ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			while (true) {
				int length = inputStream.read(buffer);
				if (length < 1) {
					break;
				}
				responseOutputStream.write(buffer, 0, length);
			}

			byte[] response = responseOutputStream.toByteArray();
			return response;

		} finally {
			if (outputStream != null) {
				outputStream.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(new String(Wpost.post("http://10.101.8.68/",
				new HashMap<String, String>()), "utf8"));
	}
}
