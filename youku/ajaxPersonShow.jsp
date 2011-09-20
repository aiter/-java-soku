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

<%
JSONArray person_odshows  = (JSONArray)request.getAttribute("pageResult");
Integer categorytabObject = (Integer)request.getAttribute("categorytab");
int categorytab = categorytabObject == null ? 0 : categorytabObject.intValue();
%>

                        <% if(JSONUtil.isEmpty(person_odshows)) { %>
                            暂无节目
                        <% } %>
                        
                        <% if(!JSONUtil.isEmpty(person_odshows)) { %>
                        <div class="showImgColl_W">
                            <div class="shows">
                                <% for(int i = 0; i < person_odshows.length() && i < 4; i++) { %>
                                    <% JSONObject odshow = person_odshows.optJSONObject(i); %>
                                    <% if(odshow == null){ continue; } %>
                                    <ul class="show">
                                        <li class="show_link"><a href="http://www.youku.com/show_page/id_z<%= odshow.optString("showid") %>.html" onclick="logSearchClick('youku_click','video','http://www.youku.com/show_page/id_z<%= odshow.optString("showid") %>.html','<%= WebUtils.jsEscape(request.getParameter("personname")) %>',<%=odshow.optString("showcateid")%>)" title="<%= WebUtils.htmlEscape(odshow.optString("showname")) %>" target="_blank" charset="801-105"></a></li>
                                        <li class="show_img"><img src="<%= odshow.optString("show_thumburl") %>" alt="<%= WebUtils.htmlEscape(odshow.optString("showname")) %>" title="<%= WebUtils.htmlEscape(odshow.optString("showname")) %>" /></li>
                                        <li class="show_title"><a href="http://www.youku.com/show_page/id_z<%= odshow.optString("showid") %>.html" onclick="logSearchClick('youku_click','video','http://www.youku.com/show_page/id_z<%= odshow.optString("showid") %>.html','<%= WebUtils.jsEscape(request.getParameter("personname")) %>',<%=odshow.optString("showcateid")%>)" title="<%= WebUtils.htmlEscape(odshow.optString("showname")) %>" target="_blank" charset="801-105"><%= WebUtils.htmlEscape(odshow.optString("showname")) %></a></li>
                                        
                                        <% String display_status = odshow.optString("display_status"); %>
                                        <% JSONArray streamtypes = odshow.optJSONArray("streamtypes"); %>
                                        <% boolean containHdStreamtype = JSONUtil.contain(streamtypes, "hd"); %>
                                        <% if(display_status.length() > 0 || containHdStreamtype) { %>
                                        <li class="show_update">
                                            <% if(display_status.length() > 0) { %>
                                            <span class="status"><%= WebUtils.htmlEscape(display_status) %></span>
                                            <% } %>
                                        
                                        
                                            <% if(containHdStreamtype) { %>
                                            <span class="ico__HD" title="高清"></span>
                                            <% } %>
                                            
                                            <span class="bg"></span>
                                        </li>
                                        <% } %>
                                        
                                        <li class="show_pub"><span class="label">上映:</span> <span class="num"><%= WebUtils.htmlEscape(odshow.optString("releaseyear")) %>-<%= WebUtils.htmlEscape(odshow.optString("releasemonth")) %></span></li>
                                    </ul>
                                <% } %>
                                <div class="clear"></div>
                            </div>
                        </div>
                        
                        <% if(person_odshows.length() > 4) { %>
                        <div class="expand" id="actorExpand_<%= categorytab %>">
                            <ul>
                                <% for(int i = 4; i < person_odshows.length() && i < 100; i++) { %>
                                    <% JSONObject odshow = person_odshows.optJSONObject(i); %>
                                    <% if(odshow == null){ continue; } %>
                                    <li><a href="http://www.youku.com/show_page/id_z<%= odshow.optString("showid") %>.html" onclick="logSearchClick('youku_click','video','http://www.youku.com/show_page/id_z<%= odshow.optString("showid") %>.html','<%= WebUtils.jsEscape(request.getParameter("personname")) %>',<%=odshow.optString("showcateid")%>)" title="<%= WebUtils.htmlEscape(odshow.optString("showname")) %>" target="_blank" charset="801-105"><%= WebUtils.htmlEscape(odshow.optString("showname"), 10, "...") %></a> <span class="num"><%= WebUtils.htmlEscape(odshow.optString("releaseyear")) %></span></li>
                                <% } %>
                            </ul>
                            <div class="clear"></div>
                        </div>
                        <% } %>
                        
                        <%--
                        {if count($pageResult->person_odshows)>4 && false}
                        <div class="handle"><a id="actorHandle_{$categorytab}" onclick="javascript:showExpand(document.getElementById('actorHandle_{$categorytab}'),
                                                                                            document.getElementById('actorExpand_{$categorytab}'), 
                                                                                            '收起', 
                                                                                            '展开');return false;" href="" charset="801-9-0">展开</a><span class="num">({php}echo count($this->_tpl_vars['pageResult']->person_odshows);{/php})</span></div>
                        {/if}
                        --%>
                        <% } %>
                        
