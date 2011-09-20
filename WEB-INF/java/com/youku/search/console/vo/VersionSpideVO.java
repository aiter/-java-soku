package com.youku.search.console.vo;

public class VersionSpideVO {
	/** The value for the id field */
    private int id;

    /** The value for the status field */
    private int status = 0;

    /** The value for the name field */
    private String teleName;

    /** The value for the deeltime field */
    private String update_time;

    private String versionName;
    
    private int fkTeleplayId;
    
	public int getFkTeleplayId() {
		return fkTeleplayId;
	}

	public void setFkTeleplayId(int fkTeleplayId) {
		this.fkTeleplayId = fkTeleplayId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTeleName() {
		return teleName;
	}

	public void setTeleName(String teleName) {
		this.teleName = teleName;
	}
	

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
    
    
}
