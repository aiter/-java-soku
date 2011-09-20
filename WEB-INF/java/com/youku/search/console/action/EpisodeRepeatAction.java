package com.youku.search.console.action;

import java.util.List;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.operate.juji.EpisodeVideoRepeatMgt;
import com.youku.search.console.vo.EpisodeRepeatVO;

public class EpisodeRepeatAction {
	List<EpisodeRepeatVO> repeatVideos;
	
	public String list(){
		repeatVideos=EpisodeVideoRepeatMgt.getInstance().getEpisodeArrs();
		return Action.SUCCESS;
	}

	public List<EpisodeRepeatVO> getRepeatVideos() {
		return repeatVideos;
	}

	public void setRepeatVideos(List<EpisodeRepeatVO> repeatVideos) {
		this.repeatVideos = repeatVideos;
	}
	
	
}
