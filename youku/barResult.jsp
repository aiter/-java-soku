<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="java.util.*" %>

<%@ page import="com.youku.search.sort.entity.CategoryMap.Category" %>
<%@ page import="com.youku.search.sort.json.util.JSONUtil" %>
<%@ page import="com.youku.search.sort.servlet.search_page.WebParam" %>
<%@ page import="com.youku.search.sort.servlet.search_page.WebParamHelper" %>
<%@ page import="com.youku.search.sort.servlet.util.WebUtils" %>
<%@ page import="com.youku.search.sort.servlet.search_page.util.MiscUtils" %>

<%@ page import="org.json.*" %>

<%@ include file="inc/cdn.jsp" %>

<%
/*
String searchType       = "video";
String searchCaption    = "视频";

String searchType       = "playlist";
String searchCaption    = "专辑";

String searchType       = "bar";
String searchCaption    = "看吧";

String searchType       = "user";
String searchCaption    = "空间";


String searchExtend     = "bar";
String searchExtend     = "post";
String searchExtend     = "user";
*/

String searchType       = (String)request.getAttribute("searchType");
String searchCaption    = (String)request.getAttribute("searchCaption");
String searchExtend     = (String)request.getAttribute("searchExtend");
%>

<%
WebParam webParam          = (WebParam)request.getAttribute("webParam");
JSONObject bar             = (JSONObject)request.getAttribute("bar");
JSONObject relatedBars     = (JSONObject)request.getAttribute("relatedBars");
JSONArray bar_catalogs     = (JSONArray)request.getAttribute("bar_catalogs");
JSONArray lastest_posts    = (JSONArray)request.getAttribute("lastest_posts");
JSONObject postsearchJson= (JSONObject)request.getAttribute("postsearch");
JSONObject result=null;
%>

<%
String base_url             = "/search_bar/";
%>

<%
if(JSONUtil.isEmpty(bar)) {
    response.sendRedirect("http://kanba.youku.com/bar_search?kw=" + WebUtils.urlEncode(webParam.getQ()) + "&st=1");
}
%>
<!DOCTYPE HTML>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%= WebUtils.htmlEscape(webParam.getQ()) %> 看吧</title>
<meta name="title" content="<%= WebUtils.htmlEscape(webParam.getQ()) %> 优酷视频"> 
<meta name="keywords" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>, <%= WebUtils.htmlEscape(webParam.getQ()) %>视频"> 
<meta name="description" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>搜索 - 优酷网为你提供最为专业全面的<%= WebUtils.htmlEscape(webParam.getQ()) %>视频搜索">
<link href="<%=STATIC_SERVER%>/css/soku.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=STATIC_SERVER%>/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="<%=STATIC_SERVER%>/js/jquery.js"></script>
</head>
<body class="foryouku">
<div class="window">
<div class="screen">

<%@ include file="inc/newhead.jsp" %>
<div class="body">
<div class="typechk">
<div class="main">
			
		</div>
