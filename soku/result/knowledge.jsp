<%@page contentType="text/html; charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.sort.Parameter,
				com.youku.soku.web.SearchResult,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				com.youku.soku.util.MyUtil,
				org.json.*,
				com.youku.soku.web.SearchResult.Content,
				com.youku.soku.web.db.CateManager,
				com.youku.search.util.DataFormat,
				com.youku.soku.index.manager.db.SiteManager
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

String encodeKeyword = MyUtil.urlEncode(param.keyword);
String url="/k?keyword="+encodeKeyword;
int pre_time_ength=0,pre_limit_date=0,pre_hd=0,pre_site=0;
if(param.time_length>0) pre_time_ength=param.time_length;
if(param.limit_date>0) pre_limit_date=param.limit_date;
if(param.hd>0) pre_hd=param.hd;
if(param.site>0) pre_site=param.site;
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>soku</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/sotool.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/soku.js"></script>
</head>

<body>
<div class="window">
<div class="screen">

<%@ include file="inc/header.jsp" %>
<div class="body">

<div class="typechk">
<div class="main">
		<ul>
		<%String pidStr=request.getParameter("pid");
			  String personName=request.getParameter("personName");
		if(pidStr!=null){
			out.print("<li><a href='/v?keyword="+keyword+"&pid="+pidStr+"&redirect=0'>视频</a></li>");
			out.print("<li><a href='/a?keyword="+keyword+"&pid="+pidStr+"'>专辑</a></li>");
			out.print("<li class='current'><span>知识</span></li>");
			out.print("<li style='margin-left:10px;'><a href='/detail/show/"+pidStr+"?keyword="+MyUtil.urlEncode(keyword)+"'>详情</a></li>");		
			
		}else if(personName!=null){
			out.print("<li><a href='/v?keyword="+keyword+"&personName="+personName+"&redirect=0'>视频</a></li>");
			out.print("<li><a href='/a?keyword="+keyword+"&personName="+personName+"'>专辑</a></li>");
			out.print("<li class='current'><span>知识</span></li>");
			out.print("<li style='margin-left:10px;'><a href='/detail/person/"+personName+"?keyword="+MyUtil.urlEncode(keyword)+"'>详情</a></li>");
		}
		else{
			out.print("<li><a href='/v?keyword="+keyword+"'>视频</a></li>");
			out.print("<li><a href='/a?keyword="+keyword+"'>专辑</a></li>");
			out.print("<li class='current'><span>知识</span></li>");
		}
		%>
	   
	   </ul>
	</div>
</div><div class="layout_16">	
	<div class="sidecol">
				<div class="filter">

<div class="panel">
						<div class="item">
                <label>时长:</label>
                     <ul class="col1">
                <li class="<%=pre_time_ength==0?"current":"" %>"><a href="<%=url+"&time_length=0&limit_date="+pre_limit_date+"&hd="+pre_hd%>" target="_self">不限</a></li>
                        <li class="<%=pre_time_ength==1?"current":"" %>"><a href="<%=url+"&time_length=1&limit_date="+pre_limit_date+"&hd="+pre_hd%>">0-10分钟</a></li>
                        <li class="<%=pre_time_ength==2?"current":"" %>"><a href="<%=url+"&time_length=2&limit_date="+pre_limit_date+"&hd="+pre_hd%>">10-30分钟</a></li>
                        <li class="<%=pre_time_ength==3?"current":"" %>"><a href="<%=url+"&time_length=3&limit_date="+pre_limit_date+"&hd="+pre_hd%>">30-60分钟</a></li>
                        <li class="<%=pre_time_ength==4?"current":"" %>"><a href="<%=url+"&time_length=4&limit_date="+pre_limit_date+"&hd="+pre_hd%>">60分钟以上</a></li>
                        </ul>
                        <div class="clear"></div>
                </div>
                <div class="item">
                        <label>发布时间:</label>
                        <ul>
                        <li class="<%=pre_limit_date==0?"current":"" %>"><a href="<%=url+"&time_length="+pre_time_ength+"&limit_date=0&hd="+pre_hd%>">不限</a></li>    
                        <li class="<%=pre_limit_date==1?"current":"" %>"><a href="<%=url+"&time_length="+pre_time_ength+"&limit_date=1&hd="+pre_hd%>">一天</a></li>    
                        <li class="<%=pre_limit_date==7?"current":"" %>"><a href="<%=url+"&time_length="+pre_time_ength+"&limit_date=7&hd="+pre_hd%>">一周</a></li>    
                        <li class="<%=pre_limit_date==30?"current":"" %>"><a href="<%=url+"&time_length="+pre_time_ength+"&limit_date=30&hd="+pre_hd%>">一月</a></li>  
                        </ul>
                        <div class="clear"></div>
                </div>
                <div class="item">
                        <label>画质:</label>
                        <ul>
                        <li class="<%=pre_hd==0?"current":"" %>"><a href="<%=url+"&time_length="+pre_time_ength+"&limit_date="+pre_limit_date+"&hd=0&site="+pre_site%>">不限</a></li>
                        <li class="<%=pre_hd==1?"current":"" %>"><a href="<%=url+"&time_length="+pre_time_ength+"&limit_date="+pre_limit_date+"&hd=1&site="+pre_site%>">高清</a></li>
                        </ul>
                        <div class="clear"></div>
                </div>
		
				

