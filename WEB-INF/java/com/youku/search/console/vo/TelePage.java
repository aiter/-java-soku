package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.List;

public class TelePage {
	int teleplaySize;
	int maxpage;
	int page;
	
	List<SingleTeleplay> sl=new ArrayList<SingleTeleplay>();

	public int getTeleplaySize() {
		return teleplaySize;
	}

	public void setTeleplaySize(int teleplaySize) {
		this.teleplaySize = teleplaySize;
	}

	public int getMaxpage() {
		return maxpage;
	}

	public void setMaxpage(int maxpage) {
		this.maxpage = maxpage;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<SingleTeleplay> getSl() {
		return sl;
	}

	public void setSl(List<SingleTeleplay> sl) {
		this.sl = sl;
	}
	
	
}
