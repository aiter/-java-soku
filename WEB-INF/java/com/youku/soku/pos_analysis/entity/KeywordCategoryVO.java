package com.youku.soku.pos_analysis.entity;

import java.util.ArrayList;
import java.util.List;

public class KeywordCategoryVO {
	List<String> category_name_list = new ArrayList<String>();
	String click_date;
	String keyword;
	boolean isPerson = false;
	boolean isMajor = false;
	boolean isFromBaidu = false;
	List<CategoryClickVO> ccvo = new ArrayList<CategoryClickVO>();
	boolean isMerge = false;
	String merge_keyword;

	
	public boolean isMajor() {
		return isMajor;
	}

	public void setMajor(boolean isMajor) {
		this.isMajor = isMajor;
	}

	public boolean isFromBaidu() {
		return isFromBaidu;
	}

	public void setFromBaidu(boolean isFromBaidu) {
		this.isFromBaidu = isFromBaidu;
	}

	public List<String> getCategory_name_list() {
		return category_name_list;
	}

	public void setCategory_name_list(List<String> categoryNameList) {
		category_name_list = categoryNameList;
	}

	public String getClick_date() {
		return click_date;
	}

	public void setClick_date(String clickDate) {
		click_date = clickDate;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public boolean isPerson() {
		return isPerson;
	}

	public void setPerson(boolean isPerson) {
		this.isPerson = isPerson;
	}

	public List<CategoryClickVO> getCcvo() {
		return ccvo;
	}

	public void setCcvo(List<CategoryClickVO> ccvo) {
		this.ccvo = ccvo;
	}

	public boolean isMerge() {
		return isMerge;
	}

	public void setMerge(boolean isMerge) {
		this.isMerge = isMerge;
	}

	public String getMerge_keyword() {
		return merge_keyword;
	}

	public void setMerge_keyword(String mergeKeyword) {
		merge_keyword = mergeKeyword;
	}

	@Override
	public String toString() {
		final int maxLen = 3;
		return "KeywordCategoryVO [click_date="
				+ click_date
				+ ", keyword="
				+ keyword
				+ ", isPerson="
				+ isPerson
				+ ",category_name_list="
				+ category_name_list
				+ ",ccvo="
				+ (ccvo != null ? ccvo
						.subList(0, Math.min(ccvo.size(), maxLen)) : null)
				+ ", merge_keyword="
				+ merge_keyword;
	}
}
