package com.youku.search.console.config.time;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 将版权音乐导出，搜索结果，保存结果到memcached中，供搜索使用
 * @author liuyunjian
 * 2010-11-15
 */
public class CopyrightMusicTimeTask implements ServletContextListener {
	/**
	 */
	long delay = 10 * 1000; 
	long period = 115 * 60 * 1000;
	/**
	 * 定时器
	 */
	private Timer timer;

	/**
	 * 在Web应用启动时初始化任务
	 */
	public void contextInitialized(ServletContextEvent event) {
		// 定义定时器
		timer = new Timer("Load Copyright music to memcached", true);
		timer.schedule(new CopyrightMusicListener(), delay, period);
	}

	/**
	 * 在Web应用结束时停止任务
	 */
	public void contextDestroyed(ServletContextEvent event) {
		timer.cancel(); // 定时器销毁
	}
}