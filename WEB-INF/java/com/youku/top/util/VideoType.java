package com.youku.top.util;

public enum VideoType{
	
	topconcern(0),
	teleplay(1),
	anime(5),
	movie(2),
	variety(3),
	fun(9),
	music(4),
	//crosstalk,
	sports(7),
	science(8),
	person(6);
	
	private int value;
	VideoType( int arg1) {
		value=arg1;
	}
	public int getValue() {
		return value;
	} 
	
	
}
