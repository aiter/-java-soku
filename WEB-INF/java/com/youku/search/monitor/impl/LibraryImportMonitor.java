package com.youku.search.monitor.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.monitor.Result;
import com.youku.search.util.Request;

public class LibraryImportMonitor extends DMonitor {

	public LibraryImportMonitor() {
		super();
		super.period = 1000 * 60 * 60 * 24;  //一天执行一次
	}
	
	@Override
	public Result check() {
		Result r = new Result();
		String url = "http://10.102.23.81/soku/versionimportlog.jsp";
		String result = Request.requestGet(url);
		System.out.println(result);
		if(result != null) {
			if(result.startsWith("{")) {
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					
					int teleplayImportCount = json.getInt("teleplayImport");
					int teleplayUpdateCount = json.getInt("teleplayUpdate");
					int movieImportCount = json.getInt("movieImport");
					int movieUpdateCount = json.getInt("movieUpdate");
					int varietyImportCount = json.getInt("varietyImport");
					int varietyUpdateCount = json.getInt("varietyUpdate");
					
					r.setMessage(url + "\n 上次直达区数据导入量\n 电视剧: 导入 "+ teleplayImportCount + "更新 ：" + teleplayUpdateCount
														   +"\n 电影：    导入  " + movieImportCount + "更新： " + movieUpdateCount
														   +"\n 综艺节目： 导入  " + varietyImportCount + "更新： " + varietyUpdateCount);
					r.setOk(false);
				} catch (JSONException e) {
					e.printStackTrace();
					r.setMessage(url + "\n "+result);
					r.setException(e);
					r.setOk(false);
				}
			} else{
				r.setMessage(url+"\n"+result);
				r.setOk(false);
			}
		}
		return r;
	}
	
	
}
