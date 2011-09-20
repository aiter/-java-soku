package com.youku.search.console.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONArray;
import org.json.JSONException;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.LogInfoConverter;
import com.youku.search.console.vo.LogInfo;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Request;

public class LogIndexQueryAction implements ServletRequestAware{
	
	HttpServletRequest request;
//	String filepath;
	String message;
	String filename;
	String downfilename;
	String keywords;
	String startdate;
	String enddate;
	String downdate ;
//	Map<String,String> filenames = new HashMap<String, String>();
	Map<String,List<LogInfo>> infomaps = new HashMap<String, List<LogInfo>>();
	String opt = null;
	String nextdate = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Map<String, List<LogInfo>> getInfomaps() {
		return infomaps;
	}

	public void setInfomaps(Map<String, List<LogInfo>> infomaps) {
		this.infomaps = infomaps;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}


	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getFilename() {
		return filename;
	}
	
	public String getDowndate() {
		return downdate;
	}

	public void setDowndate(String downdate) {
		this.downdate = downdate;
	}

	public InputStream getInputStream() {
		if(StringUtils.isBlank(keywords)||StringUtils.isBlank(downdate)) {
			message = "文件找不到";
			return null;
		}
		String url = buildUrl(downdate,keywords,0);
		if(!StringUtils.isBlank(url)){
			String res = Request.requestGet(url);
			if(!StringUtils.isBlank(res)){
				try {
					JSONArray jarr = new JSONArray(res);
					List<LogInfo> es = LogInfoConverter.convert(jarr,0);
					return new ByteArrayInputStream(es2inputStr(es,downdate));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return null;
	}
	       
	public String down(){
		LogInfoWriter.operate_log.info("日志查询下载----下载");
		return Action.SUCCESS;
	}

	
	public String query(){
		
		if(StringUtils.isBlank(keywords)) {
			message = "请输入关键词";
			return Action.SUCCESS;
		}
		if(!StringUtils.isBlank(startdate)&&!startdate.matches("\\d{4}_\\d{2}_\\d{2}")){
			message = "日期格式错误";
			return Action.SUCCESS;
		}
		
		if(StringUtils.isBlank(startdate)) startdate = nextdate;
		List<String> dates = new ArrayList<String>();
		dates.add(startdate);
		if(!StringUtils.isBlank(enddate)&&enddate.matches("\\d{4}_\\d{2}_\\d{2}")){
			String temp = null;
			for(int i=1;;i++){
				temp = DataFormat.formatDate(DataFormat.getNextDate(DataFormat.parseUtilDate(startdate,DataFormat.FMT_DATE_YYYY_MM_DD),i), DataFormat.FMT_DATE_YYYY_MM_DD);
				dates.add(temp);
				if(temp.equalsIgnoreCase(enddate))
					break;
			}
		}
		for(String d:dates){
		String url = buildUrl(d,keywords,100);
		if(!StringUtils.isBlank(url)){
			String res = Request.requestGet(url);
			if(!StringUtils.isBlank(res)){
				try {
					JSONArray jarr = new JSONArray(res);
					List<LogInfo> es = LogInfoConverter.convert(jarr,100);
					if(es.size()>0)
					infomaps.put(d, es);
//					actionend(es,d);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		}
		LogInfoWriter.operate_log.info("日志查询下载----查询");
		return Action.SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	
	
	public String getDownfilename() {
		filename = buildFileName(downdate);
		String downfilename = filename;
		try {
			downfilename = new String(downfilename.getBytes(), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return downfilename;
	}
	
	private String buildUrl(String date,String k,int limit){
		if(StringUtils.isBlank(k)) return null;
		try {
			return "http://10.103.8.225/index/log/console/query.jsp?word="+URLEncoder.encode(k, "utf-8")+"&limit="+limit+"&d="+date;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String buildFileName(String d){
		StringBuilder bf = new StringBuilder();
		if(!StringUtils.isBlank(d))
			bf.append(d);
		if(!StringUtils.isBlank(keywords)){
			bf.append("_");
			bf.append(keywords);
		}
		bf.append(".txt");
		return bf.toString();
	}
	
	public byte[] es2inputStr(List<LogInfo> es,String d){
		StringBuilder sbf = new StringBuilder("搜索条件: logdate:");
		sbf.append(d);
		sbf.append(",");
		if(!StringUtils.isBlank(keywords)){
			sbf.append("keywords:");
			sbf.append(keywords);
			sbf.append(",");
		}
		sbf.append("\r\n");
		sbf.append("\r\n");
		sbf.append("关键词\t搜索次数");
		sbf.append("\r\n");
		if(null!=es&&es.size()>0){
		for(LogInfo e:es){
			sbf.append("\r\n");
			sbf.append(e.getKeyword()+"\t"+e.getCounts());
		}
		}
		return sbf.toString().getBytes();
	}
}
