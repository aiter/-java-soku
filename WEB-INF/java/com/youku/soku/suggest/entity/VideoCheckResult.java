package com.youku.soku.suggest.entity;

public class VideoCheckResult {
	
	private boolean isVideo;
	
	private String viewUrl;
	
	private boolean isTeleplay;
	
	private boolean isMovie;
	
	private boolean isAnime;
	
	private boolean isVariety;
	
	private String teleplayName;
	
	private String movieName;
	
	private String animeName;
	
	private String varietyName;

	public boolean isMovie() {
		return isMovie;
	}

	public void setMovie(boolean isMovie) {
		this.isMovie = isMovie;
	}

	public String getTeleplayName() {
		return teleplayName;
	}

	public void setTeleplayName(String teleplayName) {
		this.teleplayName = teleplayName;
	}

	public boolean isVideo() {
		return isVideo;
	}

	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	public boolean isTeleplay() {
		return isTeleplay;
	}

	public void setTeleplay(boolean isTeleplay) {
		this.isTeleplay = isTeleplay;
	}
	
	

	public boolean isAnime() {
		return isAnime;
	}

	public void setAnime(boolean isAnime) {
		this.isAnime = isAnime;
	}

	public boolean isVariety() {
		return isVariety;
	}

	public void setVariety(boolean isVariety) {
		this.isVariety = isVariety;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getAnimeName() {
		return animeName;
	}

	public void setAnimeName(String animeName) {
		this.animeName = animeName;
	}

	public String getVarietyName() {
		return varietyName;
	}

	public void setVarietyName(String varietyName) {
		this.varietyName = varietyName;
	}

	@Override
	public String toString() {
		return "VideoCheckResult [animeName=" + animeName + ", isAnime=" + isAnime + ", isMovie=" + isMovie + ", isTeleplay=" + isTeleplay + ", isVariety="
				+ isVariety + ", isVideo=" + isVideo + ", movieName=" + movieName + ", teleplayName=" + teleplayName + ", varietyName=" + varietyName
				+ ", viewUrl=" + viewUrl + "]";
	}

	
	
}
