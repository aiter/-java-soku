/**
 * 
 */
package com.youku.search.index.schedule;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.db.PartnerManager;
import com.youku.search.index.db.SynonymManager;
import com.youku.search.index.db.UserManager;
import com.youku.search.index.db.VideoPermissionManager;

/**
 * 合作方表定时扫描
 */
public class IndexManagerTimer{
	Log logger = LogFactory.getLog(getClass());

	public void start() {
		Timer timer = new Timer(true);

		final long delay = 10 * 1000;// 10秒
		final long period = 60 *60 * 1000;// 1800秒

		TimerTask task = new TimerTask() {
			public void run() {
				try {
					//启动定时更新合作方Map
					logger.info("Partner is reloading.....");
					PartnerManager.getInstance().init();
					logger.info("Partner load over.....");
					
					//启动定时更新同义词Map
					logger.info("Synonym is reloading.....");
					SynonymManager.getInstance().init();
					logger.info("Synonym load over.....");
					
					
					//启动定时更新限制用户
					logger.info("Limit User is reloading.....");
					UserManager.getInstance().loadLimitUser();
					logger.info("Limit User load over.....");
					
					//启动定时更新视频屏蔽表
					logger.info("Video Permission is reloading.....");
					VideoPermissionManager.getInstance().loadPermission();
					logger.info("Video Permission load over.....");
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		};
		timer.schedule(task, delay, period);
	}
	
}
