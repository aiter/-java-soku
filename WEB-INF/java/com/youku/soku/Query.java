/**
 * 
 */
package com.youku.soku;

import java.io.Serializable;

import com.youku.search.index.entity.BasePageQuery;
import com.youku.search.util.Constant;

/**
 * @author 1verge
 *
 */
public class Query extends BasePageQuery implements Serializable{

	private static final long serialVersionUID = 976668630199883407L;
	
	public int field;			//搜索类型
	public String keywords ;	//关键字
	
	public int hd;			//是否高清
	public int limitDate;	//时间参数
	public int site;		//来源站点ID
	public int timeLength;		//时长限制
	
	public int sort;//排序规则
	public boolean reverse = true; //默认倒序
	public int[] exclude_sites; //排除站点ID “,”逗号分隔
	public int[] include_sites;
	
	public int operator = Constant.Operator.AND;		//搜索且还是或 默认 and	
	public boolean highlight = true;
	public String hl_prefix = null;
	public String hl_suffix = null;
	
	
	public String toString() {
		return( 
		"keyword:" + keywords+"\t"
		+"start:" +	start	+"\t"
		  + "end:" + end	+"\t"
		  + "field:" + field	+"\t"
		  + "sort:" + sort	+"\t"
		  + "reverse:" + reverse	+"\t"
		  + "hd:" + hd	+"\t"
		  + "limitDate:" + limitDate	+"\t"
		  + "site:" + site	+"\t"
		  + "timeLength:" + timeLength +"\t"
		  + "exclude_sites:" + exclude_sites 
		  );
	}
	
}
