<%@page import="javax.servlet.http.Cookie"%>
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
<%@ page import="org.apache.commons.lang.StringUtils" %>

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
Map<String,String> webParameter=(Map<String,String>)request.getAttribute("webParameter");
List<Category> categories  = (List<Category>)request.getAttribute("categories");
JSONObject result          = (JSONObject)request.getAttribute("result");
%>

<%
JSONArray cms              = (JSONArray)request.getAttribute("cms");
JSONArray recommendShows   = (JSONArray)request.getAttribute("recommendShows");
JSONObject bar             = (JSONObject)request.getAttribute("bar");
JSONObject postsearchJson= (JSONObject)request.getAttribute("postsearch");
%>

<%
String base_url             = "/search_video/";
%>


<!DOCTYPE HTML>
<html>
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title><%= WebUtils.htmlEscape(webParam.getQ()) %> 优酷视频</title>
        <meta name="title" content="<%= WebUtils.htmlEscape(webParam.getQ()) %> 优酷视频"> 
        <meta name="keywords" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>, <%= WebUtils.htmlEscape(webParam.getQ()) %>视频"> 
        <meta name="description" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>搜索 - 优酷网为你提供最为专业全面的<%= WebUtils.htmlEscape(webParam.getQ()) %>视频搜索"> 
        <link rel="alternate" type="application/rss+xml" title="优酷视频-<%= WebUtils.htmlEscape(webParam.getQ()) %>搜索结果" href="/search/rss/type/video/q/<%= WebUtils.urlEncode(webParam.getQ()) %>" />
        <link href="<%=STATIC_SERVER%>/css/soku.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="<%=STATIC_SERVER%>/js/autocomplete1.1.js"></script>
		<script type="text/javascript" src="<%=STATIC_SERVER%>/js/jquery.js"></script>
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
			<ul>
			<%String encodeKeyword=WebUtils.htmlEscape(webParam.getQ()); %>
			<%if(searchType!=null && searchType.equals("video")){%>
			<li class="current"><span>视频</span></li>
			<%}else{ %>
			<li><a href="/search_video/q_<%=encodeKeyword%>">视频</a></li>
			<%} %>
			<%if(searchType!=null && searchType.equals("playlist")){%>
			<li class="current"><span>专辑</span></li>
			<%}else{ %>
			<li><a href="/search_playlist/q_<%=encodeKeyword%>">专辑</a></li>
			<%} %>		
			</ul>
		</div>
	</div>
	
<div class="layout_21">
	
	<div class="maincol">
		<% String suggestion = result.optString("suggestion",""); %>
		<% if(suggestion.length()>0){ %>
			<div class="keylike">你是不是要找： &quot;<a href="<%= base_url %>q_<%= WebUtils.urlEncode(suggestion) %>"><%= WebUtils.htmlEscape(suggestion) %></a>&quot; </div>
		<%} %>




<div class="result">
			<!--缩略图方式||列表方式-->
<!--2,5,7 演示站外视频形式-->
<div class="null">
	<div class="sorry">
		抱歉，没有找到<span class="key"><%= WebUtils.htmlEscape(webParam.getQ()) %></span>的相关视频。	</div>
	<h3>建议您：</h3>
	<ul>
		<li>• 检查输入的关键词是否有误。</li>
		<li>• 缩短关键词。</li>
		<li>• 使用相近、相同或其他语义的关键词。</li>
		<li>• 放宽筛选条件。</li>
	</ul>
</div>

</div><!--result end-->
<% if(!JSONUtil.isEmpty(JSONUtil.getProperty(result, JSONObject.class, "relevantwords", "items"))) { %>
<div class="relkeys">
	<label>相关搜索:</label>
	<ul>

                        <% JSONObject items = JSONUtil.getProperty(result, JSONObject.class, "relevantwords", "items"); %>
                        <% String[] names = JSONObject.getNames(items); %>
                        <% for(int i = 0; names != null && i < names.length && i<10; i++) { %>
                            <% JSONObject item = items.optJSONObject(names[i]); %>
                            <% String word = item != null ? item.optString("query") : ""; %>
                            <% if (word.length() == 0) { continue; } %>
                            <li><a href="<%=base_url%>q_<%=WebUtils.urlEncode(word)%>" target="_blank" title="" >
                            <%=word%></a></li>
                        <% } %>

   </ul>
	<div class="clear"></div>
</div>
 <% } %>		
	</div><!--maincol end-->
	<div class="sidecol">
	
		 <!--cms-->
         <%@ include file="inc/partCMS.jsp" %>
     
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