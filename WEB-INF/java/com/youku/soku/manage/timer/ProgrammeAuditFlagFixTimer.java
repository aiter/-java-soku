package com.youku.soku.manage.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.manage.datamaintain.ProgrammeSearchNumberLoader;
import com.youku.soku.manage.service.AuditEpisodeLogService;

public class ProgrammeAuditFlagFixTimer {
	

	
	private Logger logger = Logger.getLogger(this.getClass());
	public void start() {
		Timer timer = new Timer();
		
		final long delay = 15 * 1000L;
		final long period = 60 * 1000 * 60 * 2L;
		
		TimerTask task = new TimerTask() {
			public void run() {

				logger.info("节目审核完成标记修正  start");
				long start = System.currentTimeMillis();
				try {
					AuditEpisodeLogService.fixAuditFlag();
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
				long end = System.currentTimeMillis();
				
				logger.info("节目审核完成标记修正  end cost: " + (end - start));
			
				
			
			}
		};
		
		timer.schedule(task, delay, period);
	}



}
