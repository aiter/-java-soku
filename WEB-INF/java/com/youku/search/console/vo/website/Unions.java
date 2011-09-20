package com.youku.search.console.vo.website;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Unions {
	public String uniondate;
	public int show_nums;
	public int show_nums1;
	public int show_nums2;
	public int click_nums;
	public int click_nums1;
	public int click_nums2;
	public int search_nums;
	public int search_nums1;
	public int search_nums2;
	public Map<String,Site> sites=new HashMap<String, Site>();
	public int getShow_nums1() {
		return show_nums1;
	}
	public void setShow_nums1(int show_nums1) {
		this.show_nums1 = show_nums1;
	}
	public int getShow_nums2() {
		return show_nums2;
	}
	public void setShow_nums2(int show_nums2) {
		this.show_nums2 = show_nums2;
	}
	public int getClick_nums1() {
		return click_nums1;
	}
	public void setClick_nums1(int click_nums1) {
		this.click_nums1 = click_nums1;
	}
	public int getClick_nums2() {
		return click_nums2;
	}
	public void setClick_nums2(int click_nums2) {
		this.click_nums2 = click_nums2;
	}
	public int getSearch_nums1() {
		return search_nums1;
	}
	public void setSearch_nums1(int search_nums1) {
		this.search_nums1 = search_nums1;
	}
	public int getSearch_nums2() {
		return search_nums2;
	}
	public void setSearch_nums2(int search_nums2) {
		this.search_nums2 = search_nums2;
	}
	public int getSearch_nums() {
		return search_nums;
	}
	public void setSearch_nums(int search_nums) {
		this.search_nums = search_nums;
	}
	public String getUniondate() {
		return uniondate;
	}
	public void setUniondate(String uniondate) {
		this.uniondate = uniondate;
	}
	public int getShow_nums() {
		return show_nums;
	}
	public void setShow_nums(int show_nums) {
		this.show_nums = show_nums;
	}
	public int getClick_nums() {
		return click_nums;
	}
	public void setClick_nums(int click_nums) {
		this.click_nums = click_nums;
	}
	public Map<String, Site> getSites() {
		return sites;
	}
	public void setSites(Map<String, Site> sites) {
		this.sites = sites;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("uniondate:");
		str.append(uniondate);
		str.append(",show_nums:");
		str.append(show_nums);
		str.append(",show_nums1:");
		str.append(show_nums1);
		str.append(",show_nums2:");
		str.append(show_nums2);
		str.append(",search_nums:");
		str.append(search_nums);
		str.append(",search_nums1:");
		str.append(search_nums1);
		str.append(",search_nums2:");
		str.append(search_nums2);
		str.append(",click_nums:");
		str.append(click_nums);
		str.append(",click_nums1:");
		str.append(click_nums1);
		str.append(",click_nums2:");
		str.append(click_nums2);
		str.append(",sites:{");
		 for(Entry<String, Site> entry:sites.entrySet()){
			 str.append("[");
			 str.append(entry.getKey());
			 str.append(",");
			 str.append(entry.getValue().getNum1());
			 str.append(",");
			 str.append(entry.getValue().getNum2());
			 str.append("]");
		 }
		return str.toString();
	}
}
