package com.youku.soku.sort.word_match;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import com.youku.search.pool.net.util.Cost;
import com.youku.top.new_dick.DickGetter;

public class WordMatchWriterNew {

//	public static final String write_dir = "/tmp/word_match_dict_new/";
	public static final String write_dir_youku = "/tmp/word_match_dict_youku/";
	public static final String write_dir_soku = "/tmp/word_match_dict_soku/";

	static Log logger = LogFactory.getLog(WordMatchWriterNew.class);

	/**
	 * 加载词语匹配字典
	 */
	public void loadAndWrite(String type) throws Exception {

		logger.info("[new]开始加载字典数据 and 写入文件...");
		Cost loadCost = new Cost();

		if("soku".equals(type)){
			DickGetter.writeDick(write_dir_soku,2);
		}else if("youku".equals(type)){
			DickGetter.writeDick(write_dir_youku,1);
		}else {
			logger.info("[ERROR] 参数无效！");
		}

		loadCost.updateEnd();
		logger.info("[new]加载字典数据完成 and 写入文件, cost: " + loadCost.getCost());

	}


	public static void main(String[] args) throws Exception {
		// usage
		if(args==null || args.length<2){
			System.out.println("usage: log4j youku/soku");
		}

		// logger
		String log4j = args[0];
		System.out.println("初始化log4j: " + log4j);
		DOMConfigurator.configure(log4j);

		new WordMatchWriterNew().loadAndWrite(args[1]);
	}
}
