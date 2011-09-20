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
 * 会员搜索监控
 */
public class BarpostMonitor extends AMonitor{

	public BarpostMonitor() {
		super();
		super.period = 1000*60*30; //30分钟执行一次
	}

	/* (non-Javadoc)
	 * @see com.youku.search.monitor.Monitor#check()
	 */
	@Override
	public Result check() {
		Result r = new Result();
		String url = "http://"+ Config.getYoukuHost() +"/search?type=4&keyword=%E4%BC%98%E9%85%B7&pagesize=1&source=monitor";
		String result = Request.requestGet(url);
		if (result != null)
		{
			if (result.startsWith("{")){
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					int total = json.getInt("total");
					int cost = json.getInt("cost");
					boolean miss = json.getBoolean("miss");
					if( total < 10 || cost >= 2000 || miss)
					{
						r.setMessage(url + "\n "+result);
						r.setOk(false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					r.setMessage(url);
					r.setException(e);
					r.setOk(false);
				}
			}
			else{
//				r.setMessage(url+"\n"+result);
//				r.setOk(false);
			}
		}
		return r;
	}

}
