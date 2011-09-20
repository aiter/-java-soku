package com.youku.search.console.operate.juji;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TeleplaySpiderCheckService {
	static Log logger = LogFactory.getLog(TeleplaySpiderCheckService.class);

	private TeleplaySpiderCheckService() {
	}

	private static TeleplaySpiderCheckService instance = null;

	public static synchronized TeleplaySpiderCheckService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new TeleplaySpiderCheckService();
			return instance;
		}
	}
	
	
}
