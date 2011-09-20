package com.youku.soku.manage.bo;

import java.util.Date;

public class NamesBo {
	
	 /** The value for the id field */
    private int id;

    /** The value for the name field */
    private String name;

    /** The value for the alias field */
    private String alias;

    /** The value for the fkCateId field */
    private int fkCateId;

    /** The value for the excluding field */
    private String excluding;

    /** The value for the createTime field */
    private Date createTime;

    /** The value for the updateTime field */
    private Date updateTime;
    
    /**
     * the display value of category
     */
    private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getFkCateId() {
		return fkCateId;
	}

	public void setFkCateId(int fkCateId) {
		this.fkCateId = fkCateId;
	}

	public String getExcluding() {
		return excluding;
	}

	public void setExcluding(String excluding) {
		this.excluding = excluding;
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
    
}
