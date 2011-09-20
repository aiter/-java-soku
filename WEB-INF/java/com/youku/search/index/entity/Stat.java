/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

/**
 * @author william
 *
 */
public class Stat implements Serializable{
	public int id;
	public String keyword;
	public int query_type;
	public int query_count;
	public int result;
	public float score;
}
