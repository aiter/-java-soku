/**
 * 
 */
package com.youku.search.monitor.impl;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.monitor.Result;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Request;

/**
 * @author 1verge
 *
 */
public class NewVideoMonitor  extends AMonitor{

	public NewVideoMonitor() {
		super();
		super.period = 1000*60*30; //30分钟执行一次
	}

	/* (non-Javadoc)
	 * @see com.youku.search.monitor.Monitor#check()
	 */
	@Override
	public Result check() {
		Result r = new Result();
		String url = "http://"+ Config.getYoukuHost() +"/search?pagesize=1&order=1&orderfield=createtime&cateid=94&source=monitor&video_options=0000000";
		String result = Request.requestGet(url);
		if (result != null)
		{
			if (result.startsWith("{")){
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					JSONObject ja = json.getJSONObject("items");
					JSONObject jo = ja.optJSONObject("0");
					Date date = DataFormat.parseUtilDate(jo.getString("createtime"),DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
					long before = new Date().getTime() - date.getTime();
					
					System.out.println("最新视频："+(before/1000/60)+"分钟前");
					if (before/1000/60 > 240)
					{
						r.setMessage(url + "\n 最新视频："+(before/1000/60)+"分钟前,最新视频超过240分钟认为更新不及时！");
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