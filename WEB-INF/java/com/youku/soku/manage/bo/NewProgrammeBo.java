package com.youku.soku.manage.bo;

import java.util.Date;
import java.util.List;

public class NewProgrammeBo {
	
	private int id;
	
	private int siteId;
	
	private String name;
	
	private int cate;
	
	private String alias;
	
	private String type;
	
	private String director;
	
	private String year;
	
	private String actor;
	
	private String image;
	
	private int hd;
	
	private int episodeTotal;
	
	private int episodeCollected;
	
	private String playDefault;
	
	private Date creatTime;
	
	private List<NewProgrammeEpisode> urls;
	
	private String siteName;
	
	private String cateName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getHd() {
		return hd;
	}

	public void setHd(int hd) {
		this.hd = hd;
	}

	public int getEpisodeTotal() {
		return episodeTotal;
	}

	public void setEpisodeTotal(int episodeTotal) {
		this.episodeTotal = episodeTotal;
	}

	public int getEpisodeCollected() {
		return episodeCollected;
	}

	public void setEpisodeCollected(int episodeCollected) {
		this.episodeCollected = episodeCollected;
	}

	public String getPlayDefault() {
		return playDefault;
	}

	public void setPlayDefault(String playDefault) {
		this.playDefault = playDefault;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public List<NewProgrammeEpisode> getUrls() {
		return urls;
	}

	public void setUrls(List<NewProgrammeEpisode> urls) {
		this.urls = urls;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}
	
	

}
