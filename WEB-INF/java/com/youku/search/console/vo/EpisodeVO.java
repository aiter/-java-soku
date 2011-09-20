package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.List;

public class EpisodeVO {
	String title;
	int tid;
	int pid;
	int collect_count;
	int total_count;
	String firstlogo;
	int subcate;
	
	List<EpisodeSingleVO>  evo=new ArrayList<EpisodeSingleVO>();
	
	List<EpisodeSingleVO>  vevo=new ArrayList<EpisodeSingleVO>();
	
	public int getSubcate() {
		return subcate;
	}
	public void setSubcate(int subcate) {
		this.subcate = subcate;
	}
	public int getCollect_count() {
		return collect_count;
	}
	public void setCollect_count(int collect_count) {
		this.collect_count = collect_count;
	}
	public int getTotal_count() {
		return total_count;
	}
	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}
	public List<EpisodeSingleVO> getVevo() {
		return vevo;
	}
	public void setVevo(List<EpisodeSingleVO> vevo) {
		this.vevo = vevo;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<EpisodeSingleVO> getEvo() {
		return evo;
	}
	public void setEvo(List<EpisodeSingleVO> evo) {
		this.evo = evo;
	}
	public String getFirstlogo() {
		return firstlogo;
	}
	public void setFirstlogo(String firstlogo) {
		this.firstlogo = firstlogo;
	}
	
}
