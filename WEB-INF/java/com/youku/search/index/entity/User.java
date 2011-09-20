/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

/**
 * @author william
 *
 */
public class User implements Serializable{
	public int pk_user;
	public String user_name;
	public int gender;
	public int city;
	public String icon64;
	public int user_score;
	public int video_count;
	public int order_count;
	public long last_login_date;
	public long last_content_date;
	public long reg_date;
	public int fav_count;
	
	public float score;
}
