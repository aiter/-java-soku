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
public class Bar implements Serializable{
	//bar id
	public int pk_bar;
	//bar name
	public String bar_name;
	//bar 分类
	public List<Integer> bar_catalog_ids;
	public List<String> bar_catalog_names;
	//视频id
	public int[] video_ids;
	//视频logo
	public String[] video_logos;
	//encode video id
	public String[] encodeVids;
	//视频标题
	public String[] video_titles;
	//最后帖子id
	public int[] subject_ids;
	//最后帖子名称
	public String[] subjects;
	//最后回复会员id
	public int[] last_poster_ids;
	//最后回复会员名称
	public String[] last_poster_name;
	//最后回复时间
	public long[] last_post_time;
	//会员数
	public int count_member;
	//帖子数
	public int count_subject;
	//视频数
	public int count_video;
	//pv数
	public int total_pv;
	//命中分
	public float score ; 
}
