package com.youku.soku.manage.timer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.search.util.mail.MailMessage;
import com.youku.search.util.mail.MailSender;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.entity.ShieldWordConstants;
import com.youku.soku.manage.service.Top100ReportGenerator;

public class Top100ReportMailTimer {

	private final Logger log = Logger.getLogger(this.getClass());
	public void start() {
		Timer timer = new Timer(true);
		
		final long delay = 6L * 1000 ;   
		final long period = 59 * 1000 * 60; // 1 hour
		
		TimerTask task = new TimerTask() {
			public void run() {
				Calendar calendar = Calendar.getInstance();
				if(calendar.get(Calendar.DAY_OF_WEEK) == 5 && calendar.get(Calendar.HOUR_OF_DAY) == 12) {
					log.info("影视剧排行榜周报生成: start");
					sendmail();
					log.info("影视剧排行榜周报生成: end");
				}
			}
		};
		
		timer.schedule(task, delay, period);
	}
	
	private void sendmail() {
		long time = System.currentTimeMillis() - 24 * 3600 * 1000;
		String key = ShieldWordConstants.MAILVALIDATECODE + time;
		
		
		MailMessage mail = new MailMessage();
		mail.setTo("liyoujiang@youku.com");
		mail.setCc(new String[]{"tanxiuguang@youku.com"});
		mail.setSubject("影视剧排行榜周报");
		mail.setText("影视剧排行榜周报");
		Top100ReportGenerator generator = new Top100ReportGenerator();
		mail.addAttachment("movie.txt", generator.generateFile(Constants.MOVIE_CATE_ID), "application/octet-stream");
		mail.addAttachment("teleplay.txt", generator.generateFile(Constants.TELEPLAY_CATE_ID), "application/octet-stream");
		
		mail.setHost("10.10.0.12");
		mail.setPort(25);
		mail.setNeedAuth(true);
		mail.setUsername("sousuo");
		mail.setPassword("jsfq7m2byap3");
		mail.setFrom("sousuo@youku.com");
		mail.setFromname("Soku");
		mail.setHTMLText(false);
		
		try {
			MailSender.send(mail);
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
	}
	
	public static void main(String[] args) {
		//new Top100ReportMailTimer().sendmail("tanxiuguang@youku.com");
		System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
	}
}
