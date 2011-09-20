package com.youku.search.console.vo;

import com.youku.search.console.pojo.Menu;

public class MenuVO {
//	private int id;
	private int level;
//	private String name;
//	private String menuUrl;
//	private String module;
//	private int father_id;
	private Menu m;
	
	public Menu getM() {
		return m;
	}
	public void setM(Menu m) {
		this.m = m;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

}
