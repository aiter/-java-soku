package com.youku.search.console.config.time;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.sort.VideoMusicRecommend;

public class CopyrightMusicListener  extends TimerTask{
	private static Log log = LogFactory.getLog(CopyrightMusicListener.class);
	private static boolean isRunning = false;
	
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			long s=System.currentTimeMillis();
			System.out.println(new Date()+"--start--将版权音乐导出，搜索结果，保存结果到memcached中，供搜索使用start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			
			try {
				new VideoMusicRecommend().cacheMusic();
			} catch (Exception e) {
				System.out.println("加载音乐错误："+e.getMessage());
			}
			
			System.out.println(new Date()+"--end--将版权音乐导出，搜索结果，保存结果到memcached中，供搜索使用end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}

}
