package com.youku.top.topn.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LogUtil {
	
	public static void init(String file) throws IOException{
		 final String pattern = "%d %5p [%c] (%C:%L) - %m%n";
	        final String datePattern = "'.'yyyy-MM-dd";

	        System.out.println("config root logger...");
	        Logger rootLogger = Logger.getRootLogger();
	        rootLogger.setLevel(Level.INFO);
	        PatternLayout layout = new PatternLayout(pattern);
	        String logfile = new File(file).getAbsolutePath();
	        Appender appender = new DailyRollingFileAppender(layout, logfile,
	                datePattern);
	        rootLogger.addAppender(appender);
	        rootLogger.info("root logger config complete!");
	}
	
	public static void init() throws IOException{
		 init("/opt/log_analyze/top500/log/log.txt");
	}
}
