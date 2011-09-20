package com.youku.search.console.config.time;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ReverseLatestTimeTask implements ServletContextListener {
	/**
	 * 无延迟
	 */
	public static final long NO_DELAY = DelayTime.getDelayTime(1000 * 60*60*24L, 5,true);
//	public static final long NO_DELAY = 1000;
	/**
	 * 定时器
	 */
	private Timer timer;

	/**
	 * 在Web应用启动时初始化任务
	 */
	public void contextInitialized(ServletContextEvent event) {
		// 定义定时器
		timer = new Timer("根据file_id添加episode_video数据，latest upload", true);
		timer.schedule(new ReverseLatestListener(), NO_DELAY, 1000*60*60*24L);
	}

	/**
	 * 在Web应用结束时停止任务
	 */
	public void contextDestroyed(ServletContextEvent event) {
		timer.cancel(); // 定时器销毁
	}
}