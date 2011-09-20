package com.youku.search.store.pic;

import java.io.File;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.youku.search.pool.net.util.Cost;

public class MemCachedPic {
	private static final String MEM_STORED_KEY = "stored_max_id"; 
	private static final String POOL_NAME = "mem_pic"; 

	public enum StoreResult {
		success, fail, bad_cache_time
	};
    
    
    public static void init(String file) {

        try {
            Configuration p = new PropertiesConfiguration(new File(file));
            
            	 String[] servers = p.getStringArray("server");
            	 
                 int initConn = p.getInt("initconn");
                 int minconn = p.getInt("minconn");
                 int maxconn = p.getInt("maxconn");
                 int maxidle = p.getInt("maxidle");
                 int maintsleep = p.getInt("maintsleep");
                 boolean nagle = p.getBoolean("nagle");

                 int socketreadtimeout = p.getInt("socketreadtimeout");
                 int socketconntimeout = p.getInt("socketconntimeout");

                 SockIOPool pool = SockIOPool.getInstance(POOL_NAME);
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
                 

        } catch (Exception e) {
            System.out.println(e + "file " + file + " not found");
            e.printStackTrace();
        } finally {

        }
        System.out.println("Object Store init over!");
    }
    
    
    private static MemCachedClient getClient() {
		MemCachedClient client = new MemCachedClient();
		client.setPoolName(POOL_NAME);
		client.setSanitizeKeys(false);
		return client;
	}

/*	private static String processKey(String key) {
		if (key == null || key.trim().length() == 0) {
			throw new IllegalArgumentException("key不能为null、不能为空字符串");
		}

		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] bytes = digest.digest(key.getBytes("utf8"));

			StringBuilder builder = new StringBuilder();
			for (byte b : bytes) {
				String s = "0" + Integer.toHexString(b & 0xFF);
				builder.append(s.substring(s.length() - 2));
			}

			return builder.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}*/

	/**
	 * 总是检查远程的缓存服务器
	 */
	public static Object cacheGet(String key) {
		return cacheGet(key, 1);
	}
	
	/**
	 * 
	 * @param vid 视频未编码ID
	 * @param o
	 * @return
	 */
	public static Object cacheGetByVid(String vid) {
		String key = cacheKey(vid);
		return cacheGet(key, 1);
	}

	/**
	 * 如果cacheSeconds大于0，才检查远程的缓存服务器
	 */
	public static Object cacheGet(String key, int cacheSeconds) {
		if (cacheSeconds > 0) {
			MemCachedClient client = getClient();
//			key = processKey(key);
			Object o = client.get(key);
			return o;
		}

		return null;
	}
	
	public static Map<String,Object> cacheGet(String []keys) {
			MemCachedClient client = getClient();
			return client.getMulti(keys);
	}
	public static Map<String,Object> cacheGetByVid(String []keys) {
		String [] tmps = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			tmps[i] = cacheKey(keys[i]);
		}
		return cacheGet(tmps);
	}

	/**
	 * 缓存结果
	 */
	public static StoreResult cacheSet(String key, Object o/**, int cacheSeconds*/) {

//		if (cacheSeconds <= 0) {
//			return StoreResult.bad_cache_time;
//		}

//		Calendar later = Calendar.getInstance();
//		later.add(Calendar.SECOND, cacheSeconds);
//		Date laterTime = later.getTime();

		MemCachedClient client = getClient();
//		key = processKey(key);

		boolean result = client.set(key, o, new Date(0));
		if (result) {
			return StoreResult.success;
		}
		return StoreResult.fail;
	}
	
	
	
	public static StoreResult updateMaxid(int maxid) {
		MemCachedClient client = getClient();
		boolean result = client.set(MEM_STORED_KEY, maxid+"", new Date(0));
		if (result) {
			return StoreResult.success;
		}
		return StoreResult.fail;
	}
	
	public static int getMaxid() {
		MemCachedClient client = getClient();
		Object o = client.get(MEM_STORED_KEY);
		if(o==null){
			return 0;
		}else {
			if(o instanceof Integer){
				return (Integer)o;
			}else if (o instanceof String) {
				return Integer.valueOf((String)o);
			}
			
			return 0;
		}
	}
	
	/**
	 * 为了节约储存空间，尽量缩短key的长度，只在videoId前加一个字母p，表示picture 
	 */
	public static String cacheKey(String objectId) {
		return "p" + objectId;
	}
    
   public static void main(String[] args){
	   MemCachedPic.init("/home/liuyunjian/workspace/search/WEB-INF/conf/mem_pic_store.properties");
	   
//	   System.out.println(cacheSet(cacheKey("1"), 5));
	   System.out.println(MemCachedPic.getMaxid());
//	   System.out.println(cacheGetByVid("1"));
	   Map<String,Object> tmps = cacheGetByVid(new String[]{"200","201"});
	   if(tmps!=null && tmps.size()>0){
		   for (Object object : tmps.values()) {
			   System.out.println(object);
		}
	   }
	  
//	   System.out.println(cacheGetByVid("24999999"));
	   
	   
   }
   
}
