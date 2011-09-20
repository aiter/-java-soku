package com.youku.top.topn.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class KeywordQueryVO {
	private String keyword;
	private int insearchs;
	private int inresults;
	private int outsearchs=0;
	private int outresults;
	private int outclicks=0;
	private int maxsearchs=0;
	//站内搜索上升率
	private String israte;
	//站内搜索是否上升
	boolean israteIsIncre = false;
	//站外搜索上升率
	private String osrate;
	//站外搜索是否上升
	boolean osrateIsIncre  = false;
	//站外站内搜索比
	private String oisrate;
	//站外点击比
	private String ocsrate;
	//站外点击比上升率
	private String ocrate;
	//站外点击是否上升
	boolean ocrateIsIncre  = false;
	private Map<Integer,KeywordComVO> keywords = new HashMap<Integer,KeywordComVO>();
	private int inavg=0;
	private int outavg=0;
	private long insum;
	private long outsum;
	
	public long getInsum() {
		return insum;
	}
	public void setInsum(long insum) {
		this.insum = insum;
	}
	public long getOutsum() {
		return outsum;
	}
	public void setOutsum(long outsum) {
		this.outsum = outsum;
	}
	public int getInavg() {
		if(0!=inavg) return inavg;
		if(0==getInsearchs()) return 0;
		else return (int)(getInsum()/getInsearchs());
	}
	public void setInavg(int inavg) {
		this.inavg = inavg;
	}
	public int getOutavg() {
		if(0!=outavg) return outavg;
		if(0==getOutsearchs()) return 0;
		else return (int)(getOutsum()/getOutsearchs());
	}
	public void setOutavg(int outavg) {
		this.outavg = outavg;
	}
	public String getOsrate() {
		if(StringUtils.isBlank(osrate))
			return "/";
		else return osrate;
	}
	public void setOsrate(String osrate) {
		this.osrate = osrate;
	}
	public Map<Integer, KeywordComVO> getKeywords() {
		return keywords;
	}
	public void setKeywords(Map<Integer, KeywordComVO> keywords) {
		this.keywords = keywords;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getInsearchs() {
		return insearchs;
	}
	public void setInsearchs(int insearchs) {
		this.insearchs = insearchs;
	}
	public int getInresults() {
		return inresults;
	}
	public void setInresults(int inresults) {
		this.inresults = inresults;
	}
	public int getOutsearchs() {
		return outsearchs;
	}
	public void setOutsearchs(int outsearchs) {
		this.outsearchs = outsearchs;
	}
	public int getOutresults() {
		return outresults;
	}
	public void setOutresults(int outresults) {
		this.outresults = outresults;
	}
	public int getOutclicks() {
		return outclicks;
	}
	public void setOutclicks(int outclicks) {
		this.outclicks = outclicks;
	}
	public int getMaxsearchs() {
		if(0==maxsearchs)
			return Math.max(getInsearchs(), getOutsearchs());
		else return maxsearchs;
	}
	public void setMaxsearchs(int maxsearchs) {
		this.maxsearchs = maxsearchs;
	}
	public String getIsrate() {
		if(StringUtils.isBlank(israte))
			return "/";
		else return israte;
	}
	public void setIsrate(String israte) {
		this.israte = israte;
	}
	public String getOisrate() {
		if(this.insearchs==0) return "-";
		else return String.format("%.2f",100.0*(this.outsearchs)/this.insearchs);
	}
	public void setOisrate(String oisrate) {
		this.oisrate = oisrate;
	}
	public String getOcsrate() {
		if(this.outsearchs==0) return "-";
		else return String.format("%.2f",100.0*(this.outclicks)/this.outsearchs);
	}
	public void setOcsrate(String ocsrate) {
		this.ocsrate = ocsrate;
	}
	public boolean isIsrateIsIncre() {
		return israteIsIncre;
	}
	public void setIsrateIsIncre(boolean israteIsIncre) {
		this.israteIsIncre = israteIsIncre;
	}
	public boolean isOsrateIsIncre() {
		return osrateIsIncre;
	}
	public void setOsrateIsIncre(boolean osrateIsIncre) {
		this.osrateIsIncre = osrateIsIncre;
	}
	public String getOcrate() {
		if(StringUtils.isBlank(ocrate))
			return "/";
		else return ocrate;
	}
	public void setOcrate(String ocrate) {
		this.ocrate = ocrate;
	}
	public boolean isOcrateIsIncre() {
		return ocrateIsIncre;
	}
	public void setOcrateIsIncre(boolean ocrateIsIncre) {
		this.ocrateIsIncre = ocrateIsIncre;
	}
	
}
