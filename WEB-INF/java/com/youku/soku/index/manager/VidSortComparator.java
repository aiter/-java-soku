/**
 * 
 */
package com.youku.soku.index.manager;

import org.apache.lucene.search.SortComparator;

public class VidSortComparator extends SortComparator {
	protected Comparable getComparable(String termtext) {
		return Integer.parseInt(termtext.substring(termtext.indexOf("_")+1));
	}
	
	
}
