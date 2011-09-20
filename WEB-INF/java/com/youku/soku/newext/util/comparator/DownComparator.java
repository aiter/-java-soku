/**
 * 
 */
package com.youku.soku.newext.util.comparator;

import java.util.Comparator;

/**
 * @author liuyunjian
 * 2011-4-26
 */
public class DownComparator<T> implements Comparator<T> {
	private Comparator<T> comparator = null;
	
	public DownComparator(Comparator<T> comparator) {
		super();
		this.comparator = comparator;
	}

	@Override
	public int compare(T o1, T o2) {
		if(comparator!=null){
			return comparator.compare(o2, o1);
		}
		return 0;
	}

}
