package com.youku.soku.manage.shield;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.Record;
import com.youku.search.console.util.Wget;
import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;

public class ShieldVideoAction extends BaseActionSupport{
	
	public String search() {
		String sokuQuery = "http://www.soku.com/v?";
		sokuQuery += "keyword=" + getKeyword();
		sokuQuery += "&curpage=" + getCurpage();
		sokuQuery += "&print";
		
		byte[] bytes;
		try{
			System.out.println(sokuQuery);
			bytes = Wget.get(sokuQuery);
			String result = new String(bytes);
			
			System.out.println(result);
			result = checkVideoDeleted(result);
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			
			PrintWriter out = response.getWriter();
			out.println(result);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String clearCache() {
		String sokuQuery = "http://www.soku.com/v?";
		sokuQuery += "keyword=" + getKeyword();
		sokuQuery += "&curpage=" + getCurpage();
		sokuQuery += "&__ic";
		
		byte[] bytes;
		try{
			System.out.println(sokuQuery);
			bytes = Wget.get(sokuQuery);
			String result = new String(bytes);
			
			result = checkVideoDeleted(result);
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			
			PrintWriter out = response.getWriter();
			out.println(result);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
public String delete() {
		
		StringBuilder sb = new StringBuilder("http://10.103.8.4/index/delete_byurl_server.jsp?urls=");
		
		sb.append(getUrl());
		
		byte[] bytes ;
		try {
			System.out.println(sb.toString());
			bytes = Wget.get(sb.toString());
			String result = new String(bytes);
			//String result = "ok";
			System.out.println(result.trim());
			String status = "";
			String statusDb = "";
			if(result.indexOf("ok") >= 0) {
				status = "删除成功";
				statusDb = "ok";
			} else if(result.indexOf("fail") >= 0){
				status = "删除失败";
				statusDb = "fail";
			}
			
			createVideoDeleteLog(getUrl(), statusDb);
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();			
			//out.print("<script type='text/javascript'>alert('" + status +"'); window.history.back();</script>");
			out.println(statusDb);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return SUCCESS;
	}
	
	private String checkVideoDeleted(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray items = obj.getJSONArray("items");
			for(int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				
				System.out.println("url in json: " + item.getString("url"));
				item.put("delete_flag", isVideoDeleted(item.getString("url")));
			}
			
			
			return obj.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private boolean isVideoDeleted(String url) {
		String sql = "SELECT count(*) from shield_videos where url = '" + url + "'";
		int recordsCount = 0;
		try {
			List<Record> records = BasePeer.executeQuery(sql, "soku");
			recordsCount = ((Record) records.get(0)).getValue(1).asInt();
		} catch (TorqueException e) {
			e.printStackTrace();
		} catch (DataSetException e) {
			e.printStackTrace();
		}

		return recordsCount > 0;		
	}
	
	private void createVideoDeleteLog(String url, String status) {
		
		if(isVideoDeleted(url)) {
			return;
		}
		String sql = "INSERT INTO shield_videos (url, modifier, status, create_time) VALUES ('" + url + "', '" + getUserName() + "', '" + status + "', '"+ DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDDHHMMSS) + "')"; 
		System.out.println(sql);
		try {
			BasePeer.executeStatement(sql, "soku");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String list() {
		return Constants.LIST;
	}
	
	private String keyword;
	
	private int curpage;
	
	private String url;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getCurpage() {
		return curpage;
	}

	public void setCurpage(int curpage) {
		this.curpage = curpage;
	}

	public String getUrl() {
		return StringUtil.urlDecode(url, "utf-8");
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
