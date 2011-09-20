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

public class ProgrammeSiteHdSynTimer {
	private final Logger log = Logger.getLogger(this.getClass());

	public void start() {
		Timer timer = new Timer(true);

		final long delay = 60L * 1000; // one hour
		final long period = 24 * 60 * 1000 * 60; // 24 hour

		TimerTask task = new TimerTask() {
			public void run() {
				log.debug("******* begin syn hd in programme_site ********");
				int count = synProgrammeSiteHd();
				log.debug("******* syn hd in programme_site:" + count
						+ " ********");
			}
		};

		timer.schedule(task, delay, period);
	}

	private int synProgrammeSiteHd() {
		int count = 0;
		try {
			// 获取所有hd=0的节目-站点
			Criteria crit = new Criteria();
			crit.add(ProgrammeSitePeer.HD, 0);
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(crit);
			if (null == psList || psList.size() == 0)
				return count;
			for (ProgrammeSite ps : psList) {
				// 获取节目-站点的所有剧集
				Criteria allcrit = new Criteria();
				allcrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps
						.getId());
				List<ProgrammeEpisode> allpe = ProgrammeEpisodePeer
						.doSelect(allcrit);
				// 获取节目-站点的所有高清剧集
				Criteria hdcrit = new Criteria();
				hdcrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps
						.getId());
				hdcrit.add(ProgrammeEpisodePeer.HD, 1);
				List<ProgrammeEpisode> hdpe = ProgrammeEpisodePeer
						.doSelect(hdcrit);
				//若所有剧集和高清剧集大于1且二者size()相当,则该节目-站点的hd为1
				if (allpe != null && hdpe != null && allpe.size() > 0
						&& allpe.size() == hdpe.size()) {
					ps.setHd(1);
					count++;
				}else
					ps.setHd(0);
				ProgrammeSitePeer.doUpdate(ps);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
}
