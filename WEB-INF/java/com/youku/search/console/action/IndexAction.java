package com.youku.search.console.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.youku.search.console.operate.ErrorInfoMgt;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.pojo.Errorinfo;
import com.youku.search.index.server.Server;
import com.youku.search.index.server.ServerManager;

public class IndexAction extends ActionSupport implements SessionAware,
		ServletRequestAware {
	private Map att;
	private HttpServletRequest request;
	private String indexid;
	private UserMgt um = UserMgt.getInstance();
//	private int type;
	private String typestr;
	private List<Integer> orders=new ArrayList<Integer>();
	private int order;
	private String info;
	ErrorInfoMgt em=ErrorInfoMgt.getInstance();
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTypestr() {
		return typestr;
	}

	public void setTypestr(String typestr) {
		this.typestr = typestr;
	}

	public List<Integer> getOrders() {
		return orders;
	}

	public void setOrders(List<Integer> orders) {
		this.orders = orders;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}

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

	public String getIndexid() {
		return indexid;
	}

	public void setIndexid(String indexid) {
		this.indexid = indexid;
	}

	public void setSession(Map arg0) {
		this.att = arg0;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

	public String list() {
		return SUCCESS;
	}

	public String show() {
		int type=0;
		try{
			type=Integer.parseInt(request.getParameter("type"));
			typestr=""+type;
		}catch(Exception e){
			type=Integer.parseInt(""+typestr);
		}
		if (type == Constants.VIDEO){
			for(int i=1;i<=Constants.VIDEOORDER;i++)
				orders.add(i);
		}
		if (type == Constants.FOLDER){
			for(int i=1;i<=Constants.FOLDERORDER;i++)
				orders.add(i);
		}
		if (type == Constants.BARPOST_SUBJECT){
			for(int i=1;i<=Constants.BARORDER;i++)
				orders.add(i);
		}
		if(type == Constants.MEMBER){
			for(int i=1;i<=Constants.USERORDER;i++)
				orders.add(i);
		}
		if(type == Constants.PK){
			for(int i=1;i<=Constants.PKORDER;i++)
				orders.add(i);
		}
		return SUCCESS;
	}

	public String remove() {
		String conpath = "/index/interface/index_delete.jsp?id=" + indexid
				+ "&type=" + typestr;
		// TODO 删除接口
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod;
		StringBuffer sb= new StringBuffer();
		int order;
		List<Server> sl=ServerManager.getServersById(Integer.parseInt(indexid), Integer.parseInt(typestr));
		order=sl.get(0).getOrder();
		sl.add(ServerManager.getManagerServer(order,Integer.parseInt(typestr)));
		String url;
		Errorinfo err;
		for(int i=0;i<sl.size();i++){
			url="http://" + sl.get(i).getIp() + conpath;
		getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			}
			String st=new String(getMethod.getResponseBody());
			if(!st.equals("ok")&&!em.check(url)){
				err=new Errorinfo();
				err.setUrl(url);
				err.setLasttime(""+new Date());
				err.setNum(1);
				try {
					err.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sb.append(sl.get(i).getIp());
			sb.append(":");
			sb.append(st);
			if(i!=sl.size()-1)
				sb.append(";");
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.err.println("Please check your provided http address!");
			e.printStackTrace();
			if(!em.check(url)){
			err=new Errorinfo();
			err.setUrl(url);
			err.setLasttime(""+new Date());
			err.setNum(1);
			try {
				err.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			}
			return ERROR;
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
			if(!em.check(url)){
			err=new Errorinfo();
			err.setUrl(url);
			err.setLasttime(""+new Date());
			err.setNum(1);
			try {
				err.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			}
			return ERROR;
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		}
		info=sb.toString();
		return SUCCESS;
	}

	public String rebulit() {
		String conpath = "/index/interface/index_create.jsp?order=" + order
				+ "&type=" + typestr;
		String url="http://" + ServerManager.getManagerServer(order, Integer.parseInt(typestr)).getIp() + conpath;
		Errorinfo err;
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
			if(!info.equals("ok")&&!em.check(url)){
				err=new Errorinfo();
				err.setUrl(url);
				err.setLasttime(""+new Date());
				err.setNum(1);
				try {
					err.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//System.out.println(new String(getMethod.getResponseBody()));
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.err.println("Please check your provided http address!");
			e.printStackTrace();
			if(!em.check(url)){
			err=new Errorinfo();
			err.setUrl(url);
			err.setLasttime(""+new Date());
			err.setNum(1);
			try {
				err.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			}
			return ERROR;
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
			if(!em.check(url)){
			err=new Errorinfo();
			err.setUrl(url);
			err.setLasttime(""+new Date());
			err.setNum(1);
			try {
				err.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			}
			return ERROR;
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return SUCCESS;
	}

	public String synIndex() {
		String conpath = "/index/interface/index_add.jsp?type="+typestr;
		int type=Integer.parseInt(typestr);
		int ord=0;
		if (type == Constants.VIDEO){
			ord=ServerManager.getVideoServers(1).size();
		}
		if (type == Constants.FOLDER){
			ord=ServerManager.getFolderServers(1).size();
		}
		if (type == Constants.BARPOST_SUBJECT){
			ord=ServerManager.getBarServers(1).size();
		}
		if(type == Constants.MEMBER){
			ord=ServerManager.getUserServers(1).size();
		}
		if(type == Constants.PK){
			ord=ServerManager.getPkServers(1).size();
		}
		Errorinfo err;
		String url="http://" + ServerManager.getManagerServer(ord, type).getIp()+ conpath;
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
			if(!info.equals("ok")&&!em.check(url)){
				err=new Errorinfo();
				err.setUrl(url);
				err.setLasttime(""+new Date());
				err.setNum(1);
				try {
					err.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//System.out.println(new String(getMethod.getResponseBody()));
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.err.println("Please check your provided http address!");
			e.printStackTrace();
			if(!em.check(url)){
			err=new Errorinfo();
			err.setUrl(url);
			err.setLasttime(""+new Date());
			err.setNum(1);
			try {
				err.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			}
			return ERROR;
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
			if(!em.check(url)){
			err=new Errorinfo();
			err.setUrl(url);
			err.setLasttime(""+new Date());
			err.setNum(1);
			try {
				err.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			}
			return ERROR;
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return SUCCESS;
	}
}
