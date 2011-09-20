/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

/**
 * @author william
 *
 */
public class Folder implements Serializable{
	public int pk_folder;
	public String title;
//	public String description;
	public int owner;
	public String owner_name ;
	public int cate_ids;
	public long update_time;
	public int video_count;
	public int total_pv;
	public String logo;
	public String total_seconds;
	public int total_comment;
	public String[] video_order_no;
	public String[] video_title;
	public String[] video_seconds;
	public String[] video_md5;
	public String tags;
	
	public String title_hl;
    public String tags_hl;
	
	public float score;
}
