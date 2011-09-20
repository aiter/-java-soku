<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.console.operate.TeleplayOneUsed,java.util.*"%>
<html>
	<head>
		<title>美剧列表</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<%
	TeleplayOneUsed t=new TeleplayOneUsed();
	List<String[]> sl=t.getTeleplayAndVersion();
	 %>
	<table cellSpacing="1" cellPadding="3" width="100%"  bgcolor="#999999"
			 align="center">
	<tr align="center" bgcolor="#A4D6FF">
	<td width="35%">电视剧名称</td>
	<td width="65%">版本名称</td>
	</tr>

	<% for(String[] s:sl){ %>
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td><%=s[0] %></td>
	<td><%=s[1] %></td>
	</tr>
	<%} %>
	
	</table>
	</body>
</html>