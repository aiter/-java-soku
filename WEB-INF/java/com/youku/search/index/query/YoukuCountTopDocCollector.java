/**
 * 
 */
package com.youku.search.index.query;

import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.PriorityQueue;

import com.youku.search.index.entity.Video;

/**
 * @author 1verge
 *
 */
public class YoukuCountTopDocCollector  extends HitCollector {
	private ScoreDoc reusableSD;
	
	  int totalHits;
	  PriorityQueue hq;
	 
	  public YoukuCountTopDocCollector(int numHits) {
		    this(numHits, new YoukuHitQueue(numHits));
		  }
	  

	  YoukuCountTopDocCollector(int numHits, PriorityQueue hq) {
	    this.hq = hq;
	  }

	  // javadoc inherited
	  public void collect(int doc, float score) {
		    if (score > 0.0f) {
//			      int count  = VideoQueryManager.getInstance().commentCount[doc];
		    	  int count = 0;
			      totalHits++;
			      if (reusableSD == null) {
			        reusableSD = new ScoreDoc(doc, count);
			      } else if (count >= reusableSD.score) {
			        // reusableSD holds the last "rejected" entry, so, if
			        // this new score is not better than that, there's no
			        // need to try inserting it
			        reusableSD.doc = doc;
			        reusableSD.score = count;
			      } else {
			        return;
			      }
			      reusableSD = (ScoreDoc) hq.insertWithOverflow(reusableSD);
			      
			    }}

	  /** The total number of documents that matched this query. */
	  public int getTotalHits() { return totalHits; }

	  /** The top-scoring hits. */
	  public TopDocs topDocs() {
	    ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
	    for (int i = hq.size()-1; i >= 0; i--)      // put docs in array
	      scoreDocs[i] = (ScoreDoc)hq.pop();
	      
	    float maxScore = (totalHits==0)
	      ? Float.NEGATIVE_INFINITY
	      : scoreDocs[0].score;
	    
	    return new TopDocs(totalHits, scoreDocs, maxScore);
	  }
	  
//	  public HashMap<Integer,Integer> getCateMap()
//	  {
//		  return cateMap;
//	  }
}
