package com.youku.soku.manage.bo;

import java.util.List;

import com.youku.soku.library.load.ProgrammeEpisode;

public class ProgrammeEpisodeDetailBo {
	
	private int orderId;
	
	private int cate;
	
	private int orderStage;
	
	private List<ProgrammeEpisode> siteEpisode;

	
	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getOrderStage() {
		return orderStage;
	}

	public void setOrderStage(int orderStage) {
		this.orderStage = orderStage;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public List<ProgrammeEpisode> getSiteEpisode() {
		return siteEpisode;
	}

	public void setSiteEpisode(List<ProgrammeEpisode> siteEpisode) {
		this.siteEpisode = siteEpisode;
	}
	
	

}
