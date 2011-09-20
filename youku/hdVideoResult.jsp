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
int blursearch             = (Integer)request.getAttribute("blursearch");
%>

<%
JSONArray cms              = (JSONArray)request.getAttribute("cms");
JSONArray recommendShows   = (JSONArray)request.getAttribute("recommendShows");
JSONObject bar             = (JSONObject)request.getAttribute("bar");
JSONObject postsearchJson= (JSONObject)request.getAttribute("postsearch");
%>

<%
String base_url             = "/search_hdvideo/";
int orderby=webParam.getOrderby();
//orderby_1_lengthtype_2_hd_1_cateid_97
String base_filter=base_url+"q_"+WebUtils.htmlEscape(webParam.getQ())+"_orderby_"+orderby;
String pre_lengthtype_str=webParameter.get("lengthtype");
String pre_hd_str=webParameter.get("hd");
String pre_cateid_str=webParameter.get("cateid");
String pre_limitdate_str=webParam.getLimitdate();

String pre_lengthtype="",pre_hd="",pre_cateid="",pre_limitdate="";

if(pre_lengthtype_str!=null){
  pre_hd=pre_hd+"_lengthtype_"+pre_lengthtype_str;
  pre_cateid=pre_cateid+"_lengthtype_"+pre_lengthtype_str;
  pre_limitdate=pre_limitdate+"_lengthtype_"+pre_lengthtype_str;
}

if(pre_hd_str!=null){
	pre_lengthtype=pre_lengthtype+"_hd_"+pre_hd_str;
	pre_cateid=pre_cateid+"_hd_"+pre_hd_str;
	pre_limitdate=pre_limitdate+"_hd_"+pre_hd_str;
}

if(pre_cateid_str!=null){
	pre_lengthtype=pre_lengthtype+"_cateid_"+pre_cateid_str;
	pre_hd=pre_hd+"_cateid_"+pre_cateid_str;
	pre_limitdate=pre_limitdate+"_cateid_"+pre_cateid_str;
}

if(pre_limitdate_str!=null){
	pre_lengthtype=pre_lengthtype+"_limitdate_"+pre_limitdate_str;
	pre_hd=pre_hd+"_limitdate_"+pre_limitdate_str;
	pre_cateid=pre_cateid+"_limitdate_"+pre_limitdate_str;
}
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
	<div id='ab_596'></div>	
	<div class="maincol">
		<% String suggestion = result.optString("suggestion",""); %>
		<% if(suggestion.length()>0){ %>
			<div class="keylike">你是不是要找： &quot;<a href="<%= base_url %>q_<%= WebUtils.urlEncode(suggestion) %>"><%= WebUtils.htmlEscape(suggestion) %></a>&quot; </div>
		<%} %>
<% if(blursearch != 1) { %>		
<div class="rsstat">
站内搜索<span class="key"><%= encodeKeyword%></span>共找到 <%= result.optInt("total") %> 个视频;
<%JSONObject outSearchJson=result.optJSONObject("outSearchResult");
	   if(outSearchJson!=null){
		   JSONArray extArr=outSearchJson.optJSONArray("ext");
		   if(extArr!=null && extArr.length()>0){
			   out.print(" 全网搜索找到相关&nbsp;");
			   for(int arrIndex=0;arrIndex<extArr.length();arrIndex++){
				   JSONObject tmpObj=extArr.getJSONObject(arrIndex);%>
				   <a href="<%=tmpObj.optString("url")%>" target="_blank"><%= tmpObj.optString("cat")%><%= tmpObj.optString("count")%>部</a>
				   <%if(arrIndex<extArr.length()-1){%>
				   ，
					<%}else{%>
					。&nbsp;   
				   <%}
			   }
		   }
	   }
%>
</div>
<%} %> 
<%@ include file="inc/commendList.jsp" %>

<%
Cookie cookies[]=request.getCookies(); 
Cookie sCookie=null;
String svalue="thumb"; 
String sname="youkuviewby";

String filtervalue="hide";
String filtername="filterforyouku";

