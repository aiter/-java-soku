<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>多站点搜索</title>

</head>
<frameset rows="10%,90%" height="100%" id="fs" name="fs">
	<frame src="search/sw.html" id="sw" name="sw" border="0" noresize scrolling="no" >
	<frameset cols="90%,*" id="sf">
		<frame src="search/yk.html" name="yk" id="yk">
	</frameset>
	<s:iterator value="topwordsvo" status="sta">
		<frame src="" id="<s:property value='keyword'/>" name="keywords" border="0" noresize scrolling="no" >
	</s:iterator>
</frameset>
<noframes></noframes>
</html>
