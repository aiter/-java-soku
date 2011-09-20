package com.youku.soku.manage.bo;

import java.util.Date;

public class VarietySubBo {
	
	 /** The value for the id field */
    private int id;

    /** The value for the fkVarietyId field */
    private int fkVarietyId;

    /** The value for the year field */
    private int year = 0;

    /** The value for the month field */
    private int month = 0;

    /** The value for the day field */
    private int day = 0;

    /** The value for the orderId field */
    private int orderId = 0;

    /** The value for the locked field */
    private int locked = 0;

    /** The value for the subName field */
    private String subName;

    /** The value for the createTime field */
    private Date createTime;

    /** The value for the updateTime field */
    private Date updateTime;
    
    private String varietyName;
    
    private String names;
    
    private String ymd;
    
    private VarietyEpisodeBo ve;

	public VarietyEpisodeBo getVe() {
		return ve;
	}


	public void setVe(VarietyEpisodeBo ve) {
		this.ve = ve;
	}


	public String getYmd() {
		String ymd = year + "";
		
		if(month < 10) {
			ymd += "-0" + month;
		} else {
			ymd += "-" + month;
		}
		
		if(day < 10) {
			ymd += "-0" + day;
		} else {
			ymd += "-" + day;
		}
		return ymd;
	}

	public String getYmdStr() {
		return ymd;
	}
	
	public void setYmd(String ymd) {
		this.ymd = ymd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkVarietyId() {
		return fkVarietyId;
	}

	public void setFkVarietyId(int fkVarietyId) {
		this.fkVarietyId = fkVarietyId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getLocked() {
		return locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getVarietyName() {
		return varietyName;
	}

	public void setVarietyName(String varietyName) {
		this.varietyName = varietyName;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

}
