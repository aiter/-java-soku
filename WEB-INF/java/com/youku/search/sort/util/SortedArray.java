package com.youku.search.sort.util;

import java.util.ArrayList;

/* use to create a list sorted by a special number, this number is appointed for recommending order manually
 */
public class SortedArray <T extends Comparable<T>> {
	private ArrayList<T> elements = null;
	private boolean desc = true;
	public SortedArray(boolean _desc) {
		elements = new ArrayList<T>();
		desc = _desc;
	}
	public SortedArray() {
		elements = new ArrayList<T>();
		desc = true;
	}

	private int insert_by_desc(T v) {
		int i = 0;
		for(; i < elements.size(); i ++) {
			if( v.compareTo(elements.get(i)) >= 0 ) {
				elements.add(i, v);
				return i;
			}
		}
		elements.add(i, v);
		return i;
	}

	private int insert_by_asc(T v) {
		int i = 0;
		for(; i < elements.size(); i ++) {
			if( v.compareTo(elements.get(i)) <= 0 ) {
				elements.add(i, v);
				return i;
			}
		}
		elements.add(i, v);
		return i;
	}

	public int insert(T v) {
		if(elements.size() == 0) {
			elements.add(0, v);
			return 0;
		}

		if(desc) return insert_by_desc(v);
		else return insert_by_asc(v);
	}

	public T get(int loc) {
		if(loc >= elements.size()) return null;

		return elements.get(loc);
	}

	public int size() {
		return elements.size();
	}

	public static void main(String[] args) {
		SortedArray<Integer> sortedArray = new SortedArray<Integer>();
		int x = sortedArray.insert(2);
		System.err.println("insert 2 in "+x);
		x = sortedArray.insert(3);
		System.err.println("insert 3 in "+x);
		x = sortedArray.insert(1);
		System.err.println("insert 1 in "+x);
		x = sortedArray.insert(4);
		System.err.println("insert 4 in "+x);
		x = sortedArray.insert(6);
		System.err.println("insert 6 in "+x);
		x = sortedArray.insert(9);
		System.err.println("insert 9 in "+x);
		x = sortedArray.insert(5);
		System.err.println("insert 5 in "+x);

		for(int i = 0; i < sortedArray.size(); i ++) {
			System.out.println("No."+i+":"+sortedArray.get(i).toString());
		}
	}
}
