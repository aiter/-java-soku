<%@ page contentType="text/html;charset=utf-8" language="java"%>
	<%
		String info=(String)request.getAttribute("info");
		request.removeAttribute("info");
		out.print("返回状态:"+info); %>