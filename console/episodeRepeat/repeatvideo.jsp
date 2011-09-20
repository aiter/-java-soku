<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>
		重复视频
		 </title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<table cellSpacing="1" cellPadding="3" width="100%"  bgcolor="#999999"
			 align="center">
	<tr align="center" bgcolor="#A4D6FF">
	<td width="35%">电视剧名称</td>
	<td width="10%">版本名称</td>
	<td width="10%">单集名称</td>
	<td width="45%">URL</td>
	</tr>

	<s:iterator value="repeatVideos">
	<s:iterator value="repeatVideo">
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td><s:property value="tname"/></td>
	<td><s:property value="pvname"/></td>
	<td><s:property value="order"/></td>
	<td><s:property value="urlstr"/></td>
	</tr>
	</s:iterator>
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td colspan=4 height=10></td>
	</tr>
	</s:iterator>
	</table>
	</body>
</html>