<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.youku.soku.knowledge.KnowledgeData,
				com.youku.soku.knowledge.data.KnowledgeDataHolder,
				java.util.Map,
				java.util.List,
				com.youku.soku.library.load.KnowledgeColumn" %>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>soku</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
</head>

<body>
<div class="window">
<div class="screen">
<%String keyword = ""; %>

<%@ include file="/result/inc/header.jsp" %>
<div class="body">

<div class="typechk">
<div class="main">
	</div>
</div><div class="layout">
<div class="ranktype">
	<h1 class="rankname">知识排行榜</h1>
	<label>选择排行榜:</label>
	<%@ include file="/top/inc/topnavigate.inc" %>
</div>	
	<div class="rank_knowledge">
		
		<%
			try {
				//data.printKnowledgeTree(0, KnowledgeDataHolder.getCurrentThreadLocal());
				KnowledgeData dataMap = KnowledgeDataHolder.getCurrentThreadLocal();
				Map<String, List<KnowledgeColumn>> knowledgeRankMap = dataMap.getKnowledgeRankMap();
				for(String key : knowledgeRankMap.keySet()) {
					%>
						<div class="type">
						<div class="caption">
							<div class="title"><h3><%=key %></h3></div>
							<div class="extend"><a href="#" target="_blank">查看全部分类&gt;&gt;</a></div>
						</div>
						<div class="items">					
					<%
					List<KnowledgeColumn> rankList = knowledgeRankMap.get(key);
					for(int i = 0; i < rankList.size(); i++) {
						KnowledgeColumn kc = rankList.get(i);
						 if(i < 5) {
							 %>
								<ul class="item">
									<li class="photo"><a href="/k?keyword=<%=kc.getName() %>" target="_blank"><img src="http://g4.ykimg.com/0100641F464A92674312AC000000007CFC75D6-1816-BF85-F8D9-AD5E1B3DAD1B" /></a></li>
									<li class="key"><a href="/k?keyword=<%=kc.getName() %>" target="_blank"><%=kc.getName() %></a></li>
								</ul>
							<%
						 }  if(i == 4){
							 %>
							 	<div class="clear"></div>
								</div>
								<ul class="keys">
							 <%
						 } if (i >=4) {
							 %>
							 	<li><em>•</em><a href="/k?keyword=<%=kc.getName() %>" target="_blank"><%=kc.getName() %></a></li>
							 <%
						 }
					}
					%>
						</ul>
						<div class="clear"></div>		
					</div>
					<%
				}				
				%>
				<div class="all">
			<h3>全部分类</h3>
			<ul><%
				for(String key : knowledgeRankMap.keySet()) {
					%>
						<li>
							<label><a href="#" target="_blank"><%=key %></a></label>				
					<%
					List<KnowledgeColumn> rankList = knowledgeRankMap.get(key);
					for(int i = 0; i < rankList.size(); i++) {
						KnowledgeColumn kc = rankList.get(i);
						 if(i < 4) {
							 %>
								<span><a href="/k?keyword=<%=kc.getName() %>" target="_blank"><%=kc.getName() %></a></span>
							<%
						 }  
					}
					%><span class="more"><a href="#" target="_blank">更多&gt;&gt;</a></span></li><%
				}
				
			} catch (Exception e) {
				
			} finally {
				KnowledgeDataHolder.removeCurrentThreadLocal();
			}
		
		%>

				</ul>
		<div class="clear"></div>	
	</div>

	
</div></div><!--layout end-->
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
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/soku.js"></script>
</body>
</html>