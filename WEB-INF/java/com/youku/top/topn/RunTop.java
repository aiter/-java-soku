package com.youku.top.topn;

import java.util.Date;

import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.top.topn.util.LogUtil;

public class RunTop {
	static Logger logger = Logger.getLogger(RunTop.class);
	
	public static void main(String[] args) {
		try {
			String end = null;
			String start = null;
			String before_date = null;
			if(null!=args&&args.length==3){
				start = args[0];
				end = args[1];
				before_date = args[2];
			}else{
				end = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
				start = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
				String beforeend = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-8), DataFormat.FMT_DATE_SPECIAL);
				String beforestart = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-14), DataFormat.FMT_DATE_SPECIAL);
				before_date = new StringBuilder(beforestart).append("_").append(beforeend).toString();
			}
			long s1=System.currentTimeMillis();
			LogUtil.init();
			logger.info("--end--log4j初始化完成,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s1));
			
			logger.info("参数,start:"+start+",end:"+end+",before_date:"+before_date);
			
			logger.info("sleep20s---start---");
			Thread.sleep(20*1000);
			logger.info("sleep20s---end---");
			
			long s2=System.currentTimeMillis();
			KeywordWeekMerge kwm = new KeywordWeekMerge();
			kwm.merge(start, end);
			logger.info("--end--关键词合并完成,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s2));
			
			logger.info("sleep20s---start---");
			Thread.sleep(20*1000);
			logger.info("sleep20s---end---");
			
			long s3=System.currentTimeMillis();
			KeywordWeekUnion ku=new KeywordWeekUnion();
			ku.insertTopKeywords(start, end);
			logger.info("--end--关键词统计预处理完成,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s3));
			
			logger.info("sleep20s---start---");
			Thread.sleep(20*1000);
			logger.info("sleep20s---end---");
			
			long s4=System.currentTimeMillis();
			TopMailSender tms=new TopMailSender();
			tms.mailSend(new StringBuilder(start).append("_").append(end).toString(),before_date);
			logger.info("--end--top500邮件发送完成,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s4));
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
