<%@ page contentType="text/html; charset=utf-8" language="java" %>
<!DOCTYPE HTML>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>抱歉，没有找到页面 搜库 找你好看</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
</head><body>
<div class="window">
<div class="screen">

<% String keyword = "";%>
<%@ include file="/result/inc/header.jsp" %>

<div class="body">
<div class="typechk">
<div class="main">
	</div>
</div><div class="layout">	
	<div class="p404">
	<h2>抱歉, 信号迷失:(</h2>
	<div>重新搜索 或 <a href="/">返回首页</a></div>
	</div>
</div><!--layout end-->
</div><!--body end-->

<%@ include file="/result/inc/footer.jsp" %>
</div><!--screen end-->
</div><!--widnow end-->
</body>
<script type="text/javascript">
		document.getElementById("headq").focus();
		Ab('');
</script>
<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
<script type="text/javascript">
if(typeof youkuTracker == "function") {
	youkuTracker( "soku=" + library + "|" + result_count );sokuHz();
}
</script>
</html>