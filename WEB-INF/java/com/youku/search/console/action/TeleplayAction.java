package com.youku.search.console.action;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.juji.EpisodeAnalyzeService;
import com.youku.search.console.operate.juji.EpisodeMgt;
import com.youku.search.console.operate.juji.EpisodeService;
import com.youku.search.console.operate.juji.JujiService;
import com.youku.search.console.operate.juji.JujiUtils;
import com.youku.search.console.operate.juji.PlayNameMgt;
import com.youku.search.console.operate.juji.PlayVersionMgt;
import com.youku.search.console.operate.juji.PlayVersionService;
import com.youku.search.console.operate.juji.TeleplayService;
import com.youku.search.console.operate.juji.TeleplayUpdateService;
import com.youku.search.console.operate.juji.VarietyMgt;
import com.youku.search.console.operate.juji.VersionEpisodeSpiderService;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.vo.EpisodeVO;
import com.youku.search.console.vo.SingleVersion;
import com.youku.search.console.vo.TeleCate;
import com.youku.search.console.vo.TeleName;
import com.youku.search.console.vo.TelePage;
import com.youku.search.console.vo.TeleUpdateVO;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;

public class TeleplayAction extends ActionSupport implements ServletRequestAware,SessionAware{
	String name;
	String alias;
	String message;
	TeleCate tc;
	String excludes;
	TelePage tp;
	int tid=-1;
	int pid=-1;
	int fix=-1;
	int eid=-1;
	int i;
	int count=-2;
	TeleUpdateVO tuvo;
	SingleVersion sv;
	EpisodeVO epvo;
	HttpServletRequest request;
	int status=0;
	String fronturl;
	String encodename;
	int collect_count=0;
	int subcate=0;
	int teleplaySize;
	int maxpage;
	int page;
	int isPrecise=0;
	Map att;
	public Map getAtt() {
		return att;
	}
	public int getIsPrecise() {
		return isPrecise;
	}
	public void setIsPrecise(int isPrecise) {
		this.isPrecise = isPrecise;
	}
	public int getTeleplaySize() {
		return teleplaySize;
	}
	public void setTeleplaySize(int teleplaySize) {
		this.teleplaySize = teleplaySize;
	}
	public int getMaxpage() {
		return maxpage;
	}
	public void setMaxpage(int maxpage) {
		this.maxpage = maxpage;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public TelePage getTp() {
		return tp;
	}
	public void setTp(TelePage tp) {
		this.tp = tp;
	}
	public String getExcludes() {
		return excludes;
	}
	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}
	public TeleCate getTc() {
		return tc;
	}
	public void setTc(TeleCate tc) {
		this.tc = tc;
	}
	public int getSubcate() {
		return subcate;
	}
	public void setSubcate(int subcate) {
		this.subcate = subcate;
	}
	public int getCollect_count() {
		return collect_count;
	}
	public void setCollect_count(int collect_count) {
		this.collect_count = collect_count;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getEid() {
		return eid;
	}
	public void setEid(int eid) {
		this.eid = eid;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public int getFix() {
		return fix;
	}
	public void setFix(int fix) {
		this.fix = fix;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public EpisodeVO getEpvo() {
		return epvo;
	}
	public void setEpvo(EpisodeVO epvo) {
		this.epvo = epvo;
	}
	public SingleVersion getSv() {
		return sv;
	}
	public void setSv(SingleVersion sv) {
		this.sv = sv;
	}
	
	public TeleUpdateVO getTuvo() {
		return tuvo;
	}
	public void setTuvo(TeleUpdateVO tuvo) {
		this.tuvo = tuvo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public String teleplaySave(){
		if(!StringUtils.isBlank(name))
			name=name.trim().toLowerCase();
		if(!StringUtils.isBlank(excludes))
			excludes=excludes.trim().toLowerCase();
		if(!StringUtils.isBlank(alias))
			alias=alias.trim().toLowerCase();
		else alias = null;
		try {
			List<String> al=new ArrayList<String>();
			boolean[] f=JujiService.getInstance().saveTeleplay(name, alias, excludes, tc);
			if(!f[0]){
				if(f[1])
					message="未知异常";
				else message="名称已经存在";
			}else message="保存电视剧成功";
			
			tSearch();
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String teleplayToSave(){
		tc=new TeleCate();
		tc.setViewbeancate();
		return SUCCESS;
	}
	
	public void tSearch(){
		if(name!=null&&name.trim().length()>0)
			name=name.trim().toLowerCase();
		if(null==tp) tp=new TelePage();
		tp.setPage(page);
		tp=JujiService.getInstance().searchTeleplay(name, status,isPrecise,tp);
		teleplaySize=tp.getTeleplaySize();
		maxpage=tp.getMaxpage();
		page=tp.getPage();
		fronturl="teleplaySearch.html";
		try {
			encodename=URLEncoder.encode(name,"UTF-8");
		} catch (Exception e) {
			encodename=name;
		}
		request.setAttribute("cuurpage", tp.getPage());
		request.setAttribute("cuurmaxpage", tp.getMaxpage());
	}
	
	public String teleplaySearch(){
		tSearch();
		return SUCCESS;
	}
	
	public String teleplayToSearch(){
		if(null==tp) tp=new TelePage();
		if(page<1)page=1;
		if(maxpage<1)maxpage=1;
		return SUCCESS;
	}
	
	public String teleplayToUpdate(){
		
		Connection conn = null;
		try {
			conn = DataConn.getTeleplayConn();
			tuvo=TeleplayService.getInstance().searchTeleplayByID(tid,conn);
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		finally {
			JdbcUtil.close(conn);
		}
		return SUCCESS;
	}
	
	public String teleplayUpdate(){
		
		try {
			if(tuvo.getKeyword()!=null&&tuvo.getKeyword().trim().length()>0)
				tuvo.setKeyword(tuvo.getKeyword().trim().toLowerCase());
			if(tuvo.getAliasStr()!=null&&tuvo.getAliasStr().trim().length()>0)
				tuvo.setAliasStr(tuvo.getAliasStr().trim().toLowerCase());
			if(tuvo.getExclude()!=null&&tuvo.getExclude().trim().length()>0)
				tuvo.setExclude(tuvo.getExclude().trim().toLowerCase());
			boolean[] f=TeleplayUpdateService.getInstance().updateTeleplay(tuvo);
			if(!f[0]){
				if(f[1])
					message="未知异常";
				else message="名称已经存在";
			}else{
				message="更新电视剧成功";
			}
			name=tuvo.getKeyword();
			tSearch();
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String teleplayDelete(){
		
		try {
			boolean f=JujiService.getInstance().deleteTeleplay(tid);
			if(f)message="删除电视剧成功";else message="删除电视剧失败";
			if(null==tp) tp=new TelePage();
			teleplaySize=teleplaySize-1<0?0:teleplaySize-1;
			if(teleplaySize%Constants.PAGESIZE==0)
				maxpage=teleplaySize/Constants.PAGESIZE;
			else maxpage=teleplaySize/Constants.PAGESIZE+1;
			if(page<1)page=1;
			if(maxpage<1)maxpage=1;
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String teleplayVersionToSave(){
//		System.out.println("function teleplayVersionToSave tid="+tid);
		sv=new SingleVersion();
		sv.setFkTeleplayId(tid);
		sv.setViewbeancate();
		return SUCCESS;
	}
	
	public String teleplayVersionSave(){
//		System.out.println("function teleplayVersionSave versin name="+sv.getVersionname()+" fkid="+sv.getFkTeleplayId());
		try {
			if(sv.getVersionname()!=null&&sv.getVersionname().trim().length()>0)
				sv.setVersionname(JujiUtils.analyzer(sv.getVersionname().trim().toLowerCase()));
			if(sv.getViewname()!=null&&sv.getViewname().trim().length()>0)
				sv.setViewname(JujiUtils.analyzer(sv.getViewname().trim().toLowerCase()));
			else sv.setVersionname(sv.getVersionname());
			if(sv.getAlias()!=null&&sv.getAlias().trim().length()>0)
				sv.setAlias(JujiUtils.analyzer(sv.getAlias().trim().toLowerCase()));
			boolean f=PlayVersionService.getInstance().addVersion(sv);
			if(f){
				message="添加版本成功";
			}else message="添加版本失败";
			name=PlayNameMgt.getInstance().getTeleplayMainName(sv.getFkTeleplayId());
			tSearch();
			
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String teleplayVersionToUpdate(){
		try {
			PlayVersion pv=PlayVersionMgt.getInstance().getPlayVersionByID(pid);
			if(null==pv) {
				System.out.println("NO teleplayVersion ");
				return ERROR;
			}
			sv=new SingleVersion();
			sv.setPid(pid);
			sv.setFkTeleplayId(pv.getFkTeleplayId());
			sv.setVersionname(pv.getName());
			sv.setViewname(pv.getViewName());
			sv.setCate(pv.getCate());
			sv.setSubcate(pv.getSubcate());
			sv.setAlias(pv.getAlias());
			sv.setTotal_Count(pv.getTotalCount());
			sv.setOrderId(pv.getOrderId());
			sv.setFixed(pv.getFixed());
			sv.setReverse(pv.getReverse());
			sv.setFirstlogo(pv.getFirstlogo());
			sv.setViewbeancate();
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String teleplayVersionUpdate(){
		try {
			if(sv.getVersionname()!=null&&sv.getVersionname().trim().length()>0)
				sv.setVersionname(JujiUtils.analyzer(sv.getVersionname().trim().toLowerCase()));
			if(sv.getViewname()!=null&&sv.getViewname().trim().length()>0)
				sv.setViewname(JujiUtils.analyzer(sv.getViewname().trim().toLowerCase()));
			else sv.setVersionname(sv.getVersionname());
			if(sv.getAlias()!=null&&sv.getAlias().trim().length()>0)
				sv.setAlias(JujiUtils.analyzer(sv.getAlias().trim().toLowerCase()));
			boolean f=PlayVersionService.getInstance().updateVersion(sv);
			if(f){
				message="更新版本成功";
			}else message="更新版本失败";
			
			name=PlayNameMgt.getInstance().getTeleplayMainName(sv.getFkTeleplayId());
			
			tSearch();
			
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}  
	
	public String teleplayVersionDelete(){
		try {
			boolean f=PlayVersionService.getInstance().deleteVersionByID(tid, pid);
			tSearch();
			if(f)message="删除版本成功";else message="删除版本失败";
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String teleplayEpisodeAnalyze(){
		try {
			epvo=VersionEpisodeSpiderService.getInstance().reEpisodeAnalyze(epvo, tid, pid, subcate, count, collect_count);
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getTeleplayEpisode(){
		
		try {
			epvo=EpisodeService.getInstance().getEpisodeVO(tid, pid, count, subcate);
			
			collect_count=epvo.getCollect_count();
		} catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public void tESearch(){
		String tempsearchwords="";
		if(subcate!=2078){
			if(eid<1){
				String tn=PlayNameMgt.getInstance().getTeleplayMainName(tid);
				String pn=PlayVersionMgt.getInstance().getVersionName(pid);
				if(StringUtils.isBlank(pn))tempsearchwords=tn+"第"+i+"集";
				else{
					tempsearchwords=tn+pn+"第"+i+"集";
				}
			}else
				tempsearchwords=TeleplayService.getInstance().getfullWords(eid, false);
		}else{
			TeleName telename=PlayNameMgt.getInstance().getTeleplayNameByIdReturnTeleName(tid);
			String dn=request.getParameter("dn");
			tempsearchwords=telename.getName()+" "+dn;
		}
		String searchwords="";
		try {
			searchwords=URLEncoder.encode(tempsearchwords,"UTF-8");
		} catch (Exception e) {
			searchwords=tempsearchwords;
		}
		request.setAttribute("searchwords", searchwords);
	}
	
	public String teleplayEpisodeSearch(){
		try{
			tESearch();
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String teleplayEpisodeAllSearch(){
		try{
			if(null==tp) tp=new TelePage();
			tp.setPage(page);
			tp=JujiService.getInstance().searchTeleplay(tp);
			teleplaySize=tp.getTeleplaySize();
			maxpage=tp.getMaxpage();
			page=tp.getPage();
			fronturl="teleplaySearchAll.html";
			request.setAttribute("cuurpage", tp.getPage());
			request.setAttribute("cuurmaxpage", tp.getMaxpage());
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String teleplayEpisodeSingleSave(){
		String urlstr=request.getParameter("eurl");
		
		int tid=DataFormat.parseInt(request.getParameter("ttid"),-1);
		int pid=DataFormat.parseInt(request.getParameter("ppid"),-1);
		int eid=DataFormat.parseInt(request.getParameter("eeid"),-1);
		int subcate=DataFormat.parseInt(request.getParameter("subcate"),-1);
		String eurl=urlstr==null?"":urlstr;
		int i=DataFormat.parseInt(request.getParameter("ii"),-1);
		if(tid<0||pid<0||i<0){
			request.setAttribute("f", 0);
			return SUCCESS;
		}
		try{
			eurl=eurl.replace("$","=");
		}catch(Exception e){
			eurl=eurl;
		}
		
		int f=-1;
		try {
			if(subcate!=2078){
				f=EpisodeService.getInstance().updateSingleEpisode(eid, eurl, tid, pid, i);
			}
			else {
				int dn=DataFormat.parseInt(request.getParameter("dn"),-1);
				if(dn<0){
					request.setAttribute("f", 0);
					return SUCCESS;
				}
				VarietyMgt vmgt=VarietyMgt.getInstance();
				f=vmgt.updateSingleVariety(eid,eurl,tid,pid,i,dn);
			}
		} catch(Exception e){
			e.printStackTrace();
			f=-1;
		}
		request.setAttribute("f", f);
		return SUCCESS;
	}
	
	public String teleplayEpisodeSingleDelete(){
		String tidstr=request.getParameter("ttid");
		String pidstr=request.getParameter("ppid");
		String istr=request.getParameter("ii");
		String eidstr=request.getParameter("eeid");
//		System.out.println("function teleplayEpisodeSingleDelete tid="+tidstr+"========pid="+pidstr+"=======eid="+eidstr+"========i="+istr);
		try{
			EpisodeService.getInstance().deleteEpisode(Integer.parseInt(tidstr), Integer.parseInt(pidstr), Integer.parseInt(eidstr), Integer.parseInt(istr), subcate);
			
			epvo=EpisodeService.getInstance().getEpisodeVO(Integer.parseInt(tidstr), Integer.parseInt(pidstr), count, subcate);
			
			collect_count=epvo.getCollect_count();
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String teleplayEpisode2AddBlack(){
		int pid=DataFormat.parseInt(request.getParameter("ppid"),-1);
		int eid=DataFormat.parseInt(request.getParameter("eeid"),-1);
		int dn=DataFormat.parseInt(request.getParameter("dn"),-1);
		boolean f=false;
		String tempurl="";
		try {
			f=EpisodeAnalyzeService.getInstance().add2blacklist(eid,pid,dn);
			tempurl=EpisodeMgt.getInstance().getEpisodeUrl(eid);
		} catch(Exception e){
			e.printStackTrace();
		}
		StringBuffer sbf=new StringBuffer();
		sbf.append("<?xml version=\"1.0\" ?>");
		sbf.append("<responses>");
		sbf.append("<flag>");
		if(f)
			sbf.append("1");
		else
			sbf.append("0");
		sbf.append("</flag>");
		sbf.append("<url>");
		sbf.append(tempurl);
		sbf.append("</url>");
		sbf.append("</responses>");
		request.setAttribute("sbf", sbf.toString());
		System.out.println(sbf);
		return SUCCESS;
	}
	
	public String lock(){
		int isLock=DataFormat.parseInt(request.getParameter("isLock"),0);
		int eid=DataFormat.parseInt(request.getParameter("eeid"),-1);
		try{
			EpisodeService.getInstance().updateSingleEpisode(eid, isLock);
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("flag", isLock==1?0:1);
			return SUCCESS;
		}
		request.setAttribute("flag", isLock);
		return SUCCESS;
	}
	
	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getFronturl() {
		return fronturl;
	}
	public void setFronturl(String fronturl) {
		this.fronturl = fronturl;
	}
	public String getEncodename() {
		return encodename;
	}
	public void setEncodename(String encodename) {
		this.encodename = encodename;
	}
	
	public void setSession(Map arg0) {
		this.att=arg0;
	}
	
}
