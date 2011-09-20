package com.youku.search.console.operate;

import java.util.HashMap;

public class Channel {
	public static HashMap<Integer,String> cateMap=new HashMap<Integer, String>();
	public static HashMap<Integer,HashMap<Integer,String>> subcateMap=new HashMap<Integer, HashMap<Integer,String>>();
	public static final String SYSUSER="sys";
	public static void listInit(){
		cateMap.put(0, "未分类");
		cateMap.put(97, "电视");
		cateMap.put(100, "动漫");
		HashMap<Integer,String> temp=new HashMap<Integer, String>();
		temp.put(2081, "美剧");
		temp.put(2070, "韩剧");
		temp.put(2039, "港剧");
		temp.put(2068, "台剧");
		temp.put(2038, "大陆剧");
		temp.put(2069, "日剧");
		temp.put(2080, "其他地区");
		temp.put(2078, "综艺节目");
		subcateMap.put(97, temp);
		
		temp=new HashMap<Integer, String>();
		temp.put(2224, "日韩");
		temp.put(2226, "欧美");
		temp.put(2223, "国产");
		temp.put(2225, "港台");
		subcateMap.put(100, temp);
		
		temp=new HashMap<Integer, String>();
		temp.put(0, "未分类");
		subcateMap.put(0, temp);
	}
	
}
