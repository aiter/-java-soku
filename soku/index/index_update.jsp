<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.index.server.*,
				com.youku.soku.util.*,
				com.youku.soku.index.manager.*,
				com.youku.soku.index.query.*"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

VideoIndexManager manager = new VideoIndexManager(Config.getVideoIndexPath());
int row = manager.updateVideo();

if (row > 0)
	VideoQueryManager.getInstance().reopenIndex();
%>