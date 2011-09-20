package com.youku.soku.manage.common;

public class CommonVideo {
	
	private String logo;
	
	private String title;
	
	private double seconds;
	
	private int hd;
	
	private boolean isYouku;

	public boolean isYouku() {
		return isYouku;
	}

	public void setYouku(boolean isYouku) {
		this.isYouku = isYouku;
	}

	public int getHd() {
		return hd;
	}

	public void setHd(int hd) {
		this.hd = hd;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getSeconds() {
		return seconds;
	}

	public void setSeconds(double seconds) {
		this.seconds = seconds;
	}

	@Override
	public String toString() {
		return "CommonVideo [hd=" + hd + ", isYouku=" + isYouku + ", logo="
				+ logo + ", seconds=" + seconds + ", title=" + title + "]";
	}
	
	
	
}
