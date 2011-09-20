package com.youku.top.index.entity;

import java.util.ArrayList;
import java.util.List;

public class Result {
	String src_keyword;
	String merge_keyword;
	boolean isMerge = false;
	List<Item> items = new ArrayList<Item>();
	
	public String getSrc_keyword() {
		return src_keyword;
	}
	public void setSrc_keyword(String srcKeyword) {
		src_keyword = srcKeyword;
	}
	public String getMerge_keyword() {
		return merge_keyword;
	}
	public void setMerge_keyword(String mergeKeyword) {
		merge_keyword = mergeKeyword;
	}
	public boolean isMerge() {
		return isMerge;
	}
	public void setMerge(boolean isMerge) {
		this.isMerge = isMerge;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
}
