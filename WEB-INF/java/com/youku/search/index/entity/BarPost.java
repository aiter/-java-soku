/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

/**
 * @author william
 *
 */
public class BarPost implements Serializable{
	public int pk_post ;
	public int pk_bar ;
	public int fk_subject;
	public String bar_name;
	public String subject;
	public String content;
	public int videoId;
	public String encodeVid ;
	public String videologo ;
	public int first;
	public int floor;
	public int poster_id;
	public String poster_name;
	public long post_time;
	public long last_post_time;
	public float score;
	
}
