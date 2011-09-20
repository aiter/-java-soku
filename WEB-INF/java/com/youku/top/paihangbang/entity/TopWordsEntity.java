package com.youku.top.paihangbang.entity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.youku.soku.top.mapping.TopWords;

public class TopWordsEntity{
	TopWords topwords;
	
	//new 
	int year;
	int content_id;
	Set<Integer> types = new HashSet<Integer>();
	Set<Integer> areas = new HashSet<Integer>();
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Set<Integer> getTypes() {
		return types;
	}
	public void setTypes(Set<Integer> types) {
		this.types = types;
	}
	public Set<Integer> getAreas() {
		return areas;
	}
	public void setAreas(Set<Integer> areas) {
		this.areas = areas;
	}
	public int getContent_id() {
		return content_id;
	}
	public void setContent_id(int contentId) {
		content_id = contentId;
	}
	public TopWords getTopwords() {
		return topwords;
	}
	public void setTopwords(TopWords topwords) {
		this.topwords = topwords;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		return "TopWordsVO [areas="
				+ (areas != null ? toString(areas, maxLen) : null)
				+ ", content_id=" + content_id + ", topwords=" + topwords
				+ ", types=" + (types != null ? toString(types, maxLen) : null)
				+ "]";
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
