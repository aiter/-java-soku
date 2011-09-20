<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.analyzer.*
				com.youku.search.analyzer.danga.MemCached.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				org.apache.lucene.search.*,
				org.apache.lucene.index.*,
				com.youku.search.index.query.*,
				java.io.*,
				com.youku.search.index.*"%>
<%
int order =DataFormat.parseInt(request.getParameter("order"));
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
VideoIndexManager m = new VideoIndexManager(1,100,ServerManager.getVideoIndexPath()+order);
String vids =  request.getParameter("vids");
String[] arr = null;

if (vids != null)
{
	arr = vids.split(",");
}
int[] ivids = new int[arr.length];


for (int j=0;j<arr.length;j++)
{
	ivids[j] = DataFormat.parseInt(arr[j]);
}
while (true)
{
	File file = new File(m.getIndexPath()+"/write.lock");
	if (file.exists())
	{
		try{
			Thread.sleep(5000);
		}catch(Exception e){}
	}
	else
		break;
}
int row = m.updateIndex(ivids);

if (row > 0)
{
List<Server> servers = ServerManager.getServersByOrder(1,order);
for (int i = 0;i<servers.size();i++)
{
            Server s = servers.get(i);
            CmdManager.rsyncIndex(1,s.getIp(),order);
             _log.info("传输索引完成"+s.getIp());
           
             Request.requestGet("http://"+s.getIp()+"/index/interface/index_reload.jsp?type=1");
             _log.info("更新索引完成"+s.getIp());
}
}
%>
