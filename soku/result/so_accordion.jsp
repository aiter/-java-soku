<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>
<%@page import="com.youku.soku.netspeed.SpeedDb"%>
<%@page import="com.youku.soku.netspeed.Speed"%>
<%@page import="com.youku.soku.netspeed.IpArea.Region"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="javax.servlet.http.Cookie"%>
<%@page
	import="java.util.*,com.youku.soku.sort.Parameter,com.youku.soku.web.SearchResult,
			com.youku.soku.web.util.*,
			com.youku.soku.util.Constant,com.youku.soku.util.MyUtil,org.json.*,
			com.youku.soku.web.SearchResult.Content,
			com.youku.search.util.DataFormat,
			com.youku.soku.index.manager.db.SiteManager,
			org.apache.commons.lang.StringUtils,
			com.youku.common.http.ResinChineseUrl"%>
<%@page import="java.text.SimpleDateFormat"%>
<%! 
int getSpeed(int site,Map<Integer,Speed> speedMap, Region currentRegion){
	if(speedMap.containsKey(site)){
		return speedMap.get(site).getValue();
	}else{
		Speed speed=SpeedDb.getSpeedByRegion(currentRegion,site);
		speedMap.put(site,speed);
		return speed.getValue();
	}
	
}


String printStar(Double db){
	StringBuffer resultBf=new StringBuffer();
	if(db<=0) return "";
	int fullInt=new Double(db/2).intValue(); 
	int index=0;
	for( index=0;index<fullInt;index++){
		resultBf.append("<em class='ico__ratefull'></em>");
	}
	
	if(db/2-fullInt>0){
		resultBf.append("<em class='ico__ratepart'></em>");
		index++;
	}
	
	
	for( ;index<5;index++){
		resultBf.append("<em class='ico__ratenull'></em>");
	}
	
	DecimalFormat df=new DecimalFormat("#.0"); 
	resultBf.append("<em class='num'>"+df.format(db)+"</em>");
	
	return resultBf.toString();
  }

String showPerformer(JSONArray performerArr,String highLightName,int log_type,int log_ct,int log_pos,int log_directpos){
	if(performerArr==null || performerArr.length()==0) return "";
	
	StringBuffer resultBf=new StringBuffer();
	try{
	for(int i=0;i<performerArr.length()&& i<4;i++){
		JSONObject elePersonObj=performerArr.optJSONObject(i);
		if(elePersonObj==null) continue;
		String personName=StringUtils.trimToEmpty(elePersonObj.optString("name"));
		String encodePerformer=MyUtil.urlEncode(personName);
		
		if(!highLightName.equals(personName)){
			resultBf.append("<a  href='/v?keyword="+encodePerformer+"' target='_self'")
				.append(" onclick='sokuClickStat(this);' " + " _log_type="+log_type+"  _log_ct="+log_ct+"  _log_pos="+log_pos+" _log_directpos="+log_directpos+">"+
					((personName.length()>10)?personName.substring(0,10):personName)
						+"</a>").append(" / ");
		}else{
			resultBf.append("<a href='/v?keyword="+encodePerformer+"' target='_self'")
				.append(" onclick='sokuClickStat(this);' "+ "  _log_type="+log_type+"  _log_ct="+log_ct+"  _log_pos="+log_pos+" _log_directpos="+log_directpos+
				"><span style='color: #CC0000;'>"+
					((personName.length()>10)?personName.substring(0,10):personName)
					+"</span></a>").append(" / ");
		}
	}
	String returnStr=resultBf.toString();
	if(returnStr.length()>0) returnStr=returnStr.substring(0,returnStr.length()-2);

	return returnStr;
	}catch(Exception e){
		System.out.println("error:"+e.getMessage());
		return "";
	}
 }



  
  String showPic(String firstLogo,String middPic,String missPic){
	  //竖版的无截图
	  if(firstLogo!=null && StringUtils.trimToEmpty(firstLogo).length()>0 &&
			  !firstLogo.endsWith("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E")){
		  return StringUtils.trimToEmpty(firstLogo);
	  }else if(middPic!=null && StringUtils.trimToEmpty(middPic).length()>0){
		  return StringUtils.trimToEmpty(middPic);
	  }else{
		  return missPic;
	  }
	  
	  
  }
  
  String showBG(String updateStatus, String seconds,String hdflag){
	  if( (updateStatus==null || StringUtils.trimToEmpty(updateStatus).length()==0)&& 
			  (hdflag==null || StringUtils.trimToEmpty(hdflag).length()==0) &&
			  (seconds==null || StringUtils.trimToEmpty(seconds).length()==0))
	  {
		return "";
	  }else{
		  return "bg";
	  }
	  
  }
String getClientIp(HttpServletRequest request){
	String clientIp=request.getHeader("X-Forwarded-For");
	if(clientIp != null){
			int pos = clientIp.indexOf(",");
			if (pos > -1)clientIp = clientIp.substring(0,pos);
	}
	return clientIp;
}
%>
<%  
	String clientIp=getClientIp(request);
	Region currentRegion=SpeedDb.getRegion(clientIp);
	
	Map<Integer,Speed> speedMap=new HashMap<Integer,Speed>();

	SearchResult result = (SearchResult) request
			.getAttribute("content");
	int pagesize = Constant.Web.SEARCH_PAGESIZE;
	String cost = result.getCost();
	Content content = result.getContent();
	int total = content.getTotal();

	JSONObject extJson = content.getExt();
	int extLength=0;
	if(extJson!=null) extLength=extJson.optInt("size");
	
	
	String encodeKeyword = MyUtil.urlEncode(result.getKeyword());
	
	Parameter param = result.getParam();
	
	String url="/v?keyword="+encodeKeyword;
	
	String pre_time_length_str=null,pre_limit_date_str=null,pre_hd_str=null,pre_site_str=null;
	ResinChineseUrl _request = (ResinChineseUrl)request.getAttribute(ResinChineseUrl.STORE_KEY);
	if(_request != null){
		pre_time_length_str=_request.getParameter("time_length");
		pre_limit_date_str=_request.getParameter("limit_date");
		pre_hd_str=_request.getParameter("hd");
		pre_site_str=_request.getParameter("site");
	}else{
		pre_time_length_str=request.getParameter("time_length");
		pre_limit_date_str=request.getParameter("limit_date");
		pre_hd_str=request.getParameter("hd");
		pre_site_str=request.getParameter("site");
	}
	
	
	String pre_time="",pre_date="",pre_hd="",pre_site="";
	
	if(pre_time_length_str!=null){
	  pre_date=pre_date+"&time_length="+pre_time_length_str;
	  pre_hd=pre_hd+"&time_length="+pre_time_length_str;
	  pre_site=pre_site+"&time_length="+pre_time_length_str;
	}
	
	if(pre_limit_date_str!=null){
	  pre_time=pre_time+"&limit_date="+pre_limit_date_str;
	  pre_hd=pre_hd+"&limit_date="+pre_limit_date_str;
	  pre_site=pre_site+"&limit_date="+pre_limit_date_str;
	}
	
	if(pre_hd_str!=null){
		pre_time=pre_time+"&hd="+pre_hd_str;
		pre_date=pre_date+"&hd="+pre_hd_str;
	  pre_site=pre_site+"&hd="+pre_hd_str;
	}
	
	if(pre_site_str!=null){
		pre_time=pre_time+"&site="+pre_site_str;
		pre_date=pre_date+"&site="+pre_site_str;
	    pre_hd=pre_hd+"&site="+pre_site_str;
	}

	boolean hasDirect = false;
%>

<!DOCTYPE HTML>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=result.getKeyword()%> 搜库 视频搜索</title>

<link type="text/css" rel="stylesheet" href="/css/soku.css">
<link rel="stylesheet" type="text/css" href="/css/accordion.css" />

<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/sotool.js"></script>
<script language="javascript" type="text/javascript" src="/js/jquery.msAccordion.js"></script>
<script type="text/javascript">
_glogParam.curpage=<%=param.page%>;
_glogParam.keyword='<%=encodeKeyword%>';
</script>
</head><body>
<div class="window">
<div class="screen">

