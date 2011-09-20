/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

/**
 * @author william
 *
 */
public class Pk implements Serializable{
	public int pk_pk;
	public String pk_name;
	public String description;
	public String logo;
	public String status;
	public long begintime;
	public long endtime;
	public int owner;
	public String user_name;
	public int video_count;
	public int vote_count;
	public int actor_count;
	public int total_pv;
	public String tags;
	public String[] video_id;
	public String[] video_title;
	public String[] video_seconds;
	
	public float score;
}
