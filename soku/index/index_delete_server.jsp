<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.index.server.*,
				com.youku.soku.util.*,
				com.youku.soku.index.*,
				com.youku.search.util.*"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

int group_count = ServerManager.getGroupCount();
for (int i=1;i<=group_count;i++){
		List<Server> servers = ServerManager.getVideoServers(i);
		if (servers == null || servers.isEmpty()){
			_log.error("server 为空");
			return;
		}

		for (Server server:servers)
		{
			_log.info("http://"+server.getIp()+"/index/index_delete.jsp");

			Request.requestGet("http://"+server.getIp()+"/index/index_delete.jsp");
		}
}
%>