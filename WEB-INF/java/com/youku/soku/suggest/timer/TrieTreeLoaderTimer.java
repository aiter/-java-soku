package com.youku.soku.suggest.timer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.suggest.data.TrieTreeHolder;
import com.youku.soku.suggest.data.loader.TrieTreeLoader;
import com.youku.soku.suggest.trie.TrieTree;

public class TrieTreeLoaderTimer {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private static final long delay = 1000 * 2;
	
	private static final long period = 1000 * 60 * 60;
	
	private static int counter = 0;
	
	public void start() {
		
		
		log.info("TrieTree Loader Timer Begin...... ");
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Calendar calendar = Calendar.getInstance();  
			       
				if(calendar.get(Calendar.HOUR_OF_DAY) == 7 || counter == 0){
					
					log.info("开始加载 TrieTree .....Counter: " + counter);
					TrieTree tree = new TrieTree();
					TrieTreeLoader loader = new TrieTreeLoader(tree);
					loader.init();
					TrieTreeHolder.setCurreantTrieTree(tree);
					/*ObjectSaverAndLoader saver = new ObjectSaverAndLoader("/opt/trietree/", "trietree");
					saver.save(tree);*/
					log.info("TrieTree 加载成功");
					counter++;
				}
			
			}
			
		};
		Timer timer = new Timer();
		timer.schedule(task, delay, period);
	}

}