<% String keyword = result.getKeyword();%>
<%@ include file="inc/header.jsp" %>

<div class="body">
<div class="typechk">
<div class="main">
		<ul>
		<%    String pidStr=request.getParameter("pid");
			  String personNameDetail=request.getParameter("personName");
		if(pidStr!=null){
			out.print("<li class='current'><span>视频</span></li>");
			out.print("<li><a href='/a?keyword="+encodeKeyword+"&pid="+pidStr+"'>专辑</a></li>");
			//out.print("<li><a href='/k?keyword="+keyword+"&pid="+pidStr+"'>知识</a></li>");
			out.print("<li style='margin-left:10px;'><a href='/detail/show/"+pidStr+"?keyword="+encodeKeyword+"'>详情</a></li>");		
			
		}else if(personNameDetail!=null){
			out.print("<li class='current'><span>视频</span></li>");
			out.print("<li><a href='/a?keyword="+encodeKeyword+"&personName="+MyUtil.urlEncode(personNameDetail)+"'>专辑</a></li>");
			//out.print("<li><a href='/k?keyword="+keyword+"&personName="+personNameDetail+"'>知识</a></li>");
			out.print("<li style='margin-left:10px;'><a href='/detail/person/"+personNameDetail+"?keyword="+MyUtil.urlEncode(keyword)+"'>详情</a></li>");
		}
		else{
			out.print("<li class='current'><span>视频</span></li>");
			out.print("<li><a href='/a?keyword="+encodeKeyword+"'>专辑</a></li>");
			//out.print("<li><a href='/k?keyword="+keyword+"'>知识</a></li>");
			
		}
		%>
						
		</ul>
	</div>
</div><div class="layout_16">	

	
	
	<div class="maincol">
		<%if (content.getCorrect_word()!=null){%>
			<div class="keylike">你是不是要找： "<a href="/v?keyword=<%=MyUtil.urlEncode(content.getCorrect_word())%>"><%=content.getCorrect_word()%></a>" </div>
			
			<%}%>
	<div id="accordionGiftLelo" class="direct">		

<% 

