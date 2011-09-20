/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

/**
 * @author william
 *	查询结果对象
 */
public class Video implements Serializable{
	
	public int vid;
	public String encodeVid;
	public String title;
	public String memo;
	public String tags;
	public long createtime;
	public int total_pv;
	public int total_comment;
	public int total_fav;
//	public String rate_total;
//	public String rate_average;
//	public String tag_ids;
	public int cate_ids;
	public String logo;
    public String seconds;
    public String owner;
    public String owner_username;
    public String md5;
    public int size;
//    public String thumb3;
//    public String thumb6;
    public int public_type;
    public String ftype;
    
    public String title_hl;
    public String tags_hl;
    public String username_hl;

    public float score ; //匹配度
    public float lucenescore;
    public float youkuscore;
}
