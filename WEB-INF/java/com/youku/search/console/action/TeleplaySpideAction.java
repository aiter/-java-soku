package com.youku.search.console.action;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.TeleplaySpideMgt;
import com.youku.search.console.operate.juji.JujiService;
import com.youku.search.console.pojo.TeleplaySpide;
import com.youku.search.console.vo.TeleCate;
import com.youku.search.console.vo.TeleplaySpideVO;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;

public class TeleplaySpideAction implements ServletRequestAware,SessionAware {
	HttpServletRequest request;
	List<TeleplaySpideVO> tsvos;
	int status=0;
	int page=0;
	int maxpage=0;
	String name;
	int totalSize;
	String encodeKeyword;
	String message;
	TeleCate tc;
	Map att;
	
	public Map getAtt() {
		return att;
	}

	public TeleCate getTc() {
		return tc;
	}

	public void setTc(TeleCate tc) {
		this.tc = tc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<TeleplaySpideVO> getTsvos() {
		return tsvos;
	}

	public void setTsvos(List<TeleplaySpideVO> tsvos) {
		this.tsvos = tsvos;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}
	
	public String select(){
		tc=new TeleCate();
		tc.setViewbeancate();
		
		return Action.SUCCESS;
	}
	
	public String update(){
		int id=DataFormat.parseInt(request.getParameter("sid"));
		int s=DataFormat.parseInt(request.getParameter("sstatus"));
		int cate=DataFormat.parseInt(request.getParameter("cate"));
		int subcate=DataFormat.parseInt(request.getParameter("subcate"));
		boolean[] flag;
		try{
			if(id<1||s<0){
				message="操作出现错误!";
			}else{
				TeleplaySpideMgt tsm=TeleplaySpideMgt.getInstance();
				TeleplaySpide ts=tsm.getTeleplaySpideByID(id);
				if(s==2){
					TeleCate tc = new TeleCate();
					tc.setCate(cate);
					tc.setSubcate(subcate);
					flag=JujiService.getInstance().saveTeleplay(ts.getName(),null,null,tc);
					if(!flag[0]){
						message="添加电视出现错误";
						if(!flag[1])
							message="该名称已经存在!";
					}
					else
						tsm.updateTeleplaySpide(ts, s);
				}
				if(s==1)
					tsm.updateTeleplaySpide(ts, s);
			}
			listSpide();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
		
	public String list(){
		listSpide();
		return Action.SUCCESS;
	}
	
	public void listSpide(){
			Connection conn = null;
			try{
			conn=DataConn.getTeleplayConn();
			if(name!=null&&name.trim().length()>0)
				name=name.trim();
			TeleplaySpideMgt tsmgt=TeleplaySpideMgt.getInstance();
			totalSize=tsmgt.searchSize(name, status,conn);
			if(totalSize%Constants.PAGESIZE==0)
				maxpage=totalSize/Constants.PAGESIZE;
			else maxpage=totalSize/Constants.PAGESIZE+1;
			if(page<1)page=1;
			if(maxpage<1)maxpage=1;
			tsvos=tsmgt.search(name, status, page, Constants.PAGESIZE,conn);
			
			try {
				encodeKeyword=URLEncoder.encode(name,"UTF-8");
			} catch (Exception e) {
				encodeKeyword=name;
			}
			request.setAttribute("cuurpage", page);
			request.setAttribute("cuurmaxpage", maxpage);
			}catch(Exception e){
				
			}finally{
				JdbcUtil.close(conn);
			}
	}

	
	public void setSession(Map arg0) {
		this.att=arg0;
	}
}
