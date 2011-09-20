package com.youku.soku.haibaospider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Hao123VO {
	
	String title;
	String url;
	String pic;
	Set<String> actors = new HashSet<String>();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Set<String> getActors() {
		return actors;
	}
	public void setActors(Set<String> actors) {
		this.actors = actors;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Hao123VO [actors="
				+ (actors != null ? toString(actors, maxLen) : null) + ", pic="
				+ pic + ", title=" + title + ", url=" + url + "]";
	}
	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
	
}
