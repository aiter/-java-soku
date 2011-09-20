/**
 * 
 */
package com.youku.search.index.query;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.PriorityQueue;

import com.youku.search.util.DataFormat;

/**
 * @author 1verge
 *
 */
public class BarTopDocCollector  extends HitCollector {

	private ScoreDoc reusableSD;
	
	  int totalHits;
	  PriorityQueue hq;
	 
	  /** Construct to collect a given number of hits.
	   * @param numHits the maximum number of hits to collect
	   */
	  public BarTopDocCollector(int numHits) {
	    this.hq = new YoukuHitQueue(numHits);
	  }

	  // javadoc inherited
	  public void collect(int doc, float score) {
	    if (score > 0.0f) {
	    	try {
				  float boost = DataFormat.parseFloat(BarQueryManager.getInstance().getIndexReader().document(doc).get("boost"),0);
				  score += boost;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	  
//	  public HashMap<Integer,Integer> getCateMap()
//	  {
//		  return cateMap;
//	  }

}
