package com.youku.soku.suggest.entity;

import java.io.Serializable;
import java.util.Date;

public class NamesEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7250575289069505067L;

	private String names;
	
	private String versionName;
	
	private int cate; // 1, 电视剧， 2, 电影  3, 综艺节目， 4, 动漫
	
	private int siteVersionId;
	
	private Date realeaseTime;
	
	private String thumb;
	
	private int totalEpisode;  //总集数
	
	private String performers; //演员
	
	private String directors;  //导演
	
	private String hosts;  //主持人
	
	private int programmeId;
	
	private int paid;   //是否付费
	
	private int maxOrderId;  //收录的最大集数
	

	//add on 2011-06-04   //站内剧集页id
	private String showIdStr;
	
	private boolean hasYoukuDetail;   //是否有站内节目页， 根据firstepisode_videourl字段判断

	public boolean isHasYoukuDetail() {
		return hasYoukuDetail;
	}

	public void setHasYoukuDetail(boolean hasYoukuDetail) {
		this.hasYoukuDetail = hasYoukuDetail;
	}
	

	public String getShowIdStr() {
		return showIdStr;
	}

	public void setShowIdStr(String showIdStr) {
		this.showIdStr = showIdStr;
	}

	public int getMaxOrderId() {
		return maxOrderId;
	}

	public void setMaxOrderId(int maxOrderId) {
		this.maxOrderId = maxOrderId;
	}

	public int getPaid() {
		return paid;
	}

	public void setPaid(int paid) {
		this.paid = paid;
	}

	/**
	 * can't be null   REASON: VideoInfoService.personJsonArrayToString
	 * @return
	 */
	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	/**
	 * can't be null
	 * @return
	 */
	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directors) {
		this.directors = directors;
	}

	/**
	 * can't be null
	 * @return
	 */
	public String getPerformers() {
		return performers;
	}

	public void setPerformers(String performers) {
		this.performers = performers;
	}

	public int getTotalEpisode() {
		return totalEpisode;
	}

	public void setTotalEpisode(int totalEpisode) {
		this.totalEpisode = totalEpisode;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public Date getRealeaseTime() {
		return realeaseTime;
	}

	public void setRealeaseTime(Date realeaseTime) {
		this.realeaseTime = realeaseTime;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}


	public int getSiteVersionId() {
		return siteVersionId;
	}

	public void setSiteVersionId(int siteVersionId) {
		this.siteVersionId = siteVersionId;
	}

	@Override
	public String toString() {
		return "NamesEntity [cate=" + cate + ", names=" + names + ", performers=" + performers + ", realeaseTime=" + realeaseTime + ", siteVersionId="
				+ siteVersionId + ", thumb=" + thumb + ", totalEpisode=" + totalEpisode + ", versionName=" + versionName + "]";
	}

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getProgrammeId() {
		return programmeId;
	}

	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}
	
	

}
