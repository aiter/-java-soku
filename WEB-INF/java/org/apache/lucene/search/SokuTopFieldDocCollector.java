/**
 * 
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.HashSet;

import org.apache.lucene.index.IndexReader;

import com.youku.search.index.query.SecondFilter;


/**
 * @author 1verge
 *
 */
public class SokuTopFieldDocCollector extends TopDocCollector {


	  private FieldDoc reusableFD;
	  String[] pks =  null;
	  float[] seconds = null;
	  HashSet<String> siteSet = null; 
	  boolean isCollectSite = false;
	  SecondFilter secondFilter = null;
	  
	  /** Construct to collect a given number of hits.
	   * @param reader the index to be searched
	   * @param sort the sort criteria
	   * @param numHits the maximum number of hits to collect
	   */
	  public SokuTopFieldDocCollector(IndexReader reader, Sort sort, int numHits,String[] pks,boolean isCollectSite,float[] seconds,SecondFilter filter)
	    throws IOException {
		 
		  super(numHits, new FieldSortedHitQueue(reader, sort.fields, numHits));
		  this.pks = pks;
		  this.isCollectSite = isCollectSite;
		  if (isCollectSite)
			  siteSet = new HashSet<String>();
		  this.secondFilter = filter;
		  this.seconds = seconds;
	  }

	  // javadoc inherited
	  public void collect(int doc, float score) {
		
	    if (score > 0.0f) {
	    	if (secondFilter!= null && seconds!=null){
	    		if (!secondFilter.checkValue(seconds[doc])){
	    			return;
	    		}
	    	}
	    	
	    	if (pks != null){
    			String id = pks[doc];
    			
    			if (isCollectSite)
    				siteSet.add(id.substring(0,id.indexOf("_")));
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

	public HashSet<String> getSiteSet() {
		return siteSet;
	}
	  
	  
}
