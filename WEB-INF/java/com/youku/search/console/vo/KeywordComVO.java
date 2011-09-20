package com.youku.search.console.vo;

import java.util.LinkedList;
import java.util.List;

public class KeywordComVO {
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
	String israte;
	String osrate1;
	String osrate2;
	String osrate;
	String ocrate;
	String ocrate1;
	String ocrate2;
	List<KeywordComVO> keywords = new LinkedList<KeywordComVO>();
	int inavg;
	int outavg;
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
	public List<KeywordComVO> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<KeywordComVO> keywords) {
		this.keywords = keywords;
	}
	public String getIsrate() {
		return israte;
	}
	public void setIsrate(String israte) {
		this.israte = israte;
	}
	public String getOsrate() {
		return osrate;
	}
	public void setOsrate(String osrate) {
		this.osrate = osrate;
	}
	public String getOsrate1() {
		return osrate1;
	}
	public void setOsrate1(String osrate1) {
		this.osrate1 = osrate1;
	}
	public String getOsrate2() {
		return osrate2;
	}
	public void setOsrate2(String osrate2) {
		this.osrate2 = osrate2;
	}
	public String getOcrate() {
		return ocrate;
	}
	public void setOcrate(String ocrate) {
		this.ocrate = ocrate;
	}
	public String getOcrate1() {
		return ocrate1;
	}
	public void setOcrate1(String ocrate1) {
		this.ocrate1 = ocrate1;
	}
	public String getOcrate2() {
		return ocrate2;
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
