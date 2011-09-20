package com.youku.search.console.vo;

import java.util.List;
import java.util.Map;

public class SingleDayDate {
	private Map<String,List<Entity>> em;
	private String date;
	public Entity newEntity(){
		return new Entity();
	}
	public class Entity{
		String keyword;
		int counts;
		String type;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getKeyword() {
			return keyword;
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
		public int getCounts() {
			return counts;
		}
		public void setCounts(int counts) {
			this.counts = counts;
		}
	}
	
	public Map<String, List<Entity>> getEm() {
		return em;
	}


	public void setEm(Map<String, List<Entity>> em) {
		this.em = em;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public Entity Entity() {
		return new Entity();
	}
}
