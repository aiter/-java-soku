package com.youku.search.recomend;

import java.util.ArrayList;
import java.util.List;

public class Result {
	class Item{
		String keyword;
		int count;
		@Override
		public String toString() {
			return "Item [count=" + count + ", keyword=" + keyword + "]";
		}
		
	}
	public Item newItem(){
		return new Item();
	}
	String type;
	String keyword;
	List<Item> items = new ArrayList<Item>();
	long cost;
	@Override
	public String toString() {
		return "Result [cost=" + cost + ", items=" + items + ", keyword="
				+ keyword + ", type=" + type + "]";
	}
	
}
