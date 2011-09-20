<%@page contentType="text/html; charset=utf-8"%>
<%@page import="java.util.*,
				javax.servlet.http.Cookie,
				org.apache.commons.lang.StringUtils,
				com.youku.soku.sort.Parameter,
				com.youku.soku.web.SearchResult,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				com.youku.soku.util.MyUtil,
				org.json.*,
				com.youku.soku.web.SearchResult.Content,
				com.youku.soku.web.db.CateManager,
				com.youku.search.util.DataFormat,
				com.youku.soku.index.manager.db.SiteManager,
				com.youku.search.sort.servlet.search_page.util.MiscUtils,
				com.youku.search.sort.servlet.util.WebUtils
				"%>
<%
JSONObject content = (JSONObject)request.getAttribute("content");
Parameter param = (Parameter)request.getAttribute("param");
int pagesize = Constant.Web.SEARCH_PAGESIZE;
int total = 0;
if(content.has("total")) {
	total = content.optInt("total");
}
int curpage = param.page;
String keyword = param.keyword;

keyword=WebUtil.formatHtml(keyword);
String encodeKeyword = MyUtil.urlEncode(keyword);
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title> 搜库 视频搜索</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>

</head>

<body>
<div class="window">
<div class="screen">

<%@ include file="inc/header.jsp" %>
<!--header end-->
<div class="body">

<div class="typechk">
<div class="main">
		<ul>
		<%String pidStr=request.getParameter("pid");
			  String personName=request.getParameter("personName");
		if(pidStr!=null){
			out.print("<li><a href='/v?keyword="+encodeKeyword+"&pid="+pidStr+"&redirect=0'>视频</a></li>");
			out.print("<li class='current'><span>专辑</span></li>");
			//out.print("<li><a href='/k?keyword="+keyword+"&pid="+pidStr+"'>知识</a></li>");
			out.print("<li style='margin-left:10px;'><a href='/detail/show/"+pidStr+"?keyword="+encodeKeyword+"'>详情</a></li>");		
			
		}else if(personName!=null){
			out.print("<li><a href='/v?keyword="+encodeKeyword+"&personName="+MyUtil.urlEncode(personName)+"&redirect=0'>视频</a></li>");
			out.print("<li class='current'><span>专辑</span></li>");
			//out.print("<li><a href='/k?keyword="+keyword+"&personName="+personName+"'>知识</a></li>");
			out.print("<li style='margin-left:10px;'><a href='/detail/person/"+MyUtil.urlEncode(personName)+"?keyword="+encodeKeyword+"'>详情</a></li>");
		}
		else{
			out.print("<li><a href='/v?keyword="+encodeKeyword+"'>视频</a></li>");
			out.print("<li class='current'><span>专辑</span></li>");
			//out.print("<li><a href='/k?keyword="+keyword+"'>知识</a></li>");
			
		}
		%>
	   </ul>
	</div>
</div><div class="layout_16">	
	<div class="sidecol">
				<div class="filter">

<div class="panel">
						<div class="item">
			<label>分类:</label>
			<ul>
			
			<li <%=param.cateid == 0 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>">不限</a></li>
			<li <%=param.cateid == 128 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=128">电影</a></li>
			<li <%=param.cateid == 129 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=129">电视剧</a></li>

			<li <%=param.cateid == 123 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=123">资讯</a></li>
			<li <%=param.cateid == 124 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=124">原创</a></li>
			<li <%=param.cateid == 130 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=130">体育</a></li>
			<li <%=param.cateid == 127 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=127">音乐</a></li>
			<li <%=param.cateid == 131 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=131">游戏</a></li>
			<li <%=param.cateid == 132 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=132">动漫</a></li>

			<li <%=param.cateid == 126 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=126">搞笑</a></li>
			<li <%=param.cateid == 136 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=136">汽车</a></li>
			<li <%=param.cateid == 137 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=137">科技</a></li>
			<li <%=param.cateid == 139 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=139">时尚</a></li>
			<li <%=param.cateid == 135 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=135">生活</a></li>

			<li <%=param.cateid == 140 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=140">母婴</a></li>
			<li <%=param.cateid == 141 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=141">旅游</a></li>
			<li <%=param.cateid == 142 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=142">教育</a></li>
			<li <%=param.cateid == 143 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=143">娱乐</a></li>

			<li <%=param.cateid == 134 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=134">广告</a></li>
			<li <%=param.cateid == 138 ? "class=current" : ""%>><a href="/a?keyword=<%=encodeKeyword %>&cateid=138">其他</a></li>
			</ul>
			<div class="clear"></div>
		</div>
				<div class="clear"></div>
</div><!--panel end-->	
</div><!--filter end-->	</div><!--sidecol end-->

	<div class="maincol">
				<!--direct end--><div class="viewchange">
<%
Cookie cookies[]=request.getCookies(); 

Cookie sCookie=null;

String svalue="thumb"; 

String sname="viewby"; 

