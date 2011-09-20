package com.youku.soku.manage.bo;

public class SokuFeedbackVO {
	String keyword;
	int count;
	int source;
	int state;
	String feeduniondate;

	public String getFeeduniondate() {
		return feeduniondate;
	}

	public void setFeeduniondate(String feeduniondate) {
		this.feeduniondate = feeduniondate;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
