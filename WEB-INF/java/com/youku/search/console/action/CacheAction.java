package com.youku.search.console.action;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.danga.MemCached.MemCachedClient;
import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.vo.MenCache;

public class CacheAction extends ActionSupport implements SessionAware,
		ServletRequestAware {
	private Map att;
	private HttpServletRequest request;
	private UserMgt um = UserMgt.getInstance();
	private MenCache mc;
	private int pageno = 0;
	private static int nums = Constants.SERVER_NUM;
	private static String[] ips = Constants.cacheiparr;
	private int type;
	private String keyword;
	private String info;
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public MenCache getMc() {
		return mc;
	}

	public void setMc(MenCache mc) {
		this.mc = mc;
	}

	public int getPageno() {
		return pageno;
	}

	public void setPageno(int pageno) {
		this.pageno = pageno;
	}

	public Map getAtt() {
		return att;
	}

	public void setAtt(Map att) {
		this.att = att;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setSession(Map arg0) {
		this.att = arg0;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

	public String cleanByIp() {
		String path = "http://"+ Constants.iparr[0]+"/search?keyword="+URLEncoder.encode(keyword)+"&type="+type+"&admin=delcache";
		System.out.println(path);
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(path);
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
			System.out.println("info="+info);
			request.setAttribute("info", info);
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.err.println("Please check your provided http address!");
			e.printStackTrace();
			return ERROR;
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
			return ERROR;
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return SUCCESS;
	}

	public String toCleanByIp() {
		request.setAttribute("ips", ips);
		request.setAttribute("startpage", pageno);
		return SUCCESS;
	}

	public String list() {
		mc = new MenCache();
		request.setAttribute("ips", ips);
		request.setAttribute("startpage", pageno);
		//System.out.println("pageno="+pageno);
//		MemcachedInit.getInstance().init("memcached.properties", pageno);
		MemCachedClient mcc = new MemCachedClient();
		Map statsmap = mcc.stats(new String[]{ips[pageno]});
		Set entries = statsmap.entrySet();
		Iterator entryIter = entries.iterator();
		Map.Entry entry;
		String myjson;
		JSONObject jo;
		Map<String, String> stats = new HashMap<String, String>();
		while (entryIter.hasNext()) {
			entry = (Map.Entry) entryIter.next();
			myjson = "" + entry.getValue();
			myjson = myjson.replaceAll("=", ":");
			jo = JSONObject.fromObject(myjson);

			mc = (MenCache) jo.toBean(jo, MenCache.class);
		}
		return SUCCESS;
	}
}
