package com.youku.search.console.vo.website;

import java.util.ArrayList;
import java.util.List;

public class ClickView {
	List<Site> clicks=new ArrayList<Site>();
	String keyword;
	public List<Site> getClicks() {
		return clicks;
	}
	public void setClicks(List<Site> clicks) {
		this.clicks = clicks;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
