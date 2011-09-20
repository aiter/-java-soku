<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.*,
				java.io.File,
				com.youku.search.util.*,
				com.youku.search.util.mail.*"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

int type = DataFormat.parseInt(request.getParameter("type"));
int order = DataFormat.parseInt(request.getParameter("order"));

List<Server> servers = ServerManager.getServersByOrder(type,order);
if (type == 0 || order == 0 || servers == null || servers.size()==0)
{       
        return;
}       

int start = servers.get(0).getStart();
int end = servers.get(0).getEnd();

BaseIndexManager manager = null;
if (type == Constant.QueryField.VIDEO){
        manager = new VideoIndexManager(start,end,ServerManager.getVideoIndexPath()+"new/"+order);
}
else if (type == Constant.QueryField.FOLDER){
        manager = new FolderIndexManager(start,end,ServerManager.getFolderIndexPath()+"new/"+order);
}
else if (type == Constant.QueryField.MEMBER){
        manager = new UserIndexManager(start,end,ServerManager.getUserIndexPath()+"new/"+order);
}
else if (type == Constant.QueryField.PK){
        manager = new PkIndexManager(start,end,ServerManager.getPkIndexPath()+"new/"+order);
}       
else if (type == Constant.QueryField.BARPOST_SUBJECT){
        manager = new BarpostIndexManager(start,end,ServerManager.getBarpostIndexPath()+"new/"+order);
}               
else if (type == Constant.QueryField.STAT){
        //if (true)return;
        manager = new StatIndexManager(ServerManager.getStatIndexPath()+"new/"+order);
}
	else if (type == Constant.QueryField.RING){
			manager = new RingIndexManager(start,end,ServerManager.getRingIndexPath()+"new/"+order);
	}
	else if (type == Constant.QueryField.BAR){
			manager = new BarIndexManager(start,end,ServerManager.getBarIndexPath()+"new/"+order);
	}
	else
			return;

	int total = manager.createIndex();

	_log.info("total="+total);


	File indexDir = new File(manager.getIndexPath());
	if(indexDir.list().length > 6)
	{
		 _log.error("索引没有优化，type:"+type+",order="+order);
		return;
	}


	if (total > 1000){

        while (true)
        {
                if ((new File(manager.getIndexPath().replace("new/","") +"/write.lock")).exists())
                {
                        _log.error(manager.getIndexPath().replace("new/","") +"/write.lock 存在，sleep 5000ms");
                        try{
                                Thread.sleep(5000);
                        }catch(Exception e){}
                }
                else
                        break;
        }

        _log.info("开始移动索引");
        CmdManager.moveIndex(type,order);
        _log.info("结束移动索引");
		

        manager.clearLastUpdateTime();

        _log.info("创建索引成功");
        for (int i = 0;i<servers.size();i++)
        {
				Server s = servers.get(i);
                CmdManager.rsyncIndex(type,s.getIp(),s.getOrder());
                _log.info("传输索引完成"+s.getIp());
                Request.requestGet("http://"+s.getIp()+"/index/interface/index_reload.jsp?type="+type);
                _log.info("更新索引完成"+s.getIp());
        }
	}
	else if (total == -1)
	{

		MailMessage message = new MailMessage();

		message.setHost("10.10.0.12");
		message.setPort(25);
		message.setNeedAuth(true);
		message.setUsername("jiabaozhen");
		message.setPassword("12345679");
		String ip = request.getServerName();
		message.setFrom(ip+"@youku.com");
		message.setTo("luwei@youku.com");

		message.setSubject(ip + "创建索引数据库异常： " + DataFormat.formatDate(new Date(),2));

		message.setHTMLText(false);
		message.setText(ip + "创建索引数据库异常：：\n" + DataFormat.formatDate(new Date(),2) + "\n<hr><hr>");

		MailSender.send(message);
	}

%>