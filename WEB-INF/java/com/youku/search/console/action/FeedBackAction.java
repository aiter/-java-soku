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
import com.youku.search.console.operate.juji.BlacklistMgt;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.juji.EpisodeAnalyzeService;
import com.youku.search.console.operate.juji.EpisodeMgt;
import com.youku.search.console.operate.juji.EpisodeService;
import com.youku.search.console.operate.juji.FeedBackMgt;
import com.youku.search.console.operate.juji.PlayNameMgt;
import com.youku.search.console.operate.juji.PlayVersionMgt;
import com.youku.search.console.operate.juji.TeleplayService;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.pojo.User;
import com.youku.search.console.vo.FeedBackHandleVO;
import com.youku.search.console.vo.FeedBackVO;
import com.youku.search.console.vo.FeedbackUnion;
import com.youku.search.console.vo.TeleName;
import com.youku.search.util.DataFormat;

public class FeedBackAction implements SessionAware, ServletRequestAware{
	HttpServletRequest request;
	List<FeedBackVO> fbvos;
	int page=0;
	int maxpage=0;
	String message;
	String keyword;
	int totalSize;
	String encodeKeyword;
	FeedBackHandleVO fbhvo;
	String result;
	int order=0;
	private Map att;
	List<FeedbackUnion> fus;
	
	public List<FeedbackUnion> getFus() {
		return fus;
	}

	public void setFus(List<FeedbackUnion> fus) {
		this.fus = fus;
	}

	public Map getAtt() {
		return att;
	}

