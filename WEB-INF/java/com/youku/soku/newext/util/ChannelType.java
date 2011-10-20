package com.youku.soku.newext.util;

public enum ChannelType {
	MOVIE(2),TELEPLAY(1),VARIETY(3),MUSIC(4),ANIME(5),PERSON(10), DOCUMENTARY(9);
	
	
	
	private int value;
	private ChannelType(int value) {
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}

}
