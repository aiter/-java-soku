package com.youku.search.console.action;

import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.juji.BlacklistMgt;
import com.youku.search.console.operate.juji.EpisodeAnalyzeService;
import com.youku.search.console.operate.juji.EpisodeLogMgt;
import com.youku.search.console.operate.juji.EpisodeMgt;
import com.youku.search.console.operate.juji.EpisodeService;
import com.youku.search.console.pojo.EpisodeLog;
import com.youku.search.console.pojo.EpisodeLogPeer;
import com.youku.search.console.vo.EpisodeLogVO;
import com.youku.search.util.DataFormat;

public class EpisodeLogAction implements ServletRequestAware{
	HttpServletRequest request;
	List<EpisodeLogVO> elvos;
	int status=0;
	int page=0;
	int maxpage=0;
	String message;
	String keyword;
	int totalSize;
	String encodeKeyword;
	EpisodeLogVO elvo;
	
	public List<EpisodeLogVO> getElvos() {
		return elvos;
	}


	public void setElvos(List<EpisodeLogVO> elvos) {
		this.elvos = elvos;
	}


	public EpisodeLogVO getElvo() {
		return elvo;
	}


	public void setElvo(EpisodeLogVO elvo) {
		this.elvo = elvo;
	}


	public String getEncodeKeyword() {
		return encodeKeyword;
	}


	public void setEncodeKeyword(String encodeKeyword) {
		this.encodeKeyword = encodeKeyword;
	}


	public int getTotalSize() {
		return totalSize;
	}


	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}


	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
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


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public HttpServletRequest getRequest() {
		return request;
	}
	
	public String delete(){
		EpisodeLogMgt fbmgt=EpisodeLogMgt.getInstance();
		int tempid=DataFormat.parseInt(request.getParameter("episodeLogId"));
		if(tempid<1)return Action.SUCCESS;
		try{
			EpisodeLog fb=EpisodeLogPeer.retrieveByPK(tempid);
			fbmgt.update(tempid, 1);
			//设置锁
			EpisodeService.getInstance().updateSingleEpisode(fb.getFkEpisodeId(), 1);
			
			listlog();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String list(){
		listlog();
		return Action.SUCCESS;
	}
	
	public void listlog(){
		if(keyword!=null&&keyword.trim().length()>0)
			keyword=keyword.trim();
		EpisodeLogMgt fbmgt=EpisodeLogMgt.getInstance();
		totalSize=fbmgt.searchSize(keyword, status);
		if(totalSize%Constants.PAGESIZE==0)
			maxpage=totalSize/Constants.PAGESIZE;
		else maxpage=totalSize/Constants.PAGESIZE+1;
		if(page<1)page=1;
		if(maxpage<1)maxpage=1;
		elvos=fbmgt.search(keyword, status, page, Constants.PAGESIZE);
		try {
			encodeKeyword=URLEncoder.encode(keyword,"UTF-8");
		} catch (Exception e) {
			encodeKeyword=keyword;
		}
		request.setAttribute("cuurpage", page);
		request.setAttribute("cuurmaxpage", maxpage);
	}
	
	public String tohandling(){
		int episodeLogId=DataFormat.parseInt(request.getParameter("episodeLogId"));
		if(episodeLogId<1)return Action.ERROR;
		try{
			elvo=EpisodeLogMgt.getInstance().search(episodeLogId);
			elvo.setKey(keyword);
			elvo.setStatus(status);
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		if(null==elvo)return Action.ERROR;
		return Action.SUCCESS;
	}
	
	public String update(){
		int episodeLogId=DataFormat.parseInt(request.getParameter("episodeLogId"));
		int eid=DataFormat.parseInt(request.getParameter("eid"));
		//System.out.println("episodeLogId====="+episodeLogId+"======eid="+eid);
		elvo.setId(episodeLogId);
		elvo.setFkEpisodeId(eid);
		
		try{
			EpisodeAnalyzeService.getInstance().updateSingleEpisode(elvo);
			EpisodeLogMgt fbmgt=EpisodeLogMgt.getInstance();
			fbmgt.update(elvo.getId(), 2);
			
			keyword=elvo.getKey();
			status=elvo.getStatus();
			listlog();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String deleteEpisode(){
		int episodeLogId=DataFormat.parseInt(request.getParameter("episodeLogId"));
		int eid=DataFormat.parseInt(request.getParameter("eid"));
		//System.out.println("episodeLogId====="+episodeLogId+"======eid="+eid);
		elvo.setId(episodeLogId);
		elvo.setFkEpisodeId(eid);
		try{
			BlacklistMgt.getInstance().add2Blacklist(EpisodeMgt.getInstance().getEpisodeByID(eid));
			
			EpisodeMgt.getInstance().deleteEpisodeByEid(eid);
			
			EpisodeLogMgt fbmgt=EpisodeLogMgt.getInstance();
			fbmgt.update(episodeLogId, 2);
			
			keyword=elvo.getKey();
			status=elvo.getStatus();
			listlog();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		
		return Action.SUCCESS;
	}
	
	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}
	
}
