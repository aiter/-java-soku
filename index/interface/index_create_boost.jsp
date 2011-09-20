<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.*,
				java.io.File"%>
<%
int type = DataFormat.parseInt(request.getParameter("type"));
int order = DataFormat.parseInt(request.getParameter("order"));

List<Server> servers = ServerManager.getServersByOrder(type,order);
if (servers == null || servers.size()==0)
{
	return;
}

int start = servers.get(0).getStart();
int end = servers.get(0).getEnd();

VideoIndexManager manager = null;
	manager = new VideoIndexManager(start,end,ServerManager.getVideoIndexPath()+order);


while (true)
{
	File file = new File(manager.getIndexPath()+ order +"/write.lock");
	if (file.exists())
	{
		try{
			Thread.sleep(5000);
		}catch(Exception e){}
	}
	else
		break;
}

manager.createBoost();

org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

_log.info("创建Boost成功");

if (true)
	return;

for (int i = 0;i<servers.size();i++)
{
	Server s = servers.get(i);
	CmdManager.rsyncIndex(type,s.getIp(),s.getOrder());
	_log.info("传输索引完成"+s.getIp());
	Request.requestGet("http://"+s.getIp()+"/index/interface/index_reload.jsp?type="+type);
	_log.info("更新索引完成"+s.getIp());
}
%>