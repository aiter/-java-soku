package com.youku.search.console.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.LogInfoConverter;
import com.youku.search.console.operate.log.LogQuery;
import com.youku.search.console.vo.LogInfo;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Request;
import com.youku.search.util.StringUtil;

public class LogQueryAction implements ServletRequestAware{
	
	HttpServletRequest request;
//	String filepath;
	String message;
	String filename;
	String downfilename;
	int rssize = 0;
	String keywords;
	String logdate;
	String[] types;
	
	List<LogInfo> es = null;
	List<String> ks = null;
	String opt = null;
	List<String> ts = null;
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

	public int getRssize() {
		return rssize;
	}

	public void setRssize(int rssize) {
		this.rssize = rssize;
	}

	

	public List<LogInfo> getEs() {
		return es;
	}

	public void setEs(List<LogInfo> es) {
		this.es = es;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getLogdate() {
		return logdate;
	}

	public void setLogdate(String logdate) {
		this.logdate = logdate;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String getFilename() {
		return filename;
	}
	
	public InputStream getInputStream() {
		actionstart();
		
		if(null==ks&&null==ts) {
			message = "文件找不到";
			return null;
		}
		
		if(null==ks) ks = new ArrayList<String>();
		if(null==ts) ts = new ArrayList<String>();
		
		if(StringUtils.isBlank(logdate)) logdate = nextdate;
		
//		if(ks.size()==1&&logdate.equalsIgnoreCase(nextdate)&&ts.size()==1&&ts.get(0).equalsIgnoreCase("video")){
//			String url = buildUrl(ks.get(0));
//			if(!StringUtils.isBlank(url)){
//				String res = Request.requestGet(url);
//				if(!StringUtils.isBlank(res)){
//					try {
//						JSONArray jarr = new JSONObject(res).getJSONArray("terms");
//						es = LogInfoConverter.convert(jarr,0);
//						return new ByteArrayInputStream(es2inputStr());
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				
//			}
//		}
		
		LogQuery lq = new LogQuery();
		es = lq.getLogsByKeyword(logdate, ks, opt, ts,0);
		
		return new ByteArrayInputStream(es2inputStr());
	}
	       
	public String down(){
		LogInfoWriter.operate_log.info("日志查询----下载");
		return Action.SUCCESS;
	}

	
	public String query(){
		
		actionstart();
		
		if(null==ks&&null==ts) {
			message = "请输入关键词或者选择类型";
			return Action.SUCCESS;
		}
		if(!StringUtils.isBlank(logdate)&&!logdate.matches("\\d{4}_\\d{2}_\\d{2}")){
			message = "日期格式错误";
			return Action.SUCCESS;
		}
		
		if(null==ks) ks = new ArrayList<String>();
		if(null==ts) ts = new ArrayList<String>();
		
		if(StringUtils.isBlank(logdate)) logdate = nextdate;
		
//		if(ks.size()==1&&logdate.equalsIgnoreCase(nextdate)&&ts.size()==1&&ts.get(0).equalsIgnoreCase("video")){
//			String url = buildUrl(ks.get(0));
//			if(!StringUtils.isBlank(url)){
//				String res = Request.requestGet(url);
//				if(!StringUtils.isBlank(res)){
//					try {
//						JSONArray jarr = new JSONObject(res).getJSONArray("terms");
//						es = LogInfoConverter.convert(jarr,100);
//						actionend();
//						return Action.SUCCESS;
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				
//			}
//		}
		
		LogQuery lq = new LogQuery();
		es = lq.getLogsByKeyword(logdate, ks, opt, ts,100);
		
		actionend();
		
		LogInfoWriter.operate_log.info("日志查询----查询");
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
		String downfilename = filename;
		try {
			downfilename = new String(downfilename.getBytes(), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return downfilename;
	}
	
	private String buildUrl(String k){
		if(StringUtils.isBlank(k)) return null;
		try {
			return "http://10.101.8.100/index/word.jsp?word="+URLEncoder.encode(k, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void actionstart(){
		if(!StringUtils.isBlank(keywords)){
			if(keywords.contains("+")){
				ks = StringUtil.parseStr2List(keywords, "\\+");
				opt = "and";
			}
			else if(keywords.contains("|")){
				ks = StringUtil.parseStr2List(keywords, "\\|");
				opt = "or";
			}
			else {
				ks = new ArrayList<String>();
				ks.add(keywords);
			}
		}
		ts = StringUtil.parseArr2List(types);
	}
	
	public void actionend(){
		if(null!=es){
			rssize = es.size();
			StringBuilder bf = new StringBuilder(logdate);
			if(!StringUtils.isBlank(keywords)){
				bf.append("_");
				bf.append(keywords);
			}
			if(null!=types&&types.length>0){
				for(String t:types){
					bf.append("_");
					bf.append(t);
				}
			}
			bf.append(".txt");
			filename = bf.toString();
		}
	}
	
	public byte[] es2inputStr(){
		StringBuilder sbf = new StringBuilder("搜索条件: logdate:");
		sbf.append(logdate);
		sbf.append(",");
		if(!StringUtils.isBlank(keywords)){
			sbf.append("keywords:");
			sbf.append(keywords);
			sbf.append(",");
		}
		if(null!=types&&types.length>0){
			sbf.append("types:");
			sbf.append(StringUtil.arrToString(types, "_"));
			sbf.append(",");
		}
		sbf.append("\r\n");
		sbf.append("\r\n");
		sbf.append("关键词\t搜索次数");
		sbf.append("\r\n");
		for(LogInfo e:es){
			sbf.append("\r\n");
			sbf.append(e.getKeyword()+"\t"+e.getCounts());
		}
		return sbf.toString().getBytes();
	}
}
