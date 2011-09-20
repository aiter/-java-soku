<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.index.manager.*,
				com.youku.search.util.DataFormat,
				com.youku.search.util.*,
				com.youku.soku.index.server.*,
				com.youku.soku.index.*,
				java.io.File"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

int type = DataFormat.parseInt(request.getParameter("type"));

if (type == 1){
	VideoIndexManager manager = new VideoIndexManager();
	manager.createIndex();
}
else if (type == 2){
	WordIndexManager manager = new WordIndexManager();
	int row = manager.createIndex();
	 _log.info("创建索引成功");

	 	List<Server> servers =  ServerManager.getWordServers(1);


	if (row > 0){
		Command.rsyncWord(1,servers.get(0).getIp());
	}
	 _log.info("传输索引成功");
	

	 Request.requestGet("http://"+servers.get(0).getIp()+"/index/index_reload.jsp?type=2");
     _log.info("更新索引完成");
}
else if (type ==3){
	WordIndexManager manager = new WordIndexManager();
	int row = manager.createIndex();
	 _log.info("创建索引成功");

	 	List<Server> servers =  ServerManager.getWordServers(1);


	if (row > 0){
		Command.rsyncWord(1,servers.get(0).getIp());
	}
	 _log.info("传输索引成功");
	

	 Request.requestGet("http://"+servers.get(0).getIp()+"/index/index_reload.jsp?type=2");
     _log.info("更新索引完成");
}

%>