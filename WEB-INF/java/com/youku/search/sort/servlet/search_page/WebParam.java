package com.youku.search.sort.servlet.search_page;

public class WebParam implements Cloneable {

	public static final String TYPE_BAR = "bar";
	public static final String TYPE_POST = "post";
	public static final String TYPE_USER = "user";

	private String q;
	private int orderby;
	private int lengthtype;
	private int hd;
	private int cateid;
	private String source;
	private String type;
	private int page;

	// 高级搜索选项
	private boolean advance;

	private int pagesize;
	private String fields;
	private String limitdate;
	private String categories;
	private int pv;
	private int comments;

	// bar搜索
	private String sbt;

	@Override
	public WebParam clone() {
		try {
			return (WebParam) super.clone();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCateid() {
		return cateid;
	}

	public void setCateid(int cateid) {
		this.cateid = cateid;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getOrderby() {
		return orderby;
	}

	public void setOrderby(int orderby) {
		this.orderby = orderby;
	}

	public int getLengthtype() {
		return lengthtype;
	}

	public void setLengthtype(int lengthtype) {
		this.lengthtype = lengthtype;
	}

	public int getHd() {
		return hd;
	}

	public void setHd(int hd) {
		this.hd = hd;
	}

	public boolean isAdvance() {
		return advance;
	}

	public void setAdvance(boolean advance) {
		this.advance = advance;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getLimitdate() {
		return limitdate;
	}

	public void setLimitdate(String limitdate) {
		this.limitdate = limitdate;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public String getSbt() {
		return sbt;
	}

	public void setSbt(String sbt) {
		this.sbt = sbt;
	}

}
