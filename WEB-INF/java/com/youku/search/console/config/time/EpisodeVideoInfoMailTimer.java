package com.youku.search.console.config.time;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.youku.search.console.operate.juji.EpisodeVideoUpdateInfoMgt;
import com.youku.search.console.operate.juji.SearchLogMgt;
import com.youku.search.console.pojo.EpisodeVideoUpdateInfo;
import com.youku.search.util.DataFormat;
import com.youku.search.util.mail.MailMessage;
import com.youku.search.util.mail.MailSender;

public class EpisodeVideoInfoMailTimer {
	
	private static boolean isRunning = false;
	
	public void start() {
		Timer timer = new Timer(true);

		final long delay = DelayTime.getDelayTime(1000 * 60*60*24L,5,true);
		final long period = 1000L * 60 * 60 * 24;// 24小时

		TimerTask task = new TimerTask() {
			public void run() {
				if (!isRunning) {
					isRunning = true;
					try {
						mail();
					} catch (Exception e) {
						e.printStackTrace();
					}
				isRunning = false;
			}
			}
		};

		timer.schedule(task, delay, period);
	}
	
	public void mail(){
		Date d = DataFormat.getNextDate(new Date(), -1);
		String day_date = DataFormat.formatDate(d, DataFormat.FMT_DATE_YYYYMMDD);
		List<EpisodeVideoUpdateInfo> evinfos = EpisodeVideoUpdateInfoMgt.getInstance().getInfoByDayDate(day_date);
		String info = contentBuild(evinfos,day_date);
		MailMessage message = new MailMessage();
		message.setHost("10.10.0.12");
		message.setPort(25);
		message.setNeedAuth(true);
		message.setUsername("sousuo");
		message.setPassword("jsfq7m2byap3");
		message.setFrom("sousuo@youku.com");
		message.setFromname("站内剧集相关视频搜索");
//		if (!StringUtils.isBlank(finfo) || week == 1) {
//			message.setTo("yinyuzong@youku.com");
//			String[] cc = new String[] { "jiabaozhen@youku.com",
//					"luwei@youku.com", "wubin@youku.com"};
//			message.setCc(cc);
//		} else
			message.setTo("wubin@youku.com");
		String subj = "站内剧集相关视频搜索";

		message.setSubject(subj);
		message.setHTMLText(true);
		message.setText(info);
		try {
			MailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("邮件发送失败,subject:" + subj);
		}
	}
	
	public String contentBuild(List<EpisodeVideoUpdateInfo> sls,String search_time) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table width=\"80%\">");
		sb.append("<tr>");
		sb.append("<td width=\"35%\">");
		sb.append("日期");
		sb.append("</td>");
		sb.append("<td width=\"25%\">");
		sb.append("操作");
		sb.append("</td>");
		sb.append("<td width=\"25%\">");
		sb.append("数据数");
		sb.append("</td>");
		sb.append("<td width=\"15%\">");
		sb.append("状态");
		sb.append("</td>");
		sb.append("</tr>");
		for (EpisodeVideoUpdateInfo sl : sls) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(sl.getDayDate());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(sl.getOperateName());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(sl.getNum());
			sb.append("</td>");
			sb.append("<td>");
			if("insert".equalsIgnoreCase(sl.getOperateName())){
				if("1".equalsIgnoreCase(sl.getState()))
					sb.append("通知api成功");
				else if("2".equalsIgnoreCase(sl.getState()))
					sb.append("通知失败");
				else
					sb.append("未通知");
			}
			sb.append("</td>");
			sb.append("</tr>");
		}
		
		//search
		sb.append("<tr><td>");
		sb.append(search_time);
		sb.append("</td>");
		sb.append("<td>");
		sb.append("search");
		sb.append("</td>");
		sb.append("<td>");
		sb.append(SearchLogMgt.getInstance().getSearchLogNum(search_time, -1));
		sb.append("</td>");
		sb.append("<td>");
		sb.append("搜索总数");
		sb.append("</td>");
		sb.append("</tr>");
		
		sb.append("<tr><td>");
		sb.append(search_time);
		sb.append("</td>");
		sb.append("<td>");
		sb.append("search");
		sb.append("</td>");
		sb.append("<td>");
		sb.append(SearchLogMgt.getInstance().getSearchLogNum(search_time, 0));
		sb.append("</td>");
		sb.append("<td>");
		sb.append("搜索词组合总数");
		sb.append("</td>");
		sb.append("</tr>");
		
		sb.append("</table>");
		return sb.toString();
	}
}
