<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				com.youku.search.index.data.*,
				com.youku.search.index.*,
				java.io.File,
				com.youku.search.recomend.*,
				com.youku.search.util.*,
				com.youku.search.util.mail.*"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

VideoTestManager  manager = new VideoTestManager(ServerManager.getVideoIndexPath()+"new/");


	int total = manager.createIndex(1,5000000);

	_log.info("total="+total);


    _log.info("创建索引成功");
       
%>