<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page
	import="java.util.*,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				org.apache.commons.lang.StringUtils,
				com.youku.soku.util.MyUtil,
				com.youku.search.util.DataFormat,
				org.json.JSONObject,
				org.json.JSONArray,
				com.youku.top.directory_top.entity.LibraryResult,
				com.youku.top.directory_top.entity.Library,
				com.youku.top.directory_top.DirectoryTopUtils,
				com.youku.soku.library.directory.DirectoryUtils,
				com.youku.soku.library.directory.DirectoryDateGetter,
				com.youku.top.topindex.utils.VideoTypeUtil.VideoType,
				com.youku.soku.web.util.PageUtil,
				com.youku.soku.index.manager.db.SiteManager,
				com.youku.soku.netspeed.SpeedDb,
				com.youku.top.directory_top.entity.MovieTopResult"%>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>

<% //综艺
String clientIp=request.getHeader("X-Forwarded-For");
String channel ="variety";
int cate =DirectoryUtils.channel2Cate(channel);
if(cate==0) cate=3;

String type_str = request.getParameter("type");
String area_str = request.getParameter("area");
String pageSize_str = request.getParameter("ps");
String pageNo_str=request.getParameter("pn");

int type=DataFormat.parseInt(type_str, 0);
int area=DataFormat.parseInt(area_str, 0);
int pageSize = DataFormat.parseInt(pageSize_str, 20);
int pageNo = DataFormat.parseInt(pageNo_str, 1);

int fromIndex=(pageNo-1)*pageSize;


String view_date = request.getParameter("date");
if(StringUtils.isBlank(view_date)||!view_date.matches("\\d{4}_\\d{2}_\\d{2}"))
	view_date = null;

String preUrl = "/top/rankzy.jsp?ps="+pageSize+"&channel="+channel;

if(view_date!=null) preUrl=preUrl+"&date="+view_date;

if(type!=0){
	preUrl=preUrl+"&type="+type;
}else{
	preUrl=preUrl+"&type=0";
}

if(area!=0){
	preUrl=preUrl+"&area="+area;
}else{
	preUrl=preUrl+"&area=0";
}
preUrl=preUrl+"&pn=";


MovieTopResult mtr = null;
mtr = DirectoryDateGetter.queryTopList(view_date,channel, new Integer(type).toString(),
		new Integer(area).toString(), (pageNo-1)*pageSize, pageSize);
boolean show=true;
if(mtr==null ){
	//跳到没有节目页面
	show=false;
}

JSONObject contents=new JSONObject();
JSONArray programmeArr=new JSONArray();
int total=0;
int size=0;
if(show){
	contents = mtr.getContents();
	total=mtr.getTotal();

	size = (null==contents||contents.length()<1)?0:contents.length();
	
	programmeArr=contents.optJSONArray("array");
}



String isJson = request.getParameter("json");
if(isJson!=null && isJson.equalsIgnoreCase("json")){
	out.print(contents.toString(4));
	return;
}


%>

<!DOCTYPE HTML>
<html><head>


<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>soku</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css">
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
</head><body>
<div class="window">
<div class="screen">

<% String keyword = request.getParameter("keyword");
	keyword="";
%>
<%@ include file="/result/inc/header.jsp" %>

<div class="body">
<div class="typechk">
<div class="main">
	</div>
</div><div class="layout">
<div class="ranktype">
	<h1 class="rankname">综艺排行榜</h1>
	<label>选择排行榜:</label>
	<%@ include file="/top/inc/topnavigate.inc" %>
</div>	
	<div class="sidecol">
		<div class="filter">
<div class="panel">
						<div class="clear"></div>
