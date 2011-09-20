package com.youku.search.console.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopQuickVO {
	String date;
	Map<String,List<TopView>> topviewmap =new HashMap<String, List<TopView>>();
	
	public TopView getTopView(){
		return new TopView();
	}
	public class TopView{
		String keyword;
		String query_type;
		int a_query_count;
		int a_result;
		int b_query_count;
		int b_result;
		String rate;
		
		public String getRate() {
			return rate;
		}
		public void setRate(String rate) {
			this.rate = rate;
		}
		public String getKeyword() {
			return keyword;
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
		public String getQuery_type() {
			return query_type;
		}
		public void setQuery_type(String query_type) {
			this.query_type = query_type;
		}
		public int getA_query_count() {
			return a_query_count;
		}
		public void setA_query_count(int a_query_count) {
			this.a_query_count = a_query_count;
		}
		public int getA_result() {
			return a_result;
		}
		public void setA_result(int a_result) {
			this.a_result = a_result;
		}
		public int getB_query_count() {
			return b_query_count;
		}
		public void setB_query_count(int b_query_count) {
			this.b_query_count = b_query_count;
		}
		public int getB_result() {
			return b_result;
		}
		public void setB_result(int b_result) {
			this.b_result = b_result;
		}
		
	}
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Map<String, List<TopView>> getTopviewmap() {
		return topviewmap;
	}

	public void setTopviewmap(Map<String, List<TopView>> topviewmap) {
		this.topviewmap = topviewmap;
	}
	
}
