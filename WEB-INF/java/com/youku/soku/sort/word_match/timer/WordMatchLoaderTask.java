package com.youku.soku.sort.word_match.timer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.aword.dict.Dict;
import com.youku.search.pool.net.util.Cost;
import com.youku.soku.sort.word_match.ProgramMatcher;
import com.youku.soku.sort.word_match.WordMatchLoader;
import com.youku.soku.sort.word_match.WordMatchWriterNew;
import com.youku.soku.sort.word_match.like.LikeMatcher;
import com.youku.soku.sort.word_match.prefix.PrefixMatcher;
import com.youku.soku.zhidaqu.v2.ZhidaquFinder;

public class WordMatchLoaderTask {

	private String load_dir = WordMatchWriterNew.write_dir_soku;

	Logger logger = Logger.getLogger(getClass().getName());
	private long delay = 10 * 1000;
	private long period = 60 * 1000;

	private long last = 0;

	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			Cost cost = new Cost();
			logger.info("开始加载字典数据, period: " + period + "...");

			//由于modifiers文件可能会经常维护，字典加载判断增加modifiers判断
			try {
				if (shouldLoad(WordMatchLoader.dict_movie) ||shouldLoad(WordMatchLoader.dict_modifiers)) {
					run_();
				}
				
			} catch (Exception e) {
				logger.error("加载字典数据发生异常", e);
			}

			cost.updateEnd();
			logger.info("开始加载字典数据结束, cost: " + cost.getCost());
		}

		private void run_() throws Exception {
			
			PrefixMatcher prefixMatcher = new PrefixMatcher();
			Dict dict = new WordMatchLoader(load_dir).load(prefixMatcher);
			logger.info("加载到的dict对象: " + System.identityHashCode(dict));

			if (dict != null) {
				ProgramMatcher.getSokuMatcher().setLikeMatcher( new LikeMatcher(new ZhidaquFinder(dict)));
				ProgramMatcher.getSokuMatcher().setPrefixMatcher( prefixMatcher);
			}
		}
	};

	public void start() {
		logger.info("部署定时加载字典数据任务: delay: " + delay + "; period: " + period);
		Timer timer = new Timer(true);
		timer.schedule(task, delay, period);
	}

	private boolean shouldLoad(String dict) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		File file = new File(load_dir, dict);
		if (!file.exists()) {
			logger.info("不应该加载：数据不存在，file：" + file.getAbsolutePath());
			return false;
		}

		if (file.lastModified() > last) {
			logger.info("应该加载：发现数据新版本，file：" + file.getAbsolutePath());
			last = file.lastModified();
			return true;
		}

		logger.info("不应该加载：没有发现数据新版本，file：" + file.getAbsolutePath()
				+ "；上次修改时间：" + format.format(new Date(last)));
		return false;
	}
	
	
}
