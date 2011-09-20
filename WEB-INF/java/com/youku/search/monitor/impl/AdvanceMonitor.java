/**
 * 
 */
package com.youku.search.monitor.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.monitor.Result;
import com.youku.search.util.Request;

/**
 * @author 1verge
 * 高级搜索监控
 */
public class AdvanceMonitor extends AMonitor{

	public AdvanceMonitor() {
		super();
		super.period = 1000*60*30; //30分钟执行一次
	}
	
	/* (non-Javadoc)
	 * @see com.youku.search.monitor.Monitor#check()
	 */
	@Override
	public Result check() {
		Result r = new Result();
		String url = "http://"+ Config.getYoukuHost() +"/search?type=1&keyword=%7B%2Bmm%7D&pagesize=1&advance=1&_source=monitor";
		String result = Request.requestGet(url);
		if (result != null)
		{
			JSONObject json = null;
			try {
				json = new JSONObject(result);
				int total = json.getInt("total");
				int cost = json.getInt("cost");
				boolean miss = json.getBoolean("miss");
				if( total < 100 || cost >= 2000 || miss)
				{
					r.setMessage(url + "\n "+result);
					r.setOk(false);
				}
				
			} catch (JSONException e) {
//				e.printStackTrace();
//				r.setMessage(url);
//				r.setException(e);
//				r.setOk(false);
			}
		}
		return r;
	
	}

	
}
