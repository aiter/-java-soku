<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page import="java.text.DecimalFormat"%>
<%@page
	import="java.util.*,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				org.apache.commons.lang.StringUtils,
				com.youku.soku.util.MyUtil,
				com.youku.search.util.DataFormat,
				org.json.JSONObject,
				org.json.JSONArray,
				com.youku.soku.top.directory.DirectoryUtil,
				com.youku.soku.top.directory.DirectoryDateGetter,
				com.youku.soku.web.util.PageUtil,
				com.youku.soku.top.directory.MovieTopResult"%>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>
<%! String printStar(Double db){
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

String showPic(String firstLogo,String middPic,String missPic){
	  if(firstLogo!=null && StringUtils.trimToEmpty(firstLogo).length()>0){
		  return StringUtils.trimToEmpty(firstLogo);
	  }else if(middPic!=null && StringUtils.trimToEmpty(middPic).length()>0){
		  return StringUtils.trimToEmpty(middPic);
	  }else{
		  return missPic;
	  }
	  
	  
}

String showPerformer(JSONArray performerArr,String highLightName,int limit){
	if(performerArr==null || performerArr.length()==0) return "";
	
	StringBuffer resultBf=new StringBuffer();
	try{
	for(int i=0;i<performerArr.length()&& i<limit;i++){
		JSONObject elePersonObj=performerArr.optJSONObject(i);
		if(elePersonObj==null) continue;
		String personName=StringUtils.trimToEmpty(elePersonObj.optString("name"));
		String encodePerformer=MyUtil.urlEncode(personName);
		
		if(!highLightName.equals(personName)){
			resultBf.append("<a href='/v?keyword="+encodePerformer+"' target='_self'>"+
					((personName.length()>10)?personName.substring(0,10):personName)
						+"</a>").append("&nbsp;&nbsp;");
		}else{
			resultBf.append("<a href='/v?keyword="+encodePerformer+"' target='_self'><span style='color: #CC0000;'>"+
					((personName.length()>10)?personName.substring(0,10):personName)
					+"</span></a>").append("&nbsp;&nbsp;");
		}
	}
	String returnStr=resultBf.toString();
	//if(returnStr.length()>0) returnStr=returnStr.substring(0,returnStr.length()-2);

	return returnStr;
	}catch(Exception e){
		System.out.println("error:"+e.getMessage());
		return "";
	}
}

%>
<% //电视剧
String clientIp=request.getHeader("X-Forwarded-For");
String channel ="teleplay";
int cate =DirectoryUtil.channel2Cate(channel);
if(cate==0) cate=1;

String type_str = request.getParameter("type");
String area_str = request.getParameter("area");
String year_str= request.getParameter("year");
String pageSize_str = request.getParameter("ps");
String pageNo_str=request.getParameter("pn");

int type=DataFormat.parseInt(type_str, 0);
int area=DataFormat.parseInt(area_str, 0);
int year=DataFormat.parseInt(year_str,0);
int pageSize = DataFormat.parseInt(pageSize_str, 20);
int pageNo = DataFormat.parseInt(pageNo_str, 1);
if(pageNo>100) pageNo=100;

String view_date = request.getParameter("date");
if(StringUtils.isBlank(view_date)||!view_date.matches("\\d{4}_\\d{2}_\\d{2}"))
	view_date = null;

int tatalcount=DirectoryDateGetter.queryTopCount(view_date, year_str,channel, new Integer(type).toString(),
		new Integer(area).toString(), (pageNo-1)*pageSize, pageSize);
int totalpage = (int)(tatalcount+(pageSize-1))/pageSize;
if(pageNo>totalpage && totalpage>=1)
	pageNo=totalpage;


MovieTopResult mtr = null;
mtr = DirectoryDateGetter.queryTopList(view_date, year_str,channel, new Integer(type).toString(),
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
<title>电视剧排行榜-搜库</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css">
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
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
</div><div class="layout_16">
<div class="ranktype">
	<h1 class="rankname">电视剧</h1>
	<label></label>
</div>	
	<div class="sidecol">
		<div class="filter">
<div class="panel">
		<div class="item">
	<label>类型:</label>
			<ul>
			<li class="<%=(type_str!=null && type_str.equals("0"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_0_<%=StringUtils.trimToEmpty(area_str)%>_1.html">全部</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1001"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1001_<%=StringUtils.trimToEmpty(area_str)%>_1.html">偶像</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1002"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1002_<%=StringUtils.trimToEmpty(area_str)%>_1.html">言情</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1003"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1003_<%=StringUtils.trimToEmpty(area_str)%>_1.html">都市</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1004"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1004_<%=StringUtils.trimToEmpty(area_str)%>_1.html">喜剧</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1005"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1005_<%=StringUtils.trimToEmpty(area_str)%>_1.html">历史</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1006"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1006_<%=StringUtils.trimToEmpty(area_str)%>_1.html">谍战</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1007"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1007_<%=StringUtils.trimToEmpty(area_str)%>_1.html">古装</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1008"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1008_<%=StringUtils.trimToEmpty(area_str)%>_1.html">武侠</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1009"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1009_<%=StringUtils.trimToEmpty(area_str)%>_1.html">刑侦</a></li>
			<li class="<%=(type_str!=null && type_str.equals("1111"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_1111_<%=StringUtils.trimToEmpty(area_str)%>_1.html">其它</a></li>
			</ul>
		<div class="clear"></div>
		</div>
		
		<div class="item">		
			<label>国家/地区:</label>
			<ul>
			<li class="<%=(area_str!=null && area_str.equals("0"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_<%=StringUtils.trimToEmpty(type_str)%>_0_1.html">全部</a></li>
			<li class="<%=(area_str!=null && area_str.equals("1001"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_<%=StringUtils.trimToEmpty(type_str)%>_1001_1.html">内地</a></li>
			<li class="<%=(area_str!=null && area_str.equals("1002"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_<%=StringUtils.trimToEmpty(type_str)%>_1002_1.html">港台</a></li>
			<li class="<%=(area_str!=null && area_str.equals("1003"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_<%=StringUtils.trimToEmpty(type_str)%>_1003_1.html">韩国</a></li>
			<li class="<%=(area_str!=null && area_str.equals("1004"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_<%=StringUtils.trimToEmpty(type_str)%>_1004_1.html">日本</a></li>
			<li class="<%=(area_str!=null && area_str.equals("1111"))?"current":"" %>"><a href="/channel/teleplay_<%=StringUtils.trimToEmpty(year_str)%>_<%=StringUtils.trimToEmpty(type_str)%>_1111_1.html">其它</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		
		
		<div class="item">		
			<label>年代:</label>
			<ul>
			<li class="<%=(year_str!=null && year_str.equals("0"))?"current":"" %>"><a href="/channel/teleplay_0_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">全部</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2011"))?"current":"" %>"><a href="/channel/teleplay_2011_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2011</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2010"))?"current":"" %>"><a href="/channel/teleplay_2010_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2010</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2009"))?"current":"" %>"><a href="/channel/teleplay_2009_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2009</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2008"))?"current":"" %>"><a href="/channel/teleplay_2008_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2008</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2007"))?"current":"" %>"><a href="/channel/teleplay_2007_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2007</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2006"))?"current":"" %>"><a href="/channel/teleplay_2006_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2006</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2005"))?"current":"" %>"><a href="/channel/teleplay_2005_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2005</a></li>
			<li class="<%=(year_str!=null && year_str.equals("2004"))?"current":"" %>"><a href="/channel/teleplay_2004_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">2004</a></li>
			<li class="<%=(year_str!=null && year_str.equals("-1"))?"current":"" %>"><a href="/channel/teleplay_9999_<%=StringUtils.trimToEmpty(type_str)%>_<%=StringUtils.trimToEmpty(area_str)%>_1.html">其它</a></li>
			</ul>
			<div class="clear"></div>
		</div>		
		
		<div class="clear"></div>
