package com.youku.search.store;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.danga.MemCached.SockIOPool;
import com.youku.search.config.Config;
import com.youku.soku.util.Constant;

public class MemCachedConfig {

//	protected static final String POOL_NAME = "obj_store";
    private static final String POOL_NAME_PREFIX = "MEMSTORE_";
    
    private static String cur_pool_name = POOL_NAME_PREFIX + Config.getGroupNumber();
    
    private static Set<String> pools = new HashSet<String>();
    
    public static void init(String file) {

        try {
            Configuration p = new PropertiesConfiguration(new File(file));
            String[] tables = p.getStringArray("servername");
            
            for (String table:tables){
            	//索引管理机器，全部加载
            	if(Config.getServerType() != Constant.ServerType.INDEX_MANAGER){
	            	//排序机器，只加载当前组需要的
	            	if(Config.getServerType() == Constant.ServerType.SORT){
	            		if (Config.getGroupNumber() != Integer.parseInt(table))
	            			continue;
	            	}
            	}
            	
            	 String[] servers = p.getStringArray("server"+table);
            	 System.out.println("server"+table+Arrays.toString(servers));
            	 
            	 if (servers != null && servers.length > 0)
            		 pools.add(POOL_NAME_PREFIX + table);
                 int initConn = p.getInt("initconn");
                 int minconn = p.getInt("minconn");
                 int maxconn = p.getInt("maxconn");
                 int maxidle = p.getInt("maxidle");
                 int maintsleep = p.getInt("maintsleep");
                 boolean nagle = p.getBoolean("nagle");

                 int socketreadtimeout = p.getInt("socketreadtimeout");
                 int socketconntimeout = p.getInt("socketconntimeout");

                 SockIOPool pool = SockIOPool.getInstance(POOL_NAME_PREFIX + table);
                 pool.setServers(servers);
                 pool.setInitConn(initConn);
                 pool.setMinConn(minconn);
                 pool.setMaxConn(maxconn);
                 pool.setMaxIdle(maxidle);
                 pool.setMaintSleep(maintsleep);
                 pool.setNagle(nagle);
                 pool.setSocketTO(socketreadtimeout);
                 pool.setSocketConnectTO(socketconntimeout);
                 pool.initialize();
            }

        } catch (Exception e) {
            System.out.println(e + "file " + file + " not found");
            e.printStackTrace();
        } finally {

        }
        System.out.println("Object Store init over!");
    }
    
    
   public static String getCurPoolName()
   {
	   return cur_pool_name;
   }
   public static Set<String> getAllPools(){
	   return pools;
   }
   
   public static void shutdown(){
	   Set<String> pools =  getAllPools();
	   if (pools != null){
		   for (String poolname:pools){
			   SockIOPool pool = SockIOPool.getInstance(poolname);
			   if (pool != null){
				   pool.shutDown();
			   }
		   }
	   }
   }
   
   public static void main(String[] args){
	   MemCachedConfig.init("E:/work/youku/search/src2/WEB-INF/conf/mem_obj_store.properties");
   }
   
}
