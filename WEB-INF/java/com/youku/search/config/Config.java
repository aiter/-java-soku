package com.youku.search.config;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.server.Server;
import com.youku.search.index.server.ServerManager;
import com.youku.search.sort.servlet.ChangeParam;

public class Config {

    private static Log logger = LogFactory.getLog(Config.class);

    private static Configuration config = null;

    private static InetSocketAddress[] video_addresses = null;
    private static InetSocketAddress[] folder_addresses = null;
    private static InetSocketAddress[] user_addresses = null;
    private static InetSocketAddress[] pk_addresses = null;
    private static InetSocketAddress[] bar_addresses = null;
    private static InetSocketAddress[] ring_addresses = null;
    private static int video_index_start = 0;
    private static int video_index_end = 0;
    private static String wordsfile = null;
    private static String indexDir_online_video = null;
    private static String indexDir_temp_video = null;
    private static String indexDir_online_folder = null;
    private static String indexDir_temp_folder = null;
    private static String indexDir_bak = null;
    private static String analyzer_server = null; // 分词服务器

    private static int server_type = 0;
    private static String index_type = null;
    private static String youku_host = null;
    private static String[] soku_host = null;
    private static String[] soku_ext_host = null;

    // search sort configuration start
    private static String videoCategoryIds = "";
    private static String folderCategoryIds = "";
    private static String categoryTorqueName = "";
    private static String filterTorqueName = "";
    private static int filterUpdateFrq = -1;
    private static String recommendTorqueName = "";
    private static int cacheTimeOut = -1;
    private static int group = -1;
    private static int folderEmbededNum = -1;
    private static int duplicateStatus = -1;
    private static int recommendStatus = -1;
    private static int copyrightMusicStatus = -1;
    private static int filterStatus = -1;
    private static int suggestionStatus = -1;
    private static int relevantWordStatus = -1;
    private static int resortType = -1;
    
    // search sort configuration end
    
    private static String middleTierOfflineHost = null;
	private static String middleTierOnlineHost = null;

    private Config() {
    }

    public static void init(String configPath) throws ConfigurationException {
        ConfigurationFactory factory = new ConfigurationFactory(configPath);
        config = factory.getConfiguration();
    }

    public static Configuration getConfiguration() {
        return config;
    }

    public static void configChanged(ChangeParam param) {

        logger.info("receive change config request: " + param);

        final int group = getGroupNumber();
        if (group != param.group) {
            logger.warn("IGNORE: current group: " + group + "; request group: "
                    + param.group);
            return;
        }

        synchronized (config) {

            logger.info("HANDLE request: " + param + "; "
                    + Arrays.toString(video_addresses));

            ServerManager.setOnlineVideoServers(param.group, param.back);

            List<Server> servers = ServerManager.getOnlineVideoServers(group);

            video_addresses = getSockets(servers);

            logger.info("HANDLE request complete! " + param + "; "
                    + Arrays.toString(video_addresses));
        }
    }
    
    /**
     * 新的C-Server每次取得的VideoServer地址是动态的，所以此方法不应该再被使用
     * 
     * @deprecated
     */
    public static InetSocketAddress[] getVideoIndexSocket() {
        synchronized (config) {
            if (video_addresses == null) {
                List<Server> servers = ServerManager
                        .getOnlineVideoServers(getGroupNumber());

                video_addresses = getSockets(servers);
            }
        }
        return video_addresses;
    }

    public static InetSocketAddress[] getFolderIndexSocket() {
        synchronized (config) {
            if (folder_addresses == null) {
                List<Server> servers = ServerManager
                        .getFolderServers(getGroupNumber());

                folder_addresses = getSockets(servers);
            }
        }
        return folder_addresses;
    }

    public static InetSocketAddress[] getBarIndexSocket() {
        synchronized (config) {
            if (bar_addresses == null) {
                List<Server> servers = ServerManager
                        .getBarServers(getGroupNumber());

                bar_addresses = getSockets(servers);
            }
        }
        return bar_addresses;
    }

