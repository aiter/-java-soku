package com.youku.search.console.vo;

public class FeedBackVO {
	/** The value for the id field */
    private int id;
      
    /** The value for the fkVersionId field */
    private int fkVersionId;
      
    /** The value for the episodeId field */
    private int episodeId;
      
    /** The value for the videoId field */
    private int videoId;
      
    /** The value for the errorType field */
    private int errorType;
    
    private String keyword;
    
    private String url;
    
    private String errorContent;
    
    private int feedbacknum;
    
    int total;

    String lastModefyDate;
    
	public String getLastModefyDate() {
		return lastModefyDate;
	}

	public void setLastModefyDate(String lastModefyDate) {
		this.lastModefyDate = lastModefyDate;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getErrorContent() {
		return errorContent;
	}

	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}

	public int getFeedbacknum() {
		return feedbacknum;
	}

	public void setFeedbacknum(int feedbacknum) {
		this.feedbacknum = feedbacknum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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

	public int getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(int episodeId) {
		this.episodeId = episodeId;
	}

	public int getVideoId() {
		return videoId;
	}

	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

}
