package com.youku.top.recomend;

import java.util.ArrayList;
import java.util.List;

public class Result {
	class Item{
		String keyword;
		int count;
		int type;
		@Override
		public String toString() {
			return "Item [count=" + count + ", keyword=" + keyword + ", type="
					+ type + "]";
		}
	}
	public Item newItem(){
		return new Item();
	}
	String keyword;
	List<Item> items = new ArrayList<Item>();
	long cost;
	@Override
	public String toString() {
		return "Result [cost=" + cost + ", items=" + items + ", keyword="
				+ keyword + "]";
	}
	
	
}
