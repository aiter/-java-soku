package com.youku.search.sort.log;

import static com.youku.search.log.LogFactory.getLogger;

import com.youku.search.log.Logger;
import com.youku.search.sort.search.LogInfo;

public class RemoteLogger {

	// 优酷站内搜索的查询日志
	public static final Logger youkuQuery = getLogger("query_log");
	public static final Logger youkuClick = getLogger("youku_click_log");

	// soku的点击、搜索日志
	public static final Logger sokuClick = getLogger("click_log");
	public static final Logger sokuQuery = getLogger("query_log");

	public static void log(Logger logger, LogInfo info) {
		log(logger, info.getLogString());
	}

	public static void log(Logger logger, String info) {
		if (!info.endsWith("\n")) {
			info = info + "\n";
		}
		
		if (null != logger) {
			logger.log(info);
		}
	}
}
