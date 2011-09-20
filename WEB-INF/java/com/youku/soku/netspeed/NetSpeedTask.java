package com.youku.soku.netspeed;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.config.Config;

public class NetSpeedTask {
	
	String dataPath = null;
	
	public NetSpeedTask(String dataPath){
		this.dataPath = dataPath;
		
		IpArea.init(dataPath + Config.getIpFile());
	}
	
	private Log logger = LogFactory.getLog(NetSpeedTask.class);
	
	private long last = 0;
	
	public void start() {
		Timer timer = new Timer(true);

		long delay = 1 ;
		long period = 5 * 60 * 1000;// 5分钟

		TimerTask task = new TimerTask() {
			public void run() {
				SpeedDb.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				logger.info("当前小时："+SpeedDb.hour);
				
			}
		};

		timer.schedule(task, delay, period);
		
		delay = 1 ;
		period = 60 * 60 * 1000;// 60分钟
		task = new TimerTask() {
			public void run() {
				String speedFile = dataPath + Config.getSpeedFile();
				if (shouldLoadDb(speedFile)){
					SpeedDb.updateTree(speedFile);
				}
			}
		};

		timer.schedule(task, delay, period);
	}
	
	
	private boolean shouldLoadDb(String data) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		File file = new File(SpeedDb.class.getResource(data).getFile());
		if (!file.exists()) {
			logger.info("不应该加载：数据不存在，file：" + file.getAbsolutePath());
			return false;
		}

		if (file.lastModified() > last) {
			logger.info("应该加载：发现数据新版本，file：" + file.getAbsolutePath());
			last = file.lastModified();
			return true;
		}

		logger.info("不应该加载：没有发现数据新版本，file：" + file.getAbsolutePath()
				+ "；上次修改时间：" + format.format(new Date(last)));
		return false;
	}
	
	public static void main(String[] args){
	}
}
