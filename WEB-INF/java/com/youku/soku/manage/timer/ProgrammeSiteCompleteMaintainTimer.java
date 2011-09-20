package com.youku.soku.manage.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.manage.datamaintain.FixProgrammeSiteCompleteFlag;
import com.youku.soku.manage.datamaintain.ProgrammeSearchNumberLoader;

public class ProgrammeSiteCompleteMaintainTimer {
	
	private Logger logger = Logger.getLogger(this.getClass());
	public void start() {
		Timer timer = new Timer();
		
		final long delay = 12 * 1000L;
		final long period = 60 * 1000 * 15L;
		
		TimerTask task = new TimerTask() {
			public void run() {
				
				logger.info("站点版本收录完成，收录集数字段修正  start");
				long start = System.currentTimeMillis();
				FixProgrammeSiteCompleteFlag.fix();
				long end = System.currentTimeMillis();
				
				logger.info("站点版本收录完成，收录集数字段修正  end cost: " + (end - start));
				
			}
		};
		
		timer.schedule(task, delay, period);
	}

}
