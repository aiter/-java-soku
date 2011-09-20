package com.youku.soku.manage.shield;

import java.util.List;

public class ShieldInfo {
	
	private String keyword;
	
	private boolean matched;
	
	private int shieldType;
	
	private int youkuEffect;
	
	private int othersEffect;
	
	private List<Integer> shieldChannel;
	
	private List<Integer> folderChannel;
	
	private List<Integer> whiteSiteList;
	
	private List<Integer> blackSiteList;
	
	

	public List<Integer> getFolderChannel() {
		return folderChannel;
	}

	public void setFolderChannel(List<Integer> folderChannel) {
		this.folderChannel = folderChannel;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public int getShieldType() {
		return shieldType;
	}

	public void setShieldType(int shieldType) {
		this.shieldType = shieldType;
	}

	public List<Integer> getShieldChannel() {
		return shieldChannel;
	}

	public void setShieldChannel(List<Integer> shieldChannel) {
		this.shieldChannel = shieldChannel;
	}

	public List<Integer> getWhiteSiteList() {
		return whiteSiteList;
	}

	public void setWhiteSiteList(List<Integer> whiteSiteList) {
		this.whiteSiteList = whiteSiteList;
	}

	public List<Integer> getBlackSiteList() {
		return blackSiteList;
	}

	public void setBlackSiteList(List<Integer> blackSiteList) {
		this.blackSiteList = blackSiteList;
	}
	
	

	public int getYoukuEffect() {
		return youkuEffect;
	}

	public void setYoukuEffect(int youkuEffect) {
		this.youkuEffect = youkuEffect;
	}

	public int getOthersEffect() {
		return othersEffect;
	}

	public void setOthersEffect(int othersEffect) {
		this.othersEffect = othersEffect;
	}

	@Override
	public String toString() {
		return "ShieldInfo [blackSiteList=" + blackSiteList
				+ ", folderChannel=" + folderChannel + ", keyword=" + keyword
				+ ", matched=" + matched + ", othersEffect=" + othersEffect
				+ ", shieldChannel=" + shieldChannel + ", shieldType="
				+ shieldType + ", whiteSiteList=" + whiteSiteList
				+ ", youkuEffect=" + youkuEffect + "]";
	}
	
	
}
