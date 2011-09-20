package com.youku.search.console.action;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.FeedbackSearchMgt;
import com.youku.search.console.vo.FeedbackSearchVO;
import com.youku.search.util.DataFormat;

public class FeedbackSearchAction implements ServletRequestAware,ServletResponseAware{
	HttpServletResponse response;
	HttpServletRequest request;
	String result;
	String keyword;
	int page=0;
	int maxpage=0;
	String message;
	int totalSize;
	String encodeKeyword;
	int order=1;
	List<FeedbackSearchVO> fbs;
	String[] fids;
	int isPrecise=0;
	
	public int getIsPrecise() {
		return isPrecise;
	}

	public void setIsPrecise(int isPrecise) {
		this.isPrecise = isPrecise;
	}

	public String[] getFids() {
		return fids;
	}

	public void setFids(String[] fids) {
		this.fids = fids;
	}

	public List<FeedbackSearchVO> getFbs() {
		return fbs;
	}

	public void setFbs(List<FeedbackSearchVO> fbs) {
		this.fbs = fbs;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getMaxpage() {
		return maxpage;
	}

	public void setMaxpage(int maxpage) {
		this.maxpage = maxpage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public String getEncodeKeyword() {
		return encodeKeyword;
	}

	public void setEncodeKeyword(String encodeKeyword) {
		this.encodeKeyword = encodeKeyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String save() throws IOException{
		String url=request.getParameter("url");
		JSONObject js=new JSONObject();
		if(null==url||url.trim().length()<1){
//			result="{\"result\":\"false\"}";
			js.put("result", "false");
			result=js.toString();
			return Action.SUCCESS;
		}
		int error_type=DataFormat.parseInt(request.getParameter("error_type"),-1);
		String keyword=request.getParameter("keyword");
		int page=DataFormat.parseInt(request.getParameter("page"),1);
		int creator=DataFormat.parseInt(request.getParameter("creator"),-1);
		String description=request.getParameter("description");
		System.out.println("error_type:"+error_type+"\turl:"+url+"\tkeyword:"+keyword+"\tdescription:"+description+"\tpage:"+page+"\tcreator:"+creator);
		int res=FeedbackSearchMgt.getInstance().create(error_type, description, keyword, page, creator,url);
//		System.out.println("res:"+res);
//		if(res>0)result="{\"result\":\"true\"}";
//		else result="{\"result\":\"false\"}";
		if(res>-1)js.put("result", "true");
		else js.put("result", "false");
		result=js.toString(); 
		response.getWriter().print(result);
//		System.out.println("result:"+getResult());
		return null;
	}
	
	public void listFeed(Connection conn){
		int f=DataFormat.parseInt(request.getParameter("f"),-1);
		if(f==1&&order==2){
			order=1;
			isPrecise=1;
			page=1;
		}
		if(keyword!=null&&keyword.trim().length()>0)
			keyword=keyword.trim();
		FeedbackSearchMgt fbmgt=FeedbackSearchMgt.getInstance();
		totalSize=fbmgt.searchSize(keyword,order,isPrecise,conn);
		if(totalSize%Constants.PAGESIZE==0)
			maxpage=totalSize/Constants.PAGESIZE;
		else maxpage=totalSize/Constants.PAGESIZE+1;
		if(page<1)page=1;
		if(maxpage<1)maxpage=1;
		if(page>maxpage) page=maxpage;
//		System.out.println("keyword:"+keyword+"\tpage:"+page+"\torder:"+order+"\tisPrecise:"+isPrecise);
		fbs=fbmgt.search(keyword, page,Constants.PAGESIZE, order,isPrecise,conn);
		try {
			encodeKeyword=URLEncoder.encode(keyword,"UTF-8");
		} catch (Exception e) {
			encodeKeyword=keyword;
		}
		request.setAttribute("cuurpage", page);
		request.setAttribute("cuurmaxpage", maxpage);
	}
	
	public String list(){
		Connection conn=null;
		try{
			conn=DataConn.getConsoleConn();
			listFeed(conn);
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}finally{
			DataConn.releaseConn(conn);
		}
		return Action.SUCCESS;
	}
	
	public String delete(){
		FeedbackSearchMgt fbmgt=FeedbackSearchMgt.getInstance();
		Connection conn=null;
		try{
			conn=DataConn.getConsoleConn();
//			System.out.println("fids:"+fids+"\torder:"+order);
			fbmgt.delete(fids, order, conn);
			listFeed(conn);
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}finally{
			DataConn.releaseConn(conn);
		}
		return Action.SUCCESS;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setServletResponse(HttpServletResponse arg0) {
		this.response=arg0;
	}

}
