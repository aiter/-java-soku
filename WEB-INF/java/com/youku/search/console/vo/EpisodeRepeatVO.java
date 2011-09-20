package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.List;

public class EpisodeRepeatVO {
	List<Repeat> repeatVideo=new ArrayList<Repeat>();
	
	public Repeat getRepeat(){
		return new Repeat();
	}
	public class Repeat{
		String tname;
		String pvname;
		int order;
		String urlstr;
		public String getTname() {
			return tname;
		}
		public void setTname(String tname) {
			this.tname = tname;
		}
		public String getPvname() {
			return pvname;
		}
		public void setPvname(String pvname) {
			this.pvname = pvname;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		public String getUrlstr() {
			return urlstr;
		}
		public void setUrlstr(String urlstr) {
			this.urlstr = urlstr;
		}
		
	}


	public List<Repeat> getRepeatVideo() {
		return repeatVideo;
	}


	public void setRepeatVideo(List<Repeat> repeatVideo) {
		this.repeatVideo = repeatVideo;
	}
}
