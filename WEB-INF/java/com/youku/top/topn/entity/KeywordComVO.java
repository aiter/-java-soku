package com.youku.top.topn.entity;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class KeywordComVO {
	private String keyword;
	private int insearchs;
	private int inresults;
	private int outsearchs=0;
	private int outresults;
	private int outclicks=0;
	private int maxsearchs=0;
	//站内搜索上升率
	private String israte;
	//站外搜索上升率
	private String osrate;
	//站外点击比上升率
	private String ocrate;
	//站外站内搜索比
	private String oisrate;
	//站外站内点击比
	private String oicrate;
	
	//占总数比
	private String uisrate;
	//占总数比
	private String uosrate;
	//占总数比
	private String uocrate;
	
	List<KeywordComVO> keywords = new LinkedList<KeywordComVO>();
	int inavg;
	int outavg;
	long insum;
	long outsum;
	
	//站内搜索是否上升
	boolean israteIsIncre = false;
	//站外搜索是否上升
	boolean osrateIsIncre  = false;
	//站外点击是否上升
	boolean ocrateIsIncre  = false;
	
	public String getOcrate() {
		return ocrate;
	}
	public void setOcrate(String ocrate) {
		this.ocrate = ocrate;
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
	public boolean isOcrateIsIncre() {
		return ocrateIsIncre;
	}
	public void setOcrateIsIncre(boolean ocrateIsIncre) {
		this.ocrateIsIncre = ocrateIsIncre;
	}
	public String getUisrate() {
		if(StringUtils.isBlank(uisrate))
			return "/";
		else return uisrate;
	}
	public void setUisrate(String uisrate) {
		this.uisrate = uisrate;
	}
	public String getUosrate() {
		if(StringUtils.isBlank(uosrate))
			return "/";
		else return uosrate;
	}
	public void setUosrate(String uosrate) {
		this.uosrate = uosrate;
	}

	public String getUocrate() {
		if(StringUtils.isBlank(uocrate))
			return "/";
		else return uocrate;
	}
	public void setUocrate(String uocrate) {
		this.uocrate = uocrate;
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
		return maxsearchs;
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
	public String getOsrate() {
		if(StringUtils.isBlank(osrate))
			return "/";
		else return osrate;
	}
	public void setOsrate(String osrate) {
		this.osrate = osrate;
	}
	public String getOisrate() {
		if(StringUtils.isBlank(oisrate))
			return "/";
		else return oisrate;
	}
	public void setOisrate(String oisrate) {
		this.oisrate = oisrate;
	}
	public String getOicrate() {
		if(StringUtils.isBlank(oicrate))
			return "/";
		else return oicrate;
	}
	public void setOicrate(String oicrate) {
		this.oicrate = oicrate;
	}
	public List<KeywordComVO> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<KeywordComVO> keywords) {
		this.keywords = keywords;
	}
	public int getInavg() {
		if(inavg>0)
			return inavg;
		if(this.insearchs<1)
			return 0;
		return (int)(this.insum/this.insearchs);
	}
	public void setInavg(int inavg) {
		this.inavg = inavg;
	}
	public int getOutavg() {
		if(outavg>0)
			return outavg;
		if(this.outsearchs<1)
			return 0;
		return (int)(this.outsum/this.outsearchs);
	}
	public void setOutavg(int outavg) {
		this.outavg = outavg;
	}
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
	
	
}
