package com.youku.search.monitor.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import com.youku.search.console.util.Wget;
import com.youku.search.monitor.Monitor;
import com.youku.search.monitor.MonitorLevel;
import com.youku.search.monitor.Person;
import com.youku.search.monitor.Result;

public class ExtIncrementStatusMonitor extends Monitor{
	protected static Log logger = LogFactory.getLog(ExtIncrementStatusMonitor.class);
	
	protected final String[] addresses = new String[]{"http://10.103.8.217/","http://10.103.8.218/"};
	
	
	public ExtIncrementStatusMonitor() {
		super(Person.tanxiuguang, MonitorLevel.error);
		super.period = 1000 * 60 * 60;
	}
	
	@Override
	public Result check() {
		Result rs = null;
		for(String addr:addresses){
				rs = checkSingle(new StringBuffer(addr).append("/ext/check").toString());
				if(null!=rs) return rs;
		}
		return new Result();
	}
	
	private Result checkSingle(String url){
		byte[] bytes = null;
		Result rs = new Result();
		rs.setOk(true);
		try {
			bytes = Wget.get(url);
			if(null == bytes){
				return rs;
			}
			String result = new String(bytes,"utf-8");
			if((new Date().getTime() - Integer.valueOf(result)) > 60 * 60 * 1000 * 5) {
				rs.setOk(false);
				rs.setMessage("增量更新错误，上次错误时间: " + result);
			}
						
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return rs;
	}
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		ExtIncrementStatusMonitor sm = new ExtIncrementStatusMonitor();
		Result rs = sm.check();
		System.out.println(rs.isOk());
	}
}