for(int i=0;i<extLength;i++){
	JSONObject channelJson=extJson.optJSONObject(new Integer(i).toString());
	if(channelJson==null) continue;
	String nameType=StringUtils.trimToEmpty(channelJson.optString("nameTypes"));
	String searchKey=channelJson.optString("searchKey");
	if(nameType.equals("MOVIE")){
		JSONArray programmeArr=channelJson.optJSONArray(nameType);
		if(programmeArr==null && programmeArr.length()<=0) continue;
		for(int j=0;j<programmeArr.length();j++){
			JSONObject programmeJson=programmeArr.getJSONObject(j);
			
			JSONObject proInfo=programmeJson.optJSONObject("programme");
			JSONObject middInfo=programmeJson.optJSONObject("midd");
			JSONObject psInfo=programmeJson.optJSONObject("ProgrammeSite");
			
			if(proInfo==null || middInfo==null)continue;
			
			hasDirect = true;

			String playUrl=StringUtils.trimToEmpty(proInfo.optString("url"));
			if(playUrl.length()==0) continue;
			
			
			String pic=showPic(StringUtils.trimToEmpty(proInfo.optString("vpic")),"",
					"http://g1.ykimg.com/0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E");
			
			int programmeId=proInfo.optInt("programmeId");
			
			String realeaseyear=StringUtils.trimToEmpty(proInfo.optString("releaseyear"));
			
			JSONArray siteArr=proInfo.optJSONArray("sites");
			
			String highLightPerson=StringUtils.trimToEmpty(proInfo.optString("highLightPerson"));
			
	%>	
	<div class="set">
	 <div class="title">
	 	<h4><%=StringUtils.trimToEmpty(proInfo.optString("highLightName"))%><%if(proInfo.has("aliasName")){%>
                                (<%=proInfo.optString("aliasName")%>)<%}%> </h4> </div>
	<div class="content">		
	<div class="item">
	<div class="movie">
		<ul class="base">
		<%String detailUrl="/detail/show/"+com.youku.search.util.MyUtil.encodeVideoId(programmeId)+"?keyword="+encodeKeyword;
		%>
			<li class="base_name"><h1><a href="<%=detailUrl %>" target="_self" 
				onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="2" _log_pos="<%=i+1 %>"  _log_directpos="2" >
				<%=StringUtils.trimToEmpty(proInfo.optString("highLightName"))%></a><%if(proInfo.has("aliasName")){%>
                                (<%=proInfo.optString("aliasName")%>)<%}%>   
				</h1> </li>
				<li class="base_what">&nbsp;<%=realeaseyear%> 电影</li>
				<%String mainPerformer=showPerformer(proInfo.optJSONArray("performer"),highLightPerson,2,2,i+1,3); %>
			<li class="base_actor"> <label><%=mainPerformer.length()>0?"主演：":"" %> </label><span><%=mainPerformer%></span></li>     
			<li class="base_rating"><div class="rating"><%=printStar(proInfo.optDouble("score"))%></div></li>
		</ul><!--base end-->
		<div class="detail">
			<div class="G">
				<ul class="p pv">
					<li class="p_link"><a href="<%=detailUrl %>" target="_self"  
					 	onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="2" _log_pos="<%=i+1 %>"  _log_directpos="1"  
						title="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></a></li>
					<li class="p_thumb"><img src="<%=pic%>" alt="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></li>
					<li class="p_status"><span class="status">正片</span><span class="bg"></span></li>
					<li class="p_ishd"><span class="" title=""></span></li>			
				</ul>
				<ul class="rel">
					
				</ul>
				<div class="clear"></div>
			</div><!--G end-->
			<div class="T">
			<%String littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")); if(StringUtils.trimToEmpty(proInfo.optString("brief")).length()>300)littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")).substring(0,300)+"...";  %>
				<div class="intro"><%=StringUtils.trimToEmpty(littleBrief).equals("null")?"":StringUtils.trimToEmpty(littleBrief)%>
				<a  onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="2" _log_pos="<%=i+1 %>"  _log_directpos="6"  
					href="<%=detailUrl %>" target="_self">查看详情&gt;&gt;</a></div>
				
				<%Iterator<String> siteIterator=psInfo.keys(); %>
				<div style="display:none;">
				<%if(siteIterator!=null){
					int movieIndex=1;
					while(siteIterator.hasNext()){
						String  siteStr=siteIterator.next();
						if(siteStr==null) continue;
					%>
				<%if(movieIndex==1){%>
					<div class="linkpanels site<%=siteStr%>">	
				<%}else{%>
					<div style="display:none;" class="linkpanels site<%=siteStr%>">
				<%}
				movieIndex++;
				%>
					<ul class="linkpanel panel_15">
					<%JSONArray episodesArr= psInfo.optJSONObject(siteStr).optJSONArray("episodes");
					  String siteUrl="";
					  if(episodesArr!=null && episodesArr.length()>0){
						  siteUrl=StringUtils.trimToEmpty(episodesArr.optJSONObject(0).optString("url"));
					  }
					  %>
					<li><a  href="<%=siteUrl%>"></a></li>
					</ul>
					<div class="clear"></div>
				</div> <!-- linkpanels end -->
				<%} }%>
				</div>
				
				
				
				<div class="gotoplay">
					<div class="btnplay_large"><a target="_blank" 
						onclick="sokuClickStat(this);" _log_type="2"    _log_ct="2" _log_pos="<%=i+1 %>"  _log_directpos="5">播放</a></div>
					
					<% if(siteArr!=null && siteArr.length()>0){
						
						if(siteArr.length()<=1){
							out.print("<div class='source  source_one'> ");
						}else{
							out.print("<div class='source'> ");
						}
						
						//获取每一个站点
						String eleSiteStr=siteArr.getString(0);
						JSONObject tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
						if(tmpSite==null) continue;
						
						String hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
						String stypeName="";
						String stype="";
						if(hdStr!=null && hdStr.length()>0){
							stypeName=hdStr.split(",")[0];
							stype=hdStr.split(",")[1];
						}
						
						int speedInt=5;
						//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
						speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
						
						out.print("<div class='check'><label  _log_ct='2'  _log_pos='"+(i+1)+"' title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
								" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
								SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></div>");
						if(siteArr.length()>1) out.print("<ul class='other'>");
						for(int speedIndex=1;speedIndex<siteArr.length();speedIndex++){
							eleSiteStr=siteArr.getString(speedIndex);
							tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
							if(tmpSite==null) continue;
							
							hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
							stypeName="";
							stype="";
							if(hdStr!=null && hdStr.length()>0){
								stypeName=hdStr.split(",")[0];
								stype=hdStr.split(",")[1];
							}
							
							
							speedInt=5;
							//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
							speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
							
							out.print("<li><label  _log_ct='2'  _log_pos='"+(i+1)+"'  title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
									" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
									SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></li>");
						}
						if(siteArr.length()>1) out.print("</ul>");
						}
					
						 %>
					
					</div>
				</div>
			</div><!--T end-->
			<div class="clear"></div>
		</div><!--detail end-->
	</div><!--movie end-->
</div><!--item end-->	
</div><!-- accordion set content end -->	
</div><!-- accordion set end -->	
	<%}}
	else if(nameType.equals("TELEPLAY")){
		JSONArray programmeArr=channelJson.optJSONArray(nameType);
		if(programmeArr==null && programmeArr.length()<=0) continue;
		for(int j=0;j<programmeArr.length();j++){
		JSONObject programmeJson=programmeArr.getJSONObject(j);
		int currentEpisodeNumber = channelJson.optInt("currentEpisodeNumber");
		JSONObject proInfo=programmeJson.optJSONObject("programme");
		JSONObject middInfo=programmeJson.optJSONObject("midd");
		JSONObject psInfo=programmeJson.optJSONObject("ProgrammeSite");
		if(proInfo==null || middInfo==null || psInfo==null)continue;
		
		int programmeId=proInfo.optInt("programmeId");
		
		int episodeTotal=proInfo.optInt("episodeTotal");
		
		JSONArray siteArr=proInfo.optJSONArray("sites");
		if(siteArr==null || siteArr.length()<=0) continue;
		String updateTo="";
		JSONObject currentSite=psInfo.optJSONObject(siteArr.getString(0).split(":")[0]);
		if(currentSite==null) continue;

		hasDirect = true;
		//if(episodeArr.getJSONObject(0).optInt("orderId")>1) updateTo=episodeArr.getJSONObject(0).optInt("orderId");
		String realeaseyear=StringUtils.trimToEmpty(proInfo.optString("releaseyear"));
		
		String updateNotice=StringUtils.trimToEmpty(proInfo.optString("update_notice"));
		//横版无截图
		String pic=showPic(proInfo.optString("vpic"),proInfo.optString("pic"),
				"http://g1.ykimg.com/0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80");
		boolean showV=pic.equals(proInfo.optString("vpic"));
		
		String highLightPerson=StringUtils.trimToEmpty(proInfo.optString("highLightPerson"));
		
		String playUrl=StringUtils.trimToEmpty(proInfo.optString("url"));
		
		String detailUrl="/detail/show/"+com.youku.search.util.MyUtil.encodeVideoId(programmeId)+"?keyword="+encodeKeyword;
		
	%>		
<!--电视剧-->
<div class="set">
	 <div class="title"> <%=i %></div>
	<div class="content">
<div class="item">
	<div class="tv">
		<ul class="base">
			<li class="base_name"><h1><a href="<%=detailUrl %>" target="_self"
				onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="1" _log_pos="<%=i+1 %>"  _log_directpos="2" >
				<%=StringUtils.trimToEmpty(proInfo.optString("highLightName"))%></a><%if(proInfo.has("aliasName")){%>
                                (<%=proInfo.optString("aliasName")%>)<%}%>   
			
			</h1></li>
			<li class="base_what"><%if(episodeTotal>0) out.print(episodeTotal+"集");%>电视剧</li>
			<%String mainPerformer=showPerformer(proInfo.optJSONArray("performer"),highLightPerson,2,1,i+1,3); %>
			<li class="base_actor"><label><%=mainPerformer.length()>0?"|&nbsp;&nbsp;主演：":"" %> </label><span><%=mainPerformer%></span></li>     
			<li class="base_update"><label><%if(updateNotice.length()>0){out.print("更新："+updateNotice);}%></label> </li>

			<li class="base_rating"><div class="rating"><%=printStar(proInfo.optDouble("score"))%></div></li>
		</ul><!--base end-->
		<div class="detail">
			<div class="G">
				<ul class="p <%=showV?"pv":"" %>">
					<li class="p_link"><a href="<%=detailUrl %>" target="_self" title="<%StringUtils.trimToEmpty(proInfo.optString("name"));%>" 
					onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="1" _log_pos="<%=i+1 %>"  _log_directpos="1"></a></li>
					<li class="p_thumb"><img src="<%=pic%>" alt="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></li>
					<li class="p_status"><span class="status"><%=currentSite.optString("display_status") %></span><span class="bg"></span></li>
					<li class="p_ishd"><span title="" class=""></span></li>
				</ul>
				<ul class="rel">
					
				</ul>
				<div class="clear"></div>
			</div><!--G end-->
			<div class="T">
				<%for(int ii=0;ii<siteArr.length();ii++){
					String eleSite=siteArr.getString(ii);
					if(ii==0){
						out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"'>");	
					}else{
						out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"' style='display: none;'> ");
					}
					
					out.print("<ul class='linkpanel panel_15'>");
				    
					//获取每一个站点
					JSONObject tmpSite=psInfo.optJSONObject(eleSite.split(":")[0]);
					if(tmpSite==null) continue;
					//得到每一个站点的剧集列表
					JSONArray episodeArr=tmpSite.optJSONArray("episodes");
					if(episodeArr==null) continue;
					
			        for(int episodeIndex=0;episodeIndex<episodeArr.length()&& episodeIndex<43;episodeIndex++){
			        		
							if(episodeArr.getJSONObject(episodeIndex)==null)continue;
							int orderId = episodeArr.getJSONObject(episodeIndex).optInt("orderId");
							int orderStage=episodeArr.getJSONObject(episodeIndex).optInt("orderStage");
							if (currentEpisodeNumber > 0 && orderId == currentEpisodeNumber){
								%>
								<li class="aim">
								<div class="aimtip">
								<div class="close" onclick="this.parentNode.style.display = 'none';"></div>
								<div class="arrow"></div>
									<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>	第<%=orderId%>集
								</div>
								<%
							}
							else{
								out.println("<li>");
							}
							String link = episodeArr.getJSONObject(episodeIndex).optString("url");
							if (link!=null && !link.isEmpty()){
								out.print("<a href='"+StringUtils.trimToEmpty(link)+
									"' onclick='sokuClickStat(this);' _log_type='2' _log_ct='1' _log_pos="+(i+1)+"  _log_directpos='4' "+
									" target='_blank'>"+ ((orderStage<=0 || orderStage>2000)?orderId:orderStage) +"</a></li>");
							}
							else{
								//没链接地址显示为灰色
							}
				    }
						%>	
						<%	if(episodeArr.length()>43){ %>									
							<li class="handle"><span>全部</span></li><%} %>
						</ul>
						<div class="clear"></div>
						<div class="panellocation">
						<div class="panelexpand">
						<ul class="linkpanel panel_15">
								<%for(int jj=43;jj<episodeArr.length();jj++){
								if(episodeArr.getJSONObject(jj)==null)continue;
									out.print("<li><a href='"+StringUtils.trimToEmpty(episodeArr.getJSONObject(jj).optString("url"))+
											"' onclick='sokuClickStat(this);' _log_type='2' _log_ct='1' _log_pos="+(i+1)+"  _log_directpos='4' "+
										">"+episodeArr.getJSONObject(jj).optInt("orderId")+"</a></li>");
								}%>																						
						</ul>
						<div class="clear"></div>
						</div>	
						</div>
			    </div>
			    <%} %>
				
				<div class="gotoplay">
					<div class="btnplay_large"><a target="_blank" 
					onclick="sokuClickStat(this);" _log_type="2"    _log_ct="1" _log_pos="<%=i+1 %>"  _log_directpos="5" >播放</a></div>
					
					<% if(siteArr!=null && siteArr.length()>0){
						
						if(siteArr.length()<=1){
							out.print("<div class='source  source_one'> ");
						}else{
							out.print("<div class='source'> ");
						}
						
						//获取每一个站点
						String eleSiteStr=siteArr.getString(0);
						JSONObject tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
						if(tmpSite==null) continue;
						
						String hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
						String stypeName="";
						String stype="";
						if(hdStr!=null && hdStr.length()>0){
							stypeName=hdStr.split(",")[0];
							stype=hdStr.split(",")[1];
						}
						
						
						int speedInt=5;
						//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
						speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
						
						out.print("<div class='check'><label  _log_ct='1'  _log_pos='"+(i+1)+"'  title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
								" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
								SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></div>");
						if(siteArr.length()>1) out.print("<ul class='other'>");
						for(int speedIndex=1;speedIndex<siteArr.length();speedIndex++){
							eleSiteStr=siteArr.getString(speedIndex);
							tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
							if(tmpSite==null) continue;
							
							hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
							stypeName="";
							stype="";
							if(hdStr!=null && hdStr.length()>0){
								stypeName=hdStr.split(",")[0];
								stype=hdStr.split(",")[1];
							}
							
							
							speedInt=5;
							//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
							speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
							
							out.print("<li><label  _log_ct='1'  _log_pos='"+(i+1)+"' title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
									" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
									SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></li>");
						}
						if(siteArr.length()>1) out.print("</ul>");
						}
					
						 %>
					</div>
				</div>
				<div class="like"></div>
				<%String littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")); if(StringUtils.trimToEmpty(proInfo.optString("brief")).length()>100)littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")).substring(0,100)+"...";  %>
				<div class="intro"><%=StringUtils.trimToEmpty(littleBrief).equals("null")?"":StringUtils.trimToEmpty(littleBrief)%>
				<a  href="<%=detailUrl %>" target="_self" 
				onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="1" _log_pos="<%=i+1 %>"  _log_directpos="6">查看详情&gt;&gt;</a></div>
			</div><!--T end-->
			<div class="clear"></div>
		</div><!--detail end-->
	</div><!--tv end-->
</div><!--item end-->
</div><!-- accordion set content end -->	
</div><!-- accordion set end -->
	<%
		}
	}else if(nameType.equals("ANIME")){
		JSONArray programmeArr=channelJson.optJSONArray(nameType);
		if(programmeArr==null || programmeArr.length()<=0) continue;
		
 		int currentEpisodeNumber = channelJson.optInt("currentEpisodeNumber");
		for(int j=0;j<programmeArr.length();j++){
		JSONObject programmeJson=programmeArr.getJSONObject(j);
		JSONObject proInfo=programmeJson.optJSONObject("programme");
		JSONObject middInfo=programmeJson.optJSONObject("midd");
		JSONObject psInfo=programmeJson.optJSONObject("ProgrammeSite");
		if(proInfo==null || middInfo==null || psInfo==null)continue;
		
		hasDirect = true;

		int programmeId=proInfo.optInt("programmeId");
		
		int episodeTotal=proInfo.optInt("episodeTotal");
		
		JSONArray siteArr=proInfo.optJSONArray("sites");
		if(siteArr==null || siteArr.length()<=0) continue;
		
		String updateNotice=StringUtils.trimToEmpty(proInfo.optString("update_notice"));
		
		String pic=showPic(proInfo.optString("vpic"),proInfo.optString("pic"),
			"http://g1.ykimg.com/0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80");
		boolean showV=pic.equals(proInfo.optString("vpic"));
		
		String highLightPerson=StringUtils.trimToEmpty(proInfo.optString("highLightPerson"));
		
		String playUrl=StringUtils.trimToEmpty(proInfo.optString("url"));
		
		String detailUrl="/detail/show/"+com.youku.search.util.MyUtil.encodeVideoId(programmeId)+"?keyword="+encodeKeyword;
		
	%>		
<!--动漫-->
<div class="set">
	 <div class="title">
	 	<h4> <%=proInfo.optString("name") %> </h4></div>
	<div class="content">	
<div class="item">
	<div class="tv">
		<ul class="base">
			<li class="base_name"><h1><a href="<%=detailUrl %>" target="_self" 
				onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="5" _log_pos="<%=i+1 %>"  _log_directpos="2"><%=StringUtils.trimToEmpty(proInfo.optString("highLightName"))%></a><%if(proInfo.has("aliasName")){%>
                                (<%=proInfo.optString("aliasName")%>)<%}%>   </h1></li>
			<li class="base_what"><%if(episodeTotal>0) out.print(episodeTotal+"集");%>动漫</li>
			<%String mainPerformer=showPerformer(proInfo.optJSONArray("performer"),highLightPerson,2,5,i+1,3); %>
			<li class="base_actor"><label><%=mainPerformer.length()>0?"|&nbsp;&nbsp;主演：":"" %> </label><span><%=mainPerformer%></span></li>     
			<li class="base_update"><label><%if(updateNotice.length()>0){out.print("更新："+updateNotice);}%></label></li>
			<li class="base_rating"><div class="rating"><%=printStar(proInfo.optDouble("score"))%></div></li>
		</ul><!--base end-->
		<div class="detail">
			<div class="G">
				<ul class="p <%=showV?"pv":"" %>">
					<li class="p_link"><a href="<%=detailUrl %>" target="_self" title="<%StringUtils.trimToEmpty(proInfo.optString("name"));%>" 
					onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="5" _log_pos="<%=i+1 %>"  _log_directpos="1"></a></li>
					<li class="p_thumb"><img src="<%=pic%>" alt="<%StringUtils.trimToEmpty(proInfo.optString("name"));%>"></li>
					<li class="p_status"><span class="status"></span><span class="bg"></span></li>
					<li class="p_ishd"><span title="" class=""></span></li>
				</ul>
				<ul class="rel">
					
				</ul>
				<div class="clear"></div>
			</div><!--G end-->
			<div class="T">
				<%for(int ii=0;ii<siteArr.length();ii++){
					String eleSite=siteArr.getString(ii);
					if(ii==0){
						out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"'>");	
					}else{
						out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"' style='display:none;'> ");
					}
					
					out.print("<ul class='linkpanel panel_15'>");
				    
					JSONArray episodeArr=psInfo.optJSONObject(eleSite.split(":")[0]).optJSONArray("episodes");
					if(episodeArr==null) continue;
					
				        for(int episodeIndex=0;episodeIndex<episodeArr.length()&& episodeIndex<43;episodeIndex++){
						if(episodeArr.getJSONObject(episodeIndex)==null)continue;
						int orderId = episodeArr.getJSONObject(episodeIndex).optInt("orderId");
						int orderStage=episodeArr.getJSONObject(episodeIndex).optInt("orderStage");
						if (currentEpisodeNumber > 0 && orderId == currentEpisodeNumber){
						%>
					       <li class="aim">
						   <div class="aimtip">
						    <div class="close" onclick="this.parentNode.style.display = 'none';"></div>
						    <div class="arrow"></div>
   		     				<%=StringUtils.trimToEmpty(proInfo.optString("name"))%> 第<%=orderId%>集
						</div>
						<%
						     }       
						     else{
							     out.println("<li>");
						     }
						     String link = episodeArr.getJSONObject(episodeIndex).optString("url");
						     if (link!=null && !link.isEmpty()){
							     out.print("<a href='"+StringUtils.trimToEmpty(link)+
							    		 "' onclick='sokuClickStat(this);' _log_type='2' _log_ct='5' _log_pos="+(i+1)+"  _log_directpos='4' "+
									     	" target='_blank'>"+ ((orderStage<=0 || orderStage>2000)?orderId:orderStage) +"</a></li>");
						     }       
						     else{
							     //没链接地址显示为灰色
						     }
				}
						%>											
							<%	if(episodeArr.length()>43){ %>									
							<li class="handle"><span>全部</span></li><%} %>
						</ul>
						<div class="clear"></div>
						<div class="panellocation">
						<div class="panelexpand">
						<ul class="linkpanel panel_15">
								<%for(int jj=43;jj<episodeArr.length();jj++){
								if(episodeArr.getJSONObject(jj)==null)continue;
									out.print("<li><a href='"+StringUtils.trimToEmpty(episodeArr.getJSONObject(jj).optString("url"))+
											"' onclick='sokuClickStat(this);' _log_type='2' _log_ct='5' _log_pos="+(i+1)+"  directpos='4' "+
										">"+episodeArr.getJSONObject(jj).optInt("orderId")+"</a></li>");
								}%>																						
						</ul>
						<div class="clear"></div>
						</div>	
						</div>
			    </div>
			    <%} %>
				
				<div class="gotoplay">
					<div class="btnplay_large"><a href="#" target="_blank" 
					onclick="sokuClickStat(this);" _log_type="2"    _log_ct="5" _log_pos="<%=i+1 %>"  _log_directpos="5" >播放</a></div>
					
					<% if(siteArr!=null && siteArr.length()>0){
						
						if(siteArr.length()<=1){
							out.print("<div class='source  source_one'> ");
						}else{
							out.print("<div class='source'> ");
						}
						
						//获取每一个站点
						String eleSiteStr=siteArr.getString(0);
						JSONObject tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
						if(tmpSite==null) continue;
						
						String hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
						String stypeName="";
						String stype="";
						if(hdStr!=null && hdStr.length()>0){
							stypeName=hdStr.split(",")[0];
							stype=hdStr.split(",")[1];
						}
						
						int speedInt=5;
						//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
						speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
						
						out.print("<div class='check'><label  _log_ct='5'  _log_pos='"+(i+1)+"'  title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
								" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
								SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></div>");
						if(siteArr.length()>1) out.print("<ul class='other'>");
						for(int speedIndex=1;speedIndex<siteArr.length();speedIndex++){
							eleSiteStr=siteArr.getString(speedIndex);
							tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
							if(tmpSite==null) continue;
							
							hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
							stypeName="";
							stype="";
							if(hdStr!=null && hdStr.length()>0){
								stypeName=hdStr.split(",")[0];
								stype=hdStr.split(",")[1];
							}
							
							speedInt=5;
							//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
							speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
							
							out.print("<li><label  _log_ct='5'  _log_pos='"+(i+1)+"'  title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
									" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
									SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></li>");
						}
						if(siteArr.length()>1) out.print("</ul>");
						}
					
						 %>
					</div>
				</div>
				<div class="like"></div>
				<%String littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")); if(StringUtils.trimToEmpty(proInfo.optString("brief")).length()>300)littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")).substring(0,300)+"...";  %>
				<div class="intro"><%=StringUtils.trimToEmpty(littleBrief).equals("null")?"":StringUtils.trimToEmpty(littleBrief)%>
				<a href="<%=detailUrl %>" target="_self"  
				onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="5" _log_pos="<%=i+1 %>"  _log_directpos="6">查看详情&gt;&gt;</a></div>
			</div><!--T end-->
			<div class="clear"></div>
		</div><!--detail end-->
	</div><!--ANIME end-->
