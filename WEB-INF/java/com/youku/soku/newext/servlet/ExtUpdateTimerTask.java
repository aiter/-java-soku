package com.youku.soku.newext.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.newext.loader.UpdateAlias;

/**
 * 直达区数据增量更新
 */
public class ExtUpdateTimerTask {

	Log logger = LogFactory.getLog(getClass());

	long delay = 10 * 1000 * 60;
	long period = 5 * 60 * 1000;  //5分钟做一次增量更新
	
	public static int counter = 0;
	
	public void start() {
		Timer timer = new Timer(true);
		timer.schedule(new Task(), delay, period);
	}

	class Task extends TimerTask {

		long last = -1;
		
		int zeroUpdateCounter = 0;

		@Override
		public void run() {
			logger.info("开始直达区增量更新...");
			Cost cost = new Cost();
			try {
				run_();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			cost.updateEnd();
			logger.info("直达区增量更新结束, cost: " + cost.getCost());
			counter++;
		}

		void run_() {
			
			Date startDate=null,nowDate=new Date();
			if(last==-1){
//				如果第一次运行，设置为前24小时,考虑到全量记载是24小时，为了避免丢失数据
				Calendar calendar=Calendar.getInstance();
				calendar.add(Calendar.HOUR, -24);
				last=calendar.getTime().getTime();
			}
			startDate=new Date(last);	
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.info("更新开始时间："+sdf.format(startDate)+
					"结束时间："+sdf.format(nowDate));
			
			int updateProgrammeSiteCount = new UpdateAlias().doUpdate(startDate,nowDate);
			if(updateProgrammeSiteCount != 0) {  //必须有站点更新，才修改lastUpdate时间
				last=nowDate.getTime();
				zeroUpdateCounter = 0;
			} else {
				zeroUpdateCounter++;
			}
			
			if(zeroUpdateCounter > 50) {
				try {
					File file = new File("/opt/update_error");
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write(new Date().getTime() + "");
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
