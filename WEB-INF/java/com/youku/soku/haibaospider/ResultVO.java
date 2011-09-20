package com.youku.soku.haibaospider;

public class ResultVO {
	
	int id;
	String title;
	String pic;
	String ziliao_url;
	int cate;
	String youku_pic;
	boolean dbid_match = false;
	boolean imdb_match = false;
	boolean title_match = false;
	int queryCount;
	
	public int getQueryCount() {
		return queryCount;
	}
	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}
	public String getYouku_pic() {
		return youku_pic;
	}
	public void setYouku_pic(String youkuPic) {
		youku_pic = youkuPic;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getZiliao_url() {
		return ziliao_url;
	}
	public void setZiliao_url(String ziliaoUrl) {
		ziliao_url = ziliaoUrl;
	}
	public int getCate() {
		return cate;
	}
	public void setCate(int cate) {
		this.cate = cate;
	}
	public boolean isDbid_match() {
		return dbid_match;
	}
	public void setDbid_match(boolean dbidMatch) {
		dbid_match = dbidMatch;
	}
	public boolean isImdb_match() {
		return imdb_match;
	}
	public void setImdb_match(boolean imdbMatch) {
		imdb_match = imdbMatch;
	}
	public boolean isTitle_match() {
		return title_match;
	}
	public void setTitle_match(boolean titleMatch) {
		title_match = titleMatch;
	}
	@Override
	public String toString() {
		return "ResultVO [cate=" + cate + ", dbid_match=" + dbid_match
				+ ", id=" + id + ", imdb_match=" + imdb_match + ", pic=" + pic
				+ ", title=" + title + ", title_match=" + title_match
				+ ", youku_pic=" + youku_pic + ", ziliao_url=" + ziliao_url
				+ "]";
	}
	
}
