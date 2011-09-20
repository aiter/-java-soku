package com.youku.soku.manage.bo;

public class VideoInfoBo {

	private int id;

	private int fkNamesId;

	private String name;

	private String serialAlias;

	private int category;

	private String categoryName;

	private String versionName;

	private String versionAlias;

	private String area;

	private int order;

	private int block;

	/** added on May 20 */
	private String createTime;

	private String haibao;

	private int haveRight;

	private int complete;

	private String brief;
	
	private int episodeTotal;

	
	//add on 2010-02-24
	private int contentId;
	
	private String source;
	
	private String updateTime;
	
	private String releaseDate;
	
	private String showDescription;
	
	private String thumb;
	
	private String poster;
	
	private String performers;
	
	private String nameAlias;
	
	private String seriesName;
	
	private int seriesId;
	
	private int seriesOrder;
	
	private int defaultDisplaySite;
	
	private String defaultDisplaySiteName;
	
	private String genre;
	
	private String directors;
	
	private int concernLevel;
	
	//add on 2011-04-07
	private String alias;
	
	//add on 2011-05-07
	private String hosts;  //主持人
	
	//add on 2011-05-13
	private int paid;  //是否付费
	
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

	public int getPaid() {
		return paid;
	}

	public void setPaid(int paid) {
		this.paid = paid;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getConcernLevel() {
		return concernLevel;
	}

	public void setConcernLevel(int concernLevel) {
		this.concernLevel = concernLevel;
	}

	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directors) {
		this.directors = directors;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getDefaultDisplaySite() {
		return defaultDisplaySite;
	}

	public void setDefaultDisplaySite(int defaultDisplaySite) {
		this.defaultDisplaySite = defaultDisplaySite;
	}

	public int getSeriesOrder() {
		return seriesOrder;
	}

	public void setSeriesOrder(int seriesOrder) {
		this.seriesOrder = seriesOrder;
	}

	public int getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getNameAlias() {
		return nameAlias;
	}

	public void setNameAlias(String nameAlias) {
		this.nameAlias = nameAlias;
	}

	public String getPerformers() {
		return performers;
	}

	public void setPerformers(String performers) {
		this.performers = performers;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getHaibao() {
		return haibao;
	}

	public void setHaibao(String haibao) {
		this.haibao = haibao;
	}

	public int getHaveRight() {
		return haveRight;
	}

	public void setHaveRight(int haveRight) {
		this.haveRight = haveRight;
	}

	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getEpisodeTotal() {
		return episodeTotal;
	}

	public void setEpisodeTotal(int episodeTotal) {
		this.episodeTotal = episodeTotal;
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

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionAlias() {
		return versionAlias;
	}

	public void setVersionAlias(String versionAlias) {
		this.versionAlias = versionAlias;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getFkNamesId() {
		return fkNamesId;
	}

	public void setFkNamesId(int fkNamesId) {
		this.fkNamesId = fkNamesId;
	}
	
	public String getSerialAlias() {
		return serialAlias;
	}

	public void setSerialAlias(String serialAlias) {
		this.serialAlias = serialAlias;
	}

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getShowDescription() {
		return showDescription;
	}

	public void setShowDescription(String showDescription) {
		this.showDescription = showDescription;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}
	

	public String getDefaultDisplaySiteName() {
		return defaultDisplaySiteName;
	}

	public void setDefaultDisplaySiteName(String defaultDisplaySiteName) {
		this.defaultDisplaySiteName = defaultDisplaySiteName;
	}

	@Override
	public String toString() {
		return "VideoInfoBo [area=" + area + ", block=" + block + ", brief="
				+ brief + ", category=" + category + ", categoryName="
				+ categoryName + ", complete=" + complete + ", contentId="
				+ contentId + ", createTime=" + createTime
				+ ", defaultDisplaySite=" + defaultDisplaySite + ", directors="
				+ directors + ", episodeTotal=" + episodeTotal + ", fkNamesId="
				+ fkNamesId + ", genre=" + genre + ", haibao=" + haibao
				+ ", haveRight=" + haveRight + ", id=" + id + ", name=" + name
				+ ", nameAlias=" + nameAlias + ", order=" + order
				+ ", performers=" + performers + ", poster=" + poster
				+ ", releaseDate=" + releaseDate + ", serialAlias="
				+ serialAlias + ", seriesId=" + seriesId + ", seriesName="
				+ seriesName + ", seriesOrder=" + seriesOrder
				+ ", showDescription=" + showDescription + ", source=" + source
				+ ", thumb=" + thumb + ", updateTime=" + updateTime
				+ ", versionAlias=" + versionAlias + ", versionName="
				+ versionName + "]";
	}
	
	
}
