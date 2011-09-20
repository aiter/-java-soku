package com.youku.search.index.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.youku.search.config.Config;
import com.youku.search.config.XMLConfigReader;
import com.youku.search.util.StringUtil;

public class CServerManager {
	
	private static Map<Integer, CServer> md5ServerMap;
	
	/**
	 * Map&lt;groupId, Map&lt;serverId_majorIp_minorIp, CServerPair&gt;&gt;
	 */
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, CServerPair>> cachedServerPairMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, CServerPair>>();

	/**
	 * Map&lt;CServer, InetSocketAddress&gt;
	 */
	private static ConcurrentHashMap<CServer, InetSocketAddress> cachedAddressesMap = new ConcurrentHashMap<CServer, InetSocketAddress>();

	/**
	 * Map&lt;serverId_serverIp, CServer&gt;
	 */
	private static ConcurrentHashMap<String, CServer> cachedServersMap = new ConcurrentHashMap<String, CServer>();

	/**
	 * Map&lt;groupId, port&gt;
	 */
	private static Map<Integer, Integer> groupDefaultPort = new HashMap<Integer, Integer>();

	/**
	 * Map&lt;groupId, XMLElement&gt;
	 */
	private static Map<Integer, Element> groupElementMap = new HashMap<Integer, Element>();

	private static XMLConfigReader xmlReader = new XMLConfigReader();

	public static File startChangeFlagFile;

	public static File endChangeFlagFile;

	public static File changeListFile;

	private static Log logger = LogFactory.getLog(CServerManager.class);

	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static String configFilePath;

	/**
	 * @param configPath
	 * @throws DocumentException
	 */
	public static void init(String configPath) throws DocumentException {
		xmlReader.setEncoding(DEFAULT_ENCODING);
		xmlReader.load(configPath);
		configFilePath = configPath;
		
		Map<Integer, CServer> md5Map = new HashMap<Integer, CServer>();
		
		List<Element> groupList = xmlReader
				.getMultiElement("//config/groups/group");
		for (Element group : groupList) {
			Integer groupId = Integer.parseInt(group.attributeValue("num"));
			
			Element md5ServerElement = group.element("md5Server");
			if (null != md5ServerElement) {
				String md5ServerId = md5ServerElement.attributeValue("id");
				String md5ServerIp = md5ServerElement.attributeValue("ip");
				int md5ServerPort = Integer.parseInt(md5ServerElement.attributeValue("port"));
				CServer md5Server = new CServer(groupId, md5ServerId, md5ServerIp, md5ServerPort);
				md5Map.put(groupId, md5Server);
			}
			
			ConcurrentHashMap<String, CServerPair> oneGroup = new ConcurrentHashMap<String, CServerPair>();
			List<Element> serverList = group.elements("server");
			int defaultPort = Integer.parseInt(group
					.attributeValue("defaultPort"));
			groupDefaultPort.put(groupId, defaultPort);
			groupElementMap.put(groupId, group);
			for (Element server : serverList) {
				String serverId = server.attributeValue("id");

				String majorIp = server.element("major").attributeValue("ip");
				int majorPort = Integer.parseInt(server.element("major")
						.attributeValue("port", String.valueOf(defaultPort)));

				String minorIp = server.element("minor").attributeValue("ip");
				int minorPort = Integer.parseInt(server.element("minor")
						.attributeValue("port", String.valueOf(defaultPort)));

				CServer majorServer = new CServer(groupId, serverId, majorIp, majorPort);
				CServer minorServer = new CServer(groupId, serverId, minorIp, minorPort);
				CServerPair serverPair = new CServerPair(groupId, serverId, majorServer, minorServer);
				String serverPairKey = getServerPairKey(serverId, majorIp, minorIp);

				// init cached ServerPair Map
				oneGroup.put(serverPairKey, serverPair);

				// init cached InetSocketAddress map
				getAddress(majorServer);
				getAddress(minorServer);

				// init cached Servers Map
				cachedServersMap.put(getServerKey(serverId, majorIp), majorServer);
				cachedServersMap.put(getServerKey(serverId, minorIp), minorServer);
			}

			cachedServerPairMap.put(groupId, oneGroup);
		}
		
		md5ServerMap = Collections.unmodifiableMap(md5Map);

		Element changeServerFiles = xmlReader.getSingleElement("//config/changeServerFiles");
		String startFlagPath = changeServerFiles.elementTextTrim("startFlag");
		String endFlagPath = changeServerFiles.elementTextTrim("endFlag");
		String changeListPath = changeServerFiles.elementTextTrim("changeList");

		startChangeFlagFile = new File(startFlagPath);
		endChangeFlagFile = new File(endFlagPath);
		changeListFile = new File(changeListPath);
	}

