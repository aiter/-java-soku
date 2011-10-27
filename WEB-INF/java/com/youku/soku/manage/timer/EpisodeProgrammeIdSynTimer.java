package com.youku.soku.manage.timer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;

public class EpisodeProgrammeIdSynTimer {
	private final Logger log = Logger.getLogger(this.getClass());

	public void start() {
		Timer timer = new Timer(true);

		final long delay = 60 * 1000; // one min
		final long period = 4 * 60 * 1000 * 60; // 4 hour

		TimerTask task = new TimerTask() {
			public void run() {
				log.debug("******* begin syn episode programmeid ********");
				int count = 0;
				try {
					// 获取所有programmeid=0的节目剧集
					Criteria crit = new Criteria();
					crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_ID, 0);
					List<ProgrammeEpisode> peList = ProgrammeEpisodePeer
							.doSelect(crit);
					if (null == peList || peList.size() == 0)
						return;
					log.debug("*******  syn peList:"+peList.size()+"  ********");
					for (ProgrammeEpisode pe : peList) {
						// 获取节目-站点
						ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(pe
								.getFkProgrammeSiteId());
						if (null != ps) {
							pe.setFkProgrammeId(ps.getFkProgrammeId());
							pe.setNew(false);
							ProgrammeEpisodePeer.doUpdate(pe);
							count++;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.debug("******* end syn episode programmeid:" + count
						+ " ********");
			}
		};

		timer.schedule(task, delay, period);
	}
}