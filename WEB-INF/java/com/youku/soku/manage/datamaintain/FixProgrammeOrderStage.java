package com.youku.soku.manage.datamaintain;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.EpisodeLogPeer;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.common.Constants;

public class FixProgrammeOrderStage {

	private static Logger logger = Logger.getLogger(FixProgrammeOrderStage.class);

	public static void fix() {
		try {
			List<Programme> pList = ProgrammePeer.doSelect(new Criteria());
			for (Programme p : pList) {

				Criteria psCrit = new Criteria();
				psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());

				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
				boolean deleteProgramme = false;
				for (ProgrammeSite ps : psList) {
					Criteria peCrit = new Criteria();
					peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());

					List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
					for (ProgrammeEpisode pe : peList) {
						if (pe.getOrderStage() == 0) {

							if (p.getCate() != Constants.VARIETY_CATE_ID) {
								pe.setOrderStage(pe.getOrderId());
								ProgrammeEpisodePeer.doUpdate(pe);
								logger.info("update stage order: " + pe.getId() + p.getName() + "cate: " + p.getCate());
							} else { // 修正审核时没有设置orderStage的错误数据
								Criteria logCrit = new Criteria();
								logCrit.add(EpisodeLogPeer.URL, pe.getUrl());
								List<EpisodeLog> logList = EpisodeLogPeer.doSelect(logCrit);
								if (logList != null && !logList.isEmpty()) {
									int orderStage = 0;
									for (EpisodeLog log : logList) {
										orderStage = log.getOrderStage();
										if (orderStage > 0) {
											break;
										}
									}
									
									pe.setOrderStage(orderStage);
									ProgrammeEpisodePeer.doUpdate(pe);
									logger.info("update variety stage order: " + pe.getId() + p.getName() + "cate: " + p.getCate());
					
								}
							}
						}

					}
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