    public static InetSocketAddress[] getUserIndexSocket() {
        synchronized (config) {
            if (user_addresses == null) {
                List<Server> servers = ServerManager
                        .getUserServers(getGroupNumber());

                user_addresses = getSockets(servers);
            }
        }
        return user_addresses;
    }

    public static InetSocketAddress[] getPkIndexSocket() {
        synchronized (config) {
            if (pk_addresses == null) {
                List<Server> servers = ServerManager
                        .getPkServers(getGroupNumber());

                pk_addresses = getSockets(servers);
            }
        }
        return pk_addresses;
    }

    public static InetSocketAddress[] getRingIndexSocket() {
        synchronized (config) {
            if (ring_addresses == null) {
                List<Server> servers = ServerManager
                        .getRingServers(getGroupNumber());

                ring_addresses = getSockets(servers);
            }
        }
        return ring_addresses;
    }

    
    public static int getVideoIndexStart() {
        if (video_index_start == 0)
            video_index_start = config.getInt("video.start");
        return video_index_start;
    }

    public static int getVideoIndexEnd() {
        if (video_index_end == 0)
            video_index_end = config.getInt("video.end");
        return video_index_end;
    }

    public static String getWordsFile() {
        if (null == wordsfile)
            wordsfile = config.getString("wordsfile");
        return wordsfile;
    }

    public static String getVideoIndexDirOnline() {
        if (null == indexDir_online_video)
            indexDir_online_video = config.getString("video.indexDir.online");
        return indexDir_online_video;
    }

    public static String getVideoIndexDirTemp() {
        if (null == indexDir_temp_video)
            indexDir_temp_video = config.getString("video.indexDir.temp");
        return indexDir_temp_video;
    }

    public static String getFolderIndexDirOnline() {
        if (null == indexDir_online_folder)
            indexDir_online_folder = config.getString("folder.indexDir.online");
        return indexDir_online_folder;
    }

    public static String getFolderIndexDirTemp() {
        if (null == indexDir_temp_folder)
            indexDir_temp_folder = config.getString("folder.indexDir.temp");
        return indexDir_temp_folder;
    }

    public static String getIndexDirBak() {
        if (null == indexDir_bak)
            indexDir_bak = config.getString("video.indexDir.bak");
        return indexDir_bak;
    }

    public static String getAnalyzerServer() {
        if (null == analyzer_server)
            analyzer_server = config.getString("analyzer.server");
        return analyzer_server;
    }

    public static String getYoukuHost(){
    	 if (null == youku_host)
    		 youku_host = config.getString("host.youku.server");
         return youku_host;
    }
    public static String[] getSokuHosts(){
   	 if (null == soku_host)
   		soku_host = config.getStringArray("host.soku.server");
        return soku_host;
   }
    public static String getSokuHost(){
    	if ( null == soku_host ){
    		getSokuHosts();
    	}
    	if ( null != soku_host ){
    		int index= (int) (Math.random()*soku_host.length);
    		return soku_host[index];
    	}
    	else{
    		return null;
    	}
    }
    public static String[] getSokuExtHosts(){
      	 if (null == soku_ext_host)
      		soku_ext_host = config.getStringArray("host.soku-ext.server");
           return soku_ext_host;
      }
       public static String getSokuExtHost(){
       	if ( null == soku_ext_host ){
       		getSokuExtHosts();
       	}
       	if ( null != soku_ext_host ){
       		int index= (int) (Math.random()*soku_ext_host.length);
       		return soku_ext_host[index];
       	}
       	else{
       		return null;
       	}
       }
    
    // *********************************************************
    // sort parts:
    public static int getServerType() {
        if (server_type == 0)
            server_type = config.getInt("server_type");
        return server_type;
    }

    public static String getIndexType() {
        if (index_type == null)
            index_type = config.getString("index_type");
        return index_type;
    }
    
    public static int getResortType(){
    	if (resortType == -1) {
    		resortType = config.getInt("resort_type");
		}
    	return resortType;
    }

