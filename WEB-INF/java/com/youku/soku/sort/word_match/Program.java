package com.youku.soku.sort.word_match;

import com.youku.soku.zhidaqu.v2.Element;

public class Program implements Comparable<Object>{
	
	public Program(String word,Element element){
		this.keyword  = word;
		this.element = element;
	}
	
	private String keyword;
	private Element element;
	private MatchType matchType;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
	public String toString(){
		return keyword + "\t ELEMENT:" + element;
	}
	@Override
	public int compareTo(Object o) {
		Program b = (Program)o;
		
		return b.getElement().getReleaseDate() - element.getReleaseDate() ;
	}
	public boolean equals(Object entity){
		if(entity!=null && entity instanceof Program){
			Program program = (Program)entity;
			return program.getKeyword().equals(keyword) && program.getElement().getType()== element.getType();
		}
		return false;
		
	}
	public MatchType getMatchType() {
		return matchType;
	}
	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}
	
	
}
