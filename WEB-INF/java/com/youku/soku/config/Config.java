package com.youku.soku.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;

public class Config {
	private static Configuration config = null;

	private static String videoIndexPath;
	private static String wordIndexPath;
	private static int serverType = 0;
	private static String analyzerServer = null; // 分词服务器
	private static int group = -1;
	private static int cacheTimeOut = -1;

	private static int duplicateStatus = -1;
	private static int relevantWordStatus = -1;
	private static int suggestionStatus = -1;
	private static int picInfosStatus = -1;

	private static String indexType = null;
	private static String[] controlServers = null;
	private static String[] autoCompleteServers = null;

	private static String youku_host = null;
	private static String[] soku_host = null;

	private static String middleTierOfflineHost = null;
	private static String middleTierOnlineHost = null;
	
	private static String ipFile = null;
	private static String speedFile = null;

	private Config() {
	}

	public static void init(String configPath) throws ConfigurationException {
		ConfigurationFactory factory = new ConfigurationFactory(configPath);
		config = factory.getConfiguration();
	}

	public static Configuration getConfiguration() {
		return config;
	}

	public static String getAnalyzerServer() {
		if (null == analyzerServer)
			analyzerServer = config.getString("analyzer.server");
		return analyzerServer;
	}

	public static String getVideoIndexPath() {
		if (null == videoIndexPath)
			videoIndexPath = config.getString("indexpath.video");
		return videoIndexPath;
	}
	
	public static String getIpFile() {
		if (null == ipFile)
			ipFile = config.getString("ip_file");
		return ipFile;
	}
	public static String getSpeedFile() {
		if (null == speedFile)
			speedFile = config.getString("speed_file");
		return speedFile;
	}
	
	public static String getWordIndexPath() {
		if (null == wordIndexPath)
			wordIndexPath = config.getString("indexpath.word");
		return wordIndexPath;
	}

	public static String getLibraryIndexPath() {
		if (null == wordIndexPath)
			wordIndexPath = config.getString("indexpath.library");
		return wordIndexPath;
	}

	public static int getServerType() {
		if (serverType == 0)
			serverType = config.getInt("server_type");
		return serverType;
	}

	public static String getIndexType() {
		if (indexType == null)
			indexType = config.getString("index_type");
		return indexType;
	}

	public static int getGroupNumber() {
		if (group == -1) {
			group = config.getInt("group");
			if (group < 0)
				group = 0;
		}
		return group;
	}

	public static int getCacheTimeOut() {
		if (cacheTimeOut == -1) {
			cacheTimeOut = config.getInt("cache_timeout");
			if (cacheTimeOut < 0) {
				cacheTimeOut = 60 * 10;
			}
		}
		return cacheTimeOut;
	}

	public static int getDuplicateStatus() {
		if (duplicateStatus == -1) {
			duplicateStatus = config.getInt("duplicate_status");
			if (duplicateStatus < 0)
				duplicateStatus = 0;
		}
		return duplicateStatus;
	}

	public static int getRelevantWordStatus() {
		if (relevantWordStatus == -1) {
			relevantWordStatus = config.getInt("relevant_word_status");
			if (relevantWordStatus < 0) {
				relevantWordStatus = 0;
			}
		}
		return relevantWordStatus;
	}

	public static int getSuggestionStatus() {
		if (suggestionStatus == -1) {
			suggestionStatus = config.getInt("suggestion_status");
			if (suggestionStatus < 0) {
				suggestionStatus = 0;
			}
		}
		return suggestionStatus;
	}

	public static int getPicInfosStatus() {
		if (picInfosStatus == -1) {
			picInfosStatus = config.getInt("pic_infos_status");
			if (picInfosStatus < 0) {
				picInfosStatus = 0;
			}
		}
		return picInfosStatus;
	}

	public static String[] getControlServers() {
		if (controlServers == null) {
			controlServers = config.getStringArray("control_server");
		}
		return controlServers;
	}

	public static String[] getAutoCompleteServers() {
		if (autoCompleteServers == null) {
			autoCompleteServers = config
					.getStringArray("auto_complete_servers");
		}
		return autoCompleteServers;
	}

	public static String getYoukuHost() {
		if (null == youku_host)
			youku_host = config.getString("host.youku.server");
		return youku_host;
	}

	public static String[] getSokuHosts() {
		if (null == soku_host)
			soku_host = config.getStringArray("host.soku.server");
		return soku_host;
	}

	public static String getSokuHost() {
		if (null == soku_host) {
			getSokuHosts();
		}
		if (null != soku_host) {
			int index = (int) (Math.random() * soku_host.length);
			return soku_host[index];
		} else {
			return null;
		}
	}

	public static String getMiddleTierOfflineHost() {
		if (null == middleTierOfflineHost)
			middleTierOfflineHost = config
					.getString("host.middle-tier-offline.server");
		return middleTierOfflineHost;
	}

	public static String getMiddleTierOnlineHost() {
		if (null == middleTierOnlineHost)
			middleTierOnlineHost = config
					.getString("host.middle-tier-online.server");
		return middleTierOnlineHost;
	}

	public static void main(String[] args) {
		try {
			Config
					.init("/home/tanxiuguang/work/search/src/WEB-INF/soku-conf/config.xml");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Config.getControlServers()[1]);
		System.out.println(getMiddleTierOnlineHost());
		System.out.println(getMiddleTierOfflineHost());
	}
}
