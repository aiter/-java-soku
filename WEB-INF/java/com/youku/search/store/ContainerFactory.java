package com.youku.search.store;


public class ContainerFactory {
	
	static MemCachedContainer memcached = null;
	public static Container getContainer()
	{
		return memcached;
	}
	
	public static void init(String file){
		initMemCached(file);
	}
	
	public static void initMemCached(String file)
	{
		memcached = new MemCachedContainer(file);
	}
	
}
