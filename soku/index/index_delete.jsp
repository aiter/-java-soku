<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.index.manager.*,
				com.youku.soku.util.*,
				com.youku.soku.index.*,"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

VideoIndexManager manager = new VideoIndexManager(Config.getVideoIndexPath());
int row = manager.deleteVideo();

%>