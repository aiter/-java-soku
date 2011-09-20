package com.youku.soku.manage.datamaintain;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.service.ProgrammeSiteService;

public class FixProgrammeSiteCompleteFlag {
	
	private static Logger logger = Logger.getLogger(FixProgrammeSiteCompleteFlag.class);
	
	public static void fix() {
		try {
			Criteria crit = new Criteria();
			List<Programme> pList = ProgrammePeer.doSelect(crit);
			for(Programme p : pList) {
				
				Criteria pCrit = new Criteria();
				pCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(pCrit);
				if(psList != null) {
					for(ProgrammeSite ps : psList) {
						ProgrammeSiteService.updateEpisodeCollected(ps.getId());
					}
				}
			}
		}  catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
