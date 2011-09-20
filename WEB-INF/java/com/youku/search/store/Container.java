package com.youku.search.store;

import java.io.Serializable;

public interface Container {
	public <T extends Serializable> boolean push(String key,T object);
	public <T extends Serializable> T  get(String key);
	public Object[] getMultiArray(String... keys);
	public boolean remove(String key);
	public void destroy();
}
