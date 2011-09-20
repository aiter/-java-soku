<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.server.*,
				com.youku.so.index.*,
				com.youku.search.util.*,
				com.youku.search.index.*"%>
<%
IndexManager m = new IndexManager(ServerManager.getVideoIndexPath()+"1");
m.addIndex();

QueryManager.getInstance().reopenIndex();
%>
