/**
 * 
 */
package com.youku.search.index.schedule;

import java.util.TimerTask;

import com.youku.search.index.db.PartnerManager;

/**
 * @author william
 * 合作方表定时扫描
 */
public class PartnerSchedule extends TimerTask{
	public void run() {
		System.out.println("Partner is reloading.....");
		PartnerManager.getInstance().init();
		System.out.println("Partner load over.....");
	}
}
