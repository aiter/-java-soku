package com.youku.soku.manage.bo.paihangbang;

public class CateVO {
	String catename;
	int catevalue;
	public String getCatename() {
		return catename;
	}
	public void setCatename(String catename) {
		this.catename = catename;
	}
	public int getCatevalue() {
		return catevalue;
	}
	public void setCatevalue(int catevalue) {
		this.catevalue = catevalue;
	}
	public CateVO(String catename, int catevalue) {
		super();
		this.catename = catename;
		this.catevalue = catevalue;
	}
	
}
