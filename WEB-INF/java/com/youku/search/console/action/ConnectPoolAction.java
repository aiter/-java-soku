package com.youku.search.console.action;

import java.io.IOException;
import java.util.Map;

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

import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.vo.ConnInfo;

public class ConnectPoolAction extends ActionSupport implements SessionAware,
		ServletRequestAware {
	private Map att;
	private HttpServletRequest request;
	private UserMgt um = UserMgt.getInstance();
	private ConnInfo cinfo = new ConnInfo();
	private int pageno = 0;
	private static int nums = Constants.CONN_SERVER_NUM;
	private static String[] ips = Constants.iparr;

	public int getPageno() {
		return pageno;
	}

	public void setPageno(int pageno) {
		this.pageno = pageno;
	}

	public ConnInfo getCinfo() {
		return cinfo;
	}

	public void setCinfo(ConnInfo cinfo) {
		this.cinfo = cinfo;
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

	@Override
	public String execute() throws Exception {

		request.setAttribute("ips", ips);
		request.setAttribute("startpage", pageno);

		String connip = ips[pageno];
		// 连接url
		String conpath = "/pool/status";
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod("http://" + connip + conpath);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			}
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();
			// 处理内容
			JSONObject jo = JSONObject.fromObject(new String(responseBody));

			cinfo = (ConnInfo) jo.toBean(jo, ConnInfo.class);
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
}
