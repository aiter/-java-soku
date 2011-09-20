package com.youku.soku.haibaospider;

public class MidVO {
	int id;
	String title;
	int dbid;
	String imdb;
	boolean dbid_match = false;
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
	public int getDbid() {
		return dbid;
	}
	public void setDbid(int dbid) {
		this.dbid = dbid;
	}
	public String getImdb() {
		return imdb;
	}
	public void setImdb(String imdb) {
		this.imdb = imdb;
	}
	public boolean isDbid_match() {
		return dbid_match;
	}
	public void setDbid_match(boolean dbidMatch) {
		dbid_match = dbidMatch;
	}
	@Override
	public String toString() {
		return "MidVO [dbid=" + dbid + ", dbid_match=" + dbid_match + ", id="
				+ id + ", imdb=" + imdb + ", title=" + title + "]";
	}
	
	
}
