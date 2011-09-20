package com.youku.soku.library.episode;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.common.Constants;

/**
 * 加载节目详细信息
 * 
 * @author tanxiuguang
 * 
 */
public class ProgrammeDetailLoader {

	private static final String PROGRAMME_STATE_NORMAL = "normal";

	private static Logger logger = Logger.getLogger(ProgrammeDetailLoader.class);

	public static ProgrammeDetail load() {
		try {
			
			List<ProgrammeDetail> resultList = new ArrayList<ProgrammeDetail>();
			Criteria pCrit = new Criteria();
			pCrit.add(ProgrammePeer.STATE, PROGRAMME_STATE_NORMAL);

			List<Programme> programmeList = ProgrammePeer.doSelect(pCrit);
			for (Programme programme : programmeList) {
				
				Criteria psCrit = new Criteria();
				psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programme.getId());
				psCrit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);

				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
				if (!psList.isEmpty()) {
					
					ProgrammeSite youkuProgrammeSite = psList.get(0);
					ProgrammeDetail programmeDetail = new ProgrammeDetail();
					programmeDetail.setProgrammeId(programme.getId());
					programmeDetail.setProgrammeSiteId(youkuProgrammeSite.getId());
					programmeDetail.setLocked(1);  //TODO 剧集是否锁定标识，暂时现设置为1
					
					Criteria peCrit = new Criteria();
					peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, youkuProgrammeSite.getId());
					peCrit.add(ProgrammeEpisodePeer.URL, (Object) "(url is not null and url != '')", Criteria.CUSTOM);
					peCrit.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_STAGE);
					//Criteria.Criterion criterion = peCrit.getCriterion(ProgrammeEpisodePeer.URL);
					//criterion.and(peCrit.getNewCriterion(criterion.getTable(), criterion.getColumn(), "", Criteria.NOT_EQUAL));
					
					List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
					programmeDetail.setProgrammeEpisodes(peList);
					resultList.add(programmeDetail);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}


}
