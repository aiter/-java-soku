package com.youku.top.recomend;

import java.util.Calendar;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RecomendTask implements ServletContextListener {
	
	public static long getDelayTime(long d,double hour,boolean f){
		Calendar calCurrent = Calendar.getInstance();
		long now;
		if(f)
			now = calCurrent.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 + calCurrent.get(Calendar.MINUTE) * 60 * 1000 + calCurrent.get(Calendar.SECOND) * 1000;
		else now = calCurrent.get(Calendar.HOUR) * 60 * 60 * 1000 + calCurrent.get(Calendar.MINUTE) * 60 * 1000 + calCurrent.get(Calendar.SECOND) * 1000;
		long run = (long) (hour * 60 * 60 * 1000);
		
		if (run < now){
			run = d-(now - run) ;
		}
		else{
			run = run - now;
		}
		return run;
	}
	/**
	 * 无延迟
	 */
	public static final long DELAY = getDelayTime(1000 * 60*60*24,2,true);
	/**
	 * 定时器
	 */
	private Timer timer;

	/**
	 * 应用启动时初始化任务
	 */
	public void contextInitialized(ServletContextEvent event) {
		// 定义定时器
		timer = new Timer("搜索联想数据树生成", true);
		timer.scheduleAtFixedRate(new RecomendListener(), DELAY, 1000*60*60*24);
	}

	/**
	 * 在应用结束时停止任务
	 */
	public void contextDestroyed(ServletContextEvent event) {
		timer.cancel(); // 定时器销毁
	}
}