    public static String getVideoCategoryIds() {
        if (videoCategoryIds.length() <= 0)
            videoCategoryIds = config.getString("video_category_id");
        return videoCategoryIds;
    }

    public static String getFolderCategoryIds() {
        if (folderCategoryIds.length() <= 0)
            folderCategoryIds = config.getString("folder_category_id");
        return folderCategoryIds;
    }

    public static String getCategoryTorqueName() {
        if (categoryTorqueName.length() <= 0)
            categoryTorqueName = config.getString("category_torque");
        return categoryTorqueName;
    }

    public static String getRecommendTorqueName() {
        if (recommendTorqueName.length() <= 0)
            recommendTorqueName = config.getString("recommend_torque");
        return recommendTorqueName;
    }

    public static String getFilterTorqueName() {
        if (filterTorqueName.length() <= 0)
            filterTorqueName = config.getString("filter_torque");
        return filterTorqueName;
    }

    public static int getFilterUpdateFrq() {
        if (filterUpdateFrq < 0)
            filterUpdateFrq = config.getInt("filter_update_frequency");
        if (filterUpdateFrq < 0)
            filterUpdateFrq = 600;
        return filterUpdateFrq * 1000;
    }

    public static int getCacheTimeOut() {
        if (cacheTimeOut == -1) {
            cacheTimeOut = config.getInt("cache_timeout");
            if (cacheTimeOut < 0)
                cacheTimeOut = 60 * 10;
        }
        return cacheTimeOut;
    }

    public static int getGroupNumber() {
        if (group == -1) {
            group = config.getInt("group");
            if (group < 0)
                group = 0;
        }
        return group;
    }

    public static int getFolderNumEmbededVideoSearch() {
        if (folderEmbededNum == -1) {
            folderEmbededNum = config
                    .getInt("folder_number_embeded_video_search");
            if (folderEmbededNum < 0)
                folderEmbededNum = 0;
        }
        return folderEmbededNum;
    }

    public static int getDuplicateStatus() {
        if (duplicateStatus == -1) {
            duplicateStatus = config.getInt("duplicate_status");
            if (duplicateStatus < 0)
                duplicateStatus = 0;
        }
        return duplicateStatus;
    }

    public static int getRecommendStatus() {
        if (recommendStatus == -1) {
            recommendStatus = config.getInt("recommend_status");
            if (recommendStatus < 0)
                recommendStatus = 0;
        }
        return recommendStatus;
    }
    
    public static int getCopyrightMusicStatus() {
        if (copyrightMusicStatus == -1) {
        	copyrightMusicStatus = config.getInt("copyright_music_status");
            if (copyrightMusicStatus < 0)
            	copyrightMusicStatus = 0;
        }
        return copyrightMusicStatus;
    }

    public static int getFilterStatus() {
        if (filterStatus == -1) {
            filterStatus = config.getInt("filter_status");
            if (filterStatus < 0)
                filterStatus = 0;
        }
        return filterStatus;
    }

    public static int getSuggestionStatus() {
        if (suggestionStatus == -1) {
            suggestionStatus = config.getInt("suggestion_status");
            if (suggestionStatus < 0)
                suggestionStatus = 0;
        }
        return suggestionStatus;
    }

    public static int getRelevantWordStatus() {
        if (relevantWordStatus == -1) {
            relevantWordStatus = config.getInt("relevant_word_status");
            if (relevantWordStatus < 0)
                relevantWordStatus = 0;
        }
        return relevantWordStatus;
    }

    // sort parts end.
    // ********************************************************

    private static InetSocketAddress[] getSockets(List<Server> servers) {

        if (servers == null) {
            return null;
        }

        InetSocketAddress[] addresses = new InetSocketAddress[servers.size()];

        for (int i = 0; i < servers.size(); i++) {
            Server server = servers.get(i);

            String ip = server.getIp();
            int poolPort = server.getPoolport();

            addresses[i] = new InetSocketAddress(ip, poolPort);
        }

        return addresses;
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
            Config.init("E:/work/youku/search/src/WEB-INF/conf/config.xml");
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