	public void setAtt(Map att) {
		this.att = att;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public FeedBackHandleVO getFbhvo() {
		return fbhvo;
	}


	public void setFbhvo(FeedBackHandleVO fbhvo) {
		this.fbhvo = fbhvo;
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


	public List<FeedBackVO> getFbvos() {
		return fbvos;
	}


	public void setFbvos(List<FeedBackVO> fbvos) {
		this.fbvos = fbvos;
	}

	public HttpServletRequest getRequest() {
		return request;
	}


	public String add() {
		try {
			addfeed();
		} catch(Exception e){
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}
	public void addfeed(){
		String callback=request.getParameter("callback");
		if(null==callback||callback.trim().length()<1)
			callback=null;
		int episodeId=DataFormat.parseInt(request.getParameter("episodeId"));
		if(episodeId<1){
			if(null==callback)
				result="{\"result\":\"false\"}";
			else
				result=callback+"({\"result\":\"false\"})";
			return ;
		}
		int errorType=DataFormat.parseInt(request.getParameter("errorType"));
		if(errorType<1){
			if(null==callback)
				result="{\"result\":\"false\"}";
			else
				result=callback+"({\"result\":\"false\"})";
			return ;
		}
		String errorContent=request.getParameter("errorContent");
		Episode e=EpisodeMgt.getInstance().getEpisodeByID(episodeId);
		
		if(e==null){
			if(null==callback)
				result="{\"result\":\"false\"}";
			else
				result=callback+"({\"result\":\"false\"})";
			return ;
		}
		
		String tempkeyword=null;
		PlayVersion pv = PlayVersionMgt.getInstance().getPlayVersionByID(e.getFkVersionId());
		if(null==pv){
			result="{\"result\":\"false\"}";
			return;
		}
		if(pv.getSubcate()!=2078)
			tempkeyword=TeleplayService.getInstance().getfullWords(episodeId, true);
		else{
			TeleName telename=PlayNameMgt.getInstance().getTeleplayNameByIdReturnTeleName(e.getFkTeleplayId());
			tempkeyword=telename.getName()+e.getOrderId();
		}
		boolean flag=FeedBackMgt.getInstance().save(e,errorType,errorContent,tempkeyword);
		if(flag){
			if(null==callback)
				result="{\"result\":\"true\"}";
			else
				result=callback+"({\"result\":\"true\"})";
		}else {
			if(null==callback)
				result="{\"result\":\"false\"}";
			else
				result=callback+"({\"result\":\"false\"})";
		}
	}

	
	public String delete(){
		int episodeId=DataFormat.parseInt(request.getParameter("episodeId"));
		if(episodeId<1)return Action.SUCCESS;
		try{
			User u=UserMgt.getInstance().findById(""+att.get("user_id"));
			String uname="sys";
			if(null!=u)uname=u.getTrueName();
			FeedBackMgt.getInstance().deleteFeedbackByepisodeID(episodeId,1,uname);
			//设置锁
			EpisodeService.getInstance().updateSingleEpisode(episodeId, 1);
			
			listFeed();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public void listFeed(){
		if(keyword!=null&&keyword.trim().length()>0)
			keyword=keyword.trim();
		totalSize=FeedBackMgt.getInstance().searchSize(keyword);
		if(totalSize%Constants.PAGESIZE==0)
			maxpage=totalSize/Constants.PAGESIZE;
		else maxpage=totalSize/Constants.PAGESIZE+1;
		if(page<1)page=1;
		if(maxpage<1)maxpage=1;
		if(page>maxpage) page=maxpage;
		fbvos=FeedBackMgt.getInstance().search(keyword, page,Constants.PAGESIZE, order);
		try {
			encodeKeyword=URLEncoder.encode(keyword,"UTF-8");
		} catch (Exception e) {
			encodeKeyword=keyword;
		}
		request.setAttribute("cuurpage", page);
		request.setAttribute("cuurmaxpage", maxpage);
	}
	
	public String list(){
		try{
			listFeed();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String tohandling(){
		int episodeId=DataFormat.parseInt(request.getParameter("episodeId"));
		if(episodeId<1)return Action.ERROR;
		int pkFeedbackId=DataFormat.parseInt(request.getParameter("pkFeedbackId"));
		if(pkFeedbackId<1)return Action.ERROR;
		int page=DataFormat.parseInt(request.getParameter("page"));
		try{
			fbhvo=FeedBackMgt.getInstance().search(episodeId, pkFeedbackId);
			if(null==fbhvo)return Action.ERROR;
			fbhvo.setPage(page);
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		if(fbhvo!=null){
			fbhvo.setKeyword(keyword);
			return Action.SUCCESS;
		}
		else return Action.ERROR;
	}
	
	public String update(){
		int pkFeedbackId=DataFormat.parseInt(request.getParameter("pkFeedbackId"));
		int episodeId=DataFormat.parseInt(request.getParameter("episodeId"));
		//System.out.println("name====="+fbhvo.getEvo().getName()+"----subcate="+fbhvo.getEvo().getSubcate());
		fbhvo.setPkFeedbackId(pkFeedbackId);
		fbhvo.getEvo().setId(episodeId);
		try{
			EpisodeAnalyzeService.getInstance().updateSingleEpisode(fbhvo.getEvo());
			User u=UserMgt.getInstance().findById(""+att.get("user_id"));
			String uname="sys";
			if(null!=u)uname=u.getTrueName();
			FeedBackMgt.getInstance().deleteFeedbackByepisodeID(episodeId,2,uname);
			keyword=fbhvo.getKeyword();
			listFeed();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		
		return Action.SUCCESS;
	}
	
	public String deleteEpisode(){
		int pkFeedbackId=DataFormat.parseInt(request.getParameter("pkFeedbackId"));
		int episodeId=DataFormat.parseInt(request.getParameter("episodeId"));
		fbhvo.setPkFeedbackId(pkFeedbackId);
		fbhvo.getEvo().setId(episodeId);
		try{
			BlacklistMgt.getInstance().add2Blacklist(EpisodeMgt.getInstance().getEpisodeByID(episodeId));
			
			EpisodeMgt.getInstance().deleteEpisodeByEid(fbhvo.getEvo().getId());
			
			String uname="sys";
			User u=UserMgt.getInstance().findById(""+att.get("user_id"));
			if(null!=u)uname=u.getTrueName();
			FeedBackMgt.getInstance().deleteFeedbackByepisodeID(episodeId,2,uname);
			keyword=fbhvo.getKeyword();
			listFeed();
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String unionFeedbacks(){
		Connection conn =null;
		try{
			conn =DataConn.getTeleplayConn();
			fus=FeedBackMgt.getInstance().getUnionFeedback(conn);
			//totalSize=fbmgt.searchSize(null,conn);
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}finally{
			DataConn.releaseConn(conn);
		}
		return Action.SUCCESS;
	}
	
	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}

	public void setSession(Map arg0) {
		this.att = arg0;
	}
	
}
