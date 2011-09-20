<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.util.DataFormat"%>
		<%	
			int flag=DataFormat.parseInt(request.getAttribute("flag"),2);
			out.print(flag);
		%>