</div><!--panel end-->	
</div><!--filter end--></div>
<noscript id="noscript"><div style="text-align:center;color:red;">如无法看到图片，请检查是否开启了Java Script。<br /><a href="http://www.youku.com/youku/help/question_play.shtml#java" target="_blank">如何开启？</a></div></noscript>
<div class="maincol">
	<div class="rank_movies">
		<div class="items">
		<%
		if(programmeArr!=null && programmeArr.length()>0){
			 for(int i=0;i<programmeArr.length()&& i<20;i++){
				JSONObject programmeInfo=programmeArr.getJSONObject(i); 
			JSONObject proInfo=programmeInfo.optJSONObject("programme");
			JSONObject middJson=programmeInfo.optJSONObject("midd");
			JSONObject programmeSiteJson=programmeInfo.optJSONObject("ProgrammeSite");
			
			if(proInfo==null || middJson==null)continue;
			
			String pic=showPic(proInfo.optString("vpic"),proInfo.optString("pic"),
			"http://g1.ykimg.com/0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E"); //竖版海报缺省图
			
			int programmeId=proInfo.optInt("programmeId");
			
			String url="";
			url=StringUtils.trimToEmpty(proInfo.optString("url"));
			
			if(url.length()==0) continue;
			
			
			String display_status="";
			String stypeName="";
			String stype="";
			if(programmeSiteJson!=null){
				Iterator<String> itor=programmeSiteJson.keys();
				String siteKey="";
				if(itor.hasNext()) siteKey=itor.next();
				if(siteKey.length()>0){
					JSONObject firstSite=programmeSiteJson.optJSONObject(siteKey);
					if(firstSite!=null){
						display_status=StringUtils.trimToEmpty(firstSite.optString("display_status"));
						
						String hdStr=MyUtil.getHD(firstSite.optJSONArray("streamtypes"));
						if(hdStr!=null && hdStr.length()>0){
							stypeName=hdStr.split(",")[0];
							stype=hdStr.split(",")[1];
						}
					}
					
				}
			}
			
			
			
	        
			%>
			<ul class="p pv">
				<li class="p_link"><a href="/detail/show/<%=com.youku.search.util.MyUtil.encodeVideoId(programmeId)%>" target="_blank" 
					title="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></a></li>
				<li class="p_thumb"><img src="<%=pic%>" alt="<%=StringUtils.trimToEmpty(proInfo.optString("name"))%>"></li>
				<li class="p_status"><span class="status"><%=display_status %></span><span class="bg"></span></li>
				<li class="p_ishd"><span class="ico__<%=stype %>" title="<%= stypeName%>"></span></li>
				<li class="p_title">
					<a href="/detail/show/<%=com.youku.search.util.MyUtil.encodeVideoId(programmeId)%>" target="_blank">
					<%=StringUtils.trimToEmpty(proInfo.optString("name"))%></a></li>
				<% String mainPerformers=showPerformer(proInfo.optJSONArray("performer"),"",2);
					%>
				<li class="p_actor"><label>主演:</label><span><%=mainPerformers%></span></li>
		
				<li class="p_pub"><label>上映:</label><span><%=StringUtils.trimToEmpty(proInfo.optString("releaseyear")) %></span></li>
				<li class="p_rating"><div class="rating"><%=printStar(proInfo.optDouble("score"))%></div></li>
				<li class="p_play"><div class="btnplay"><a href="<%=StringUtils.trimToEmpty(url).length()>0?StringUtils.trimToEmpty(url):"/v?keyword="+MyUtil.urlEncode(StringUtils.trimToEmpty(proInfo.optString("name")))%>"  
					target="_blank"><em>播放</em></a></div></li>
			</ul>
			<%
			if((i+1)%5==0) out.print("<div class='clear'></div>");
		}
			 
		
		}else{%>
		<div class="result">
			<div class="null">
	<div class="sorry">
		抱歉，没有筛选到相关节目。<br/></div>
	<h3>建议您：</h3>
	<ul>
		<li>• 请放宽筛选条件。</li>
		<li>• 尝试使用搜索功能进行查找</li>
		
	</ul>
</div>
</div>
			<%
		}
		%>
			
																		
	</div>
	</div>
<%if(programmeArr!=null && programmeArr.length()>0){  %>	
	<div class="pager">
		<%=PageUtil.getNewContent("/channel/teleplay_"+StringUtils.trimToEmpty(year_str)+"_"+StringUtils.trimToEmpty(type_str)+
				"_"+StringUtils.trimToEmpty(area_str)+"_",".html",total,pageNo,pageSize)%>
	</div>
<%} %>
</div>
<div class="clear"></div></div><!--layout end-->
</div><!--body end-->
<%@ include file="/result/inc/footer.jsp" %>

</div><!--screen end-->
</div><!--widnow end-->
</body>
<script type="text/javascript">
		document.getElementById("headq").focus();
		Ab('');
</script>
<script type="text/javascript" src="/js/sotool.js"></script>
<script type="text/javascript">
		$(function(){
			if($().lazyload) {
				$("img").lazyload({placeholder : "http://static.youku.com/v1.0.0661/index/img/sprite.gif"});
			}
		});
</script>
<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
<script type="text/javascript">
if(typeof youkuTracker == "function") {
	youkuTracker( "soku=" + library + "|" + result_count );sokuHz();
}
</script>
</html>