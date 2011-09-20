<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.youku.soku.manage.datamaintain.*,
	org.apache.commons.lang.StringUtils" %>


<%
	HandleLackEpisodeData hle = new HandleLackEpisodeData();
	//hle.deleteData();
	hle.deleteDataEpisodeTotal0();
%>
