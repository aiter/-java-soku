package com.youku.search.console.vo;

public class EpisodeLogVO {
	 /** The value for the id field */
    private int id;
      
    /** The value for the keyword field */
    private String keyword;
      
    /** The value for the fkEpisodeId field */
    private int fkEpisodeId;
      
    /** The value for the createtime field */
    private String createtime;
                                          
    /** The value for the status field */
    private int status = 0;
    
    private String url;
    
    String key;
    
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getFkEpisodeId() {
		return fkEpisodeId;
	}

	public void setFkEpisodeId(int fkEpisodeId) {
		this.fkEpisodeId = fkEpisodeId;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    
}
