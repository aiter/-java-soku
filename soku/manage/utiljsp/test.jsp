<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.youku.search.util.Wget,
			com.youku.soku.library.episode.*" %>

<%
/**
String start = request.getParameter("start");
String filesize = request.getParameter("filesize");
String limit = request.getParameter("limit");
if(start == null){
	out.print("start=0&filesize=900&limit=100");
} else {
	String s = new String(Wget.get("http://10.103.18.62/download_delete.php?start=" + start + "&filesize=" + filesize + "&limit=" + limit));
	out.print(s);
}
*/

ProgrammeDetailLoader.load();

%>