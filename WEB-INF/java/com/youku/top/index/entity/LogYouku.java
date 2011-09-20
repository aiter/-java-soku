/**
 * 
 */
package com.youku.top.index.entity;

import java.io.Serializable;


public class LogYouku  implements Serializable{
	public int id;
	public String keyword;
	public String keyword_py;
	public String query_type;
	public int query_count = 0;
	public int result;
	public double rate1;
	public double rate2;
	public float score;
}
