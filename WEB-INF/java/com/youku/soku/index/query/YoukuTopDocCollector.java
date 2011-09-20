/**
 * 
 */
package com.youku.soku.index.query;

import java.util.HashSet;

import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.PriorityQueue;

import com.youku.search.index.boost.BoostReader;
import com.youku.search.index.query.SecondFilter;

/**
 * @author 1verge
 *
 */
public class YoukuTopDocCollector  extends HitCollector {
	protected ScoreDoc reusableSD;
//	private HashMap<Integer,Integer> cateMap = new HashMap<Integer,Integer>();
	
	  int totalHits;
	  boolean useBoost=true;
	  PriorityQueue hq;
	  String[] pks =  null;
	  float[] seconds = null;
	  HashSet<String> siteSet = null; 
	  boolean isCollectSite = false;
	  SecondFilter secondFilter = null;
	  
	  public YoukuTopDocCollector(int numHits,boolean isCollectSite) {
		    this(numHits, new YoukuHitQueue(numHits),false,null,isCollectSite);
		  }
	  
	  /** Construct to collect a given number of hits.
	   * @param numHits the maximum number of hits to collect
	   */
	  public YoukuTopDocCollector(int numHits,boolean useBoost,String[] keys,boolean isCollectSite,float[] seconds,SecondFilter filter) {
	    this(numHits, new YoukuHitQueue(numHits),useBoost,keys,isCollectSite);
	    this.secondFilter = filter;
	    this.seconds = seconds;
	  }

	  YoukuTopDocCollector(int numHits, PriorityQueue hq,boolean useBoost,String[] pks,boolean isCollectSite) {
	    this.hq = hq;
	    this.useBoost = useBoost;
	    this.pks = pks;
	    this.isCollectSite=isCollectSite;
	    if (isCollectSite)
	    	siteSet = new HashSet<String>();
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
    			if (useBoost)
    				score += BoostReader.getBoost(id);
    			
    			if (isCollectSite)
    				siteSet.add(id.substring(0,id.indexOf("_")));
    		}
	    	
	      totalHits++;
	      if (reusableSD == null) {
	        reusableSD = new ScoreDoc(doc, score);
	      } else if (score >= reusableSD.score) {
	        // reusableSD holds the last "rejected" entry, so, if
	        // this new score is not better than that, there's no
	        // need to try inserting it
	        reusableSD.doc = doc;
	        reusableSD.score = score;
	      } else {
	        return;
	      }
	      reusableSD = (ScoreDoc) hq.insertWithOverflow(reusableSD);
	    }
	  }

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
	  
	  	  
	  public HashSet<String> getSiteSet()
	  {
		  return siteSet;
	  }
}
