package com.youku.top.util;

public enum DiectVideoType {
	电视剧(1),
	电影(2),
	综艺(3),
	音乐(4),
	动漫(5),
	人物(6);
	private int value;
	DiectVideoType( int arg1) {
		value=arg1;
	}
	public int getValue() {
		return value;
	} 
}
