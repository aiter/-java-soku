package com.youku.soku.manage.admin;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.youku.search.console.util.Wget;
import com.youku.soku.manage.common.BaseActionSupport;

public class DeleteVideoAction extends BaseActionSupport{
	
	public String delete() {
		
		StringBuilder sb = new StringBuilder("http://10.102.23.61/index/delete_byurl_server.jsp?urls=");
		
		int index = 0;
		int listSize = 0;
		for(String s : urlList) {
			if(!StringUtils.isBlank(s)) {
				listSize++;
			}
		}
		
		for(String s : urlList) {
			index++;
			System.out.println(s);
			if(!StringUtils.isBlank(s)) {
				sb.append(s);
				if(index < listSize) {
					sb.append("||");
				}
			}
		}
		
		
		byte[] bytes;
		try {
			System.out.println(sb.toString());
			bytes = Wget.get(sb.toString());
			String result = new String(bytes);
			System.out.println(result.trim());
			String status = "";
			if(result.indexOf("ok") > 0) {
				status = "删除成功";
			} else if(result.indexOf("fail") > 0){
				status = "删除失败";
			}
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();			
			out.print("<script type='text/javascript'>alert('" + status +"'); window.history.back();</script>");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	private String[] urlList;

	public String[] getUrlList() {
		return urlList;
	}

	public void setUrlList(String[] urlList) {
		this.urlList = urlList;
	}
	
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
