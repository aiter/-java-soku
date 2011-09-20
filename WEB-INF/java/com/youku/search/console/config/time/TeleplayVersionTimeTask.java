package com.youku.search.console.config.time;

import java.util.Calendar;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TeleplayVersionTimeTask implements ServletContextListener {
	/**
	 * 无延迟
	 */
//	public static final long NO_DELAY = DelayTime.getDelayTime(1000 * 60*60*24, 0,true);
//	public static final long NO_DELAY = 0;
	/**
	 * 定时器
	 */
	private Timer timer;

	/**
	 * 在Web应用启动时初始化任务
	 */
	public void contextInitialized(ServletContextEvent event) {
		// 定义定时器
		timer = new Timer("电视剧版本自动抓取", true);
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
		date.set(Calendar.HOUR_OF_DAY, 5);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		timer.scheduleAtFixedRate(new TeleplayVersionListener(),date.getTime(), 1000 * 60*60*24*7L);
	}

	/**
	 * 在Web应用结束时停止任务
	 */
	public void contextDestroyed(ServletContextEvent event) {
		timer.cancel(); // 定时器销毁
	}
}