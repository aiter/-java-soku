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
JSONObject result          = (JSONObject)request.getAttribute("result");
%>

<%
JSONObject bar             = (JSONObject)request.getAttribute("bar");
JSONObject postsearchJson= (JSONObject)request.getAttribute("postsearch");
%>

<%
String base_url             = "/search_user/";
%>
<!DOCTYPE HTML>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%= WebUtils.htmlEscape(webParam.getQ()) %> 优酷视频</title>
<meta name="title" content="<%= WebUtils.htmlEscape(webParam.getQ()) %> - <%= WebUtils.htmlEscape(webParam.getQ()) %>空间搜索"> 
<meta name="keywords" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>, <%= WebUtils.htmlEscape(webParam.getQ()) %>空间"> 
<meta name="description" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>搜索 - 优酷网为你提供最为专业全面的<%= WebUtils.htmlEscape(webParam.getQ()) %> 视频搜索">
<link href="<%=STATIC_SERVER%>/css/soku.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=STATIC_SERVER%>/js/autocomplete1.1.js"></script>
<script src="<%=STATIC_SERVER%>/js/jquery.js"></script>
<script type="text/javascript">
            var searchResult = <%= result.optInt("total") %>;
            if(searchResult==-1) searchResult = "filtered";
            var cateStr = 'search-' + searchResult;
</script>
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
				<div class="keyInfo">
		        搜索 <span class="key"><%= WebUtils.htmlEscape(webParam.getQ()) %></span> 相关空间 共找到 <span class="num"><%= result.optInt("total") %></span> 个相关的空间
	        </div>

	<div class="result">
			<div class="ucoll">
<div class="items">
 <% JSONObject items = JSONUtil.getProperty(result, JSONObject.class, "items"); %>
				            <% if(!JSONUtil.isEmpty(items)) { %>			                
			                <% String[] names = JSONObject.getNames(items); %>
                            <% for(int i = 0; names != null && i < names.length; i++) { %>
                                <% JSONObject user = items.optJSONObject(names[i]); %>
                                <% if(JSONUtil.isEmpty(user)) { continue; } %>
	<ul class="u">
	<% int gender = user.optInt("gender"); %>
	<% String userName = WebUtils.htmlEscape(user.optString("user_name")); %>
						                        <% String user_icon = null; %>
						                        <% if (user.optString("icon64").length() > 0) { %>
						                            <% user_icon = "http://g" + (i%4+1) + ".ykimg.com/" + user.optString("icon64"); %>
						                        <% } else if (gender == -1) { %>
    						                        <% user_icon = CDN_SERVER + "/user/img/head/64/999.jpg"; %>
						                        <% } else if (gender == 1) { %>
    						                        <% user_icon = CDN_SERVER + "/user/img/head/64/200.jpg"; %>
						                        <% } else { %>
    						                        <% user_icon = CDN_SERVER + "/user/img/head/64/100.jpg"; %>
						                        <% } %>
		<li class="u_photo"><a href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(user.optString("pk_user")) %>.html" title="<%=userName%>">
			<img src="<%= user_icon %>" alt="<%=userName %>"></a></li>
		<li class="u_name"><a href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(user.optString("pk_user")) %>.html" title="<%=userName %>"><%=userName %></a></li>
		<li class="u_sex"><% if (gender == -1) { %>
									                        保密
									                    <% } else if (gender == 1) { %>
									                        女
									                    <% } else { %>
									                        男
									                    <% } %></li>
		<li class="u_city"><%= WebUtils.htmlEscape(user.optString("city")) %></li>
		<li class="u_stat"><label>视频:</label><span class="num"><%= WebUtils.formatNumber(user.optInt("video_count"), "###,###") %></span></li>
		<li class="u_stat"><label>订阅:</label><span class="num"><%= WebUtils.formatNumber(user.optInt("order_count"), "###,###") %></span></li>
	</ul>
	
	<% if ((i+1)%3 == 0) { %>
                               	<div class="clear"></div>
                               	<% } %>
                            <% } %>
                            
                       		
                       		<% if (items.length()%3 != 0) { %>
                       		<div class="clear"></div>
                       		<% } %>
                       		
                       		<% } %>
                       		
					
		</div><!--items end-->
</div><!--coll end-->		
	

<div class="pager">
	<ul class="pages">
	<%@ include file="inc/pager.jsp" %>	
	</ul>
</div>
<div id='ab_280'></div>
</div><!--result end-->

	</div><!--maincol end-->
	<div class="sidecol">
	<!--bar-->
         <%@ include file="inc/partBar.jsp" %>
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