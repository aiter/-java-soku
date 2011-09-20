<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.youku.soku.manage.datamaintain.*" %>
<%
	EpisodeVideoSync syncer = new EpisodeVideoSync();
	syncer.run();
	
%>