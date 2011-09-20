package com.youku.soku.sort.word_match.like;

import com.youku.soku.zhidaqu.v2.TokenType;

public class Modifier{
	TokenType type;
	int hd = 0;
	public TokenType getType() {
		return type;
	}
	public void setType(TokenType type) {
		this.type = type;
	}
	public int getHd() {
		return hd;
	}
	public void setHd(int hd) {
		this.hd = hd;
	}
	public String toString(){
		return type+"/"+hd;
	}
	
	public boolean isEmpty(){
		return type == null && hd == 0;
	}
}