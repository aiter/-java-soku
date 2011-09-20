package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.List;

public class FeedBackHandleVO {
	int pkFeedbackId;
	int total;
	String keyword;
	EpisodeSingleVO evo=new EpisodeSingleVO();
	List<FeedBackVO> feedbackVo=new ArrayList<FeedBackVO>();
	int page;
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<FeedBackVO> getFeedbackVo() {
		return feedbackVo;
	}
	public void setFeedbackVo(List<FeedBackVO> feedbackVo) {
		this.feedbackVo = feedbackVo;
	}
	public int getPkFeedbackId() {
		return pkFeedbackId;
	}
	public void setPkFeedbackId(int pkFeedbackId) {
		this.pkFeedbackId = pkFeedbackId;
	}
	public EpisodeSingleVO getEvo() {
		return evo;
	}
	public void setEvo(EpisodeSingleVO evo) {
		this.evo = evo;
	}
	
	
}