	public static void save(Collection<CServerPair> changeServers) throws IOException {
		// 为了写入c-server.xml文件比较好看，需要做一下排序
		List<CServerPair> sortedChangeServers = new ArrayList<CServerPair>(changeServers.size());
		sortedChangeServers.addAll(changeServers);
		Collections.sort(sortedChangeServers, new Comparator<CServerPair>() {
			@Override
			public int compare(CServerPair c1, CServerPair c2) {
				int c1Int = Integer.parseInt(c1.getId());
				int c2Int = Integer.parseInt(c2.getId());
				if (c1Int < c2Int) {
					return -1;
				} else if (c1Int > c2Int) {
					return 1;
				} else {
					return 0;
				}
				
			}
		});
		
		// create group Element
		Element newGroup = DocumentHelper.createElement("group");
		newGroup.addAttribute("num", String.valueOf(Config.getGroupNumber()));
		newGroup.addAttribute("defaultPort", String.valueOf(getDefaultPort()));
		
		// create server list for newGroup
		for (CServerPair serverPair : sortedChangeServers) {
			Element serverPairElement = DocumentHelper.createElement("server");
			serverPairElement.addAttribute("id", serverPair.getId());

			Element majorServerElement = DocumentHelper.createElement("major");
			majorServerElement.addAttribute("ip", serverPair.getMajorServer().getIp());
			majorServerElement.addAttribute("port",String.valueOf(serverPair.getMajorServer().getPort()));
			
			Element minorServerElement = DocumentHelper.createElement("minor");
			minorServerElement.addAttribute("ip", serverPair.getMinorServer().getIp());
			minorServerElement.addAttribute("port",String.valueOf(serverPair.getMinorServer().getPort()));
			
			serverPairElement.add(majorServerElement);
			serverPairElement.add(minorServerElement);
			
			newGroup.add(serverPairElement);
		}
		
		// remove oldGroup Element and add newGroup Element to Document
		Element groups = xmlReader.getSingleElement("//config/groups");
		Element oldGroup = groupElementMap.get(Config.getGroupNumber());
		
		Element md5ServerElement = oldGroup.element("md5Server");
		oldGroup.remove(md5ServerElement);
		newGroup.add(md5ServerElement);
		
		groups.remove(oldGroup);
		groups.add(newGroup);
		
		groupElementMap.put(Config.getGroupNumber(), newGroup);
		
		// save file
		xmlReader.write(configFilePath);
	}
	
	private static int getDefaultPort() {
		return groupDefaultPort.get(Config.getGroupNumber());
	}

	private static ConcurrentHashMap<String, CServerPair> getServerPairMap() {
		return cachedServerPairMap.get(Config.getGroupNumber());
	}

	public static String getServerKey(String serverId, String serverIp) {
		return serverId + "_" + serverIp;
	}

	public static String getServerPairKey(String serverId, String majorIp,
			String minorIp) {
		return serverId + "_" + majorIp + "_" + minorIp;
	}

	public static CServerPair getServerPair(String serverId, String majorIp,
			String minorIp) {
		ConcurrentHashMap<String, CServerPair> serverPairMap = getServerPairMap();
		String serverPairKey = getServerPairKey(serverId, majorIp, minorIp);
		if (!serverPairMap.containsKey(serverPairKey)) {
			CServer majorServer = getServer(serverId, majorIp);
			CServer minorServer = getServer(serverId, minorIp);
			CServerPair serverPair = new CServerPair(Config.getGroupNumber(),
					serverId, majorServer, minorServer);
			serverPairMap.putIfAbsent(serverPairKey, serverPair);
		}
		
		return serverPairMap.get(serverPairKey);
	}

	public static CServer getServer(String serverId, String serverIp) {
		String serverKey = getServerKey(serverId, serverIp);
		if (!cachedServersMap.containsKey(serverKey)) {
			CServer server = new CServer(Config.getGroupNumber(), serverId,
					serverIp, getDefaultPort());
			cachedServersMap.putIfAbsent(serverKey, server);
		}

		return cachedServersMap.get(serverKey);
	}
	
	public static InetSocketAddress[] getMD5ServerAddress(){
		CServer md5Server = md5ServerMap.get(Config.getGroupNumber());
		return new InetSocketAddress[]{getAddress(md5Server)};
	}

