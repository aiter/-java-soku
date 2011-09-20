package com.youku.soku.manage.service.paihangbang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.soku.top.mapping.TopWordsPeer;
import com.youku.top.util.TopDateType.TopDate;

public class TopDateService {
	private static Logger logger = Logger.getLogger(TopDateService.class);
	
	private static TopDateService instance = null;
	private TopDateService(){
		super();
	}
	
	
	public synchronized static TopDateService getInstance() {
		if(null==instance)
			instance = new TopDateService();
		return instance;
	}
	
	public Map<String,String> getTopDate(){
		Map<String,String> map = new HashMap<String, String>();
		try {
			List<Record> list = BasePeer.executeQuery("select * from top_date",TopWordsPeer.DATABASE_NAME);
			for(Record r:list){
				map.put(r.getValue("zhidaqu").asString(), r.getValue("online_date").asString());
			}
			return map;
		} catch (Exception e) {
			logger.error(e);
		}
		logger.info("map.size:"+map.size());
		return map;
	}
	
	public static Map<String,Integer> getTopDateMulu(){
		Map<String,Integer> map = new HashMap<String, Integer>();
		try {
			List<Record> list = BasePeer.executeQuery("select * from top_date where zhidaqu='"+TopDate.zhidaqu.name()+"'",TopWordsPeer.DATABASE_NAME);
			for(Record r:list){
				map.put(r.getValue("online_date").asString(), r.getValue("version_no").asInt());
			}
			return map;
		} catch (Exception e) {
			logger.error(e);
		}
		return map;
	}
}
