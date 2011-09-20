/**
 * 
 */
package com.youku.search.index.entity;



/**
 * @author william
 *
 */
public class AdvanceQuery extends BaseQuery{
	
	private static final long serialVersionUID = 6452040541947487273L;
	public String keywords ;	//关键字,表达式
	public int field;		//搜索类型,专辑或者视频
	
	public String fields;	//搜索字段,视频:title,tags_index,memo,专辑:folder_name,tags_index,description
	public String limitDate;
	public String categories; 
	public int pv;
	public int comments;
	public int hd;//是否只显示清晰视频 0全部 1清晰
	
	public String toString() {
		return( 
		  "keywords:" + keywords + "\n"
		  + "fields:" + fields + "\n"
		  + "limitDate:" + limitDate + "\n"
		  + "categories:" + categories + "\n"
		  + "pv:" + pv + "\n"
		  + "comments:" + comments + "\n"
		  + "start:" + start + "\n"
		  + "end:" + end + "\n"
		  + "sort:" + sort + "\n"
		  + "reverse:" + reverse + "\n"
		  + "field:" + field
		  );
	}
	
	public AdvanceQuery clone(){
		try {
			return (AdvanceQuery)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
