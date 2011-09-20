package com.youku.soku.util;

public enum ChannelType {
	MOVIE(2),TELEPLAY(1),VARIETY(3),MUSIC(4),ANIME(5);
	
	
	
	private int value;
	private ChannelType(int value) {
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}

}
