package com.youku.soku.manage.bo;

import java.util.Date;

public class EpisodeAuditLogBo {
	  /** The value for the id field */
    private int id;

    /** The value for the fkVersionId field */
    private int fkVersionId;

    /** The value for the cateId field */
    private int cateId;

    /** The value for the completeHistory field */
    private int completeHistory = 0;

    /** The value for the completeNow field */
    private int completeNow = 0;

    /** The value for the concernLevel field */
    private int concernLevel = 0;

    /** The value for the operation field */
    private int operation;

    /** The value for the completeSiteIds field */
    private String completeSiteIds;

    /** The value for the addEpisodeIds field */
    private String addEpisodeIds;

    /** The value for the deleteEpisodeIds field */
    private String deleteEpisodeIds;

    /** The value for the source field */
    private int source = 0;

    /** The value for the additionHistory field */
    private String additionHistory;

    /** The value for the deletionHistory field */
    private String deletionHistory;

    /** The value for the updateTime field */
    private Date updateTime;

    /** The value for the createTime field */
    private Date createTime;
    
    
    private String addEpisodeDetail;
    
    private String deleteEpisodeDetail;
    
    private String additionHistoryDetail;
    
    private String deletionHistoryDetail;
    
    private String episodeName;
    
    

	public String getEpisodeName() {
		return episodeName;
	}

	public void setEpisodeName(String episodeName) {
		this.episodeName = episodeName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFkVersionId() {
		return fkVersionId;
	}

	public void setFkVersionId(int fkVersionId) {
		this.fkVersionId = fkVersionId;
	}

	public int getCateId() {
		return cateId;
	}

	public void setCateId(int cateId) {
		this.cateId = cateId;
	}

	public int getCompleteHistory() {
		return completeHistory;
	}

	public void setCompleteHistory(int completeHistory) {
		this.completeHistory = completeHistory;
	}

	public int getCompleteNow() {
		return completeNow;
	}

	public void setCompleteNow(int completeNow) {
		this.completeNow = completeNow;
	}

	public int getConcernLevel() {
		return concernLevel;
	}

	public void setConcernLevel(int concernLevel) {
		this.concernLevel = concernLevel;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public String getCompleteSiteIds() {
		return completeSiteIds;
	}

	public void setCompleteSiteIds(String completeSiteIds) {
		this.completeSiteIds = completeSiteIds;
	}

	public String getAddEpisodeIds() {
		return addEpisodeIds;
	}

	public void setAddEpisodeIds(String addEpisodeIds) {
		this.addEpisodeIds = addEpisodeIds;
	}

	public String getDeleteEpisodeIds() {
		return deleteEpisodeIds;
	}

	public void setDeleteEpisodeIds(String deleteEpisodeIds) {
		this.deleteEpisodeIds = deleteEpisodeIds;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public String getAdditionHistory() {
		return additionHistory;
	}

	public void setAdditionHistory(String additionHistory) {
		this.additionHistory = additionHistory;
	}

	public String getDeletionHistory() {
		return deletionHistory;
	}

	public void setDeletionHistory(String deletionHistory) {
		this.deletionHistory = deletionHistory;
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

	public String getAddEpisodeDetail() {
		return addEpisodeDetail;
	}

	public void setAddEpisodeDetail(String addEpisodeDetail) {
		this.addEpisodeDetail = addEpisodeDetail;
	}

	public String getDeleteEpisodeDetail() {
		return deleteEpisodeDetail;
	}

	public void setDeleteEpisodeDetail(String deleteEpisodeDetail) {
		this.deleteEpisodeDetail = deleteEpisodeDetail;
	}

	public String getAdditionHistoryDetail() {
		return additionHistoryDetail;
	}

	public void setAdditionHistoryDetail(String additonHistoryDetail) {
		this.additionHistoryDetail = additonHistoryDetail;
	}

	public String getDeletionHistoryDetail() {
		return deletionHistoryDetail;
	}

	public void setDeletionHistoryDetail(String deletionHistoryDetail) {
		this.deletionHistoryDetail = deletionHistoryDetail;
	}
    
    
    
}
