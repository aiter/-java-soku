package com.youku.soku.top.directory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;

public class TopDateManager {
	
	public static final int ZHIDAQU = 1;
	public static final int TOP = 2;
	public static final int FUN = 3;
	
	static Log logger = LogFactory.getLog(TopDateManager.class);
	
	static Map<Integer,TopDate> dateMap = new HashMap<Integer,TopDate>();
	
	// 获取新排行榜的正确日期
	public static void initTopDate(){
		
		StringBuffer sql = new StringBuffer("select  * from top_date");
		logger.info("the sql:"+sql);
		try {

			List<Record> records = BasePeer.executeQuery(sql.toString(),
					"new_soku_top");
			if(records!=null && records.size()>0){
				for (Record record:records){
					int id = record.getValue("id").asInt();
					
					String returnDate=record.getValue("online_date").asString();
					int versionNo=record.getValue("version_no").asInt();

					TopDate date = new TopDate();
					date.setTopDate(returnDate);
					date.setVersion(versionNo);
					
					dateMap.put(id, date);
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(" 查询top_date表出现异常 ：" + sql.toString());
		}
	}
	
	public static TopDate getDate(int type){
		return dateMap.get(type);
	}
	
	public static void start() {
		Timer timer = new Timer(true);

		final long delay = 1 ;
		final long period = 1000L * 60 * 10 * 1;// 10分钟

		TimerTask task = new TimerTask() {
			public void run() {
				try {
					logger.info("获取排行榜日期---start---");
					TopDateManager.initTopDate();
					logger.info("获取排行榜日期---end---");

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		};

		timer.schedule(task, delay, period);
	}
}
