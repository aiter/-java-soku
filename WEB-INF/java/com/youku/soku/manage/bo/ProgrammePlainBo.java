package com.youku.soku.manage.bo;

import java.util.List;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.manage.service.CategoryService;

public class ProgrammePlainBo {
	private Programme programme;
	private List<ProgrammeEpisode> programmeEpisodes;

	public String getCateName() {
		if (programme == null)
			return "";
		int cate = programme.getCate();
		try {
			return CategoryService.getCategoryMap().get(cate);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Programme getProgramme() {
		return programme;
	}

	public void setProgramme(Programme programme) {
		this.programme = programme;
	}

	public List<ProgrammeEpisode> getProgrammeEpisodes() {
		return programmeEpisodes;
	}

	public void setProgrammeEpisodes(List<ProgrammeEpisode> programmeEpisodes) {
		this.programmeEpisodes = programmeEpisodes;
	}

}
