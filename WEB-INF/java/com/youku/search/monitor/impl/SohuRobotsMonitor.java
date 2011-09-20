/**
 * 
 */
package com.youku.search.monitor.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.youku.search.monitor.Result;

/**
 * @author 1verge
 * 会员搜索监控
 */
public class SohuRobotsMonitor extends AMonitor{

	public SohuRobotsMonitor() {
		super();
		super.period = 1000*60*10; //10分钟执行一次
	}

	/* (non-Javadoc)
	 * @see com.youku.search.monitor.Monitor#check()
	 */
	@Override
	public Result check() {
		Result r = new Result();
		
		String url = "http://tv.sohu.com/robots.txt";
		HttpClient client = new HttpClient();
		client.getHostConfiguration().setProxy("10.11.11.82", 8181);   
		String result = null;
		GetMethod method = new GetMethod(url);
		
		try {
			client.executeMethod(method);
			int status = method.getStatusCode();
			if (status != 404){
				result = method.getResponseBodyAsString();
				r.setMessage(url + "\n "+result);
				r.setOk(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("请求url失败:"+url);
		}finally{
			method.releaseConnection();
		}
		
		return r;
	}
	
	public static void main(String[] args){
		SohuRobotsMonitor s = new SohuRobotsMonitor();
		s.check();
	}
}
