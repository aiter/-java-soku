/**
 * 
 */
package com.youku.search.index.query;

import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.PriorityQueue;

import com.youku.search.index.boost.BoostReader;

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
	  int[] pks =  null;
		  
	  public YoukuTopDocCollector(int numHits) {
		    this(numHits, new YoukuHitQueue(numHits),false,null);
		  }
	  
	  /** Construct to collect a given number of hits.
	   * @param numHits the maximum number of hits to collect
	   */
	  public YoukuTopDocCollector(int numHits,boolean useBoost,int[] keys) {
	    this(numHits, new YoukuHitQueue(numHits),useBoost,keys);
	  }

	  YoukuTopDocCollector(int numHits, PriorityQueue hq,boolean useBoost,int[] pks) {
	    this.hq = hq;
	    this.useBoost = useBoost;
	    this.pks = pks;
	  }

	  // javadoc inherited
	  public void collect(int doc, float score) {
	    if (score > 0.0f) {
	    	if (useBoost){
	    		if (pks != null)
	    			score += BoostReader.getBoost(pks[doc]);
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
	      
//	      //统计分类
//	      try {
//	    	  int cate = Integer.parseInt(VideoQueryManager.getInstance().getIndexReader().document(doc).get("categories"));
//	    	  if (!cateMap.containsKey(cate))
//	    		  cateMap.put(cate,1);
//	    	  else
//	    		  cateMap.put(cate,cateMap.get(cate)+1);
//	    	  
//			} catch (Exception e) {
//				e.printStackTrace();
//			} 
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
	  
//	  public HashMap<Integer,Integer> getCateMap()
//	  {
//		  return cateMap;
//	  }
}
