/**
 * 
 */
package com.youku.search.index.entity;

import java.io.Serializable;

/**
 * @author william
 *
 */
public class Ring implements Serializable{

	private static final long serialVersionUID = -264713846360472770L;

	public String cid;
	public String cname;
	public String csinger;
	public int cprice;
	public long cdate;
	
	public float score;
}
