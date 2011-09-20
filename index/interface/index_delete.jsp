<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.query.*,
				com.youku.search.index.manager.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.config.Config,
				com.youku.search.index.server.*,
				java.io.*"%>
<%!
	void isLocked(String indexPath)
	{
		while (true)
		{
			File file = new File(indexPath+"/write.lock");
			if (file.exists())
			{
				try{
					Thread.sleep(5000);
				}catch(Exception e){}
			}
			else
				break;
		}
	}
%>

<%
int id = DataFormat.parseInt(request.getParameter("id"));
int type = DataFormat.parseInt(request.getParameter("type"));

int row = 0;
if(Config.getServerType() == 3) //索引管理服务器
{
	List<Server> servers = ServerManager.getServersById(id,type);
	int order = servers.get(0).getOrder();

	if (type == Constant.QueryField.VIDEO)
	{
		//删除远程
		for (Server server:servers)
		{
			System.out.println("http://"+ server.getIp() +"/index/interface/index_delete.jsp?type="+type+"&id="+id);
			Request.requestGet("http://"+ server.getIp() +"/index/interface/index_delete.jsp?type="+type+"&id="+id,2000);
		}
	
		//删除本地
		isLocked(ServerManager.getVideoIndexPath()+order);
		VideoIndexManager m = new VideoIndexManager(ServerManager.getVideoIndexPath()+order);
		row = m.deleteVideo(id);

		
	}
	else if (type == Constant.QueryField.FOLDER)
	{
		//删除远程
		for (Server server:servers)
		{
			System.out.println("http://"+ server.getIp() +"/index/interface/index_delete.jsp?type="+type+"&id="+id);
			Request.requestGet("http://"+ server.getIp() +"/index/interface/index_delete.jsp?type="+type+"&id="+id,2000);
		}

		isLocked(ServerManager.getFolderIndexPath()+order);
		FolderIndexManager m = new FolderIndexManager(ServerManager.getFolderIndexPath()+order);
		row = m.deleteFolder(id);
	}
	else if (type == Constant.QueryField.MEMBER)
	{
		isLocked(ServerManager.getUserIndexPath()+order);
		UserIndexManager m = new UserIndexManager(ServerManager.getUserIndexPath()+order);
		row = m.deleteUser(id);
	}
	else if (type == Constant.QueryField.BARPOST_POST)
	{
		isLocked(ServerManager.getBarpostIndexPath()+order);
		BarpostIndexManager m = new BarpostIndexManager(ServerManager.getBarpostIndexPath()+order);
		row = m.deletePost(id);
	}
	else if (type == Constant.QueryField.BARPOST_SUBJECT)
	{
		isLocked(ServerManager.getBarpostIndexPath()+order);
		BarpostIndexManager m = new BarpostIndexManager(ServerManager.getBarpostIndexPath()+order);
		row = m.deleteSubject(id);
	}
	else if (type == Constant.QueryField.BAR)
	{
		isLocked(ServerManager.getBarpostIndexPath()+order);
		BarpostIndexManager m = new BarpostIndexManager(ServerManager.getBarpostIndexPath()+order);
		row = m.deleteBar(id);
	}
	else if (type == Constant.QueryField.PK)
	{
		isLocked(ServerManager.getPkIndexPath()+order);
		PkIndexManager m = new PkIndexManager(ServerManager.getPkIndexPath()+order);
		row = m.deletePk(id);
	}
}
else
{
	if (type == Constant.QueryField.VIDEO)
	{
		row = VideoQueryManager.getInstance().deleteVideo(id);
	}
	else if (type == Constant.QueryField.FOLDER)
	{
		row = FolderQueryManager.getInstance().deleteFolder(id);
	}
	else if (type == Constant.QueryField.MEMBER)
	{
		row = UserQueryManager.getInstance().deleteUser(id);
	}
	else if (type == Constant.QueryField.BARPOST_POST)
	{
		row = BarpostQueryManager.getInstance().deletePost(id);
	}
	else if (type == Constant.QueryField.BARPOST_SUBJECT)
	{
		row = BarpostQueryManager.getInstance().deleteSubject(id);
	}
	else if (type == Constant.QueryField.BAR)
	{
		row = BarpostQueryManager.getInstance().deleteBar(id);
	}
	else if (type == Constant.QueryField.PK)
	{
		row = PkQueryManager.getInstance().deletePk(id);
	}
}
if (row > 0)
	out.print("ok");
else
	out.print("fail");
%>
