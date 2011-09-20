package com.youku.search.monitor.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.console.util.Wget;
import com.youku.search.monitor.Result;

public class SokuRecomendMonitor extends CMonitor{
	protected static Log logger = LogFactory.getLog(SokuRecomendMonitor.class);
	
	protected final String[] addresses = new String[]{"http://10.103.8.226/","http://10.103.8.227/"};
	
	private final String[] test_keys = new String[]{"k","b"};
	
	@Override
	public Result check() {
		Result rs = null;
		for(String addr:addresses){
			for(String test_key:test_keys){
				rs = checkSingle(new StringBuffer(addr).append("search_keys?k=").append(test_key).toString());
				if(null!=rs) return rs;
				if(null!=rs) return rs;
			}
		}
		if(null!=rs) return rs;
		else
			return new Result();
	}
	
	private Result checkSingle(String url){
		byte[] bytes = null;
		Result rs = new Result();
		try {
			bytes = Wget.get(url);
			if(null == bytes){
				logger.error("null returned. url:"+url);
				rs.setOk(false);
				rs.setMessage("null returned. url:"+url);
				return rs;
			}
			String res = new String(bytes,"utf-8");
			String subres = StringUtils.substringBetween(res, "showresult('(",")',false)");
			if(StringUtils.isBlank(subres)){
				logger.error("null returned. url:"+url+",response:"+res);
				rs.setOk(false);
				rs.setMessage("null returned. url:"+url+",response:"+res);
				return rs;
			}
//			subres = "'"+subres+"'";
//			JSONObject json = new JSONObject(new JSONTokener(subres).nextValue().toString());
			JSONObject json = new JSONObject(subres);
			if(null == json||json == JSONObject.NULL){
				logger.error("null json returned. url:"+url+",response:"+res);
				rs.setOk(false);
				rs.setMessage("null json returned. url:"+url+",response:"+res);
				return rs;
			}
			JSONArray jsonarr = json.optJSONArray("result");
			if(null==jsonarr||0==jsonarr.length()){
				logger.error("null result returned. url:"+url+",response:"+res);
				rs.setOk(false);
				rs.setMessage("null result returned. url:"+url+",response:"+res);
				return rs;
			}
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs.setOk(false);
			rs.setMessage(e.getMessage());
			rs.setException(e);
			return rs;
		}
	}
	
	public static void main(String[] args) {
		SokuRecomendMonitor sm = new SokuRecomendMonitor();
		Result rs = sm.check();
		System.out.println(rs.isOk());
	}
}
