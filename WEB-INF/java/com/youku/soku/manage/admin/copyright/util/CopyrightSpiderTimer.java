package com.youku.soku.manage.admin.copyright.util;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.torque.util.Criteria;

import com.youku.soku.manage.admin.copyright.load.ActConfig;
import com.youku.soku.manage.admin.copyright.load.ActConfigPeer;
import com.youku.soku.manage.admin.copyright.load.ActSearch;
import com.youku.soku.manage.admin.copyright.load.ActSearchPeer;
import common.Logger;

public class CopyrightSpiderTimer {
	private Logger log = Logger.getLogger(this.getClass());

	public void start() {
		Timer timer = new Timer();

		final long delay = 1 * 60 * 1000L;
		final long period = 30 * 1000 * 60L;

		TimerTask task = new TimerTask() {
			public void run() {
				log.debug("********* start spider copyright *******");
				int add = 0;
				int update = 0;
				try {
					List<ActConfig> confs = ActConfigPeer
							.doSelect(new Criteria());
					if (null == confs || confs.size() == 0)
						return;
					for (ActConfig conf : confs) {
						List<ActSearch> ass = CopyrightSpider.spider(conf);
						if (null == ass || ass.size() == 0)
							continue;
						for (ActSearch as : ass) {
							String id = as.getId();
							// ActSearch has_as =
							// ActSearchPeer.retrieveByPK(id);
							Criteria crit = new Criteria();
							crit.add(ActSearchPeer.ID, id);
							List<ActSearch> asList = ActSearchPeer
									.doSelect(crit);
							if (null == asList || asList.size() == 0) {
								as.save();
								add++;
							} else {
								as.setFirstCollectTime(asList.get(0).getFirstCollectTime());
								as.setLastCollectTime(new Date());
								ActSearchPeer.doUpdate(as);
								update++;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.debug("********* end spider copyright, add:" + add
						+ " update:" + update + " *******");
			}
		};

		timer.schedule(task, delay, period);
	}
}