if(cookies!=null){
for(int i=0;i<cookies.length;i++) 
{

	sCookie=cookies[i];

	if(sname.equals(sCookie.getName())){
		String tmpValue=StringUtils.trimToEmpty(sCookie.getValue());
		if("list".equals(tmpValue)) svalue="list";
	}
	
	if(filtername.equals(sCookie.getName())){
		String tmpValue=StringUtils.trimToEmpty(sCookie.getValue());
		if("show".equals(tmpValue)) filtervalue="show";
	}
 
}} %>
<% if(blursearch != 1) { %>			
<div class="viewchange">
	<div class="viewby">
		<ul>
			<li class="bythumb <%=svalue.equals("thumb")?"current":"" %>" title="网格" viewby="thumb"><em>网格</em></li>
			<li class="bylist <%=svalue.equals("list")?"current":"" %>" title="列表" viewby="list"><em>列表</em></li>
		</ul>
	</div>
	<div class="orderby">
		<label>排序:</label>
		<ul>
		<%String baseOrder=base_url+"q_"+WebUtils.htmlEscape(webParam.getQ());
			if(pre_lengthtype_str!=null) baseOrder=baseOrder+"_lengthtype_"+pre_lengthtype_str;
			if(pre_limitdate_str!=null) baseOrder=baseOrder+"_limitdate_"+pre_limitdate_str;
			if(pre_hd_str!=null) baseOrder=baseOrder+"_hd_"+pre_hd_str;
			if(pre_cateid_str!=null) baseOrder=baseOrder+"_cateid_"+pre_cateid_str;
			baseOrder=baseOrder+"_orderby_"; %>
		<li class="<%=(orderby==1)?"current":"" %>"><a href="<%=baseOrder+"1"%>">相关程度</a></li>
		<li class="<%=(orderby==3)?"current":"" %>"><a href="<%=baseOrder+"3"%>">最多播放</a></li>
		<li class="<%=(orderby==2)?"current":"" %>"><a href="<%=baseOrder+"2"%>">最新更新</a></li>
		</ul>
	</div><!--orderby end-->	
</div>
<%}%>
<div class="filter filter_foryouku">
<% if(blursearch != 1) {%>
<div class="handle"><a href="#" status="<%=filtervalue%>"><%=(filtervalue.equals("show"))?"&gt;&gt; 隐藏筛选":"&lt;&lt; 筛选视频" %></a></div>
<div class="panel" style="display:<%=(filtervalue.equals("show"))?"block;":"none;"%>">
				<div class="item">		
			<label>时长</label>
			<ul class="col1">
			<li class="<%=(pre_lengthtype_str!=null && pre_lengthtype_str.equals("0"))?"current":"" %>"><a href="<%=base_filter+pre_lengthtype+"_lengthtype_0"%>">不限</a></li>
			<li class="<%=(pre_lengthtype_str!=null && pre_lengthtype_str.equals("1"))?"current":"" %>"><a href="<%=base_filter+pre_lengthtype+"_lengthtype_1"%>">0-10分钟</a></li>
			<li class="<%=(pre_lengthtype_str!=null && pre_lengthtype_str.equals("2"))?"current":"" %>"><a href="<%=base_filter+pre_lengthtype+"_lengthtype_2"%>">10-30分钟</a></li>
			<li class="<%=(pre_lengthtype_str!=null && pre_lengthtype_str.equals("3"))?"current":"" %>"><a href="<%=base_filter+pre_lengthtype+"_lengthtype_3"%>">30-60分钟</a></li>
			<li class="<%=(pre_lengthtype_str!=null && pre_lengthtype_str.equals("4"))?"current":"" %>"><a href="<%=base_filter+pre_lengthtype+"_lengthtype_4"%>">60分钟以上</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		<div class="item">		
			<label>发布时间</label>
			<ul>
			<li class="<%=(pre_limitdate_str!=null && pre_limitdate_str.equals("0"))?"current":"" %>"><a href="<%=base_filter+pre_limitdate+"_limitdate_0"%>">不限</a></li>
			<li class="<%=(pre_limitdate_str!=null && pre_limitdate_str.equals("1"))?"current":"" %>"><a href="<%=base_filter+pre_limitdate+"_limitdate_1"%>">一天</a></li>
			<li class="<%=(pre_limitdate_str!=null && pre_limitdate_str.equals("7"))?"current":"" %>"><a href="<%=base_filter+pre_limitdate+"_limitdate_7"%>">一周</a></li>
			<li class="<%=(pre_limitdate_str!=null && pre_limitdate_str.equals("31"))?"current":"" %>"><a href="<%=base_filter+pre_limitdate+"_limitdate_31"%>">一月</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		<div class="item">
			<label>画质</label>
			<ul>
			<li class="<%=(pre_hd_str!=null && pre_hd_str.equals("0"))?"current":"" %>"><a href="<%=base_filter+pre_hd+"_hd_0"%>">不限</a></li>
			<li class="<%=(pre_hd_str!=null && pre_hd_str.equals("1"))?"current":"" %>"><a href="<%=base_filter+pre_hd+"_hd_1"%>">高清</a></li>
			<li class="<%=(pre_hd_str!=null && pre_hd_str.equals("6"))?"current":"" %>"><a href="<%=base_filter+pre_hd+"_hd_6"%>">超清</a></li>
			</ul>
			<div class="clear"></div>
		</div>
				<div class="item">
			<label>分类</label>
			<ul>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("0"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_0"%>">不限</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("96"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_96"%>">电影</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("97"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_97"%>">电视剧</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("91"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_91"%>">资讯</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("92"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_92"%>">原创</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("98"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_98"%>">体育</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("95"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_95"%>">音乐</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("99"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_99"%>">游戏</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("100"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_100"%>">动漫</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("94"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_94"%>">搞笑</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("104"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_104"%>">汽车</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("105"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_105"%>">科技</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("89"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_89"%>">时尚</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("103"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_103"%>">生活</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("90"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_90"%>">母婴</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("88"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_88"%>">旅游</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("87"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_87"%>">教育</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("86"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_86"%>">娱乐</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("85"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_85"%>">综艺</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("102"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_102"%>">广告</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("106"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_106"%>">其他</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
</div><!--panel end-->
<%}%>	
</div><!--filter end-->

<div class="result">

<% if(blursearch == 1) { %>
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
<h3 class="hotTitle">以下为模糊搜索结果：</h3>
<% } %>
			<!--缩略图方式||列表方式-->
<!--2,5,7 演示站外视频形式-->
<div class="<%=svalue.equals("thumb")?"collgrid4w":"colllist1w"%>" id="vcoll">
<div class="items">

	<% if(!JSONUtil.isEmpty(JSONUtil.getProperty(result, JSONObject.class, "items"))) { %>
                            <% JSONObject items = JSONUtil.getProperty(result, JSONObject.class, "items"); %>
                            <% String[] names = JSONObject.getNames(items); %>
                            <% for(int i = 0; names != null && i < names.length; i++) { %>
                                <% JSONObject video = items.optJSONObject(names[i]); %>
                                <% if(JSONUtil.isEmpty(video)) { continue; } %>
                                <% String videoUrl = "http://v.youku.com/v_show/id_" + video.optString("encodeVid") + ".html"; %>
                                <% String videoCharset = (webParam.getPage()<=3 ? "81" + (webParam.getPage()+3) : "801") + "-" + webParam.getOrderby() + "-" + (i+1); %>
 
		<ul class="v">
		<li class="v_link">
			<a title="<%= WebUtils.htmlEscape(video.optString("title")) %>" target="_blank" href="<%=videoUrl %>"></a></li>
		<li class="v_thumb"><img alt="<%= WebUtils.htmlEscape(video.optString("title")) %>" src="http://g<%= (i%4)+1 %>.ykimg.com/<%= video.optString("logo") %>"></li>
		<li class="v_ishd"><% if (MiscUtils.shd(video.optString("ftype"))) { %><span class="ico__SD" title="超清"></span><% }
							  else if(MiscUtils.hd(video.optString("ftype"))){%><span class="ico__HD" title="高清"></span><%} %></li>	
		<li class="v_time"><span class="num"><%= MiscUtils.timeLength(video.optString("seconds")) %></span><span class="bg"></span></li>
		<li class="v_title"><a title="<%= WebUtils.htmlEscape(video.optString("title")) %>" target="_blank" href="<%=videoUrl %>">
		<%String littleDesc="";    
		littleDesc=video.optString("memo");if (littleDesc.length() > 70 ) { littleDesc = littleDesc.substring(0, 70);}
		%>
			<% if (video.optString("title_hl").length() == 0) { %>
                                        <% String title = video.optString("title"); %>
                                        <% if (title.length() > 16 ) { title = title.substring(0, 16);} %>
                                        <%= WebUtils.htmlEscape(title) %>
                                    <% } else { %>
                                        <%=video.optString("title_hl")%>
                                    <% } %></a></li>
		<li class="v_desc"><%=littleDesc %>
		</li>
		<li class="v_user"><label>会员:</label>
		<% if (video.optString("username_hl").length() ==0) {
                                            String username = video.optString("owner_username");
                                            String userCharset = null;
                                            if (webParam.getPage() <= 3){
                                                userCharset = "81" + (webParam.getPage()) + "-" + webParam.getOrderby() + "-" + (i+1) + "-1";
                                            }else{
                                                userCharset = "801-" + webParam.getOrderby() + "-" + (i+1) + "-1";
                                            }
                                        %>
                                            <a target="_blank" charset="<%= userCharset %>" href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(video.optString("owner")) %>.html"><%= WebUtils.htmlEscape(username) %></a>
                                        <% } else {
                                                String username = video.optString("username_hl");
                                                String userCharset = null;
                                                if (webParam.getPage() <= 3){
                                                    userCharset = "81" + (webParam.getPage()) + "-" + webParam.getOrderby() + "-" + (i+1) + "-1";
                                                }else{
                                                    userCharset = "801-" + webParam.getOrderby() + "-" + (i+1) + "-1";
                                                }
                                        %>
                                            <a target="_blank" charset="<%= userCharset %>" href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(video.optString("owner")) %>.html"><%= username %></a>
                                        <% } %>
		</li>
		<li class="v_pub"><label>发布:</label><span><%= MiscUtils.createTime(video.optString("createtime")) %></span></li>
		<li class="v_stat"><label>播放:</label><span class="num"><%= WebUtils.formatNumber(video.optInt("total_pv"), "###,###") %></span></li>
	</ul>
	<% if ((i+1)%4 == 0) { %>
                                    <div class="clear"></div>
                                    <% } %>
                                    
                                <% } %>

                                <% if (names!=null && names.length%4 != 0) { %>
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


<div id='ab_600'></div>
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
                            <li><a href="<%=base_url%>q_<%=WebUtils.urlEncode(word)%>" target="_blank" title="<%=word%>" >
                            <%=word.length()>8?word.substring(0,8):word%></a></li>
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
         
         <!-- right ad start -->
				    <div id='ab_597'></div>
				    <div id='ab_598'></div>
				    <div id='ab_599'></div>
				    <div id='ab_601'></div>
			    <!-- right ad end -->
				
	</div><!--sidecol end-->
	<div class="clear"></div>
</div><!--layout end-->
</div><!--body end-->

<%@ include file="inc/newfoot.jsp" %>
</div><!--screen end-->
</div><!--widnow end-->
<div class="feedwin">
	<div class="handle" title="用户反馈:您喜欢新版搜库吗?"></div>
	<form id="feedbackform" method="post" action="/v">
	<input type="hidden" value="<%= WebUtils.htmlEscape(webParam.getQ())%>" id="keyword" name="keyword">
	<input type="hidden" value="1" id="source" name="source">
	<input type="hidden" value="" id="state" name="state">
	<div style="display: none;" class="feed">
		<div class="close" title="关闭"></div>
		<div class="step">
			<h3 class="title">您喜欢新版搜库吗？</h3>
			<div class="like"><div class="ico"></div><label>喜欢</label></div>
			<div class="unlike"><div class="ico"></div><label>不喜欢</label></div>
			<div class="clear"></div>
			<div class="input">
				<textarea id="message" name="message">倾听您的建议(100字以内)</textarea>
				<button type="button">提交</button>
			</div>
		</div>
		<div style="display: none;" class="step step3">
			<div class="thank">感谢您的参与。</div>
		</div>
	</div>
	</form>
</div>
<script type="text/javascript">document.getElementById("headq").focus(); Ab(false, true);</script>
<script type="text/javascript" src="<%=STATIC_SERVER%>/js/youkutool.js"></script>
<script type="text/javascript" src="http://urchin.lstat.youku.com/index/js/urchin.js"></script>
<script type="text/javascript">urchinTracker();</script>
<script type="text/javascript" src="http://html.atm.youku.com/html?p=596,597,598,599,600,601&k=s"></script>
</body>
</html>