	/**
	 * 得到当前在线的Servers的信息
	 * 
	 * @param onLineServers
	 * @return
	 */
	public static String getOnlineServerDescription(Collection<CServerPair> onLineServers) {
		StringBuilder sb = new StringBuilder();
		for (CServerPair serverPair : onLineServers) {
			sb.append(serverPair.toStringForLog()).append('\n');
		}
		return sb.toString();
	}

	public static Collection<CServerPair> getInitOnlineServers() {
		return getServerPairMap().values();
	}

	/**
	 * 根据当前在线的ServerPair集合随机取得一个Socket数组
	 * 
	 * @return
	 */
	public static InetSocketAddress[] getOnlineServerSockets(Collection<CServerPair> onLineServerPair) {
		int serverCount = onLineServerPair.size();
		int[] randomBit = StringUtil.getRandomBitInt(serverCount);

		InetSocketAddress[] results = new InetSocketAddress[serverCount];
		int i = 0;
		for (Iterator<CServerPair> iterator = onLineServerPair.iterator(); iterator
				.hasNext(); i++) {
			CServerPair serverPair = iterator.next();
			CServer server = serverPair.getRandomServer(randomBit[i]);
			if (server.getIsFall()) {
				randomBit[i] = (randomBit[i] == 0) ? 1 : 0;
				server = serverPair.getRandomServer(randomBit[i]);
			}
			
			results[i] = getAddress(server);
		}

		logger.debug("------ 预取的随机Bit=" + Arrays.toString(randomBit));

		return results;
	}
	
	public static InetSocketAddress getAddress(CServer server) {
		if (!cachedAddressesMap.containsKey(server)) {
			InetSocketAddress address = new InetSocketAddress(server.getIp(), server.getPort());
			cachedAddressesMap.putIfAbsent(server, address);
		}
		
		return cachedAddressesMap.get(server);
	}
	
	public static List<CServer> transformServerPairList(Collection<CServerPair> serverPairList){
		List<CServer> servers = new ArrayList<CServer>(serverPairList.size() * 2);
		for (CServerPair serverPair : serverPairList) {
			servers.add(serverPair.getMajorServer());
			servers.add(serverPair.getMinorServer());
		}
		
		return servers;
	}
	
	public static void main(String[] args) throws ConfigurationException, DocumentException, IOException {
		String CONFIG_PATH_PREFIX = "conf" + File.separatorChar;
		DOMConfigurator.configure(CONFIG_PATH_PREFIX + "log4j-test.xml");
		
		Config.init(CONFIG_PATH_PREFIX + "config.xml");
		CServerManager.init(CONFIG_PATH_PREFIX + "c-servers.xml");
		
		System.out.println("--- 初始化加载c-servers ---");
		System.out.println(CServerManager.getOnlineServerDescription(CServerManager.getInitOnlineServers()));
		
		System.out.println("--- 第一次改动后的c-servers ---");
		Collection<CServerPair> initServerPairs = CServerManager.getInitOnlineServers();
		List<CServerPair> changeServerList = new LinkedList<CServerPair>();
		CServerPair c1 = new CServerPair(1, "0", new CServer(1, "0", "111.111.111.111", 1111), new CServer(1, "0", "222.222.222.222", 2222));
		CServerPair c2 = new CServerPair(1, "5", new CServer(1, "5", "222.222.222.222", 3333), new CServer(1, "5", "333.333.333.333", 4444));
		for (CServerPair serverPair : initServerPairs) {
			if (serverPair.getId().equalsIgnoreCase("0") || serverPair.getId().equalsIgnoreCase("5")) {
				continue;
			}
			changeServerList.add(serverPair);
		}
		changeServerList.add(c1);
		changeServerList.add(c2);
		
		CServerManager.save(changeServerList);
		System.out.println(CServerManager.getOnlineServerDescription(changeServerList));
		
		System.out.println("--- 第二次改动后的c-servers ---");
		List<CServerPair> change2ServerList = new LinkedList<CServerPair>();
		c1 = new CServerPair(1, "3", new CServer(1, "3", "333.333.333.333", 1111), new CServer(1, "3", "444.444.444.444", 2222));
		c2 = new CServerPair(1, "7", new CServer(1, "7", "444.444.444.444", 3333), new CServer(1, "7", "555.555.555.555", 4444));
		for (CServerPair serverPair : changeServerList) {
			if (serverPair.getId().equalsIgnoreCase("3") || serverPair.getId().equalsIgnoreCase("7")) {
				continue;
			}
			change2ServerList.add(serverPair);
		}
		change2ServerList.add(c1);
		change2ServerList.add(c2);
		
		CServerManager.save(change2ServerList);
		System.out.println(CServerManager.getOnlineServerDescription(change2ServerList));
	}
	
}
