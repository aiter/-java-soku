package com.youku.search.console.vo;

import java.util.HashMap;
import java.util.Map;

public class KeywordQueryVO {
	String keyword;
	int insearchs;
	int inresults;
	int outsearchs=0;
	int outsearchs1;
	int outsearchs2;
	int outresults;
	int outclicks=0;
	int outclicks1;
	int outclicks2;
	int maxsearchs=0;
	int outviews=0;
	int outviews1;
	int outviews2;
	String osrate;
	String osrate1;
	String osrate2;
	String ocrate;
	String ocrate1;
	String ocrate2;
	Map<Integer,KeywordComVO> keywords = new HashMap<Integer,KeywordComVO>();
	int inavg=0;
	int outavg=0;
	long insum;
	long outsum;
	
	
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
		if(getInsearchs()==0) return "-";
		else return String.format("%.2f",100.0*getOutsearchs()/getInsearchs());
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
	public String getOsrate1() {
		if(getOutsearchs()==0) return "-";
		else return String.format("%.2f",100.0*getOutsearchs1()/getOutsearchs());
	}
	public void setOsrate1(String osrate1) {
		this.osrate1 = osrate1;
	}
	public String getOsrate2() {
		if(getOutsearchs()==0) return "-";
		else return String.format("%.2f",100.0*getOutsearchs2()/getOutsearchs());
	}
	public void setOsrate2(String osrate2) {
		this.osrate2 = osrate2;
	}
	public String getOcrate() {
		if(getOutsearchs()==0) return "-";
		else return String.format("%.2f",100.0*getOutclicks()/getOutsearchs());
	}
	public void setOcrate(String ocrate) {
		this.ocrate = ocrate;
	}
	public String getOcrate1() {
		if(getOutsearchs1()==0) return "-";
		else return String.format("%.2f",100.0*getOutclicks1()/getOutsearchs1());
	}
	public void setOcrate1(String ocrate1) {
		this.ocrate1 = ocrate1;
	}
	public String getOcrate2() {
		if(getOutsearchs2()==0) return "-";
		else return String.format("%.2f",100.0*getOutclicks2()/getOutsearchs2());
	}
	public void setOcrate2(String ocrate2) {
		this.ocrate2 = ocrate2;
	}
	public int getOutviews() {
		if(0==outviews)
			return outviews1+outviews2;
		else return outviews;
	}
	public void setOutviews(int outviews) {
		this.outviews = outviews;
	}
	public int getOutviews1() {
		return outviews1;
	}
	public void setOutviews1(int outviews1) {
		this.outviews1 = outviews1;
	}
	public int getOutviews2() {
		return outviews2;
	}
	public void setOutviews2(int outviews2) {
		this.outviews2 = outviews2;
	}
	public int getMaxsearchs() {
		if(0==maxsearchs)
			return Math.max(getInsearchs(), getOutsearchs());
		else return maxsearchs;
	}
	public void setMaxsearchs(int maxsearchs) {
		this.maxsearchs = maxsearchs;
	}
	public int getOutsearchs1() {
		return outsearchs1;
	}
	public void setOutsearchs1(int outsearchs1) {
		this.outsearchs1 = outsearchs1;
	}
	public int getOutsearchs2() {
		return outsearchs2;
	}
	public void setOutsearchs2(int outsearchs2) {
		this.outsearchs2 = outsearchs2;
	}
	public int getOutclicks1() {
		return outclicks1;
	}
	public void setOutclicks1(int outclicks1) {
		this.outclicks1 = outclicks1;
	}
	public int getOutclicks2() {
		return outclicks2;
	}
	public void setOutclicks2(int outclicks2) {
		this.outclicks2 = outclicks2;
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
		if(outclicks==0)
			return outclicks1+outclicks2;
		else return outclicks;
	}
	public void setOutclicks(int outclicks) {
		this.outclicks = outclicks;
	}
}
