<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.*"%>
<%
int type = DataFormat.parseInt(request.getParameter("type"));
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
List<Server> servers = null;
if (type == Constant.QueryField.VIDEO){
	servers = ServerManager.getNewVideoServers();
}
else if (type == Constant.QueryField.FOLDER){
	servers = ServerManager.getNewFolderServer();
}
else if (type == Constant.QueryField.MEMBER){
	servers = ServerManager.getNewUserServers();
}
else if (type == Constant.QueryField.PK || type == Constant.QueryField.BARPOST_SUBJECT || type == Constant.QueryField.BAR){
	servers = ServerManager.getNewBarpostServer();
}


if (servers == null && servers.size()==0)
{
	return;
}

int start = servers.get(0).getStart();
int end = servers.get(0).getEnd();
int order = servers.get(0).getOrder();


BaseIndexManager manager = null;
if (type == Constant.QueryField.VIDEO){
	manager = new VideoIndexManager(start,end,ServerManager.getVideoIndexPath()+order);
}
else if (type == Constant.QueryField.FOLDER){
	manager = new FolderIndexManager(start,end,ServerManager.getFolderIndexPath()+order);

}
else if (type == Constant.QueryField.MEMBER){
	_log.info(ServerManager.getUserIndexPath()+order);
	manager = new UserIndexManager(start,end,ServerManager.getUserIndexPath()+order);
}
else if (type == Constant.QueryField.BARPOST_SUBJECT){
	manager = new BarpostIndexManager(start,end,ServerManager.getBarpostIndexPath()+order);
}

else if (type == Constant.QueryField.BAR){
	manager = new BarIndexManager(start,end,ServerManager.getBarIndexPath()+order);
}
else
	return;

 manager.clearLastUpdateTime();
int row = manager.addIndex();

_log.info("开始同步affect文件");
String[] args = new String[]{CmdManager.pathPrefix + "rsync_affect.sh"}; 
int value = CmdManager.Exec(CmdManager.command ,args);
_log.info("同步affect文件完成");

if (type == Constant.QueryField.VIDEO){
	_log.info("开始删除索引");

	//远程删除
	List<Server> ts = ServerManager.getVideoServers();
	for (int i=0;i<ts.size();i++)
	{
		Server server = ts.get(i);
		Request.requestGet("http://"+ server.getIp() +"/index/interface/process_delete.jsp",2000);
	}
	_log.info("结束删除索引");
}
else if (type == Constant.QueryField.FOLDER){
	_log.info("开始删除索引");
	//远程删除
	List<Server> ts = ServerManager.getFolderServers();
	for (int i=0;i<ts.size();i++)
	{
		Server server = ts.get(i);
		Request.requestGet("http://"+ server.getIp() +"/index/interface/process_delete.jsp",2000);
	}
	_log.info("结束删除索引");
}
else if (type == Constant.QueryField.MEMBER){

	_log.info("开始删除索引");
	//远程删除
	List<Server> ts = ServerManager.getUserServers();
	for (int i=0;i<ts.size();i++)
	{
		Server server = ts.get(i);
		Request.requestGet("http://"+ server.getIp() +"/index/interface/process_delete.jsp",2000);
	}

	_log.info("结束删除索引");

}


_log.info("type="+ type +"递增索引成功");
_log.info("新加type="+ type +"索引"+row+"条");

if (row > 0)
{
	for (int i = 0;i<servers.size();i++)
	{
		Server s = servers.get(i);
		CmdManager.rsyncIndex(type,s.getIp(),s.getOrder());
		_log.info("传输索引完成"+s.getIp());

		Request.requestGet("http://"+s.getIp()+"/index/interface/index_reload.jsp?type="+type);
		_log.info("更新索引完成"+s.getIp());
	}
}
%>