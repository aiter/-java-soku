<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.console.vo.EpisodeRepeatVO,com.youku.search.console.operate.EpisodeVideoRepeatMgt,java.util.*"%>
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
	<%
	EpisodeVideoRepeatMgt erm=new EpisodeVideoRepeatMgt();
	List<EpisodeRepeatVO> sl=erm.getEpisodeArrs();
	 %>
	<table cellSpacing="1" cellPadding="3" width="100%"  bgcolor="#999999"
			 align="center">
	<tr align="center" bgcolor="#A4D6FF">
	<td width="35%">电视剧名称</td>
	<td width="10%">版本名称</td>
	<td width="10%">单集名称</td>
	<td width="45%">URL</td>
	</tr>

	<% 
		for(EpisodeRepeatVO s:sl){
	%>
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td><%=s.getRepeat().getTname() %></td>
	<td><%=s.getRepeat().getPvname() %></td>
	<td><%=s.getRepeat().getOrder() %></td>
	<td><%=s.getRepeat().getUrlstr() %></td>
	</tr>
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td colspan=4 height=10></td>
	</tr>
	<%} %>
	</table>
	</body>
</html>