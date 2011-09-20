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
 * 相关视频监控
 */
public  class LikeVideoMonitor extends AMonitor{

	public LikeVideoMonitor() {
		super();
		super.period = 1000*60*30; //30分钟执行一次
	}
	
	/* (non-Javadoc)
	 * @see com.youku.search.monitor.Monitor#check()
	 */
	@Override
	public Result check() {
		Result r = new Result();
		
		String url = "http://"+ Config.getYoukuHost() +"/search?type=22&keyword=mm%7Cmm&pagesize=1&source=monitor";
		String result = Request.requestGet(url);
		if (result != null)
		{
			if (result.startsWith("{")){
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					int total = json.getInt("total");
					int cost = json.getInt("cost");
					if( total < 10 || cost >= 2000 )
					{
						r.setMessage(url + "\n "+result);
						r.setOk(false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					r.setMessage(url + "\n "+result);
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