</div><!--item end-->
</div><!-- accordion set content end -->	
</div><!-- accordion set end -->
	<%
		}
	}else if(nameType.equals("VARIETY")){
		JSONArray programmeArr=channelJson.optJSONArray(nameType);
		if(programmeArr==null || programmeArr.length()<=0) continue;
		int currentEpisodeNumber = channelJson.optInt("currentEpisodeNumber");
		
		for(int j=0;j<programmeArr.length();j++){
		JSONObject programmeJson=programmeArr.getJSONObject(j);
		
		JSONObject proInfo=programmeJson.optJSONObject("programme");
		JSONObject middInfo=programmeJson.optJSONObject("midd");
		JSONObject psInfo=programmeJson.optJSONObject("ProgrammeSite");
		
		JSONObject seriesJson=programmeJson.optJSONObject("series");
		
		if(proInfo==null || middInfo==null)continue;
		
		hasDirect = true;

		int programmeId=proInfo.optInt("programmeId");
		
		JSONArray siteArr=proInfo.optJSONArray("sites");
		if(siteArr==null || siteArr.length()<=0) continue;
		
		String pic=showPic(proInfo.optString("pic"),"","http://g1.ykimg.com/0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80");
		
		String highLightPerson=StringUtils.trimToEmpty(proInfo.optString("highLightPerson"));
		
		String playUrl=StringUtils.trimToEmpty(proInfo.optString("url"));
		
		String detailUrl="/detail/show/"+com.youku.search.util.MyUtil.encodeVideoId(programmeId)+"?keyword="+encodeKeyword;
		
		List idList=Arrays.asList(65183, 113208,113770,113771,113809,113814,113815,114764,
				64403, 113195, 113747,  113748,  113749,  114788,104547, 119443,
				65173,  113050,  113739 ,114768 , 120125 , 122188 ,64004, 65176,
				113041,  113221,64255,124719, 89789, 115712,115754, 65049, 116659, 116712,
				89790, 116607, 65126, 79061, 79204, 81538,105685, 113775, 113776, 114809,116338,115075,113202);
		
		%>
		<!--综艺-->
<div class="item">
	<div class="zy">
		<ul class="base">
			<li class="base_name"><h1><a  href="<%=detailUrl %>" target="_self"
			onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="3" _log_pos="<%=i+1 %>"  _log_directpos="2"><%=StringUtils.trimToEmpty(proInfo.optString("highLightName"))%></a><%if(proInfo.has("aliasName")){%>
                                (<%=proInfo.optString("aliasName")%>)<%}%>   </h1></li>
			<li class="base_what"><%=JSONUtil.join(proInfo.optJSONArray("station")," ")%>  <%=JSONUtil.join(middInfo.optJSONArray("genre")," ")%></li>
			<%String host=showPerformer(middInfo.optJSONArray("host"),highLightPerson,2,3,i+1,3);%>
			<li class="base_actor">| <label><%=host.length()>0?"主持人：":"" %> </label><span><%=host%> </span></li>     
			<li class="base_rating"><div class="rating"><%=printStar(proInfo.optDouble("score"))%></div></li>
		</ul><!--base end-->
		<div class="detail">
			<div class="G">
				<ul class="p">
					<li class="p_link"><a  href="<%=detailUrl %>" target="_self" title="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>" 
					onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="3" _log_pos="<%=i+1 %>"  _log_directpos="1"></a></li>
					<li class="p_thumb"><img src="<%=pic%>" alt="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></li>
					<li class="p_status"><span class="status"></span><span class="bg"></span></li>
					<li class="p_ishd"><span class="" title=""></span></li>			
				</ul>
				<ul class="rel">
					
				</ul>
				<div class="clear"></div>
			</div><!--G end-->
			<div class="T">
				<%
				
				
				
				
				if(!idList.contains(programmeId)){
					for(int ii=0;ii<siteArr.length();ii++){
						String eleSite=siteArr.getString(ii);
						if(ii==0){
							out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"'>");	
						}else{
							out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"' style='display: none;'> ");
						}
						
						out.print("<ul class='linkpanel panel_5'>");
					    
						JSONArray episodeArr=psInfo.optJSONObject(eleSite.split(":")[0]).optJSONArray("episodes");
						if(episodeArr==null) continue;
						
				        for(int episodeIndex=0;episodeIndex<episodeArr.length()&& episodeIndex<14;episodeIndex++){
								if(episodeArr.getJSONObject(episodeIndex)==null)continue;
							
								 int orderStage = episodeArr.getJSONObject(episodeIndex).optInt("orderStage");
								 
								 String orderStageStr="";
								 try{
								 SimpleDateFormat fromSdf=new SimpleDateFormat("yyyyMMdd");
								 SimpleDateFormat toSdf=new SimpleDateFormat("yyyy-MM-dd");
								 Date fromDate=fromSdf.parse(new Integer(orderStage).toString());
								 orderStageStr=toSdf.format(fromDate);
								 }catch(Exception e){
							         System.out.print(e.getMessage());
							         orderStageStr=new Integer(orderStage).toString();
								 }
	
								 if (currentEpisodeNumber > 0 && orderStage == currentEpisodeNumber){
									 %>
										 <li class="aim">
										 <div class="aimtip">
										 <div class="close" onclick="this.parentNode.style.display = 'none';"></div>
										 <div class="arrow"></div>
										 	<%=StringUtils.trimToEmpty(proInfo.optString("name"))%> 第<%=orderStage%>期
											</div>
											<%
								 }       
								 else{
									 out.println("<li>");
								 }
								 String link = episodeArr.getJSONObject(episodeIndex).optString("url");
								 String title= StringUtils.trimToEmpty(episodeArr.getJSONObject(episodeIndex).optString("name"));
								 
								 if (link!=null && !link.isEmpty()){
									 out.print("<a href='"+StringUtils.trimToEmpty(link)+
											 	"'  title='"+title+"'  target='_blank'"+
											 	" onclick='sokuClickStat(this);' _log_type='2' _log_ct='3' _log_pos="+(i+1)+"  _log_directpos='4' "+
											 	">"+ orderStageStr +"</a></li>");
								 }       
								 else{
									 //没链接地址显示为灰色
								 }
							
					    }
							%>											
								<%	if(episodeArr.length()>14){ %>									
								<li class="handle"><span>全部</span></li><%} %>
							</ul>
							<div class="clear"></div>
							<div class="panellocation">
							<div class="panelexpand">
							<ul class="linkpanel panel_5">
									<%for(int jj=14;jj<episodeArr.length();jj++){
									if(episodeArr.getJSONObject(jj)==null)continue;
										String title= StringUtils.trimToEmpty(episodeArr.getJSONObject(jj).optString("name"));
										
										String orderStageStr="";
										int orderStage = episodeArr.getJSONObject(jj).optInt("orderStage");
										
										 try{
										 SimpleDateFormat fromSdf=new SimpleDateFormat("yyyyMMdd");
										 SimpleDateFormat toSdf=new SimpleDateFormat("yyyy-MM-dd");
										 Date fromDate=fromSdf.parse(new Integer(orderStage).toString());
										 orderStageStr=toSdf.format(fromDate);
										 }catch(Exception e){
									         System.out.print(e.getMessage());
									         orderStageStr=new Integer(orderStage).toString();
										 }
										
										out.print("<li><a href='"+StringUtils.trimToEmpty(episodeArr.getJSONObject(jj).optString("url"))+
											"' title='"+title+"'  target='_blank'"+
											" onclick='sokuClickStat(this);' _log_type='2' _log_ct='3' _log_pos="+(i+1)+"  _log_directpos='4' "+
											">"+orderStageStr+"</a></li>");
									}%>																						
							</ul>
							<div class="clear"></div>
							</div>	
							</div>
				    </div>
				    <%}
				}else{
					
					for(int ii=0;ii<siteArr.length();ii++){
						String eleSite=siteArr.getString(ii);
						if(ii==0){
							out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"'>");	
						}else{
							out.print("<div class='linkpanels site"+eleSite.split(":")[0]+"' style='display: none;'> ");
						}
						
						out.print("<ul class='linkpanel panel_2'>");
					    
						JSONArray episodeArr=psInfo.optJSONObject(eleSite.split(":")[0]).optJSONArray("episodes");
						if(episodeArr==null) continue;
						
				        for(int episodeIndex=0;episodeIndex<episodeArr.length()&& episodeIndex<9;episodeIndex++){
								if(episodeArr.getJSONObject(episodeIndex)==null)continue;
							
								 int orderStage = episodeArr.getJSONObject(episodeIndex).optInt("orderStage");
								 
								 String orderStageStr="";
								 try{
								 SimpleDateFormat fromSdf=new SimpleDateFormat("yyyyMMdd");
								 SimpleDateFormat toSdf=new SimpleDateFormat("yyyy-MM-dd");
								 Date fromDate=fromSdf.parse(new Integer(orderStage).toString());
								 orderStageStr=toSdf.format(fromDate);
								 }catch(Exception e){
									 
							         System.out.print(e.getMessage());
							         orderStageStr=new Integer(orderStage).toString();
								 }
	
								 if (currentEpisodeNumber > 0 && orderStage == currentEpisodeNumber){
									 %>
										 <li class="aim">
										 <div class="aimtip">
										 <div class="close" onclick="this.parentNode.style.display = 'none';"></div>
										 <div class="arrow"></div>
										 	<%=StringUtils.trimToEmpty(proInfo.optString("name"))%> 第<%=orderStage%>期
											</div>
											<%
								 }       
								 else{
									 out.println("<li>");
								 }
								 String link = episodeArr.getJSONObject(episodeIndex).optString("url");
								 String title= StringUtils.trimToEmpty(episodeArr.getJSONObject(episodeIndex).optString("name"));
								 
								 if (link!=null && !link.isEmpty()){
									 out.print("<a href='"+StringUtils.trimToEmpty(link)+
											 	"'  title='"+title+"'  target='_blank'"+
											 	" onclick='sokuClickStat(this);' _log_type='2' _log_ct='3' _log_pos="+(i+1)+"  _log_directpos='4' "+
											 	">"+orderStageStr+"： "+title+"</a></li>");
								 }       
								 else{
									 //没链接地址显示为灰色
								 }
							
					    }
							%>											
								<%	if(episodeArr.length()>9){ %>									
								<li class="handle"><span>全部</span></li><%} %>
							</ul>
							<div class="clear"></div>
							<div class="panellocation">
							<div class="panelexpand" style="display: none;">
							<ul class="linkpanel panel_2">
									<%for(int jj=9;jj<episodeArr.length();jj++){
									if(episodeArr.getJSONObject(jj)==null)continue;
										String title= StringUtils.trimToEmpty(episodeArr.getJSONObject(jj).optString("name"));
										
										int orderStage = episodeArr.getJSONObject(jj).optInt("orderStage");
										 
										 String orderStageStr="";
										 try{
										 SimpleDateFormat fromSdf=new SimpleDateFormat("yyyyMMdd");
										 SimpleDateFormat toSdf=new SimpleDateFormat("yyyy-MM-dd");
										 Date fromDate=fromSdf.parse(new Integer(orderStage).toString());
										 orderStageStr=toSdf.format(fromDate);
										 }catch(Exception e){
									         System.out.print(e.getMessage());
									         orderStageStr=new Integer(orderStage).toString();
										 }
										
										out.print("<li><a href='"+StringUtils.trimToEmpty(episodeArr.getJSONObject(jj).optString("url"))+
											"' title='"+title+"'  target='_blank'"+
											" onclick='sokuClickStat(this);' _log_type='2' _log_ct='3' _log_pos="+(i+1)+"  _log_directpos='4' "+
											">"+orderStageStr+"： "+title+"</a></li>");
									}%>																						
							</ul>
							<div class="clear"></div>
							</div>	
							</div>
				    </div>
				    <%}
				}%>
				<div class="gotoplay">
					<div class="btnplay_large"><a target="_blank"
						onclick="sokuClickStat(this);" _log_type="2"    _log_ct="3" _log_pos="<%=i+1 %>"  _log_directpos="5" >播放</a></div>
					
					<% if(siteArr!=null && siteArr.length()>0){
						
						if(siteArr.length()<=1){
							out.print("<div class='source  source_one'> ");
						}else{
							out.print("<div class='source'> ");
						}
						
						//获取每一个站点
						String eleSiteStr=siteArr.getString(0);
						JSONObject tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
						if(tmpSite==null) continue;
						
						String hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
						String stypeName="";
						String stype="";
						if(hdStr!=null && hdStr.length()>0){
							stypeName=hdStr.split(",")[0];
							stype=hdStr.split(",")[1];
						}
						
						int speedInt=5;
						//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
						speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
						out.print("<div class='check'><label  _log_ct='3'  _log_pos='"+(i+1)+"' title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
								" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
								SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></div>");
						if(siteArr.length()>1) out.print("<ul class='other'>");
						for(int speedIndex=1;speedIndex<siteArr.length();speedIndex++){
							eleSiteStr=siteArr.getString(speedIndex);
							tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
							if(tmpSite==null) continue;
							
							hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
							stypeName="";
							stype="";
							if(hdStr!=null && hdStr.length()>0){
								stypeName=hdStr.split(",")[0];
								stype=hdStr.split(",")[1];
							}
							
							speedInt=5;
							//speedInt=speedMap.get(new Integer(eleSiteStr.split(":")[0]).intValue()).getValue();
							speedInt=getSpeed(new Integer(eleSiteStr.split(":")[0]).intValue(),speedMap,currentRegion);
							
							out.print("<li><label _log_ct='3'  _log_pos='"+(i+1)+"'  title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
									" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
									SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+speedInt+"'></div></li>");
						}
						if(siteArr.length()>1) out.print("</ul>");
						}
					
						 %>
					</div>
				</div>
				<div class="like"><% if(seriesJson!=null){  out.print("<label>相关系列:</label>");
															JSONArray likeProgramme=seriesJson.optJSONArray("programmes");
															if(likeProgramme!=null && likeProgramme.length()>0){
																for(int likeIndex=0;likeIndex<likeProgramme.length();likeIndex++){
																	JSONObject tmpLike=likeProgramme.getJSONObject(likeIndex);
																	if(tmpLike==null) continue;
																	out.print("<a  onclick='sokuClickStat(this);' "+
																			" _log_type='2' _log_ct='3' _log_pos="+(i+1)+"  _log_directpos='7'"+
																	" href='"+detailUrl+"' target='_blank'>"+StringUtils.trimToEmpty(tmpLike.optString("name"))+"</a>");
																	
																}
															}
					
															}
														%>
				 </div>
				 <%String littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")); if(StringUtils.trimToEmpty(proInfo.optString("brief")).length()>300)littleBrief=StringUtils.trimToEmpty(proInfo.optString("brief")).substring(0,300)+"...";  %>
				<div class="intro"></div>
			</div><!--T end-->
			<div class="clear"></div>
		</div><!--detail end-->
	</div><!--zy end-->
</div><!--item end-->
		
		<%
		
	}
	}else if(nameType.equals("PERSON")){
		JSONObject persons=channelJson.optJSONObject(nameType);
		if(persons==null) continue;
		String personName=StringUtils.trimToEmpty(channelJson.optString("name"));
		
		Iterator keyIterator=persons.keys();
		if(keyIterator==null) continue;
		while(keyIterator.hasNext()){
			String personStr=keyIterator.next().toString();
			JSONObject onePerson=persons.optJSONObject(personStr);
			if(onePerson==null) continue;
			
			JSONArray programmeArr=onePerson.optJSONArray("episodes");
			if(programmeArr==null || programmeArr.length()==0) continue;

			hasDirect = true;
	%>
<!--人物-->
<div class="item">
	<div class="figure">
		<ul class="base">
		
		<%String detailPersonUrl="/detail/person/"+com.youku.search.util.MyUtil.encodeVideoId(new Integer(personStr).intValue())+"?keyword="+encodeKeyword;
		%>
			<li class="base_name"><h1><a  href="<%=detailPersonUrl %>" target="_self" 
			onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="6" _log_pos="<%=i+1 %>"  _log_directpos="3">
			<%=StringUtils.trimToEmpty(channelJson.optString("highLightName"))%></a>
			<%if(channelJson.has("aliasName")){%>(<%=channelJson.optString("aliasName")%>)<%}%>   
</h1></li>
			<li class="base_link"><a  onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="6" _log_pos="<%=i+1 %>"  _log_directpos="2" 
				href="<%=detailPersonUrl %>" target="_self">全部影视作品&gt;&gt;</a></li>
		</ul><!--base end-->
		<div class="detail">
			<div class="workscoll">
			<div class="works">
			
	<%
		for(int pindex=0;pindex<programmeArr.length()&& pindex<5;pindex++){
			JSONObject eleProgramme=programmeArr.getJSONObject(pindex);
			String personUrl=StringUtils.trimToEmpty(eleProgramme.optString("url"));
			if(personUrl==null || personUrl.length()<=0) continue;
			int programmeId=eleProgramme.optInt("pid");
			
			String programmeName=StringUtils.trimToEmpty(eleProgramme.optString("name"));
			
			String detailUrl="/detail/show/"+com.youku.search.util.MyUtil.encodeVideoId(programmeId)+"?keyword="+encodeKeyword;
			
			String pic=showPic(eleProgramme.optString("pic"),eleProgramme.optString("defaultpic"),"http://g1.ykimg.com/0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80");
		%>
		<ul class="p">
						<li class="p_link"><a href="<%=detailUrl %>" target="_self" title="<%=StringUtils.trimToEmpty(eleProgramme.optString("name"))%>"
							onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="6" _log_pos="<%=i+1 %>"  _log_directpos="1" ></a></li>
						<li class="p_thumb"><img src="<%=pic%>" alt="<%=StringUtils.trimToEmpty(eleProgramme.optString("name"))%>"></li>
						
						<%String hdStr=MyUtil.getHD(eleProgramme.optJSONArray("streamtypes")); %>
						<li class="p_status"><span class="status"><%=eleProgramme.optString("display_status")%><%=WebUtil.getSecondAsString((float)eleProgramme.optDouble("seconds"))%></span><span class="<%=showBG(eleProgramme.optString("display_status"),WebUtil.getSecondAsString((float)eleProgramme.optDouble("seconds")),hdStr) %>"></span></li>
						<%
						  String stypeName="",stype="";
						  if(hdStr!=null && hdStr.length()>0){
							 stypeName=hdStr.split(",")[0];
							 stype=hdStr.split(",")[1];
						  }
						%>
						<li class="p_ishd"><span class="ico__<%=stype %>" title="<%=stypeName%>"></span></li>
						<%String littleTitle=StringUtils.trimToEmpty(eleProgramme.optString("name"));
						  if(littleTitle.length()>10) littleTitle=littleTitle.substring(0,10);%>			
						<li class="p_title"><a  onclick="sokuClickStat(this);"	_log_type="2"    _log_ct="6" _log_pos="<%=i+1 %>"  _log_directpos="2" href="<%=detailUrl %>" target="_blank"><%=littleTitle%></a></li>
						<li class="p_typepub"><%=StringUtils.trimToEmpty(eleProgramme.optString("releaseyear"))%>&nbsp;&nbsp;&nbsp;&nbsp;<%=StringUtils.trimToEmpty(eleProgramme.optString("type"))%></li>
						<li class="p_rating"><div class="rating"><%=printStar(eleProgramme.optDouble("score"))%></div></li>
						<li class="p_play"><div class="btnplay"><a  href="<%=personUrl %>"  target="_blank"
						 onclick="sokuClickStat(this);" _log_type="2"    _log_ct="6" _log_pos="<%=i+1 %>"  _log_directpos="5">播放</a></div></li>
		</ul>
		
	
<%
		}%>
							<div class="clear"></div>
					</div><!--works end-->
			</div><!--coll end-->
		</div>
	</div>
</div><!-- item end -->		
		<%
}}
}%>

