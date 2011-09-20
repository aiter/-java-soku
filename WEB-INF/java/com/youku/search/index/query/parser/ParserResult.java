package com.youku.search.index.query.parser;

import java.util.HashMap;
import java.util.Iterator;

public class ParserResult {
	private String keyword = null;
	private HashMap<String,String> result = new HashMap<String,String>();
	
	public void setAttribute(String field,String value){
		result.put(field, value);
	}
	
	public String getValue(String field){
		return result.get(field);
	}
	
	public boolean contains(String field){
		return result.containsKey(field);
	}
	
	public String getKeyword(){
		return keyword;
	}
	public void setKeyword(String word){
		this.keyword = word;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		if (keyword != null)
			builder.append("keyword = " + keyword);
		Iterator<String> it = result.keySet().iterator();
		while (it.hasNext()){
			String key = it.next();
			builder.append(key);
			builder.append("\t");
			builder.append(result.get(key));
		}
		return builder.toString();
	}
}
