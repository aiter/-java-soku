package com.youku.soku.manage.bo.paihangbang;

import java.util.Date;

public class KeywordTypeVO {
    private int id;
    private int programmeId;
    private String keyword;
    private int cate;
    private int queryCount;
    private int visible = 1;
    private String pic;
    private String url;
    private Date topDate;
    private int istop = 1;
    private Date createTime;
    
    private String programmeName;
    private String cname;
    private int isMulu = 0;
    
	public int getIsMulu() {
		return isMulu;
	}
	public void setIsMulu(int isMulu) {
		this.isMulu = isMulu;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProgrammeId() {
		return programmeId;
	}
	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getCate() {
		return cate;
	}
	public void setCate(int cate) {
		this.cate = cate;
	}
	public int getQueryCount() {
		return queryCount;
	}
	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getTopDate() {
		return topDate;
	}
	public void setTopDate(Date topDate) {
		this.topDate = topDate;
	}
	public int getIstop() {
		return istop;
	}
	public void setIstop(int istop) {
		this.istop = istop;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getProgrammeName() {
		return programmeName;
	}
	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
    
    
}
