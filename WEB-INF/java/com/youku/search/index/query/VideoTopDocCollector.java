/**
 * 
 */
package com.youku.search.index.query;

import org.apache.lucene.search.ScoreDoc;

import com.youku.search.index.boost.BoostReader;

/**
 * @author 1verge
 *
 */
public class VideoTopDocCollector extends YoukuTopDocCollector{

	static final float MAXDOC= 1000000000f;
	/**
	 * @param numHits
	 */
	public VideoTopDocCollector(int numHits) {
		super(numHits);
	}
	
	SecondFilter secondFilter = null;
	float[] seconds = null;
	public VideoTopDocCollector(int numHits,boolean useBoost,int[] pks,float[] seconds ,SecondFilter filter) {
		super(numHits,useBoost,pks);
		this.secondFilter = filter;
		this.seconds = seconds;
	}
	
	 public void collect(int doc, float score) {
		    if (score > 0.0f) {
		    	if(useBoost){
			    	if (score > 0.01){
			    		int pk = pks[doc];
			    		float boost = BoostReader.getBoost(pk);
			    		if (boost > 0)
			    			score = boost ;
			    		else
			    		{
			    			score = (float)Math.sin(pk/MAXDOC);
			    		}
			    	}
//			    	else
//			    		score = 0f;
		    	}
		    	
		    	if (secondFilter!= null){
		    		if (!secondFilter.checkValue(seconds[doc])){
		    			return;
		    		}
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
		      
//		      //统计分类
//		      try {
//		    	  int cate = Integer.parseInt(VideoQueryManager.getInstance().getIndexReader().document(doc).get("categories"));
//		    	  if (!cateMap.containsKey(cate))
//		    		  cateMap.put(cate,1);
//		    	  else
//		    		  cateMap.put(cate,cateMap.get(cate)+1);
//		    	  
//				} catch (Exception e) {
//					e.printStackTrace();
//				} 
		    }
		  }
}
