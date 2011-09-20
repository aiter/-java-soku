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
import com.youku.search.console.operate.juji.PlayVersionMgt;
import com.youku.search.console.operate.juji.VersionSpide;
import com.youku.search.console.pojo.TempPlayVersion;
import com.youku.search.console.vo.SingleVersion;
import com.youku.search.console.vo.VersionSpideVO;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;

public class VersionSpideAction implements ServletRequestAware,SessionAware {
	HttpServletRequest request;
	List<VersionSpideVO> vsvos;
	int status=0;
	int page=0;
	int maxpage=0;
	String name;
	int totalSize;
	String encodeKeyword;
	String message;
	Map att;
	
	public Map getAtt() {
		return att;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<VersionSpideVO> getVsvos() {
		return vsvos;
	}

	public void setVsvos(List<VersionSpideVO> vsvos) {
		this.vsvos = vsvos;
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
	
	public String update(){
		int id=DataFormat.parseInt(request.getParameter("sid"));
		int s=DataFormat.parseInt(request.getParameter("sstatus"));
		boolean flag;
		try{
			if(id<1||s<0){
				message="操作出现错误!";
			}else{
				TempPlayVersion tpv=VersionSpide.getInstance().getTempPlayVersion(id);
				if(s==2){
					SingleVersion sv=new SingleVersion();
					sv.setCate(tpv.getCate());
					sv.setSubcate(tpv.getSubcate());
					sv.setVersionname(tpv.getName());
					sv.setFkTeleplayId(tpv.getFkTeleplayId());
					sv.setAlias("");
					sv.setViewname(tpv.getName());
					sv.setFirstlogo("");
					flag=PlayVersionMgt.getInstance().addVersion(sv);
					if(!flag){
						message="添加电视版本出现错误";
					}
					else
						VersionSpide.getInstance().updateTempPlayVersion(tpv, s);
				}
				if(s==1)
					VersionSpide.getInstance().updateTempPlayVersion(tpv, s);
			}
			listSpide();
			
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
		
	public String list(){
		try{
			listSpide();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public void listSpide(){
		Connection conn =null;
		try{
			conn = DataConn.getTeleplayConn();
			if(name!=null&&name.trim().length()>0)
				name=name.trim();
			
			totalSize=VersionSpide.getInstance().searchSize(name, status,conn);
			if(totalSize%Constants.PAGESIZE==0)
				maxpage=totalSize/Constants.PAGESIZE;
			else maxpage=totalSize/Constants.PAGESIZE+1;
			if(page<1)page=1;
			if(maxpage<1)maxpage=1;
			vsvos=VersionSpide.getInstance().search(name, status, page, Constants.PAGESIZE,conn);
			
			try {
				encodeKeyword=URLEncoder.encode(name,"UTF-8");
			} catch (Exception e) {
				encodeKeyword=name;
			}
			request.setAttribute("cuurpage", page);
			request.setAttribute("cuurmaxpage", maxpage);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			JdbcUtil.close(conn);
		}
	}


	public void setSession(Map arg0) {
		this.att=arg0;
	}
}
