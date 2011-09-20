/**
 * 
 */
package com.youku.soku.index.entity;

import java.io.Serializable;

/**
 * @author 1verge
 *
 */
public class Word  implements Serializable{
	public int id;
	public String keyword;
	public int query_type;
	public int query_count;
	public int result;
	public float score;
}
