package com.youku.soku.manage.bo.paihangbang;

import java.util.ArrayList;
import java.util.List;

public class TopWordsVO {
	String keyword;
	List<KeywordTypeVO> tops = new ArrayList<KeywordTypeVO>();
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public List<KeywordTypeVO> getTops() {
		return tops;
	}
	public void setTops(List<KeywordTypeVO> tops) {
		this.tops = tops;
	}
	
	
}
