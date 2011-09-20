/**
 * 
 */
package com.youku.search.index.entity;

import com.youku.search.util.Constant;

/**
 * @author william
 *
 */
public class SpecialQuery extends BaseQuery{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4407177713799422906L;
	public String title ;	//title
	public String tags ;	//tags
	public int category;		//分类
	public int partner;			//合作方id
	public int field;			//搜索类型
	public int operator = Constant.Operator.AND;		//搜索且还是或 默认 and
	public boolean needAnalyze = true;		//是否需要分词
	
	public String toString() {
		return( start
		  + "_" + end
		  + "_" + sort
		  + "_" + reverse
		  + "_" + category
		  + "_" + field
		  + "_" + title
		  + "_" + tags
		  + "_" + partner
		  + "_" + needAnalyze
		  + "_" + operator
		  );
	}
	
	public SpecialQuery clone(){
		try {
			return (SpecialQuery)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
