package com.youku.search.index.entity;

import java.io.Serializable;

public class PrimaryKey implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2982263920711681018L;
	public String key;		//用来取对象的KEY
	public float score; 	//得分
	public String md5;		//排重用
}
