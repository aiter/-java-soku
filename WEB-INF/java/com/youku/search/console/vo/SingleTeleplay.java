package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.List;

public class SingleTeleplay {
	int id;
	String keyword;
	int isvalid;
	int bid;
	String aliasStr;
	List<SingleVersion> svl=new ArrayList<SingleVersion>();
	
	public String getAliasStr() {
		return aliasStr;
	}
	public void setAliasStr(String aliasStr) {
		this.aliasStr = aliasStr;
	}
	public int getIsvalid() {
		return isvalid;
	}
	public void setIsvalid(int isvalid) {
		this.isvalid = isvalid;
	}
	public int getBid() {
		return bid;
	}
	public void setBid(int bid) {
		this.bid = bid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public List<SingleVersion> getSvl() {
		return svl;
	}
	public void setSvl(List<SingleVersion> svl) {
		this.svl = svl;
	}
	
}
