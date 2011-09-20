/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author william
 *
 */
public class Result<T> implements Serializable {
	
	private static final long serialVersionUID = 4671136214738094275L;
	public int totalCount ;
	public List<T> results = null;  //结果集
	public boolean hasNext = false; //是否已经取完，加此字段在客户端判断此属性，尽可能减少不必要的查询
	public int timecost;	// 索引服务器耗时
	public Serializable extra = null;
	
	/**
	 * 为洗脸Log记录的统计项 <br>
	 * 0-最新精确个数、1-最新总个数；2-最热精确个数、3-最热总个数；4-经典精确个数、5-经典总个数；6-type=0&&power15>2784个数、7-type=0总个数
	 */
	public int[] statCount;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\ntotalCount=").append(totalCount);
		sb.append("\ntimecost=").append(timecost);
		sb.append("\nextra=").append(extra);
		sb.append("\nresults=").append(results.toString());
		sb.append('\n').append('}');
		
		return sb.toString();
	}
}
