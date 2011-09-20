package com.youku.soku.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpClientUtil {
	private static Log logger = LogFactory.getLog(HttpClientUtil.class);

	public static String getRemoteResult(String url, String keyword) {
		String path = url;
		try {
			path = url + URLEncoder.encode(keyword, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			logger.error("UnsupportedEncodingException ：" + keyword);
			return "";
		}

		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(path);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {

			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + getMethod.getStatusLine());
			}
			String info = new String(getMethod.getResponseBody(), "utf-8");
			return info;
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			logger.error("Please check your provided http address!");
			e.printStackTrace();

		} catch (IOException e) {
			// 发生网络异常
			logger.error("发生网络异常");
			e.printStackTrace();

		} finally {
			//			 释放连接
			getMethod.releaseConnection();
		}
		return "";

	}
	
//	根据url发送请求
	public static String getRemoteResult(String url) {
		String path = url;
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(path);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		
		logger.info("the http request is:"+url);
		try {

			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + getMethod.getStatusLine());
			}
			String info = new String(getMethod.getResponseBody(), "utf-8");
			return info;
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			logger.error("Please check your provided http address!");
			e.printStackTrace();

		} catch (IOException e) {
			// 发生网络异常
			logger.error("发生网络异常");
			e.printStackTrace();

		} finally {
			//			 释放连接
			getMethod.releaseConnection();
		}
		return "";

	}

	public static void main(String[] args) {

	}
}