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
JSONArray cms              = (JSONArray)request.getAttribute("cms");
JSONObject bar             = (JSONObject)request.getAttribute("bar");
%>

<%
String base_url             = "/search_bar/";
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title><%= WebUtils.htmlEscape(webParam.getQ()) %> 看吧</title>
        <meta name="title" content="<%= WebUtils.htmlEscape(webParam.getQ()) %> 优酷视频"> 
        <meta name="keywords" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>, <%= WebUtils.htmlEscape(webParam.getQ()) %>视频"> 
        <meta name="description" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>搜索 - 优酷网为你提供最为专业全面的<%= WebUtils.htmlEscape(webParam.getQ()) %>视频搜索"> 

        <link href="<%= CDN_SERVER %>/index/css/youku.css" rel="stylesheet" type="text/css" />
        <link href="<%= CDN_SERVER %>/bar/css/barBrowse.css" rel="stylesheet" type="text/css" />
        <link href="<%= CDN_SERVER %>/search/css/search.css" rel="stylesheet" type="text/css" />

        <script type="text/javascript">NovaOptions={compatibleMode:true};__LOG__=function(){};Nova={};VERSION='<%= CDN_VERSION %>';</script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/js/prototype.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/js/nova.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/index/js/common.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/search/js/search.js"></script>
        
        <script language="javascript">
            var searchResult = <%= result.optInt("total") %>;
            if(searchResult==-1) searchResult = "filtered";
            var cateStr = 'search-' + searchResult;
        </script>
    </head>

    <body class="s_soBar">
        <%@ include file="inc/head.jsp" %>
        
	    <div class="s_main col2_21">
		    <div id='ab_282'></div>

					
		    <div class="viewTab">
			    <ul class="tabs">
				    <c:if test='${webParam.orderby == 1}'><li class="current">按发表时间倒序</li></c:if>
                    <c:if test='${webParam.orderby != 1}'><li><a href="<%= base_url %><%= WebParamHelper.encode(webParam, "orderby_1_page_1") %>" charset="801-82-1">按发表时间倒序</a> <c:if test='${webParam.orderby != 2}'>|</c:if> </li></c:if>
                    
                    <c:if test='${webParam.orderby == 2}'><li class="current">按发表时间正序</li></c:if>
                    <c:if test='${webParam.orderby != 2}'><li><a href="<%= base_url %><%= WebParamHelper.encode(webParam, "orderby_2_page_1") %>" charset="801-82-2">按发表时间正序</a></li></c:if>
			    </ul>
			    <div class="clear"></div>
		    </div>
		    
		    <div class="keyInfo">
			    搜索<span class="key"><%= WebUtils.htmlEscape(webParam.getQ()) %></span>相关帖子 共找到相关贴子<span class="num"><%= result.optInt("total") %></span>篇
		    </div>

		    <div class="left">
			    <div class="searchResult">
				    <div class="topicData">
					    <% { %>
					    <% JSONObject items = JSONUtil.getProperty(result, JSONObject.class, "items"); %>
					    <% if(!JSONUtil.isEmpty(items)) { %>
					        <table cellpadding="0" cellspacing="0">
					        <% String[] names = JSONObject.getNames(items); %>
					        <% for(int i = 0; names != null && i < names.length; i++) { %>
    					        <% JSONObject item = items.optJSONObject(names[i]); %>
	        			        <% if(JSONUtil.isEmpty(item)) { continue; } %>
					            <tr>
					                <td>
						                <% if(item.optString("videologo").length() > 0) { %>
						                <a href="http://v.youku.com/v_show/id_<%= item.optString("encodeVid") %>.html" target="_blank">
						                    <img class="vClip" src="http://g<%= (i%4)+1 %>.ykimg.com/<%= item.optString("videologo") %>" title="<%= WebUtils.htmlEscape(item.optString("subject")) %>" alt="<%= WebUtils.htmlEscape(item.optString("subject")) %>" />
						                </a>
						                <% } %>
						                
						                <ul class="tInfo">
							                <li>
							                    <a class="t" href="http://kanba.youku.com/bar_barPost/word_<%= WebUtils.urlEncode(item.optString("bar_name")) %>_subjectid_<%= WebUtils.urlEncode(item.optString("fk_subject")) %>" target="_blank"><%= WebUtils.htmlEscape(item.optString("subject")) %></a>
							                </li>
							                <li><%= WebUtils.htmlEscape(item.optString("content")) %></li>
							                <li class="tParam">
							                    <span class="c_gray">看吧:</span>
							                    <a href="http://kanba.youku.com/bar_bar/word_<%= WebUtils.urlEncode(item.optString("bar_name")) %>" target="_blank"><%= WebUtils.htmlEscape(item.optString("bar_name")) %></a>
							                    
								                <span class="c_gray">作者:</span>
								                <a href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(item.optString("poster_id")) %>.html" target="_blank" title="<%= WebUtils.htmlEscape(item.optString("poster_name")) %>"><%= WebUtils.htmlEscape(item.optString("poster_name"), 8, "...") %></a>
								                
								                <span class="c_gray">最后回复:</span>
								                <span class="num"><%= WebUtils.htmlEscape(item.optString("last_post_time")) %></span>
							                </li>
						                </ul>
						            </td>
						        </tr>
					        <% } %>
					        </table>
					    <% } %>
					    <% } %>
				    </div>
			    </div>
			
			    <div class="LPageBar">
                    <%@ include file="inc/pager.jsp" %>
                </div>
			
                <input type="hidden" name="showKeyword" id="showKeyword" value="<%= WebUtils.htmlEscape(webParam.getQ()) %>">
                <input type="hidden" name="resultTotal" id="resultTotal" value='<%= result.optInt("total") %>'>
                <input type="hidden" name="searchTime" id="searchTime" value="<%= System.currentTimeMillis()/1000 %>">

                <script type="text/javascript">
                    window.nova_init_hook_toSearchStat = function() {toSearchStat();}
                </script>
                
                <div class="clear"></div>
                <br />
                <div id='ab_280'></div>
		    </div><!--left end-->
		
		
		    <div class="right">
		        <!--bar-->
                <%@ include file="inc/partBar.jsp" %>
		        
		        <%@ include file="inc/rightAd.jsp" %>
		    </div><!--right end-->
		
		    <div class="clear"></div>
	    </div>

        <!--版权申明及其它-->
        <%@ include file="inc/foot.jsp" %>
        <%@ include file="inc/loadAd.jsp" %>
    </body>
</html>
