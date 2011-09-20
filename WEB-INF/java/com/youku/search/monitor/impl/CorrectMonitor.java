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
 * 搜索词纠错监控
 */
public class CorrectMonitor  extends AMonitor{

	public CorrectMonitor() {
		super();
		super.period = 1000*60*30; //30分钟执行一次
	}
	
	@Override
	public Result check() {
		Result r = new Result();
		String url = "http://"+ Config.getYoukuHost() +"/search?keyword=zhoujielun&pagesize=1&source=monitor&video_options=0000010";
		String result = Request.requestGet(url);
		if (result != null)
		{
			if (result.startsWith("{")){
			JSONObject json = null;
			try {
				json = new JSONObject(result);
				
				if(json.getString("suggestion")==null)
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
