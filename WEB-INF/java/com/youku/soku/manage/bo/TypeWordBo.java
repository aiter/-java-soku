package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

public class TypeWordBo {
	

	int id;
	String keyword;
	String channel_names;
	int blocked;
	String checker;
	Date create_date;
	Date update_date;
	String fk_channel_name;
	
	
	String labels;
	
	String pic;
	
	private List<String> typeList;
	
	
	public List<String> getTypeList() {
		return typeList;
	}
	public void setTypeList(List<String> typeList) {
		this.typeList = typeList;
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
	public String getChannel_names() {
		return channel_names;
	}
	public void setChannel_names(String channelNames) {
		channel_names = channelNames;
	}
	public int getBlocked() {
		return blocked;
	}
	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date createDate) {
		create_date = createDate;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date updateDate) {
		update_date = updateDate;
	}
	
	public String getLabels() {
		return labels;
	}
	public void setLabels(String labels) {
		this.labels = labels;
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
}
