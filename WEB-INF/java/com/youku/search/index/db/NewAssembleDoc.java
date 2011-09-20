package com.youku.search.index.db;

import java.io.Serializable;


public class NewAssembleDoc {
	
	private String key;
	
	private Object index;

	private Serializable store ;

	public Object getIndex() {
		return index;
	}

	public void setIndex(Object index) {
		this.index = index;
	}

	public Serializable getStore() {
		return store;
	}

	public void setStore(Serializable store) {
		this.store = store;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
	
}
