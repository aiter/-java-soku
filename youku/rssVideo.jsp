<%@ page contentType="application/xml; charset=utf-8" language="java" %>
<%@ page buffer="1kb" %>
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
List<Category> categories  = (List<Category>)request.getAttribute("categories");
JSONObject result          = (JSONObject)request.getAttribute("result");
int blursearch             = (Integer)request.getAttribute("blursearch");
%>

<%
JSONArray cms              = (JSONArray)request.getAttribute("cms");
%>

<%
String base_url             = "/search_video/";
%>

<%
response.resetBuffer();
%><?xml version="1.0" encoding="utf-8"?>

<rss version="2.0" xmlns:media="http://search.yahoo.com/mrss" xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">
    <channel>
        <title><![CDATA[<%= webParam.getQ() %>-视频搜索-优酷网]]></title>
        <description><![CDATA[<%= webParam.getQ() %>-视频搜索-优酷网]]></description>
        <link>http://www.youku.com</link>
        <lastBuildDate><%= WebUtils.formatRssDate(new java.util.Date()) %></lastBuildDate>
        <image>
            <url>http://www.youku.com/index/img/youkulogo.gif</url>
            <title>优酷网-中国第一视频网，提供视频播放，视频发布，视频搜索</title>
            <link>http://www.youku.com</link>
            <description>视频服务平台，提供视频播放，视频发布，视频搜索、视频分享</description>
        </image>
        
        <% JSONObject items = JSONUtil.getProperty(result, JSONObject.class, "items"); %>
        <% if(!JSONUtil.isEmpty(items)) { %>
            <% String[] names = JSONObject.getNames(items); %>
            <% for(int i = 0; names != null && i < names.length; i++) { %>
                <% JSONObject video = items.optJSONObject(names[i]); %>
                <% if(JSONUtil.isEmpty(video)) { continue; } %>
                <% String videoUrl = "http://v.youku.com/v_show/id_" + video.optString("encodeVid") + ".html"; %>
                <item>
                    <title><![CDATA[<%= video.optString("title") %>]]></title>
                    <link><%= videoUrl %></link>
                    <description><![CDATA[
		                    	<a href="<%= videoUrl %>"><img src="http://g<%= (i%4)+1 %>.ykimg.com/<%= video.optString("logo") %>" border="0" width="120" height="90" vspace="4" hspace="4" title="<%= WebUtils.htmlEscape(video.optString("title")) %>" target="_blank" /></a>
		                    	<p><%= WebUtils.htmlEscape(video.optString("memo")) %></p>
                    ]]></description>
                    <itunes:duration><%= video.optInt("seconds")/60 %>:<%= video.optInt("seconds")%60 %></itunes:duration>
                    <itunes:keywords><![CDATA[<%= video.optString("tags") %>]]></itunes:keywords>
                    <author><![CDATA[<%= video.optString("owner_username") %>]]></author>
                    <size><%= video.optInt("size") %></size>
                    <publicType><![CDATA[<%= WebUtils.eq(video.optString("public_type"), "", 0, video.optString("public_type")) %>]]></publicType>
                    <comments><%= videoUrl %>#replyLocation</comments>
                    <pubDate><%= MiscUtils.rssCreateTimeTime(video.optString("createtime")) %></pubDate>
                    <guid><%= videoUrl %></guid>
                    <enclosure type="application/x-shockwave-flash" url="http://player.youku.com/player.php/sid/<%= video.optString("encodeVid") %>/v.swf"></enclosure>
                </item>
            <% } %>
        <% } %>
        
    </channel>
</rss>
