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
 * 站外搜索监控
 */
public class SokuMonitor  extends AMonitor{

	public SokuMonitor() {
		super();
		super.period = 1000*60*10; //10分钟执行一次
	}

	/* (non-Javadoc)
	 * @see com.youku.search.monitor.Monitor#check()
	 */
	@Override
	public Result check() {
		Result r = new Result();
		String url = "http://"+ Config.getSokuHost() +"/9/5/2/7/search?keyword=mm&pagesize=1";
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
	public static void main(String[] args)
	{
		SokuMonitor m = new SokuMonitor();
		System.out.println(m.check().isOk());
	}
}