/**
 * 
 */
package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import com.youku.search.index.query.SecondFilter;
import com.youku.search.index.query.VideoQueryManager;





/**
 * @author 1verge
 *
 */
public class YoukuTopFieldDocCollector extends TopDocCollector {



	SecondFilter secondFilter = null;
	float[] seconds = null;
	  private FieldDoc reusableFD;

	  /** Construct to collect a given number of hits.
	   * @param reader the index to be searched
	   * @param sort the sort criteria
	   * @param numHits the maximum number of hits to collect
	   */
	  public YoukuTopFieldDocCollector(IndexReader reader, Sort sort, int numHits,float[] seconds ,SecondFilter filter)
	    throws IOException {
	    super(numHits, new FieldSortedHitQueue(reader, sort.fields, numHits));
	    this.secondFilter = filter;
	    this.seconds = seconds;
	  }

	  // javadoc inherited
	  public void collect(int doc, float score) {
	    if (score > 0.0f) {
//	    	if (filter!= null && !filter.isAnd())
//	    	{
////	    		System.out.println(VideoQueryManager.getInstance().vids[doc]+"="+score);
//		    		if (score<filter.getMinScore())
//		    			return;
//	    	}
	    	if (secondFilter!= null){
	    		if (!secondFilter.checkValue(seconds[doc])){
	    			return;
	    		}
	    	}
	      totalHits++;
	      if (reusableFD == null)
	        reusableFD = new FieldDoc(doc, score);
	      else {
	        // Whereas TopDocCollector can skip this if the
	        // score is not competitive, we cannot because the
	        // comparators in the FieldSortedHitQueue.lessThan
	        // aren't in general congruent with "higher score
	        // wins"
	        reusableFD.score = score;
	        reusableFD.doc = doc;
	      }
	      reusableFD = (FieldDoc) hq.insertWithOverflow(reusableFD);
	    }
	  }

	  // javadoc inherited
	  public TopDocs topDocs() {
	    FieldSortedHitQueue fshq = (FieldSortedHitQueue)hq;
	    ScoreDoc[] scoreDocs = new ScoreDoc[fshq.size()];
	    for (int i = fshq.size()-1; i >= 0; i--)      // put docs in array
	      scoreDocs[i] = fshq.fillFields ((FieldDoc) fshq.pop());

	    return new TopFieldDocs(totalHits, scoreDocs,
	                            fshq.getFields(), fshq.getMaxScore());
	  }

	  
}
