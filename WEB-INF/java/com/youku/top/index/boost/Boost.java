/**
 * 
 */
package com.youku.top.index.boost;

/**
 * @author 1verge
 *
 */
public class Boost {

	private String key;
	private float boost;
	
	public Boost(String key,float boost)
	{
		this.key = key;
		this.boost = boost;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public float getBoost() {
		return boost;
	}
	public void setBoost(float boost) {
		this.boost = boost;
	}
	
	
}
