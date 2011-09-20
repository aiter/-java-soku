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

<%@ page import="com.youku.search.sort.servlet.search_page.util.MiscUtils" %>

<%@ page import="org.json.*" %>

<%@ include file="inc/cdn.jsp" %>

<%
String searchType       = (String)request.getAttribute("searchType");
String searchCaption    = (String)request.getAttribute("searchCaption");
String searchExtend     = (String)request.getAttribute("searchExtend");
%>

<%
WebParam webParam          = (WebParam)request.getAttribute("webParam");
List<Category> categories  = (List<Category>)request.getAttribute("categories");
JSONObject result          = (JSONObject)request.getAttribute("result");
String showMsg             = (String)request.getAttribute("showMsg");
%>

<%
String base_url             = "/search_hdvideo/";
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>优酷视频</title>
        <meta name="title" content="<%= WebUtils.htmlEscape(webParam.getQ()) %> 优酷视频"> 
        <meta name="keywords" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>, <%= WebUtils.htmlEscape(webParam.getQ()) %>视频"> 
        <meta name="description" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>搜索 - 优酷网为你提供最为专业全面的<%= WebUtils.htmlEscape(webParam.getQ()) %>视频搜索"> 
        
        <link href="<%= CDN_SERVER %>/index/css/youku.css" rel="stylesheet" type="text/css" />
        <link href="<%= CDN_SERVER %>/search/css/search.css" rel="stylesheet" type="text/css" />

        <script type="text/javascript">NovaOptions={compatibleMode:true};__LOG__=function(){};Nova={};VERSION='<%= CDN_VERSION %>';</script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/js/prototype.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/js/nova.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/index/js/common.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/search/js/search.js"></script>

        <script language="javascript">
            var searchResult = <%= result.optInt("total") %>;
            if(searchResult==-1)searchResult="filtered";
            var cateStr = 'search-'+searchResult;
        </script>
    </head>

    <body class="s_soBar">
        <%@ include file="inc/head.jsp" %>
        
        <div class="s_main col2_21">
            <div id='ab_596'></div>

            <div class="left">
            <% String suggestion = result.optString("suggestion", ""); %>
                <% if(suggestion.length() > 0){ %>
                <div class="keyLike">
                    您要找的是不是
                    <a class="key" href="<%= base_url %>q_<%= WebUtils.urlEncode(suggestion) %>"><%= WebUtils.htmlEscape(suggestion) %></a>
                </div>
                <% } %>
                <div class="sorry">
                    <%= WebUtils.eq(showMsg, null, "", showMsg) %>
                </div>
            
                <div class="return">
                    <script language="javascript">
                    if(document.all){
                        if(window.history.length>0){
                            document.write('<a href="javascript:history.go(-1)">返回上一页</a>');
                        }
                    }else{
                            if(window.history.length>1){
                                document.write('<a href="javascript:history.go(-1)">返回上一页</a>');
                        }
                    }
                    </script>
                </div>
                
                <div class="noResultInfo">
                    <span class="f_14">建议您：</span>
                    <ul>
                        <li>• 尽量使用简洁的关键词，或用空格将多个关键词分开</li>
                        <li>• 检查输入的文字、搜索条件，再搜搜看</li>
                        <li>• 在<a href="http://kanba.youku.com/bar_bar/word_<%= WebUtils.urlEncode("优酷求片") %>" target="_blank">优酷求片吧</a>发表贴子，请大家来帮忙</li>
                        
                        <%--
                        {if !empty($bars)}
                            {section name=bar start=0 loop=$bars}
                                {if !empty($bars[bar].create) }
                                    {if $bars[bar].create==1 }
                                        <li>• 发表留言创建“<a href="{url type='bar' word=$bars[bar].bar_name}" target="_blank">{$bars[bar].bar_name|escape:htmlall}</a>”吧</li>
                                    {/if}
                                {else}
                                    <li>• <a href="{url type='bar' word=$bars[bar].bar_name}" target="_blank">查看<span class="key">{$bars[bar].bar_name|escape:htmlall}</span>的相关视频讨论<span class="num">{$bars[bar].subjectCount}</span>篇</a></li>
                                {/if}
                            {/section}
                        {/if}
                        --%>
                        
                        <li>• 向优酷管理员<a href="http://www.youku.com/service/feed/subtype/5/q/<%= WebUtils.urlEncode(webParam.getQ()) %>" target="_blank">反映反映</a></li>
                    </ul>
                </div>
            
                <% JSONArray itemsJSONArray = JSONUtil.getProperty(result, JSONArray.class, "outSearchResult", "items"); %>
                <% if(!JSONUtil.isEmpty(itemsJSONArray)) { %>
                    <div class="outSearch">
                        <div class="box_border1">
                            <h3 class="resultFrom">以下为站外搜索结果</h3>
                            <div class="vCollImg">
                                <div class="videos">
                                    <% for(int i = 0; i < itemsJSONArray.length(); i++) { %>
                                        <% JSONObject outvideo = itemsJSONArray.optJSONObject(i); %>
                                        <ul class="video">
                                            <li class="videoImg">
                                                <a href="<%= outvideo.optString("url", "") %>" target="_blank" onclick="logSearchClick('soku_click','video','<%= WebUtils.jsEscape(outvideo.optString("url", "")) %>','<%= WebUtils.jsEscape(webParam.getQ()) %>')">
                                                    <img src="<%= outvideo.optString("logo", "") %>" title="<%= WebUtils.htmlEscape(outvideo.optString("title", "")) %>" alt="<%= WebUtils.htmlEscape(outvideo.optString("title", "")) %>" />
                                                </a>
                                            </li>            
                                            
                                            <li>
                                                <a href="<%= outvideo.optString("url", "") %>" title="<%= WebUtils.htmlEscape(outvideo.optString("title", "")) %>" target="_blank" onclick="logSearchClick('soku_click','video','<%= WebUtils.jsEscape(outvideo.optString("url", "")) %>','<%= WebUtils.jsEscape(webParam.getQ()) %>')">
                                                    <%if (outvideo.optString("title_hl", "").isEmpty()) { %>
                                                        <%= outvideo.optString("title", "") %>
                                                    <% } else { %>
                                                        <%= outvideo.optString("title_hl", "") %>
                                                    <% } %>
                                                </a>
                                                
                                                <% if (outvideo.optLong("seconds") > 0) { %>
                                                    <span class="num"><%= MiscUtils.timeLength(outvideo.optLong("seconds")) %></span>
                                                <% } %>
                                            </li>
                                            
                                            <li class="videoFrom"><%= WebUtils.htmlEscape(outvideo.optString("site_name", "")) %></li>
                                        </ul>
                                        
                                        <% if((i+1)%4 == 0) { %><div class="clear"></div><% } %>
                                    <% } %>
                                    
                                    <% if(itemsJSONArray.length()%4 != 0) { %><div class="clear"></div><% } %>
                                </div>
                            </div>    
                        </div>
                    </div>
                <% } %>
                
                
                <% JSONArray recommendVideoJSONArray = JSONUtil.getProperty(result, JSONArray.class, "recommendVideos"); %>
                <% if(!JSONUtil.isEmpty(recommendVideoJSONArray)) { %>
                    <div class="hot">
                        <h3 class="hotTitle">优酷目前最热的视频，您看过了么？</h3>
                        <div class="vCollImg">
                            <div class="videos">
                                <% for(int i = 0; i < recommendVideoJSONArray.length(); i++) { %>
                                    <% JSONObject item = recommendVideoJSONArray.optJSONObject(i); %>
                                    <ul class="video">
                                        <li class="videoImg"><a href="http://v.youku.com/v_show/id_<%= item.optString("videoid", "") %>.html" alt="<%= WebUtils.htmlEscape(item.optString("title", "")) %>" title="<%= WebUtils.htmlEscape(item.optString("title", "")) %>" target="video"><img src="<%= item.optString("thumburl", "") %>" alt="<%= WebUtils.htmlEscape(item.optString("title", "")) %>" width="128" height="96" /></a></li>
                                        <li class="playMenu"><img class="QLiconB" title="添加到点播单" id="PlayListFlag_<%= item.optString("videoid", "") %>" style="display:none"/></li>
                                        <li><h1><a href="http://v.youku.com/v_show/id_<%= item.optString("videoid", "") %>.html" alt="<%= WebUtils.htmlEscape(item.optString("title", "")) %>" title="<%= WebUtils.htmlEscape(item.optString("title", "")) %>" target="video"><%= WebUtils.htmlEscape(item.optString("title", ""), 16, "...") %></a> <span class="num"><%= MiscUtils.timeLength(item.optLong("seconds")) %></span></h1></li>
                                        <li><span class="ico__statup" title="顶"></span> <span class="num"><%= item.optLong("total_up") %></span> <span class="ico__statdown" title="踩"></span> <span class="num"><%= item.optLong("total_down") %></span></li>
                                        <li class="vUser"><a target="_blank" href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(item.optString("userid")) %>.html"><%= WebUtils.htmlEscape(item.optString("username", "")) %></a></li>
                                        <li>发布: <span class="post"><%= MiscUtils.createTime(item.optString("createtime")) %></span></li>
                                        <li>播放: <span class="num"><%= WebUtils.formatNumber(item.optInt("total_vv"), "###,###") %></span></li>
                                        <li>评论: <span class="num"><%= WebUtils.formatNumber(item.optInt("total_comment"), "###,###") %></span></li>
                                    </ul>
                                <% } %>
                                
                                <div class="clear"></div>
                            </div>
                        </div>
                    </div>
                <% } %>
            
            
                <div id='ab_600'></div>
            
                <% JSONObject relevantwords = JSONUtil.getProperty(result, JSONObject.class, "relevantwords", "items"); %>
                <% if(!JSONUtil.isEmpty(relevantwords)) { %>

                    <div class="box_border aboutSearch">
                        <h3 class="title label">相关搜索:</h3>
                        
                        <ul>
                            <% String[] names = JSONObject.getNames(relevantwords); %>
                            <% for(int i = 0; names != null && i < names.length; i++) { %>
                                <% JSONObject item = relevantwords.optJSONObject(names[i]); %>
                                <% String word = (item == null) ? "" : item.optString("query", ""); %>
                                <li><a href="/search_video/q_<%= WebUtils.urlEncode(word) %>" target="_blank" title="<%= WebUtils.htmlEscape(word) %>"><%= WebUtils.htmlEscape(word) %></a></li>
                            <% } %>
                        </ul>
                        <div class="clear"></div>
                    </div>
                <% } %>
            
                <input type="hidden" name="searchTime" id="searchTime" value="<%= System.currentTimeMillis()/1000 %>">
            
            </div><!--left end-->
        
            <div class="right">
                <div id='ab_597'></div>
                <div id='ab_598'></div>
                <div id='ab_599'></div>
                <div id='ab_601'></div>
            </div><!--right end-->
        
            <div class="clear"></div>
        </div>

        
        <!--版权申明及其它-->
        <%@ include file="inc/foot.jsp" %>

        <script type="text/javascript">
            window.nova_init_hook_ab=function()
            Nova.addScript("http://html.atm.youku.com/html?p=596,597,598,599,600,601&k=<%= WebUtils.urlEncode(webParam.getQ()) %>");
        </script>

        <script>toNoResultStat();</script>
    </body>
</html>