</div><!--panel end-->	
</div><!--filter end-->	</div><!--sidecol end-->
	<div class="maincol">
		<div class="direct">
				<% if(total >0) { %>
			<div class="item">
	<div class="knowledge">
		<div class="caption">
			<div class="label"><h3>相关分类</h3></div>
			<div class="more"><a href="#">查看全部分类&gt;&gt;</a></div>

		</div>
		<ul class="keys">
				<%
			JSONArray relatedColumn = content.optJSONArray("r");
			for(int i = 0; i < relatedColumn.length(); i++) {
				%>
					<li><em>•</em><a href="/k?keyword=<%=relatedColumn.optString(i) %>"><%=relatedColumn.optString(i) %></a></li>
				
				<%
			}
		%>
		</ul>
		<div class="clear"></div>
	</div>
</div>
<%} %>
	</div><!--direct end-->
	
	<div class="viewchange">
	<% if(total >0) { %>
	<div class="viewby">
		<ul>
			<li class="bythumb current" title="网格" viewby="thumb"><em>网格</em></li>
			<li class="bylist" title="列表" viewby="list"><em>列表</em></li>
		</ul>
	</div>
	<%} %>	
</div>

<div class="result">
<% if(total >0) { %>

<!--缩略图方式||列表方式-->
<!--2,5,7 演示站外视频形式-->
<div class="collgrid4w" id="vcoll">
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
					JSONArray pics = item.optJSONArray("pics");
					%>
						<ul class="v">
							<%if(pics != null) { %>
									<li class="v_part">
									<div class="handle">
										<div class="ic"></div>
										<div class="bg"></div>
									</div>
									<div class="pop">
										<div class="content">
											<div class="caption">
																分段观看
											</div>
											<div class="partcoll">
											<div class="parts">
												
													<%for(int j = 0; j < pics.length(); j++) { 
														JSONObject pic = pics.optJSONObject(j);
													%>
													<ul class="part">
													<li class="part_link"><a target="_blank" href="<%=item.optString("url")%>?firsttime=<%=pic.optDouble("t")%>"></a></li>
													<li class="part_thumb"><img _src="<%=WebUtil.formatLogo(pic.optString("p")) %>"></li>
													<li class="part_time"><div class="time"><%=WebUtil.getSecondAsString((float)pic.optDouble("t"))%></div><div class="bg"></div></li>
													</ul>
													<%} %>
												
																	
											</div>
											</div>
										</div>
										<div class="shadow"></div>
									</div>
								</li>
							<%} %>
							<li class="v_link">
								<a href="<%=item.optString("url")%>" target="_blank" title="<%=item.optString("title")%>"></a>
							</li>
							<li class="v_thumb"><img src="<%=WebUtil.formatLogo(item.optString("logo")) %>" alt="<%=item.optString("title")%>" /></li>
							<li class="v_time"><span class="num"><%=WebUtil.getSecondAsString((float)item.getDouble("seconds"))%></span><span class="bg"></span></li>
							<li class="v_title"><a href="<%=item.optString("url")%>" target="_blank" title="<%=item.optString("title")%>"><%="".equals(item.optString("title_hl"))? item.optString("title") : item.optString("title_hl")  %></a></li>
					
							 <li class="v_desc"> </li>
							 <li class="v_stat"><label>播放:</label><span class="num"><%=item.optInt("total_pv")%></span></li>
							
					
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
	
	<%=PageUtil.getNewContent(WebUtil.getPagePrefixString("/k", param,request),total,curpage,pagesize)%>
</div>


<% } else {%>
<div class="null">
	<div class="sorry">
		抱歉，没有找到<span class="key"><%= keyword %></span>的相关内容。	</div>
	<h3>建议您：</h3>
	<ul>
		<li>• 检查输入的关键词是否有误。</li>
		<li>• 缩短关键词。</li>
		<li>• 使用相近、相同或其他语义的关键词。</li>
		<li>• 放宽筛选条件。</li>
	</ul>
</div>

<%} %>
</div>

<!--result end-->	</div><!--maincol end-->
	<div class="clear"></div>
	
	<script language="javascript">ViewBy.init();</script>

</div><!--layout end-->
</div><!--body end-->

<div class="footer">
<div class="footerbox">
<div class="main">
	<div class="logo"><a href="#"><span class="logosoku"><strong>SOKU搜库</strong></span></a></div>
	<div class="sotool">
		<div class="socore">
			<input class="sotxt" type="text" value="" autocomplete="off" />
			<button class="sobtn" type="submit"><em>搜索</em></button>

		</div>
	</div>
	<div class="clear"></div>
</div>
</div>
<div class="copyright">
	Copyright&copy;2011 优酷youku.com版权所有
	<a href="#" target="_blank">京ICP证060288号</a>
	<a href="#" target="_blank">免责声明</a>

	<a href="#" target="_blank">开放协议</a>
</div></div><!--footer end-->
</div><!--screen end-->
</div><!--widnow end-->
</body>
</html>
