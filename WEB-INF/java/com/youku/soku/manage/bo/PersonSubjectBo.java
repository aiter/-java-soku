package com.youku.soku.manage.bo;

import java.util.Date;

public class PersonSubjectBo {
	
	/** The value for the id field */
    private int id;

    /** The value for the fkPersonId field */
    private int fkPersonId;

    /** The value for the subjectId field */
    private int subjectId;

    /** The value for the tabName field */
    private String tabName;

    /** The value for the fkRoleId field */
    private int fkRoleId;

    /** The value for the createTime field */
    private Date createTime;
    
	/** The value for the updateTime field */
    private Date updateTime;
    
    /** the value for the personName */
    private String personName;
    
    /** the value for the roleName */
    private String roleName;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkPersonId() {
		return fkPersonId;
	}

	public void setFkPersonId(int fkPersonId) {
		this.fkPersonId = fkPersonId;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public int getFkRoleId() {
		return fkRoleId;
	}

	public void setFkRoleId(int fkRoleId) {
		this.fkRoleId = fkRoleId;
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

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	   public String toString()
	    {
	        StringBuffer str = new StringBuffer();
	        str.append("PersonSubjectBo:\n");
	        str.append("Id = ")
	           .append(getId())
	           .append("\n");
	        str.append("FkPersonId = ")
	           .append(getFkPersonId())
	           .append("\n");
	        str.append("SubjectId = ")
	           .append(getSubjectId())
	           .append("\n");
	        str.append("TabName = ")
	           .append(getTabName())
	           .append("\n");
	        str.append("FkRoleId = ")
	           .append(getFkRoleId())
	           .append("\n");
	        str.append("CreateTime = ")
	           .append(getCreateTime())
	           .append("\n");
	        str.append("UpdateTime = ")
	           .append(getUpdateTime())
	           .append("\n");
	        str.append("personname = ")
	        	.append(getPersonName())
	        	.append("\n");
	        str.append("roleName = ")
	        	.append(getRoleName())
	        	.append("\n");
	        return(str.toString());
	    }
}
