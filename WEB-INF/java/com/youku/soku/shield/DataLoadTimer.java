package com.youku.soku.shield;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.search.pool.net.util.Cost;

public class DataLoadTimer {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public void start() {
		Timer timer = new Timer(true);
		
		final long delay = 5 * 1000;   // 5 seconds
		final long period = 15 * 1000 * 60;  // 15min
		
		
		timer.schedule(new Task(), delay, period);
		
		
	}
	
	class Task extends TimerTask {
		public void run() {
			logger.info("开始加载屏蔽词信息 ......");
			Cost cost = new Cost();
			
			ShieldWordsInfo wordsInfo = new ShieldWordsInfo();
			new DataLoader(wordsInfo).init();
			ShieldWordsHolder.setCurrentWordsInfo(wordsInfo);
			
			cost.updateEnd();
			logger.info("屏蔽词加载完成......(cost " + cost.getCost() + ")");
		}
	}
}
