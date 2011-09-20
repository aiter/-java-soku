<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.console.operate.rights.UserMgt"%>
	<%
		String name=(String)request.getAttribute("temp_name");
		request.removeAttribute("temp_name");
		UserMgt um=UserMgt.getInstance();
		boolean f=um.checkName(name);
		if(f)
			out.print(1);
		else
			out.print(0); %>