package com.youku.soku.manage.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class DirectoryInfo implements Serializable{
	private int id;
	private String keyword;
	private String url = null;
	private int visible = 1;
	private String pic;
	private String fk_channel_name;
	private int search_indexs;
	private float rate;
	private int union_searchs;
	private int order_number;
	private Date top_date;
	private Timestamp create_date;
	private Timestamp update_date;
	
	
	private boolean hasDirect = false;
	
	private int fk_cate_id;
	
	private int fk_names_id;
	
	private int version_id;
	
	private int counter;
	
	
	private String cates;
	
	private int year;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getCates() {
		return cates;
	}
	public void setCates(String cates) {
		this.cates = cates;
	}
	
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getFk_cate_id() {
		return fk_cate_id;
	}
	public void setFk_cate_id(int fkCateId) {
		fk_cate_id = fkCateId;
	}
	public int getFk_names_id() {
		return fk_names_id;
	}
	public void setFk_names_id(int fkNamesId) {
		fk_names_id = fkNamesId;
	}
	public int getVersion_id() {
		return version_id;
	}
	public void setVersion_id(int versionId) {
		version_id = versionId;
	}
	public boolean isHasDirect() {
		return hasDirect;
	}
	public void setHasDirect(boolean hasDirect) {
		this.hasDirect = hasDirect;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getFk_channel_name() {
		return fk_channel_name;
	}
	public void setFk_channel_name(String fkChannelName) {
		fk_channel_name = fkChannelName;
	}
	public int getSearch_indexs() {
		return search_indexs;
	}
	public void setSearch_indexs(int searchIndexs) {
		search_indexs = searchIndexs;
	}
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int orderNumber) {
		order_number = orderNumber;
	}
	public Date getTop_date() {
		return top_date;
	}
	public void setTop_date(Date topDate) {
		top_date = topDate;
	}
	public Timestamp getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Timestamp createDate) {
		create_date = createDate;
	}
	public Timestamp getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Timestamp updateDate) {
		update_date = updateDate;
	}
	public int getUnion_searchs() {
		return union_searchs;
	}
	public void setUnion_searchs(int unionSearchs) {
		union_searchs = unionSearchs;
	}
	@Override
	public String toString() {
		return "DirectoryInfo [create_date=" + create_date + ", fk_cate_id="
				+ fk_cate_id + ", fk_channel_name=" + fk_channel_name
				+ ", fk_names_id=" + fk_names_id + ", hasDirect=" + hasDirect
				+ ", id=" + id + ", keyword=" + keyword + ", order_number="
				+ order_number + ", pic=" + pic + ", rate=" + rate
				+ ", search_indexs=" + search_indexs + ", top_date=" + top_date
				+ ", union_searchs=" + union_searchs + ", update_date="
				+ update_date + ", url=" + url + ", version_id=" + version_id
				+ ", visible=" + visible + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DirectoryInfo && ((DirectoryInfo) o).getFk_names_id() == fk_names_id) {
			return true;
		} else {
			return false;
		}
	}

	
	
}
