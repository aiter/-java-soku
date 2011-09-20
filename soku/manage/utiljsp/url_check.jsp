<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.youku.soku.manage.deadlink.OtherSiteUrlCheck,
	org.apache.commons.lang.StringUtils" %>
<%
// 死链检测接口格式： 10.103.8.6
//http://10.103.8.6/soku/url_check.jsp?url=xxx&programme_id=xx
String url = request.getParameter("url");
String programme_id = request.getParameter("programme_id");
response.setCharacterEncoding("utf-8");
response.setContentType("text/plain;charset=utf-8");
boolean flag = false;
if (!StringUtils.isBlank(url)) {
	try {
		OtherSiteUrlCheck urlCheck = new OtherSiteUrlCheck();
		urlCheck.saveDeadLink(url, Integer.valueOf(programme_id));
		out.print(1);
	} catch (Exception e) {
		e.printStackTrace();
		out.print(0);
	}
}
%>
