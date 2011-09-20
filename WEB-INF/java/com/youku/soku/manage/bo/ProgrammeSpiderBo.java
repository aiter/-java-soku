package com.youku.soku.manage.bo;

public class ProgrammeSpiderBo {
	private int id;
	private String name;
	private String state;
	private int cate;
	private int episode_total;

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

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getEpisode_total() {
		return episode_total;
	}

	public void setEpisode_total(int episodeTotal) {
		episode_total = episodeTotal;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
