<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.query.*,
				com.youku.search.index.manager.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.config.Config,
				com.youku.search.index.server.*,
				java.io.*"%>
<%
BarpostQueryManager.getInstance().deleteBar(DataFormat.parseInt(request.getParameter("id")));
%>
