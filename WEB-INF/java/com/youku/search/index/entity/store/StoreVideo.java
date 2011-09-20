package com.youku.search.index.entity.store;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class StoreVideo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3781798334418621850L;
	private String encodeVid;
	private String title;
	private String memo;
	private String tags;
	private long createtime;
	private int total_pv;
	private int total_comment;
	private int total_fav;
	private int cate_ids;
	private String logo;
    private int seconds;
    private int owner;
    private String owner_username;
    private String public_type;
    private String ftype;
    
	public String getEncodeVid() {
		return encodeVid;
	}

	public void setEncodeVid(String encodeVid) {
		this.encodeVid = encodeVid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public int getTotal_pv() {
		return total_pv;
	}

	public void setTotal_pv(int total_pv) {
		this.total_pv = total_pv;
	}

	public int getTotal_comment() {
		return total_comment;
	}

	public void setTotal_comment(int total_comment) {
		this.total_comment = total_comment;
	}

	public int getTotal_fav() {
		return total_fav;
	}

	public void setTotal_fav(int total_fav) {
		this.total_fav = total_fav;
	}

	public int getCate_ids() {
		return cate_ids;
	}

	public void setCate_ids(int cate_ids) {
		this.cate_ids = cate_ids;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public String getOwner_username() {
		return owner_username;
	}

	public void setOwner_username(String owner_username) {
		this.owner_username = owner_username;
	}

	
	public String getPublic_type() {
		return public_type;
	}

	public void setPublic_type(String public_type) {
		this.public_type = public_type;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getFtype() {
		return ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
    
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
