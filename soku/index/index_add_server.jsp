<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.index.server.*,
				com.youku.soku.util.*,
				com.youku.soku.index.*,
				com.youku.search.util.*,
				com.youku.soku.index.manager.db.*,
				 com.youku.soku.index.manager.*,
				 com.youku.soku.index.om.Site,
				java.io.*"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");


Date now = new Date();

List<Server> servers = ServerManager.getVideoServers(1);
if (servers == null || servers.isEmpty()){
	_log.error("server 为空");
	return;
}
List<Server> servers2 = ServerManager.getVideoServers(2);
if (servers2 == null || servers2.isEmpty()){
	_log.error("server 为空");
	return;
}

MovielogManager.getInstance().clean();

int row = 0;
VideoIndexManager manager = new VideoIndexManager(Config.getVideoIndexPath()+servers.size());

manager.deleteVideo();

if (VideoIndexManager.Flag.lastTime == null)
{
	_log.info("lasttime is empty ,add from max id");
	_log.info("get max id ing......");
	//获取每个站点最后的id
	List<Site> sites = SiteManager.getInstance().getValidSiteList();
	int[] maxids = VideoIndexManager.getMaxIds(sites);
	if (maxids == null || maxids.length == 0){
		return;
	}
	
	StringBuffer sb = new StringBuffer();
	for (int s:maxids){
		sb.append(s).append(",");
	}
	
	String ids = sb.substring(0,sb.length()-1);
	
	_log.info("max ids="+ids);
	
	row = manager.addIndex(ids,now);

	//_log.info("http://"+servers.get(rand).getIp()+"/index/index_add.jsp?ids="+ ids +"&end="+now.getTime());
	
	
	//Request.requestGet("http://"+servers.get(rand).getIp()+"/index/index_add.jsp?ids="+ ids +"&end="+now.getTime());
}	
else
{
	row = manager.addIndex(VideoIndexManager.Flag.lastTime,now);
	
	//_log.info("http://"+servers.get(rand).getIp()+"/index/index_add.jsp?start="+ VideoIndexManager.Flag.lastTime.getTime() +"&end="+now.getTime());

	//Request.requestGet("http://"+servers.get(rand).getIp()+"/index/index_add.jsp?start="+ VideoIndexManager.Flag.lastTime.getTime() +"&end="+now.getTime());
}

if (row > 10){
	String ip = servers.get(servers.size()-1).getIp();
	String ip2 = servers2.get(servers2.size()-1).getIp();
	Command.rsyncIndex(1,ip);
	_log.info("同步索引完成Group:" + 1 + " Ip:"+ip);
	Request.requestGet("http://"+ip+"/index/index_reload.jsp?type=1");
	_log.info("更新索引完成Group:" + 1 + " Ip:"+ip);

	Command.rsyncIndex(2,ip2);
	_log.info("同步索引完成Group:" + 2 + " Ip:"+ip2);
	Request.requestGet("http://"+ip2+"/index/index_reload.jsp?type=1");
	_log.info("更新索引完成Group:" + 2 + " Ip:"+ip2);
}

VideoIndexManager.Flag.lastTime = now;

_log.info("add index complete! total =" + row);
%>
