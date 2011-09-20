<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.console.operate.TeleplayOneUsed,java.util.*,com.youku.search.util.*"%>
<html>
	<head>
	<%int pid=DataFormat.parseInt(request.getParameter("pid"),2081); %>
		<title>
		<%
		if(pid==2070)out.print("未收录完韩剧列表");
		if(pid==2069)out.print("未收录完日剧列表");
		if(pid==2039)out.print("未收录完港剧列表");
		if(pid==2038)out.print("未收录完大陆剧列表");
		if(pid==2068)out.print("未收录完台剧列表");
		if(pid==2080)out.print("未收录完其他地区电视剧列表");
		if(pid==2081)out.print("未收录完美剧列表");
		 %>
		 </title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<%
	TeleplayOneUsed t=new TeleplayOneUsed();
	List<String[]> sl=t.getUnFexedUSTeleplayAndVersion(pid);
	 %>
	<table cellSpacing="1" cellPadding="3" width="100%"  bgcolor="#999999"
			 align="center">
	<tr align="center" bgcolor="#A4D6FF">
	<td width="35%">电视剧名称</td>
	<td width="10%">版本名称</td>
	<td width="10%">总集数</td>
	<td width="45%">缺少集数</td>
	</tr>

	<% for(String[] s:sl){ %>
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td><%=s[0] %></td>
	<td><%=s[1] %></td>
	<td><%=s[2] %></td>
	<td><%=s[3] %></td>
	</tr>
	<%} %>
	
	</table>
	</body>
</html>