package com.youku.search.console.config.time;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.operate.log.LogDateRead;

public class DailyUnionListener  extends TimerTask{
	private static Log log = LogFactory.getLog(DailyUnionListener.class);
	private static boolean isRunning = false;
	
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			long s=System.currentTimeMillis();
			System.out.println(new Date()+"--start--每日各种搜索统计数据start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			new LogDateRead().timeTask();
			System.out.println(new Date()+"--end--每日各种搜索统计数据end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}

}
