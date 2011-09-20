/**
 * 
 */
package com.youku.search.index.entity;


/**
 * @author william
 *
 */
public class BaseQuery extends BasePageQuery {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7304499059474670478L;
	/**
	 * 
	 */
	public int sort;//排序规则
	public boolean reverse = true; //默认倒序
	
	public int timeless;		//视频长度小于
	public int timemore;		//视频长度大于
	public int date_start;		//开始日期时间戳除以1000，包含此点
	public int date_end; 		//结束日期时间戳除以1000，不包含此点
	
	public String ftype;			//移动搜索类型mp4,3gp
	
	public boolean highlight = false;
	public String hl_prefix = null;
	public String hl_suffix = null;
	
	public int limit_level;		//过滤加密视频
	
	public String exclude_cates;
}