</div><!-- accordion end -->
<%
if (param!=null &&  param.logic == 2){
	out.print("<h3>以下为模糊搜索结果：</h3>");
}

Cookie cookies[]=request.getCookies(); 

Cookie sCookie=null;

String svalue="thumb"; 

String sname="viewby"; 

if(cookies!=null){
for(int i=0;i<cookies.length;i++) 
{

sCookie=cookies[i];

if(sname.equals(sCookie.getName())){
	String tmpValue=StringUtils.trimToEmpty(sCookie.getValue());
	if("list".equals(tmpValue)) svalue="list";
	break;
}
 
}
}
%> 																
<div class="viewchange">
	<div class="viewby">
		<ul>
			<li class="bythumb <%=svalue.equals("thumb")?"current":"" %>" title="网格" viewby="thumb"><em>网格</em></li>
			<li class="bylist <%=svalue.equals("list")?"current":"" %>" title="列表" viewby="list"><em>列表</em></li>
		</ul>
	</div>
	<div class="orderby">
		
	</div><!--orderby end-->	
</div><div class="result">
<noscript id="noscript"><div style="text-align:center;color:red;">如无法看到图片，请检查是否开启了Java Script。<br /><a href="http://www.youku.com/youku/help/question_play.shtml#java" target="_blank">如何开启？</a></div></noscript>
<!--缩略图方式||列表方式-->
<!--2,5,7 演示站外视频形式-->
<div class="<%=svalue.equals("thumb")?"collgrid4w":"colllist1w"%>" id="vcoll">

