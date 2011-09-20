package com.youku.search.console.config.time;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.operate.ErrorInfoMgt;
import com.youku.search.console.pojo.Errorinfo;

public class IndexListener extends TimerTask {
	private static Log log = LogFactory.getLog(IndexListener.class);
	private static boolean isRunning = false;
	private static ErrorInfoMgt em=ErrorInfoMgt.getInstance();
	
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
//			long s=System.currentTimeMillis();
//			System.out.println(new Date()+"--start--定时处理错误的索引操作start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			List<Errorinfo> el=em.findAll();
			for(int i=0;i<el.size();i++){
			String info;
			String url=el.get(i).getUrl();
			System.out.println("index url retry:"+url);
			HttpClient httpClient = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			try {
				// 执行getMethod
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: "
							+ getMethod.getStatusLine());
				}
				info=new String(getMethod.getResponseBody());
				if(info.equals("ok"))
					em.delete(url);
				else em.update(""+new Date(), el.get(i).getId());
				//System.out.println(new String(getMethod.getResponseBody()));
			} catch (HttpException e) {
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				System.err.println("Please check your provided http address!");
				e.printStackTrace();
				em.update(""+new Date(), el.get(i).getId());
			} catch (IOException e) {
				// 发生网络异常
				e.printStackTrace();
				em.update(""+new Date(), el.get(i).getId());
			} finally {
				// 释放连接
				getMethod.releaseConnection();
			}
			}
//			System.out.println(new Date()+"--end--定时处理错误的索引操作end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}
}
