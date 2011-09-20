package com.youku.top.index.db;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.LogInfoPrinter;
import com.youku.top.merge.LogDataDay;
import com.youku.top.merge.LogDataMerger;


public class LogKeywordMgt {
	
	static Logger logger = LogInfoPrinter.stdlogger;
	public static final String dir = "/opt/search_file/web/";
	
	public LogKeywordMgt(){
		super();
	}
	
	public LogKeywordMgt(boolean delete,String uniondate){
		super();
		if(delete){
			File f = new File(dir+"keyword_"+uniondate+".txt");
			if(f.exists())
				f.delete();
		}
			
	}
	
	class MergeKeyword{
		String keyword;
		int query_count;
		int merge_query_count;
		@Override
		public String toString() {
			return "MergeKeyword [keyword=" + keyword + ", merge_query_count="
					+ merge_query_count + ", query_count=" + query_count + "]";
		}
		
	}
	
	public void logWordsGetter(String uniondate){
		logger.info("搜索指数词生成开始,date:"+uniondate);
		long t = System.currentTimeMillis();
		List<LogDataDay> logs = LogDataMerger.getInstance().getMergeQueryKeywordByTorque("merge_query_"+uniondate);
		logger.info("输出:"+logs.size());
		File file = new File(dir+"lock.txt");
		if(!file.exists()) file.mkdirs();
		for(LogDataDay ly:logs){
			Utils.appendToFile(dir+"keyword_"+uniondate+".txt", ly.getKeyword()+"\t"+ly.getQuery_count());
		}
		File dest = new File(dir+"unlock.txt");
		file.renameTo(dest);
		logger.info("搜索指数词生成结束,date:"+uniondate+",cost:"+(System.currentTimeMillis()-t)+" ms");
	}
	
	public void multLogWordsGetter(String[] uniondates){
		logger.info("搜索指数词生成开始,date:"+Arrays.toString(uniondates));
		long t = System.currentTimeMillis();
		if(null!=uniondates){
			for(String uniondate:uniondates){
				logWordsGetter(uniondate);
			}
		}
		logger.info("搜索指数词生成结束,date:"+Arrays.toString(uniondates)+",cost:"+(System.currentTimeMillis()-t)+" ms");
	}
	
	public void logWordsGetter(){
		String uniondate = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
		logWordsGetter(uniondate);
	}
}
