package com.youku.soku.library.episode;

import java.util.List;

import com.youku.soku.library.load.ProgrammeEpisode;

/**
 * 节目详细信息，具体到每一集
 */
public class ProgrammeDetail {

	private int programmeId;

	/**
	 * 站内剧集接口，只需要用到youku站点
	 */
	private int programmeSiteId;

	private List<ProgrammeEpisode> programmeEpisodes;

	/**
	 * 是否锁定，预留字段
	 */
	private int locked;

	public int getProgrammeId() {
		return programmeId;
	}

	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}

	public int getProgrammeSiteId() {
		return programmeSiteId;
	}

	public void setProgrammeSiteId(int programmeSiteId) {
		this.programmeSiteId = programmeSiteId;
	}

	public List<ProgrammeEpisode> getProgrammeEpisodes() {
		return programmeEpisodes;
	}

	public void setProgrammeEpisodes(List<ProgrammeEpisode> programmeEpisodes) {
		this.programmeEpisodes = programmeEpisodes;
	}

	public int getLocked() {
		return locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

}
