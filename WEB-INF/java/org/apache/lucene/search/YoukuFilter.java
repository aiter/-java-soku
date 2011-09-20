/**
 * 
 */
package org.apache.lucene.search;

/**
 * @author 1verge
 *
 */
public class YoukuFilter {
	
	public YoukuFilter(boolean isAnd,float minScore){
		this.isAnd = isAnd;
		this.minScore = minScore;
	}
	
	private boolean isAnd;//操作符 是否并操作
	private float minScore ; //最小得分
	
	
	public boolean isAnd() {
		return isAnd;
	}
	public void setAnd(boolean isAnd) {
		this.isAnd = isAnd;
	}
	public float getMinScore() {
		return minScore;
	}
	public void setMinScore(float minScore) {
		this.minScore = minScore;
	}
	
	
}
