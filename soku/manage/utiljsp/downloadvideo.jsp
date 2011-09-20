<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.youku.soku.manage.datamaintain.*" %>
<html>

<form action="downloadvideo.jsp">
	<input name="showId" type="text"/>
	<input type="submit" />

</form>
<%
String showIdStr = request.getParameter("showId");
if(showIdStr != null) {
	int showId = Integer.valueOf(request.getParameter("showId"));
	VideoDownloadUtil util = new VideoDownloadUtil();
	out.print(util.getDownloadUrl(showId));
}

%>
</html>