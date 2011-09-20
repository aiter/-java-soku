/**
 * 
 */
package com.youku.soku.index.entity;

import java.io.Serializable;

/**
 * @author 1verge
 *
 */
public class Video implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4776994909312731231L;
	public int id;
	public String title;
	public String tags;
	public long uploadTime;
	public String logo;
    public String seconds;
    public int hd;
    public String url;
    public String site;
    public String cate;
    
    public String title_hl;
    public String tags_hl;
    
    public float score ; //匹配度

}