for(int i=0;i<cookies.length;i++) 
{

sCookie=cookies[i];

if(sname.equals(sCookie.getName())){
	String tmpValue=StringUtils.trimToEmpty(sCookie.getValue());
	if("list".equals(tmpValue)) svalue="list";
	break;
}
 
}
%>
<% if(total >0) { %>
	<div class="viewby">
		<ul>
			<li class="bythumb <%=svalue.equals("thumb")?"current":"" %>" title="网格" viewby="thumb"><em>网格</em></li>
			<li class="bylist <%=svalue.equals("list")?"current":"" %>" title="列表" viewby="list"><em>列表</em></li>
		</ul>
	</div>
	<%} %>
</div><div class="result">
<noscript id="noscript"><div style="text-align:center;color:red;">如无法看到图片，请检查是否开启了Java Script。<br /><a href="http://www.youku.com/youku/help/question_play.shtml#java" target="_blank">如何开启？</a></div></noscript>
<% if(total >0) { %>
<!--缩略图方式||列表方式-->
<div class="<%=svalue.equals("thumb")?"collgrid4w":"colllist1w"%>" id="pcoll">
<div class="items">
	
	
	<%
			JSONObject eduObj = content.optJSONObject("items");
			if(eduObj != null) {
				Iterator it = eduObj.keys();
				int i = 0;
				while (it.hasNext()) {
					i++;
					String key = (String) it.next();
					JSONObject item = eduObj.optJSONObject(key);
					%>
						<ul class="p">
						
							<li class="p_link">
								<a href="http://www.youku.com/playlist_show/id_<%=item.optString("pk_folder")%>.html" target="_blank" title="<%=WebUtils.htmlEscape(item.optString("title"), 30, "...")%>"></a>
							</li>
							<li class="p_thumb"><img src="<%=WebUtil.formatLogo(item.optString("logo")) %>" alt="<%=item.optString("title")%>" /></li>
							<li class="p_title"><a title="<%=item.optString("title")%>" target="_blank" href="http://www.youku.com/playlist_show/id_<%=item.optString("pk_folder") %>.html">
							<%=WebUtils.htmlEscape(item.optString("title"), 30, "...")%></a></li>
							<li class="p_user"><label>会员:</label><a href="http://u.youku.com/user_show/id_<%=MiscUtils.userid_encode(item.optString("owner")) %>.html" target="_blank" 
								title="<%=WebUtils.htmlEscape(item.optString("owner_name"))%>"><%=WebUtils.htmlEscape(item.optString("owner_name"))%></a></li>
							<li class="p_part"><label>视频:</label><span class="num"><%=item.optInt("video_count") %></span></li>

							<li class="p_stat"><label>播放:</label><span class="num"><%=item.optInt("total_pv") %></span></li>
					
						</ul>
					<%
					if(i % 4 == 0) {
						%>
							<div class="clear"></div>
						<%
					}
					
				}
			}
			
	%>
						

		</div><!--items end-->
</div><!--coll end-->
<div class="pager">	
	<%=PageUtil.getNewContent(WebUtil.getPagePrefixString("/a", param,request),total,curpage,pagesize)%>
</div>
<% } else {%>
<div class="null">
	<div class="sorry">
		抱歉，没有找到<span class="key"><%= keyword %></span>的相关专辑。	</div>
	<h3>建议您：</h3>
	<ul>
		<li>• 检查输入的关键词是否有误。</li>
		<li>• 缩短关键词。</li>
		<li>• 使用相近、相同或其他语义的关键词。</li>
		<li>• 放宽筛选条件。</li>
	</ul>
</div>

<%} %>

</div><!--result end-->
<div class="relkeys">
	
		<%
			JSONObject relevantObj = content.optJSONObject("relevantwords");
			if(relevantObj != null) {
				JSONObject relevantItems = relevantObj.optJSONObject("items");
				if(relevantItems != null) {
					%>
					<label>相关搜索:</label>
						<ul>
					<%
					Iterator it = relevantItems.keys();
					while(it.hasNext()) {
						String key = (String) it.next();
						JSONObject item = relevantItems.optJSONObject(key);
						%>
							<li><a href="/a?keyword=<%=item.optString("query") %>"><%=WebUtils.htmlEscape(item.optString("query"), 30, "...") %></a></li>
						<%
					}
					
					%>
					</ul>
					<%
				}
			}
		%>
		
	
	<div class="clear"></div>
</div>	</div><!--maincol end-->
	<div class="clear"></div>
</div><!--layout end-->
</div><!--body end-->
<script type="text/javascript" src="/js/sotool.js"></script>
<script type="text/javascript">
		$(function(){
			if($().lazyload) {
				$("img").lazyload({placeholder : "http://static.youku.com/v1.0.0661/index/img/sprite.gif"});
			}
		});
</script>
<%@ include file="inc/footer.jsp" %>

</div><!--widnow end-->

</body>
</html>