<!-- 一般搜索结果 -->
<div class="items">
	<%
			
			JSONArray items = content.getVideos();
			for (int i = 0;i< items.length();i++)
			{
				JSONObject item = items.optJSONObject(i);
				String itemurl = null;
				String title = null;
				String hltitle = null;
				String tags = null;
				String logo = null;
				
				url = item.getString("url");
				title = item.getString("title");
				hltitle = item.optString("title_hl");
				tags= item.optString("tags");
				logo = WebUtil.formatLogo(item.optString("logo"));
				String hltags = item.optString("tags_hl");
				if(hltitle == null || hltitle.isEmpty())hltitle = title;
			%>
			
			<ul class="v">
					<%String fromName=SiteManager.getInstance().getName(DataFormat.parseInt(item.getString("site")));
					  String picsStr=StringUtils.trimToEmpty(item.optString("pics"));
					  //out.print("picStr:"+picsStr);
					  if(picsStr!=null && picsStr.length()>0 &&"14".equals(StringUtils.trimToEmpty(item.getString("site")))){
						  JSONArray partsArr=new JSONArray(picsStr);
						  if(partsArr==null && partsArr.length()<=0) continue;
						  %>
						  <li class="v_part">
						  <div class="handle">
						<div class="ic"></div>
						<div class="bg"></div>
			    </div>
					<div class="pop">
					  <div class="content">
						      <div class="caption">分段观看	</div>
						  		<div class="partcoll">
							   			<div class="parts">
							   			<%
							   			for(int partIndex=0;partIndex<partsArr.length();partIndex++){
							   				JSONObject partJson=partsArr.getJSONObject(partIndex);
							   			 out.print("<ul class='part'>");
							   			 out.print("<li class='part_link'><a target='_blank' href='"+url+"?firsttime="+partJson.optString("t")+"'></a></li>");
			                             out.print("<li class='part_thumb'><img _src='"+WebUtil.formatLogo(partJson.optString("p"))+"'></li>");				   			
										 out.print("<li class='part_time'><div class='time'>"+WebUtil.getSecondAsString(new Double(partJson.optString("t")).floatValue())+"</div><div class='bg'></div></li>");
					                     out.print("</ul>");						
							   			}
										%>
										
					       			</div>
					    		</div>
					  </div>
				  
					  <div class="shadow"></div>
					</div>
					</li>
						  <%
					  }
					
					%>
			    
				<li class="v_link"><a title="<%=title%>" target="_blank" href="<%=url%>" onclick="sokuClickStat(this);"  _log_type="3" _log_pos="<%=i+1 %>"></a></li>
				<li class="v_thumb"><img alt="<%=title%>" src="<%=logo%>" ></li>
				<%if(item.optInt("hd")==1){%><li class="v_ishd"><span title="高清" class="ico__HD"></span></li><%}%>
				<%if (item.getDouble("seconds")>0){%><li class="v_time"><span class="num"><%=WebUtil.getSecondAsString((float)item.getDouble("seconds"))%></span><span class="bg"></span></li><%}%>
				<li class="v_title"><a title="<%=title%>" target="_blank" href="<%=url%>"  onclick="sokuClickStat(this);"  _log_type="3" _log_pos="<%=i+1 %>"  ><%=hltitle%></a></li>
				<li class="v_desc"> </li>
				<li class="v_user"><label>来源:</label><span style="color:#008000;"><%=fromName%></span></li>
				<%
				int speedInt=5;
				Speed domainSpeed=SpeedDb.getDomainSpeed(new Integer(item.getString("site")).intValue(),url);
				if(domainSpeed==null){
					speedInt=getSpeed(new Integer(item.getString("site")).intValue(),speedMap,currentRegion);
				}else{
					speedInt = domainSpeed.getValue();
				}
				
				%>
				
				<li class="v_speed"><label>速度:</label><div class="speed speed<%=speedInt%>"></div></li>
</ul>                                                                            

			
	<%if ( (i+1)%4==0){%>
	<div class="clear"></div>	
	<%}} %>
			
	<div class="clear"></div>
