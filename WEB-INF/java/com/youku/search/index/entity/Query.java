/**
 * 
 */
package com.youku.search.index.entity;

import com.youku.search.util.Constant;

/**
 * @author william
 * 查询接口
 */
public class Query extends BaseQuery {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1607528718676045681L;
	public String keywords ;	//关键字
	public int category;		//所在分类
	public String categories;		//多个所在分类,逗号分隔
	public int partner;			//合作方id
	public int field;			//搜索类型
	public int operator = Constant.Operator.AND;		//搜索且还是或 默认 and
	public boolean needAnalyze = true;		//是否需要分词
	public String orderFieldStr;	// 前端的排序字段
	
	// 按发布时间过滤（高级查询里的limitDate也暂时保留）
	// add by gaosong 2011-07-13
	public String limitDate;
	
	public String toString() {
		return( start
		  + "_" + end
		  + "_" + sort
		  + "_" + reverse
		  + "_" + category
		  + "_" + categories
		  + "_" + field
		  + "_" + ftype
		  + "_" + keywords
		  + "_" + partner
		  + "_" + needAnalyze
		  + "_" + operator
		  + "_" + timeless
		  + "_" + timemore
		  + "_" + limitDate
		  );
	}
	
	public Query clone(){
		try {
			return (Query)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
