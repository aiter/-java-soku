package com.youku.top.util;

import java.util.HashMap;
import java.util.Map;

public class TopWordType {
	
public enum WordType{
	
	不限(0),
	电视剧(1),
	电影(2),
	综艺(3),
	音乐(4),
	动漫(5),
	人物(6),
	体育(7),
	科教(8),
	搞笑(9);
	private int value;
	WordType( int arg1) {
		value=arg1;
	}
	public int getValue() {
		return value;
	} 
	}

	public static Map<Integer,String> wordTypeMap = new HashMap<Integer,String>();
	
	static {
		for(WordType wt:WordType.values()){
			wordTypeMap.put(wt.getValue(), wt.name());
		}
	}
}
