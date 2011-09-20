<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.youku.soku.manage.deadlink.DumpOtherSiteLink" %>
<%
	DumpOtherSiteLink dumper = new DumpOtherSiteLink();
	int offset = -1;
	int limit = -1;
	try {
		offset = Integer.valueOf(request.getParameter("offset"));
		limit = Integer.valueOf(request.getParameter("limit"));
	}catch(Exception e) {
		
	}
	out.print(dumper.dump(offset, limit));
	
%>