package com.youku.soku.manage.bo;

import java.util.Date;

import org.apache.commons.lang.ObjectUtils;


public class HotwordBo {
	/** Serial version */
	private static final long serialVersionUID = 1262764981171L;

	/** The value for the wordId field */
	private int wordId;

	/** The value for the name field */
	private String name;

	/** The value for the indexType field */
	private int indexType;

	/** The value for the sort field */
	private int sort;

	/** The value for the createDate field */
	private Date createDate;

	/** The value for the modifyDate field */
	private Date modifyDate;

	/** The value for the trend field */
	private String trend;

	/** The value for the itemId field */
	private int itemId;
	
	/** The value for the item name */
	private String itemName;

	
	/**
	 * 
	 * @return
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * 
	 * @param itemName
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Get the WordId
	 * 
	 * @return int
	 */
	public int getWordId() {
		return wordId;
	}

	/**
	 * Set the value of WordId
	 * 
	 * @param v
	 *            new value
	 */
	public void setWordId(int v) {

		if (this.wordId != v) {
			this.wordId = v;

		}

	}

	/**
	 * Get the Name
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value of Name
	 * 
	 * @param v
	 *            new value
	 */
	public void setName(String v) {

		if (!ObjectUtils.equals(this.name, v)) {
			this.name = v;

		}

	}

	/**
	 * Get the IndexType
	 * 
	 * @return int
	 */
	public int getIndexType() {
		return indexType;
	}

	/**
	 * Set the value of IndexType
	 * 
	 * @param v
	 *            new value
	 */
	public void setIndexType(int v) {

		if (this.indexType != v) {
			this.indexType = v;

		}

	}

	/**
	 * Get the Sort
	 * 
	 * @return int
	 */
	public int getSort() {
		return sort;
	}

	/**
	 * Set the value of Sort
	 * 
	 * @param v
	 *            new value
	 */
	public void setSort(int v) {

		if (this.sort != v) {
			this.sort = v;

		}

	}

	/**
	 * Get the CreateDate
	 * 
	 * @return Date
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * Set the value of CreateDate
	 * 
	 * @param v
	 *            new value
	 */
	public void setCreateDate(Date v) {

		if (!ObjectUtils.equals(this.createDate, v)) {
			this.createDate = v;

		}

	}

	/**
	 * Get the ModifyDate
	 * 
	 * @return Date
	 */
	public Date getModifyDate() {
		return modifyDate;
	}

	/**
	 * Set the value of ModifyDate
	 * 
	 * @param v
	 *            new value
	 */
	public void setModifyDate(Date v) {

		if (!ObjectUtils.equals(this.modifyDate, v)) {
			this.modifyDate = v;

		}

	}

	/**
	 * Get the Trend
	 * 
	 * @return String
	 */
	public String getTrend() {
		return trend;
	}

	/**
	 * Set the value of Trend
	 * 
	 * @param v
	 *            new value
	 */
	public void setTrend(String v) {

		if (!ObjectUtils.equals(this.trend, v)) {
			this.trend = v;
		}

	}

	/**
	 * Get the ItemId
	 * 
	 * @return int
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Set the value of ItemId
	 * 
	 * @param v
	 *            new value
	 */
	public void setItemId(int v) {

		if (this.itemId != v) {
			this.itemId = v;
		}

	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("Hotword:\n");
		str.append("WordId = ").append(getWordId()).append("\n");
		str.append("Name = ").append(getName()).append("\n");
		str.append("IndexType = ").append(getIndexType()).append("\n");
		str.append("Sort = ").append(getSort()).append("\n");
		str.append("CreateDate = ").append(getCreateDate()).append("\n");
		str.append("ModifyDate = ").append(getModifyDate()).append("\n");
		str.append("Trend = ").append(getTrend()).append("\n");
		str.append("ItemId = ").append(getItemId()).append("\n");
		return (str.toString());
	}
}
