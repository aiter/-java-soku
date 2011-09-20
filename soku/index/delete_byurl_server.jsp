<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.index.server.*,
				com.youku.soku.util.*,
				com.youku.soku.index.*,
				com.workingdogs.village.Record,
				com.youku.so.om.*,
				com.youku.soku.index.manager.db.*,
				org.apache.torque.util.*,
				org.apache.lucene.index.IndexWriter,
				com.youku.soku.index.analyzer.AnalyzerManager,
				java.io.*,
				com.youku.search.util.*"%>
<%!
int getVid(String table,String url)
{
	try
	{
		List<Record> records = BasePeer.executeQuery("select id from "+ table +" where url='"+ url +"'");
		if (records != null && records.size()>0)
		{
			Record record = records.get(0);
			return record.getValue("id").asInt();
		}
	}catch(Exception e)
	{
		System.out.println(e.getMessage());
	}
	return 0;
}
void updateState(String table,String url)
{
	String date = DataFormat.formatDate(new Date(),1);
	try
	{
		BasePeer.executeStatement("update "+ table +"set state='delete',uploadTime='"+ date +"' where url='"+ url +"'");
		
	}catch(Exception e)
	{
		System.out.println(e.getMessage());
	}
}
%>

<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
int group_count = ServerManager.getGroupCount();

String urls = request.getParameter("urls");
StringBuilder sid = new StringBuilder();
if (urls != null)
{
	String[] urlarr = urls.split("\\|\\|");
	if (urlarr!= null && urlarr.length > 0){

		while (true)
        {
                if ((new File(Config.getVideoIndexPath()+group_count+"/write.lock")).exists())
                {
                        _log.error(Config.getVideoIndexPath()+group_count+"/write.lock 存在，sleep 5000ms");
                        try{
                                Thread.sleep(5000);
                        }catch(Exception e){}
                }
                else
                        break;
        }
		
		
			for (String url:urlarr)
			{
				if (url != null && !url.trim().isEmpty())
				{
					if (url.indexOf("youku.com")>0)
					{
						String enid = url.substring(url.lastIndexOf("_")+1,url.lastIndexOf(".html"));
						int vid = com.youku.soku.util.MyUtil.decodeVideoId(enid);
						if (vid > 0)
						{
							//获取到site和视频id
							sid.append(14).append("_").append(vid).append(",");

							//记录到数据库
							MovielogManager.getInstance().create("t_video",vid,"delete");
						}
					}
					else
					{
						List<Domain> domains = DomainManager.getInstance().getDomainsByUrl(url);
						if (domains != null && domains.size()>0)
						{
							Domain domain = domains.get(0);
							int site_id = domain.getSiteId();
							Site site = SiteManager.getInstance().getSite(site_id);
							if (site != null)
							{
								String table = site.getTablename();
								int vid = getVid(table,url);
								if (vid > 0)
								{
									//获取到site和视频id
									sid.append(site_id).append("_").append(vid).append(",");

									//更新数据库状态
									updateState(table,url);
									
									//记录到数据库
									MovielogManager.getInstance().create(table,vid,"delete");
								}
							}
						}
					}
				}
			}
		
	}
}

_log.info("delete vids:"+sid);

if (sid.length()>0)
{
	String param = sid.substring(0,sid.length()-1);

	

	for (int i=1;i<=group_count;i++){
		List<Server> servers = ServerManager.getVideoServers(i);
		if (servers == null || servers.isEmpty()){
			_log.error("server 为空");
			continue;
		}

		for (Server server:servers)
		{
			_log.info("http://"+server.getIp()+"/index/delete_byurl_client.jsp?vids="+param);

			Request.requestGet("http://"+server.getIp()+"/index/delete_byurl_client.jsp?vids="+param);
		}
	}	
}
%>
ok