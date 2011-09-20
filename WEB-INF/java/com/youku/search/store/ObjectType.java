package com.youku.search.store;

public class ObjectType {
	private String prefix = null;
	
	public static final ObjectType YOUKU_VIDEO = new ObjectType("video_");
	public static final ObjectType YOUKU_FOLDER = new ObjectType("YKPL_");
	public static final ObjectType YOUKU_USER = new ObjectType("YKUSER_");
	public static final ObjectType YOUKU_BAR = new ObjectType("YKBAR_");
	public static final ObjectType YOUKU_BARPOST = new ObjectType("YKBARPOST_");
	public static final ObjectType YOUKU_STAT = new ObjectType("YKSTAT_");
	public static final ObjectType SOKU_VIDEO = new ObjectType("SKVD_");
	
	public String getKey(String key){
		return prefix + key; 
	}
	
	private ObjectType(String prefix){
		this.prefix = prefix;
	}
	
}
