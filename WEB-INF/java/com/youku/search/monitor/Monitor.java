/**
 * 
 */
package com.youku.search.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.youku.search.util.DataFormat;
import com.youku.search.util.mail.MailMessage;
import com.youku.search.util.mail.MailSender;

/**
 * @author 1verge
 * 
 */
public abstract class Monitor extends TimerTask {

	protected long period = 1000 * 60 * 3;	//默认3分钟 检查一次

	protected Person[] persons = null;
	protected MonitorLevel level = null;
	
	public Monitor(Person person) {
		this(new Person[]{person},null);
	}
	public Monitor(Person person,MonitorLevel level) {
		this(new Person[]{person},level);
	}
	public Monitor(Person[] persons) {
		this(persons,null);
	}
	public Monitor(Person[] persons,MonitorLevel level) {
		this.persons = persons;
		this.level = level;
	}
	
	/**
	 * 检查主程序，各子类实现
	 * @return 检查正常时，可以返回null或者Result.isOk()成立
	 * @return 检查失败时，必须返回Result对象，并且Result.isOk()不成立，可以通过Result.setMessage(s)设置发邮件的内容，Result.setException(e)设置异常信息
	 */
	public abstract Result check();
	
	
	/**
	 * 实现TimerTask
	 */
	public void run() {
		Result result = check();
		if (result == null || result.isOk()) {
			System.out.println("正常：" + this.getClass().getName() + " status ok!!");
		} else {
			System.err.println("异常：" + this.getClass().getName() + " At " + DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD_HHMMSS));
			sendMessage(result);
		}
	}
	
	public long getPeriod() {
		return period;
	}

	/**
	 * 发送错误程序
	 */
	private void sendMessage(Result result) {
		String message = "异常："+ this.getClass().getName()	+ " At "+ DataFormat.formatDate(new Date(),DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		
		if (level == null || level == MonitorLevel.warning || level == MonitorLevel.All){
			System.out.println("开始发送email ");
			String[] sendMails = null;
			if (persons != null && persons.length > 0) {
				List<String> mails = new ArrayList<String>();
				for (Person person:persons){
					if (person!=null && person.getEmails()!=null){
						for (String m:person.getEmails()){
							mails.add(m);
						}
					}
				}
				
				sendMails = mails.toArray(new String[mails.size()]);
			}
			
			if (sendMails == null || sendMails.length == 0)
				return;
				
				MailMessage mail = new MailMessage();
				mail.setTo(sendMails[0]);
				if (sendMails.length > 1) {
					String[] cc = new String[sendMails.length];
					System.arraycopy(sendMails, 1, cc, 0,
							sendMails.length - 1);
					mail.setCc(cc);
				}
				
				mail.setSubject(message);
				
				StringBuilder sb = new StringBuilder(message);
				
				if (result != null){
					if (result.getMessage() != null)
						sb.append("\n"+result.getMessage());
					if (result.getException() != null) {
						sb.append("\n Exception:"+result.getException() + "\n");
						StackTraceElement[] trace = result.getException().getStackTrace();
						for(int i = 0; i < trace.length; i++) {
							sb.append("\t" + trace[i] + "\n");
						}
					}
				}
					
				mail.setText(sb.toString());
	
				mail.setHost("10.10.0.12");
				mail.setPort(25);
				mail.setNeedAuth(true);
				mail.setUsername("sousuo");
				mail.setPassword("jsfq7m2byap3");
				mail.setFrom("sousuo@youku.com");
				mail.setFromname("Youku Search Monitor");
				mail.setHTMLText(false);
				
				try {
					MailSender.send(mail);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
			}
		
		
		if (level == MonitorLevel.error || level == MonitorLevel.All){
			String[] sendPhones = null;
			if (persons != null && persons.length > 0) {
				List<String> phones = new ArrayList<String>();
				for (Person person:persons){
					if (person != null && person.getPhone()!=null){
							phones.add(person.getPhone());
					}
				}
				
				sendPhones = phones.toArray(new String[phones.size()]);
			}
			
			if (sendPhones == null || sendPhones.length == 0)
				return;
			
			SmsSender sms = new SmsSender();
			try {
				sms.send(message, sendPhones);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}

}
