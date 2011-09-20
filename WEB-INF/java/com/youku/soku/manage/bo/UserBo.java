package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

public class UserBo {
	
	 /** The value for the userId field */
    private int userId;

    /** The value for the name field */
    private String name;

    /** The value for the password field */
    private String password;

    /** The value for the actualName field */
    private String actualName;

    /** The value for the email field */
    private String email;

    /** The value for the isActive field */
    private byte isActive;

    /** The value for the isSuperUser field */
    private byte isSuperUser;

    /** The value for the dateJoined field */
    private Date dateJoined;

    /** The value for the lastLogin field */
    private Date lastLogin;
    
    /**
     * the list of the user's permission
     */
    private List permissionList;

	public List getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(List permissionList) {
		this.permissionList = permissionList;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getActualName() {
		return actualName;
	}

	public void setActualName(String actualName) {
		this.actualName = actualName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte getIsActive() {
		return isActive;
	}

	public void setIsActive(byte isActive) {
		this.isActive = isActive;
	}

	public byte getIsSuperUser() {
		return isSuperUser;
	}

	public void setIsSuperUser(byte isSuperUser) {
		this.isSuperUser = isSuperUser;
	}

	public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
    
    
}
