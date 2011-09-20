package com.youku.soku.manage.bo;

import java.util.List;

import com.youku.soku.library.load.EpisodeLog;

public class AuditEpisodeLogBo {
	
	private int id;
	
	private String name;
	
	private String cateName;
	
	private int cateId;
	
	private int concernLevel;
	
	private List<SiteEpisodeLog> siteEpisodeLogList;
	
	private List<SiteEpisodeLog> hasEpisodeSiteIds;
	


	public int getConcernLevel() {
		return concernLevel;
	}

	public void setConcernLevel(int concernLevel) {
		this.concernLevel = concernLevel;
	}

	public List<SiteEpisodeLog> getHasEpisodeSiteIds() {
		return hasEpisodeSiteIds;
	}

	public void setHasEpisodeSiteIds(List<SiteEpisodeLog> hasEpisodeSiteIds) {
		this.hasEpisodeSiteIds = hasEpisodeSiteIds;
	}

	public List<SiteEpisodeLog> getSiteEpisodeLogList() {
		return siteEpisodeLogList;
	}

	public void setSiteEpisodeLogList(List<SiteEpisodeLog> siteEpisodeLogList) {
		this.siteEpisodeLogList = siteEpisodeLogList;
	}

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

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public int getCateId() {
		return cateId;
	}

	public void setCateId(int cateId) {
		this.cateId = cateId;
	}


	public static class SiteEpisodeLog {
		
		private int siteId;
		
		private String siteName;
		
		private List<EpisodeLog> episodeLogList;

		public int getSiteId() {
			return siteId;
		}

		public void setSiteId(int siteId) {
			this.siteId = siteId;
		}

		public String getSiteName() {
			return siteName;
		}

		public void setSiteName(String siteName) {
			this.siteName = siteName;
		}

		public List<EpisodeLog> getEpisodeLogList() {
			return episodeLogList;
		}

		public void setEpisodeLogList(List<EpisodeLog> episodeLogList) {
			this.episodeLogList = episodeLogList;
		}
		
		
	}
	
	
}