</div><!--items end-->
</div><!--coll end-->

<!--分页 两种-->
			<%if (param!=null){%>
			<div class="pager">
				<%=PageUtil.getNewContent(WebUtil.getPagePrefixString(param,request),total,param.page,pagesize)%>
			</div>
			<%}%>

</div><!--result end-->

<%if (content.getLike_words()!=null){%>
			<div class="relkeys">
				<label>相关搜索:</label>
				<ul>
					<%for (int i = 0;i<content.getLike_words().length();i++){
					String w = content.getLike_words().getString(i);
					%>
					<li><a href="/v?keyword=<%=MyUtil.urlEncode(w)%>"><%=w%></a></li>
					<%}%>
					</ul>
				<div class="clear"></div>
			</div>
			<%}%>
	</div><!--maincol end-->
	<div class="clear"></div>
</div><!--layout end-->
</div><!--body end-->

</div><!--screen end-->
</div><!--widnow end-->

<div class="feedwin">
	<div title="用户反馈:您喜欢新版搜库吗?" class="handle"></div>
	<form action="/v" method="post" id="feedbackform">
	<input type="hidden" name="keyword"  id="keyword"  value="<%=result.getKeyword() %>"/>
	<input type="hidden" name="source" id="source" value="<%=request.getHeader("Referer")%>"/>
	<input type="hidden" name="state" id="state" value=""/>
	<div class="feed" style="display:none;">
		<div title="关闭" class="close"></div>
		<div class="step">
			<h3 class="title">您喜欢新版搜库吗？</h3>
			<div class="like"><div class="ico"></div><label>喜欢</label></div>
			<div class="unlike"><div class="ico"></div><label>不喜欢</label></div>
			<div class="clear"></div>
			<div class="input">
				<textarea name="message" id="message">倾听您的建议(100字以内)</textarea>
				<button type="button">提交</button>
			</div>
		</div>
		<div class="step step3" style="display:none;" >
			<div class="thank">感谢您的参与。</div>
		</div>
	</div>
	</form>
</div>

<SCRIPT LANGUAGE="JavaScript">library=<%=hasDirect?1:0%>;result_count=<%=total%>;</SCRIPT>
<%@ include file="inc/footer.jsp" %>

<script type="text/javascript">
		document.getElementById("headq").focus();
		Ab('');
		$(function(){
			if($().lazyload) {
				$("img").lazyload({placeholder : "http://static.youku.com/v1.0.0661/index/img/sprite.gif"});
			}
			$("#accordionGiftLelo").msAccordion({defaultid:1});
		});
</script>
</body></html>