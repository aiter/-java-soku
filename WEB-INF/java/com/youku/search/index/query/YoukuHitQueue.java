/**
 * 
 */
package com.youku.search.index.query;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.PriorityQueue;

/**
 * @author 1verge
 *
 */
final class YoukuHitQueue extends PriorityQueue {
	YoukuHitQueue(int size) {
		    initialize(size);
		  }

		  protected final boolean lessThan(Object a, Object b) {
		    ScoreDoc hitA = (ScoreDoc)a;
		    ScoreDoc hitB = (ScoreDoc)b;
		    if (hitA.score == hitB.score)
		      return hitA.doc > hitB.doc; 
		    else
		      return hitA.score < hitB.score;
		  }
		}
