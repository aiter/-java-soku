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
String base_url             = "/search_playlist/";
int orderby=webParam.getOrderby();
//orderby_1_lengthtype_2_hd_1_cateid_97
String base_filter=base_url+"q_"+WebUtils.htmlEscape(webParam.getQ())+"_orderby_"+orderby;
String pre_cateid_str=webParameter.get("cateid");

String pre_cateid="";
%>

<!DOCTYPE HTML>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%= WebUtils.htmlEscape(webParam.getQ()) %> 优酷视频</title>
<meta name="title" content="<%= WebUtils.htmlEscape(webParam.getQ()) %> - <%= WebUtils.htmlEscape(webParam.getQ()) %>专辑搜索"> 
<meta name="keywords" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>, <%= WebUtils.htmlEscape(webParam.getQ()) %>专辑"> 
<meta name="description" content="<%= WebUtils.htmlEscape(webParam.getQ()) %>搜索 - 优酷网为你提供最为专业全面的<%= WebUtils.htmlEscape(webParam.getQ()) %>专辑搜索">
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
</div><div class="layout_21">
	<div class="maincol">
				<% String suggestion = result.optString("suggestion",""); %>
		<% if(suggestion.length()>0){ %>
			<div class="keylike">你是不是要找： &quot;<a href="<%= base_url %>q_<%= WebUtils.urlEncode(suggestion) %>"><%= WebUtils.htmlEscape(suggestion) %></a>&quot; </div>
		<%} %>
<%
Cookie cookies[]=request.getCookies(); 
Cookie sCookie=null;
String svalue="thumb"; 
String sname="youkuviewby"; 

if(cookies!=null){
for(int i=0;i<cookies.length;i++) 
{

	sCookie=cookies[i];

	if(sname.equals(sCookie.getName())){
		String tmpValue=StringUtils.trimToEmpty(sCookie.getValue());
		if("list".equals(tmpValue)) svalue="list";
		break;
	}
 
}} %>
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
			if(pre_cateid_str!=null) baseOrder=baseOrder+"_cateid_"+pre_cateid_str;
			baseOrder=baseOrder+"_orderby_"; %>
		<li class="<%=(orderby==1)?"current":"" %>"><a href="<%=baseOrder+"1"%>">相关程度</a></li>
		<li class="<%=(orderby==2)?"current":"" %>"><a href="<%=baseOrder+"2"%>">最多播放</a></li>
		<li class="<%=(orderby==3)?"current":"" %>"><a href="<%=baseOrder+"3"%>">最新更新</a></li>
		</ul>
	</div><!--orderby end-->	
</div>		<div class="filter filter_foryouku">
<div class="handle"><a href="#">&gt;&gt; 隐藏筛选</a></div>
<div style="display: block;" class="panel">
				<div class="item">
			<label>分类</label>
			<ul>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("0"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_0"%>">不限</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("123"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_123"%>">资讯</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("124"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_124"%>">原创</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("129"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_129"%>">电视剧</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("143"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_143"%>">娱乐</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("128"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_128"%>">电影</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("130"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_130"%>">体育</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("127"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_127"%>">音乐</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("131"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_131"%>">游戏</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("132"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_132"%>">动漫</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("139"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_139"%>">时尚</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("140"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_140"%>">母婴</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("136"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_136"%>">汽车</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("141"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_141"%>">旅游</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("137"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_137"%>">科技</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("142"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_142"%>">教育</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("135"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_135"%>">生活</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("126"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_126"%>">搞笑</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("102"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_134"%>">广告</a></li>
			<li class="<%=(pre_cateid_str!=null && pre_cateid_str.equals("106"))?"current":"" %>"><a href="<%=base_filter+pre_cateid+"_cateid_138"%>">其他</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
</div><!--panel end-->	
</div><!--filter end-->
<div class="result">
			<!--缩略图方式||列表方式-->
<% JSONObject items = JSONUtil.getProperty(result, JSONObject.class, "items"); %>
<div class="<%=svalue.equals("thumb")?"collgrid4w":"colllist1w"%>" id="vcoll">

<% if(!JSONUtil.isEmpty(items)) { %>
	<div class="items">
<% String[] names = JSONObject.getNames(items); %>
	<% for(int i = 0; names != null && i < names.length; i++) { %>
		<% JSONObject folder = items.optJSONObject(names[i]); %>
    	<% if(JSONUtil.isEmpty(folder)) { continue; } %>
        <% String littleTitle=WebUtils.htmlEscape(folder.optString("title"), 16, "...");%>         
		<ul class="p">
		<li class="p_link"><a href="http://www.youku.com/playlist_show/id_<%= folder.optInt("pk_folder") %>.html" 
			charset="803-<%= webParam.getOrderby() %>-<%= i+1 %>-2" target="_blank" title="<%=littleTitle%>"></a></li>
		<li class="p_thumb"><img src="http://g<%= (i%4)+1 %>.ykimg.com/<%= folder.optString("logo") %>" alt="<%=littleTitle %>"></li>
		<li class="p_time"><span class="num"><%= MiscUtils.timeLength(folder.optString("total_seconds")) %></span><span class="bg"></span></li>
		<li class="p_title"><a href="http://www.youku.com/playlist_show/id_<%= folder.optInt("pk_folder") %>.html" target="_blank" title="<%=littleTitle%>"><%=littleTitle%></a></li>
		<li class="p_user"><label>会员:</label>
			<a target="_blank" charset="803-<%= webParam.getOrderby() %>-<%= i+1 %>-1" href="http://u.youku.com/user_show/id_<%= MiscUtils.userid_encode(folder.optString("owner")) %>.html"><%= WebUtils.htmlEscape(folder.optString("owner_name")) %></a>
		</li>
		<li class="p_part"><label>视频:</label>
			<span class="num"><%= WebUtils.formatNumber(folder.optInt("video_count"), "###,###") %></span></li>
		<li class="p_stat"><label>播放:</label><span class="num"><%= WebUtils.formatNumber(folder.optInt("total_pv"), "###,###") %></span></li>
		
		<li class="p_videos">
		<% JSONObject videos = JSONUtil.getProperty(folder, JSONObject.class, "videos"); %>
        <% if(!JSONUtil.isEmpty(videos)) { %>
			<ul>
			<% String[] videoNames = JSONObject.getNames(videos); %>
                                                <% for(int j = 0; videoNames != null && j < videoNames.length &&j<3; j++) { %>
                                                    <% JSONObject video = videos.optJSONObject(videoNames[j]); %>
                                                    <% if(JSONUtil.isEmpty(video)) { continue; } %>
													<li>•
                                                        <a href="http://v.youku.com/v_playlist/f<%= folder.optInt("pk_folder") %>o1p<%= (video.optInt("order_no")-1) %>.html" charset="803-<%= webParam.getOrderby() %>-<%= i+1 %>" target="_blank" title="<%= WebUtils.htmlEscape(video.optString("title")) %>"><%= WebUtils.htmlEscape(video.optString("title"), 30, "...") %></a>
                                                        <span class="num"><%= MiscUtils.timeLength(video.optString("seconds")) %></span>
                                                    </li>
                                                    <%} %>
			</ul>
		<%} %>
		</li>
		</ul>
	
	<% if((i+1)%4 == 0) { %>
               <div class="clear"></div>
    <% } %>
                                
    <% } %>
						
						<% if(items.length()%4 != 0) { %>
                            <div class="clear"></div>
                            <% } %>
					
		
	</div><!--items end-->
<% }else{ %>
	没有专辑结果
<%} %>
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