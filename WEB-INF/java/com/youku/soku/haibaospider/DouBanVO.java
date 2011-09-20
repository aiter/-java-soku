package com.youku.soku.haibaospider;

import java.util.ArrayList;
import java.util.List;

public class DouBanVO {
	String url;
	String pic;
	String imdb;
	List<String> title = new ArrayList<String>();
	public List<String> getTitle() {
		return title;
	}
	public void setTitle(List<String> title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getImdb() {
		return imdb;
	}
	public void setImdb(String imdb) {
		this.imdb = imdb;
	}
	
}
