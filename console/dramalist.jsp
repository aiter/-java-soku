<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.console.operate.TeleplayOneUsed,java.util.*,com.youku.search.util.*"%>
<html>
	<head>
	<%int sc=DataFormat.parseInt(request.getParameter("sc"),0); %>
		<title>
		<%
		if(sc==97)out.print("电视列表");
		else if(sc==2070)out.print("韩剧列表");
		else if(sc==2069)out.print("日剧列表");
		else if(sc==2039)out.print("港剧列表");
		else if(sc==2038)out.print("大陆剧列表");
		else if(sc==2068)out.print("台剧列表");
		else if(sc==2080)out.print("其他地区电视剧列表");
		else if(sc==2081)out.print("美剧列表");
		else if(sc==2078)out.print("综艺节目列表");
		else if(sc==100)out.print("动漫列表");
		else if(sc==2224)out.print("日韩动漫列表");
		else if(sc==2226)out.print("欧美动漫列表");
		else if(sc==2223)out.print("国产动漫列表");
		else if(sc==2225)out.print("港台动漫列表");
		else{
			sc=0;
			out.print("未分类电视列表");
		 }
		 %>
		 </title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<%
	TeleplayOneUsed t=new TeleplayOneUsed();
	List<String[]> sl=t.getTeleplayURLBycate(sc);
	 %>
	<table cellSpacing="1" cellPadding="3" width="100%"  bgcolor="#999999"
			 align="center">
	<tr align="center" bgcolor="#A4D6FF">
	<td width="35%">电视剧名称</td>
	<td width="10%">版本名称</td>
	<td width="10%">单集名称</td>
	<td width="45%">URL</td>
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