</div><!--panel end-->	
</div><!--filter end--></div>
<div class="maincol">
	<div class="rank_zy">
		<div class="items">
		<%
		if(programmeArr!=null && programmeArr.length()>0){
		 for(int i=0;i<programmeArr.length()&& i<20;i++){
			JSONObject programmeInfo=programmeArr.getJSONObject(i);
			
			JSONObject proInfo=programmeInfo.optJSONObject("programme");
			JSONObject middJson=programmeInfo.optJSONObject("midd");
			JSONObject psInfo=programmeInfo.optJSONObject("ProgrammeSite");
			
			if(proInfo==null || middJson==null)continue;
			
			String pic=StringUtils.trimToEmpty(proInfo.optString("pic"));
			
			if(StringUtils.trimToEmpty(proInfo.optString("vpic")).length()>0) pic=StringUtils.trimToEmpty(proInfo.optString("vpic"));
			
			int programmeId=proInfo.optInt("programmeId");
			
			String url="";
			url=proInfo.optString("url");
			
			JSONArray siteArr=proInfo.optJSONArray("sites");
			if(siteArr==null || siteArr.length()<=0) continue;
			String stypeName="";
			String stype="";
			if(siteArr!=null && siteArr.length()>0){
				String eleSiteStr=siteArr.getString(0);
				JSONObject tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
				if(tmpSite==null) continue;
				String hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
				if(hdStr!=null && hdStr.length()>0){
					stypeName=hdStr.split(",")[0];
					stype=hdStr.split(",")[1];
				}
				
			}
			
			int episodeTotal=proInfo.optInt("episodeTotal");
			
			
			String updateTo="";
			JSONObject currentSite=psInfo.optJSONObject(siteArr.getString(0).split(":")[0]);
			if(currentSite==null) continue;
			%>
		
				<div class="item">
				<ul class="base">
					<li class="base_name"><div class="order"><span class="ordernum"><%=fromIndex+i+1 %></span></div><h1><a href="/detail/show/<%=com.youku.search.util.MyUtil.encodeVideoId(programmeId)%>" target="_blank"><%=StringUtils.trimToEmpty(proInfo.optString("name"))%></a></h1></li>
					<li class="base_what"><%=JSONUtil.join(proInfo.optJSONArray("station")," ")%>  <%=JSONUtil.join(middJson.optJSONArray("genre")," ")%></li>
					<li class="base_actor">| <label>主持人:</label><span><%=JSONUtil.join(middJson.optJSONArray("host")," ",6)%></span></li>     
					<li class="base_rating"><div class="rating"><%int fullInt=new Double(proInfo.optDouble("score")/2).intValue(); 
															for(int index=0;index<fullInt;index++){
																out.print("<em class='ico__ratefull'></em>");
															}
															if(proInfo.optDouble("score")/2-fullInt>0) out.print("<em class='ico__ratepart'></em>");
															%><em class="num"><%=proInfo.optDouble("score")%></em></div></li>
				</ul><!--base end-->
				<div class="detail">
					<div class="G">
						<ul class="p">
							<li class="p_link"><a href="/detail/show/<%=com.youku.search.util.MyUtil.encodeVideoId(programmeId)%>" target="_blank" title="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></a></li>
							<li class="p_thumb"><img src="<%=StringUtils.trimToEmpty(proInfo.optString("pic"))%>" alt="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></li>
							<li class="p_status"><span class="status"><%=currentSite.optString("display_status") %></span><span class="bg"></span></li>
										
						</ul>
					</div><!--G end-->
					<div class="T">
						<%for(int ii=0;ii<siteArr.length();ii++){
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
							out.print("<li><a href='"+StringUtils.trimToEmpty(episodeArr.getJSONObject(episodeIndex).optString("url"))+
									"'>"+episodeArr.getJSONObject(episodeIndex).optInt("orderStage")+"</a></li>");
				    }
						%>
								<li class="handle"><span>全部</span></li>
								</ul>
								<div class="clear"></div>
								<div class="panellocation">
								<div class="panelexpand">
								<ul class="linkpanel panel_5">
									<%for(int jj=14;jj<episodeArr.length();jj++){
								if(episodeArr.getJSONObject(jj)==null)continue;
									out.print("<li><a href='"+StringUtils.trimToEmpty(episodeArr.getJSONObject(jj).optString("url"))+
										"'>"+episodeArr.getJSONObject(jj).optInt("orderStage")+"</a></li>");
								}%>	
								</ul>
								<div class="clear"></div>
								</div>	
								</div>
						</div>
						<%} %>
						<div class="gotoplay">
							<div class="btnplay_large"><a href="#" target="_blank">播放</a></div>
							<div class="source">
							<% if(siteArr!=null && siteArr.length()>0){
						
						//获取每一个站点
						String eleSiteStr=siteArr.getString(0);
						JSONObject tmpSite=psInfo.optJSONObject(eleSiteStr.split(":")[0]);
						if(tmpSite==null) continue;
						
						String hdStr=MyUtil.getHD(tmpSite.optJSONArray("streamtypes"));
						if(hdStr!=null && hdStr.length()>0){
							stypeName=hdStr.split(",")[0];
							stype=hdStr.split(",")[1];
						}
						
						out.print("<div class='check'><label title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
								" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
								SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+SpeedDb.getSpeed(clientIp,new Integer(eleSiteStr.split(":")[0]).intValue()).getValue()+"'></div></div>");
						out.print("<ul class='other'>");
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
							
							out.print("<li><label title='"+StringUtils.trimToEmpty(tmpSite.optString("display_status"))+"' "+  
									" stypename='"+stypeName+"' stype='"+stype+"' id='site"+eleSiteStr.split(":")[0]+"'>"+
									SiteManager.getInstance().getName(new Integer(eleSiteStr.split(":")[0]).intValue())+":</label><div class='speed speed"+SpeedDb.getSpeed(clientIp,new Integer(eleSiteStr.split(":")[0])).getValue()+"'></div></li>");
						}
						out.print("</ul>");
						}
					
						 %>
							</div>
						</div>
					</div><!--T end-->
					<div class="clear"></div>
				</div><!--detail end-->
			</div><!-- item end -->
			
			<%
		}
		}else{
			out.print("没有详情榜单");
		}
		%>
																																															
								
										
		</div>
	</div>
	
	<div class="pager">
		<%=PageUtil.getNewContent(preUrl,total,pageNo,pageSize)%>
	</div>
	
	</div>
<div class="clear"></div></div><!--layout end-->
</div><!--body end-->

<%@ include file="/result/inc/footer.jsp" %>

</div><!--screen end-->
</div><!--widnow end-->
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/soku.js"></script>
</body></html>