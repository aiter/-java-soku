package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

import com.youku.soku.library.load.KnowledgeColumn;

public class KnowledgeColumnBo {
	
	/** The value for the id field */
	private int id;

	/** The value for the name field */
	private String name;

	/** The value for the level field */
	private int level;

	/** The value for the parentId field */
	private int parentId;

	/** The value for the pic field */
	private String pic;

	/** The value for the updateTime field */
	private Date updateTime;

	/** The value for the createTime field */
	private Date createTime;
	
	private String parentName;
	
	private List<KnowledgeColumn> childColumn;

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public List<KnowledgeColumn> getChildColumn() {
		return childColumn;
	}

	public void setChildColumn(List<KnowledgeColumn> childColumn) {
		this.childColumn = childColumn;
	}
	
	
	
}
