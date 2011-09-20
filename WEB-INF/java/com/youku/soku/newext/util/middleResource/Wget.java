package com.youku.soku.newext.util.middleResource;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class Wget {

	public static final String UA_IE = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)";
	public static final int READ_TIMEOUT = 10 * 1000;

	/**
	 * 读取指定url的字节内容，然后返回一个字节数组。
	 */
	public static byte[] get(String url) throws Exception {
		return get(url, (Map<String, String>) null, READ_TIMEOUT);
	}

	/**
	 * 读取指定url的字节内容，然后返回一个字节数组。
	 */
	public static byte[] get(String url, int read_time_out) throws Exception {
		return get(url, (Map<String, String>) null, read_time_out);
	}

	/**
	 * 将指定url的字节内容保存在文件file中
	 */
	public static byte[] get(String url, String file) throws Exception {
		return get(url, (Map<String, String>) null, READ_TIMEOUT, file);
	}

	/**
	 * 将指定url的字节内容保存在文件file中
	 */
	public static byte[] get(String url, Map<String, String> headers,
			int readTimeout, String file) throws Exception {

		byte[] bytes = get(url, headers, readTimeout);
		FileOutputStream outputStream = null;

		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(bytes);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

		return bytes;
	}

	/**
	 * 读取指定url的字节内容，然后返回一个字节数组。
	 */
	public static byte[] get(String url, Map<String, String> headers,
			int readTimeout) throws Exception {

		if (headers == null || headers.isEmpty()) {
			headers = new HashMap<String, String>();
			headers.put("User-Agent", UA_IE);
			headers.put("Referer", url);
		}

		URL urlObject = new URL(url);

		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
		byte[] buffer = new byte[1024];

		try {
			URLConnection connection = urlObject.openConnection();

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
}
