<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.*,
				java.io.File,
				com.youku.search.recomend.*,
				com.youku.search.util.*,
				com.youku.search.util.mail.*"%>
<%
ServerManager.init("/opt/search/WEB-INF/conf/index-servers.xml");
%>