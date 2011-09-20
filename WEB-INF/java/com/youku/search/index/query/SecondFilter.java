/**
 * 
 */
package com.youku.search.index.query;

/**
 * @author 1verge
 *
 */
public class SecondFilter {
	float less ;
	float more ;
	public SecondFilter(float less,float more)
	{
		if (less > 0)
			this.less = less;
		if (more > 0)
			this.more = more;
		
	}
	
	public boolean checkValue(float second)
	{
		if (more > 0 && less > 0){
			return second >= more && second <= less;
		}
		else if (more > 0){
			return second >= more;
		}
		else if (less > 0){
			return second <= less;
		}
		return false;
	}
	
}
