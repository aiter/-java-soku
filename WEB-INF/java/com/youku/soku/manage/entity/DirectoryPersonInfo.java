package com.youku.soku.manage.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class DirectoryPersonInfo implements Serializable{
	
	private int id;
	
	private int fk_person_id;
	
	private String name;
	
	private String head_logo;
	
	private String logo;
	
	private int search_nums;
	
	private int union_nums;
	
	private int visible;
	
	private int order_number;
	
	private Timestamp update_date;
	
	private Date top_date;
	
	private final String channelName = "person";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFk_person_id() {
		return fk_person_id;
	}

	public void setFk_person_id(int fkPersonId) {
		fk_person_id = fkPersonId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHead_logo() {
		return head_logo;
	}

	public void setHead_logo(String headLogo) {
		head_logo = headLogo;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getSearch_nums() {
		return search_nums;
	}

	public void setSearch_nums(int searchNums) {
		search_nums = searchNums;
	}

	public int getUnion_nums() {
		return union_nums;
	}

	public void setUnion_nums(int unionNums) {
		union_nums = unionNums;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public int getOrder_number() {
		return order_number;
	}

	public void setOrder_number(int orderNumber) {
		order_number = orderNumber;
	}

	public Timestamp getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Timestamp updateDate) {
		update_date = updateDate;
	}

	public Date getTop_date() {
		return top_date;
	}

	public void setTop_date(Date topDate) {
		top_date = topDate;
	}

	public String getChannelName() {
		return channelName;
	}
	
	
	
	
}
