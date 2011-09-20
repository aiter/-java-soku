package com.youku.soku.manage.timer;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.search.util.mail.MailMessage;
import com.youku.search.util.mail.MailSender;
import com.youku.soku.manage.entity.ShieldWordConstants;
import com.youku.soku.manage.torque.ShieldMailSetting;
import com.youku.soku.manage.torque.ShieldMailSettingPeer;

public class ShieldMailTimer {

	private final Logger log = Logger.getLogger(this.getClass());
	public void start() {
		Timer timer = new Timer(true);
		
		final long delay = 60L * 1000 ;   //one hour
		final long period = 6 * 60 * 1000 * 60; // 24 hour
		
		TimerTask task = new TimerTask() {
			public void run() {
				try {
					log.info("Begin to send mail   [Shield System]");
					List<ShieldMailSetting> settings = ShieldMailSettingPeer.doSelect(new Criteria());
					for(ShieldMailSetting setting : settings) {
						int periodSet = setting.getPeriods();
						int periodPast = setting.getPeriodsPast();
						
						if(periodPast < periodSet) {
							setting.setPeriodsPast(periodPast + 1);
							setting.setUpdateTime(new Date());
							ShieldMailSettingPeer.doUpdate(setting);
						} else {
							sendmail(setting.getEmail());
							setting.setPeriodsPast(1);
							setting.setUpdateTime(new Date());
							ShieldMailSettingPeer.doUpdate(setting);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		timer.schedule(task, delay, period);
	}
	
	private void sendmail(String mailAddress) {
		long time = System.currentTimeMillis() - 24 * 3600 * 1000;
		String key = ShieldWordConstants.MAILVALIDATECODE + time;
		
		String url = "http://10.101.168.102/SearchLogView.do?code=" + time + DigestUtils.md5Hex(key);
		
		MailMessage mail = new MailMessage();
		mail.setTo(mailAddress);
		mail.setSubject("soku 搜索关键字");
		mail.setText(url);
		
		mail.setHost("10.10.0.12");
		mail.setPort(25);
		mail.setNeedAuth(true);
		mail.setUsername("sousuo");
		mail.setPassword("jsfq7m2byap3");
		mail.setFrom("sousuo@youku.com");
		mail.setFromname("Soku 屏蔽系统");
		mail.setHTMLText(false);
		
		try {
			MailSender.send(mail);
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
	}
	
	public static void main(String[] args) {
		new ShieldMailTimer().sendmail("tanxiuguang@youku.com");
	}
}
