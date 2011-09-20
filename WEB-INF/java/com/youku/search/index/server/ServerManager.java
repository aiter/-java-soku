/**
 * 
 */
package com.youku.search.index.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public class ServerManager {
	
	private static HashMap<String,List<Server>> groupMap = new HashMap<String,List<Server>>();
	private static HashMap<Integer,List<Server>> onlineMap = new HashMap<Integer,List<Server>>();
	private static Configuration config = null;
	private static int groupCount = 0;
	
	public static void init(String configPath) throws ConfigurationException{
		config = new XMLConfiguration(configPath);
		
		Object o = config.getProperty("groups.group[@num]");
		Collection prop = null;
		
		if (o instanceof Collection)
		{
			prop = (Collection)o;
			groupCount = prop.size();
			
			if (prop != null && groupCount >0)
			{
				for (int i = 0;i<groupCount;i++)
				{
					List<Server> videoList = new ArrayList<Server>();
					List<Server> folderList = new ArrayList<Server>();
					List<Server> barList = new ArrayList<Server>();
					List<Server> userList = new ArrayList<Server>();
					List<Server> ringList = new ArrayList<Server>();
					List<Server> statList = new ArrayList<Server>();
					
					Collection servers  = (Collection)config.getProperty("groups.group("+i+").server[@ip]");
					if (servers != null && servers.size() > 0)
					{
						for (int j = 0;j<servers.size();j++)
						{
							String type = config.getString("groups.group("+i+").server("+j+")[@type]");
							Server server = new Server();
							server.setIp(config.getString("groups.group("+i+").server("+j+")[@ip]"));
							server.setBak(DataFormat.parseInt(config.getString("groups.group("+i+").server("+j+")[@bak]")));
							server.setOrder(DataFormat.parseInt(config.getString("groups.group("+i+").server("+j+")[@order]")));
							server.setPoolport( DataFormat.parseInt(config.getString("groups.group("+i+").server("+j+").pool_port")));
							server.setStart( DataFormat.parseInt(config.getString("groups.group(0).server("+j+").start")));
							server.setEnd( DataFormat.parseInt(config.getString("groups.group(0).server("+j+").end")));
							server.setManager( DataFormat.parseInt(config.getString("groups.group(0).server("+j+").manager")));
							server.setGroup(i+1);
							
							if (type .equals("video")){
								if (server.getBak() == 0)
									videoList.add(server);
							}
							else if (type .equals("folder")){
								folderList.add(server);
							}
							else if (type .equals("bar")){
								barList.add(server);
							}
							else if (type .equals("ring")){
								ringList.add(server);
							}
							else if (type .equals("user")){
								userList.add(server);
							}
							else if (type .equals("stat")){
								statList.add(server);
							}
						}
						groupMap.put("video_" + (i+1),videoList);
						groupMap.put("folder_" + (i+1),folderList);
						groupMap.put("bar_" + (i+1),barList);
						groupMap.put("ring_" + (i+1),ringList);
						groupMap.put("user_" + (i+1),userList);
						groupMap.put("stat_" + (i+1),statList);
						
					}
					
					List<Server> online = new ArrayList<Server>();
					online.addAll(videoList);
					onlineMap.put(i+1,online);
				}
			}
		}
		else if (o instanceof String) //1组机器
		{
			groupCount = 1;
			
			Collection servers  = (Collection)config.getProperty("groups.group.server[@ip]");
			if (servers != null && servers.size() > 0)
			{
				List<Server> videoList = new ArrayList<Server>();
				List<Server> folderList = new ArrayList<Server>();
				List<Server> barList = new ArrayList<Server>();
				List<Server> userList = new ArrayList<Server>();
				List<Server> ringList = new ArrayList<Server>();
				List<Server> statList = new ArrayList<Server>();
				for (int j = 0;j<servers.size();j++)
				{
					String type = config.getString("groups.group.server("+j+")[@type]");
					Server server = new Server();
					server.setIp(config.getString("groups.group.server("+j+")[@ip]"));
					server.setBak(DataFormat.parseInt(config.getString("groups.group.server("+j+")[@bak]")));
					server.setOrder(DataFormat.parseInt(config.getString("groups.group.server("+j+")[@order]")));
					server.setPoolport( DataFormat.parseInt(config.getString("groups.group.server("+j+").pool_port")));
					server.setStart( DataFormat.parseInt(config.getString("groups.group.server("+j+").start")));
					server.setEnd( DataFormat.parseInt(config.getString("groups.group.server("+j+").end")));
					server.setManager( DataFormat.parseInt(config.getString("groups.group.server("+j+").manager")));
					server.setGroup(1);
					
					if (type .equals("video")){
						if (server.getBak() == 0)
							videoList.add(server);
					}
					else if (type .equals("folder")){
						folderList.add(server);
					}
					else if (type .equals("bar")){
						barList.add(server);
					}
					else if (type .equals("ring")){
						ringList.add(server);
					}
					else if (type .equals("user")){
						userList.add(server);
					}
					else if (type .equals("stat")){
						statList.add(server);
					}
				}
				groupMap.put("video_" + 1,videoList);
				groupMap.put("folder_" + 1,folderList);
				groupMap.put("bar_" + 1,barList);
				groupMap.put("ring_" + 1,ringList);
				groupMap.put("user_" + 1,userList);
				groupMap.put("stat_" + 1,statList);
				
				List<Server> online = new ArrayList<Server>();
				online.addAll(videoList);
				onlineMap.put(1,online);
			}
		}
		else
		{
			System.out.println("error:index-servers.xml is not valid!");
			return;
		}
		
		//管理服务器
		List<Server> managerList = new ArrayList<Server>();
		String[] manager_ip = config.getStringArray("managers.server[@ip]");
		String[] manager_order = config.getStringArray("managers.server[@order]");
		for (int i = 0;i<manager_ip.length;i++)
		{
			Server server = new Server();
			server.setIp(manager_ip[i]);
			server.setOrder(DataFormat.parseInt(manager_order[i]));
			managerList.add(server);
		}
		groupMap.put("manager",managerList);
	}
	
	public static int getGroupCount()
	{
		return groupCount;
	}
	
	public static List<Server> getVideoServers(int groupNumber)
	{
		return groupMap.get("video_" + groupNumber);
	}
	
	public static List<Server> getVideoServers()
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			list.addAll(groupMap.get("video_"+i));
		}
		return list;
	}
	public static List<Server> getFolderServers(int groupNumber)
	{
		return groupMap.get("folder_" + groupNumber);
	}
	public static List<Server> getFolderServers()
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			list.addAll(groupMap.get("folder_"+i));
		}
		return list;
	}
	public static List<Server> getBarServers(int groupNumber)
	{
		return groupMap.get("bar_" + groupNumber);
	}
	public static List<Server> getBarServers()
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			list.addAll(groupMap.get("bar_"+i));
		}
		return list;
	}
	public static List<Server> getUserServers(int groupNumber)
	{
		return groupMap.get("user_" + groupNumber);
	}
	
	public static List<Server> getUserServers()
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			list.addAll(groupMap.get("user_"+i));
		}
		return list;
	}
	
	public static List<Server> getPkServers(int groupNumber)
	{
		return groupMap.get("bar_" + groupNumber);
	}
	public static List<Server> getPkServers()
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			list.addAll(groupMap.get("bar_"+i));
		}
		return list;
	}
	
	public static List<Server> getStatServers(int groupNumber)
	{
		return groupMap.get("stat_" + groupNumber);
	}
	public static List<Server> getStatServers()
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			list.addAll(groupMap.get("stat_"+i));
		}
		return list;
	}
	
	public static List<Server> getRingServers(int groupNumber)
	{
		return groupMap.get("ring_" + groupNumber);
	}
	public static List<Server> getRingServers()
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			list.addAll(groupMap.get("ring_"+i));
		}
		return list;
	}
	
	public static List<Server> getManagerServers()
	{
		return groupMap.get("manager" );
	}
	
	public static String getVideoIndexPath()
	{
		return config.getString("indexpath.video");
	}
	public static String getFolderIndexPath()
	{
		return config.getString("indexpath.folder");
	}
	public static String getUserIndexPath()
	{
		return config.getString("indexpath.user");
	}
	public static String getPkIndexPath()
	{
		return config.getString("indexpath.pk");
	}
	public static String getBarpostIndexPath()
	{
		return config.getString("indexpath.barpost");
	}
	public static String getStatIndexPath()
	{
		return config.getString("indexpath.stat");
	}
	public static String getRingIndexPath()
	{
		return config.getString("indexpath.ring");
	}
	public static String getBarIndexPath()
	{
		return config.getString("indexpath.bar");
	}
	public static Server getManagerServer(int order,int type)
	{
		List<Server> managers = getManagerServers();
		Server server = getServer(1,type,order);
		if (server != null )
		{
			if (server.getManager()==1)
			{
				return managers.get(0);
			}
			else if (server.getManager()==2)
			{
				return managers.get(1);
			}
		}
		return null;
	}
	
	public static Server getServer(int group,int type,int order)
	{
		List<Server> servers = null;
		if (type == Constant.QueryField.VIDEO){
			servers = getVideoServers(group);
		}
		else if (type == Constant.QueryField.FOLDER){
			servers = getFolderServers(group);
		}
		else if (type == Constant.QueryField.MEMBER){
			servers = getUserServers(group);
		}
		else if (type == Constant.QueryField.PK 
				|| type == Constant.QueryField.BARPOST_SUBJECT 
				|| type == Constant.QueryField.STAT
				|| type == Constant.QueryField.BAR){
			servers = getBarServers(group);
		}
		else if (type == Constant.QueryField.RING){
			servers = getRingServers(group);
		}
		else
			return null;
		for (int j= 0;j<servers.size();j++)
		{
			Server server = servers.get(j);
			if (server.getOrder() == order)
				return server;
		}
		return null;
	}
	
	public static List<Server> getServersByOrder(int type,int order)
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			List<Server> servers = null;
			if (type == Constant.QueryField.VIDEO){
				servers = getVideoServers(i);
			}
			else if (type == Constant.QueryField.FOLDER){
				servers = getFolderServers(i);
			}
			else if (type == Constant.QueryField.MEMBER){
				servers = getUserServers(i);
			}
			else if (type == Constant.QueryField.PK 
					|| type == Constant.QueryField.BARPOST_SUBJECT 
					|| type == Constant.QueryField.STAT
					|| type == Constant.QueryField.BAR){
				servers = getBarServers(i);
			}
			else if (type == Constant.QueryField.RING){
				servers = getRingServers(i);
			}
			else
				return null;
			
			for (int j= 0;j<servers.size();j++)
			{
				Server server = servers.get(j);
				if (server.getOrder() == order){
					list.add(server);
					break;
				}
			}
		}
		return list;
	}
	
	public static List<Server> getServersById(int id,int type)
	{
		List<Server> list = new ArrayList<Server>();
		for (int i=1;i<=groupCount;i++)
		{
			List<Server> servers = null;
			if (type == Constant.QueryField.VIDEO){
				servers = getVideoServers(i);
			}
			else if (type == Constant.QueryField.FOLDER){
				servers = getFolderServers(i);
			}
			else if (type == Constant.QueryField.MEMBER){
				servers = getUserServers(i);
			}
			else if (type == Constant.QueryField.PK 
					|| type == Constant.QueryField.BARPOST_SUBJECT 
					|| type == Constant.QueryField.STAT
					|| type == Constant.QueryField.BAR){
				servers = getBarServers(i);
			}
			else if (type == Constant.QueryField.RING){
				servers = getRingServers(i);
			}
			else
				return null;
			
			for (int j= 0;j<servers.size();j++)
			{
				Server server = servers.get(j);
				if (server.getStart() <= id && server.getEnd() >id){
					list.add(server);
					break;
				}
			}
		}
		return list;
	}
	
	
	public static List<Server> getNewBarpostServer() {
		List<Server> newBarpostServers = new ArrayList<Server>(); //最新看吧帖子服务器
		for (int i =1;i<=groupCount;i++)
		{
			List<Server> servers = getBarServers(i);
			newBarpostServers.add(servers.get(servers.size()-1));
		}
		return newBarpostServers;
	}

	public static List<Server> getNewFolderServer() {
		List<Server> newFolderServers = new ArrayList<Server>(); //最新专辑服务器
		for (int i =1;i<=groupCount;i++)
		{
			List<Server> servers = getFolderServers(i);
			newFolderServers.add(servers.get(servers.size()-1));
		}
		return newFolderServers;
	}

	public static List<Server> getNewVideoServers() {
		List<Server> newVideoServers = new ArrayList<Server>(); //最新视频服务器
		for (int i =1;i<=groupCount;i++)
		{
			List<Server> servers = getVideoServers(i);
			newVideoServers.add(servers.get(servers.size()-1));
		}
		return newVideoServers;
	}
	
	public static List<Server> getNewRingServers() {
		List<Server> newRingServers = new ArrayList<Server>(); //最新服务器
		for (int i =1;i<=groupCount;i++)
		{
			List<Server> servers = getRingServers(i);
			newRingServers.add(servers.get(servers.size()-1));
		}
		return newRingServers;
	}
	public static List<Server> getNewUserServers() {
		List<Server> newUserServers = new ArrayList<Server>(); //最新服务器
		for (int i =1;i<=groupCount;i++)
		{
			List<Server> servers = getUserServers(i);
			newUserServers.add(servers.get(servers.size()-1));
		}
		return newUserServers;
	}
	
	/**
	 * 设置现在正在使用的视频server，切换索引使用
	 * @param group
	 * @param useBak
	 * @return
	 */
	public static void setOnlineVideoServers(int group,boolean useBak)
	{
		List<Server> videoList = groupMap.get("video_" + group);
		List<Server> bakList = groupMap.get("video_bak_" + group);
		List<Server> onlineVideoServers = onlineMap.get(group);
		
		onlineVideoServers.remove(onlineVideoServers.size()-1);
		
		if (useBak){
			onlineVideoServers.add(bakList.get(0));  //切换服务器只有1台
		}
		else{
			onlineVideoServers.add(videoList.get(videoList.size()-1)); //使用正常列表的最后一台
		}	
	}
	
	public static List<Server> getOnlineVideoServers(int group)
	{
		return onlineMap.get(group);
	}
	
	
	public static void main(String[] args)
	{
		try {
			ServerManager.init("E:/work/youku/search/src/WEB-INF/conf/index-servers.xml");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Server> servers = ServerManager.getServersById(30100392,1);
		for (int i=0;i<servers.size();i++)
		{
			Server server = servers.get(i);
			System.out.println(server.getIp() + ":order=" +server.getOrder());
//			System.out.println("线程启动参数：num="+server.getOrder() + ";start=" + server.getStart() + ";end=" + server.getEnd() );
		}
	}
	

}
