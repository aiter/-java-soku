<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.index.manager.*,
				com.youku.soku.index.query.*,
				com.youku.soku.util.*,
				com.youku.soku.index.*,
				com.youku.search.util.*"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

int id = DataFormat.parseInt(request.getParameter("id"));
long start = DataFormat.parseLong(request.getParameter("start"));
long end = DataFormat.parseLong(request.getParameter("end"));
String ids = request.getParameter("ids");

Date startTime = null;
Date endTime = null;
try
{
	startTime = new Date(start);
}
catch(Exception e)
{
	startTime = new Date();
}
try
{
	endTime = new Date(end);
}
catch(Exception e)
{
	endTime = new Date();
}

VideoIndexManager manager = new VideoIndexManager(Config.getVideoIndexPath());
int row = 0;

if (ids != null && ids.length()>0)
	row = manager.addIndex(ids,endTime);
else
	row = manager.addIndex(startTime,endTime);

if (row > 0){
	VideoQueryManager.getInstance().reopenIndex();
}
%>