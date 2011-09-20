package com.youku.soku.manage.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.manage.datamaintain.FixProgrammeSiteCompleteFlag;
import com.youku.soku.manage.datamaintain.ProgrammeSearchNumberLoader;

public class ProgrammeSearchNumberTimer {
	
	private Logger logger = Logger.getLogger(this.getClass());
	public void start() {
		Timer timer = new Timer();
		
		final long delay = 5 * 1000L;
		final long period = 60 * 1000 * 60L;
		
		TimerTask task = new TimerTask() {
			public void run() {

				logger.info("节目搜索量更新  start");
				long start = System.currentTimeMillis();
				ProgrammeSearchNumberLoader loader = new ProgrammeSearchNumberLoader();		
				loader.init();
				long end = System.currentTimeMillis();
				
				logger.info("节目搜索量更新  end cost: " + (end - start));
			
				
			
			}
		};
		
		timer.schedule(task, delay, period);
	}

}