</div><div class="layout_21">	
	<div class="maincol">

	<div class="result">
	
	<% if(!JSONUtil.isEmpty(bar) && webParam.getPage()==1) { %>
	<div class="barcoll">
	<div class="items">
		
		<ul class="bar">
			<li class="name">
				<a href="http://kanba.youku.com/bar_bar/word_<%= WebUtils.urlEncode(bar.optString("barname")) %>" target="_blank"><span class="key"><%= WebUtils.htmlEscape(bar.optString("barname")) %></span>吧</a>
			</li>
			<li class="info">
				<span class="category"><a href="#" target="_blank">明星人物</a> <em>&gt;&gt;</em> <a href="#" target="_blank">外国明星</a></span>
				<span class="stat">
					主题:<span class="num"><%= WebUtils.formatNumber(bar.optInt("subjectcount"), "###,###") %></span>     
					吧藏视频:<span class="num"><%= WebUtils.formatNumber(bar.optInt("videocount"), "###,###") %></span>     
					吧友:<span class="num"><%= WebUtils.formatNumber(bar.optInt("membercount"), "###,###") %></span>     
					总浏览:<span class="num"><%= WebUtils.formatNumber(bar.optInt("total_pv"), "###,###") %></span>
					今日浏览:<span class="num"><%= WebUtils.formatNumber(bar.optInt("day_pv"), "###,###") %></span>
				</span>
			</li>
			
			<% if(!JSONUtil.isEmpty(lastest_posts)) { %>
			<li class="newpost">
				<label>最新帖子:</label>
				<ul>
					<% for(int i = 0; i < lastest_posts.length(); i++) { %>
    							    <% JSONObject item = lastest_posts.optJSONObject(i); %>
    							    <% if(JSONUtil.isEmpty(item)) { continue; } %>
								    <li>
								        <a href="http://kanba.youku.com/bar_barPost/word_<%= WebUtils.urlEncode(bar.optString("barname")) %>_subjectid_<%= WebUtils.urlEncode(item.optString("pk_subject")) %>" target="_blank"><%= WebUtils.htmlEscape(item.optString("subject")) %></a>
								    </li>
    						    <% } %>
				</ul>
			</li>
			<%} %>
			<li class="enter">
				<a href="http://kanba.youku.com/bar_bar/word_<%= WebUtils.urlEncode(bar.optString("barname")) %>" target="_blank" class="arrow">进入<%= WebUtils.htmlEscape(bar.optString("barname")) %>吧&gt;&gt;</a>
			</li>
		</ul>
		<% JSONObject items = JSONUtil.getProperty(relatedBars, JSONObject.class, "items"); %>
        		<% if(!JSONUtil.isEmpty(items)) { %>
		<div class="like">
			<div class="caption"><span class="key"><%= WebUtils.htmlEscape(webParam.getQ()) %></span>相关看吧:</div>
			<ul>
			<% String[] names = JSONObject.getNames(items); %>
				    <% for(int i = 0; names != null && i < names.length; i++) { %>
				        <% JSONObject item = items.optJSONObject(names[i]); %>
				        <% if(JSONUtil.isEmpty(item)) { continue; } %>
				<li>• <a class="keyLike" href="http://kanba.youku.com/bar_bar/word_<%= WebUtils.urlEncode(item.optString("bar_name")) %>" target="_blank"><%= WebUtils.htmlEscape(item.optString("bar_name")) %></a></li>
				<% } %>
			</ul>
		</div>
		 <% } %>
	
	
	</div><!--items end-->
</div><!--coll end-->

<%} %>

<div class="postcoll">
	<div class="items">
	<% JSONObject items = JSONUtil.getProperty(postsearchJson, JSONObject.class, "items"); %>
	<% if(!JSONUtil.isEmpty(items)) { %>
		<% String[] names = JSONObject.getNames(items); %>
		<% for(int i = 0; names != null && i < names.length; i++) { %>
    					        <% JSONObject item = items.optJSONObject(names[i]); %>
	        			        <% if(JSONUtil.isEmpty(item)) { continue; } %>
		<ul class="post">
			<li class="title"><a href="http://kanba.youku.com/bar_barPost/word_<%= WebUtils.urlEncode(item.optString("bar_name")) %>_subjectid_<%= WebUtils.urlEncode(item.optString("fk_subject")) %>" target="_blank">
				<%= WebUtils.htmlEscape(item.optString("subject")) %></a></li>
			<li class="summary"><%= WebUtils.htmlEscape(item.optString("content")) %></li>
			<li class="info">
				<label>看吧:</label><a href="http://kanba.youku.com/bar_bar/word_<%= WebUtils.urlEncode(item.optString("bar_name")) %>" target="_blank"><%= WebUtils.htmlEscape(item.optString("bar_name")) %></a>
				<label>作者:</label><a href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(item.optString("poster_id")) %>.html" target="_blank" title="<%= WebUtils.htmlEscape(item.optString("poster_name")) %>"><%= WebUtils.htmlEscape(item.optString("poster_name"), 8, "...") %></a> 
				<label>最后回复:</label><span class="num"><%= WebUtils.htmlEscape(item.optString("last_post_time")) %></span>
			</li>
		</ul> 
	 
		
		<%}}%>

	</div><!--items end-->
</div><!--coll end-->		

<div class="pager">
	<ul class="pages">
	<%@ include file="inc/pager.jsp" %>	
	</ul>
</div>
<div style="display: block;" id="ab_280"></div>

</div><!--result end-->
	
	</div><!--maincol end-->
	<div class="sidecol">
	<%@ include file="inc/rightAd.jsp" %>  	
	</div><!--sidecol end-->
	<div class="clear"></div>
</div><!--layout end-->
</div><!--body end-->

<%@ include file="inc/newfoot.jsp" %>
</div><!--screen end-->
</div><!--widnow end-->
</body>
<script type="text/javascript">document.getElementById("headq").focus(); Ab(false, true);</script>
<script type="text/javascript" src="<%=STATIC_SERVER%>/js/youkutool.js"></script>
<script type="text/javascript" src="http://urchin.lstat.youku.com/index/js/urchin.js"></script>
<script type="text/javascript">urchinTracker();</script>
<script type="text/javascript" src="http://html.atm.youku.com/html?p=280,282,404,405,406,492,618,714&k=s"></script>
</html>