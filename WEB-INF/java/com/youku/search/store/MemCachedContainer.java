package com.youku.search.store;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.danga.MemCached.MemCachedClient;

public class MemCachedContainer implements Container{
	
	MemCachedContainer(String filename){
		MemCachedConfig.init(filename);
	}
	
	static Log logger = LogFactory.getLog(MemCachedContainer.class.getName());
	
	public static final String PREFIX_VIDEO_OBJECT = "video_";
	public static final int MAX_LIVESECONDS = 24 * 60 * 60;//最长存活
	

	/**
	 * 从当前节点获取
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T  get(String key) {
		
		MemCachedClient client = getClient();
		Object o =  client.get(key);
		return (T)o;
	}
	
	/**
	 * 从当前节点获取
	 */
	public Object[] getMultiArray(String... keys) {
		MemCachedClient client = getClient();
		Object[] o = client.getMultiArray(keys);
//		if (keys.length != o.length)
//			System.out.println("get key:" + keys +";return arr length="+ o.length);
//		
//		
		return o;
	}
	/**
	 * 所有节点存储
	 * @param key
	 * @param o
	 * @return
	 */
	public <T extends Serializable> boolean push(String key, T o) {
		Set<String> pools = MemCachedConfig.getAllPools();
		boolean result = false;
		for (String pool:pools)
		{
			MemCachedClient client = getClient(pool);
			for (int i=0; i<30; i++){ //如果失败最多执行30次,每次暂停100毫秒
				if (result = client.set(key, o, new Date(0))){
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	
	private MemCachedClient getClient() {
		return getClient(MemCachedConfig.getCurPoolName());
	}
	
	private MemCachedClient getClient(String poolname) {
		MemCachedClient client = new MemCachedClient();
		client.setPoolName(poolname);
		client.setSanitizeKeys(false);
		return client;
	}
	
	/**
	 * 移除所有节点
	 */
	public boolean remove(String key)
	{
		Set<String> pools = MemCachedConfig.getAllPools();
		boolean result = true;
		for (String pool:pools)
		{
			MemCachedClient client = getClient(pool);
			result &= client.delete(key);
		}
		return result;
	}

	@Override
	public void destroy() {
		MemCachedConfig.shutdown();
		
	}
	
}
