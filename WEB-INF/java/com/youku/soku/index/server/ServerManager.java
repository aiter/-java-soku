/**
 * 
 */
package com.youku.soku.index.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public class ServerManager {
	
	private static HashMap<String,List<Server>> groupMap = new HashMap<String,List<Server>>();
	private static Configuration config = null;
	private static int groupCount = 0;
	
	private static final String[] types = new String[]{"video","word"};
	
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
					for (String type:types){
						List<Server> list = new ArrayList<Server>();
						
						Object ob = config.getProperty("groups.group("+i+")."+type+".server[@ip]");
						if (ob instanceof Collection){
							Collection servers  = (Collection)ob;
							if (servers != null && servers.size() > 0)
							{
								for (int j = 0;j<servers.size();j++)
								{
									Server server = new Server();
									server.setIp(config.getString("groups.group("+i+")."+type+".server("+j+")[@ip]"));
									server.setPoolport( DataFormat.parseInt(config.getString("groups.group("+i+")."+type+".server("+j+").pool_port")));
									server.setGroup(i+1);
									list.add(server);
								}
								
							}
						}
						else if (ob instanceof String) {
							Server server = new Server();
							server.setIp((String)ob);
							server.setPoolport( DataFormat.parseInt(config.getString("groups.group("+i+")."+type+".server.pool_port")));
							server.setGroup(i+1);
							list.add(server);
						}
						groupMap.put(type +"_"+ (i+1),list);
					}
				}
			}
		}
		else if (o instanceof String) //1组机器
		{
			groupCount = 1;
			
			for (String type:types){
				Object ob = config.getProperty("groups.group."+type+".server[@ip]");
				List<Server> list = new ArrayList<Server>();
				if (ob instanceof Collection){
					Collection servers  = (Collection)ob;
					if (servers != null && servers.size() > 0)
					{
						for (int j = 0;j<servers.size();j++)
						{
							Server server = new Server();
							server.setIp(config.getString("groups.group."+type+".server("+j+")[@ip]"));
							server.setPoolport( DataFormat.parseInt(config.getString("groups.group."+type+".server("+j+").pool_port")));
							server.setGroup(1);
							list.add(server);
						}
					}
				}
				else if (ob instanceof String) 
				{
					Server server = new Server();
					server.setIp((String)ob);
					server.setPoolport( DataFormat.parseInt(config.getString("groups.group."+type+".server.pool_port")));
					server.setGroup(1);
					list.add(server);
				}
				groupMap.put(type+"_"+1,list);
			}
		}
		else
		{
			System.out.println("error:index-servers.xml is not valid!");
			return;
		}
		
	}
	
	public static int getGroupCount()
	{
		return groupCount;
	}
	
	/**
	 * 获取某组所有索引服务器
	 * @param groupNumber
	 * @return
	 */
	public static List<Server> getVideoServers(int groupNumber)
	{
		return groupMap.get("video_"+groupNumber);
	}
	public static List<Server> getWordServers(int groupNumber)
	{
		return groupMap.get("word_"+groupNumber);
	}
	
	public static void main(String[] args)
	{
		try {
			ServerManager.init("E:/work/youku/search/src/WEB-INF/soku-conf/index-servers.xml");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		List<Server> lists = ServerManager.getVideoServers(2);
		for (Server server:lists)
		{
			System.out.println(server.getIp());
		}
	}
	

}
