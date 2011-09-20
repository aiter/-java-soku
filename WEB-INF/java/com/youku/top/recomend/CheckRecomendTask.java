package com.youku.top.recomend;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CheckRecomendTask implements ServletContextListener {
	
	/**
	 * 延迟
	 */
	public static final long DELAY = 1000*60*1;
	/**
	 * 定时器
	 */
	private Timer timer;

	/**
	 * 应用启动时初始化任务
	 */
	public void contextInitialized(ServletContextEvent event) {
		// 定义定时器
		timer = new Timer("搜索联想数据树检测", true);
		timer.schedule(new CheckRecomendListener(), DELAY, 1000*60*60*2);
	}

	/**
	 * 在应用结束时停止任务
	 */
	public void contextDestroyed(ServletContextEvent event) {
		timer.cancel(); // 定时器销毁
	}
}