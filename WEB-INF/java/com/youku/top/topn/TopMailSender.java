package com.youku.top.topn;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.search.util.mail.MailMessage;
import com.youku.search.util.mail.MailSender;
import com.youku.top.topn.util.LogUtil;
public class TopMailSender {
	static Logger logger = Logger.getLogger(TopMailSender.class);
	
	public void mailSend(String uniondate,String before_date){
		MailMessage message = new MailMessage();
		message.setHost("10.10.0.12");
        message.setPort(25);
        message.setNeedAuth(true);
        message.setUsername("sousuo");
        message.setPassword("jsfq7m2byap3");
        message.setFrom("sousuo@youku.com");
        message.setFromname("搜索统计");
        //TODO 测试
//        message.setTo("wubin@youku.com");
        
        message.setTo("xierui@youku.com");
        String[] cc = new String[]{"vkoo@youku.com","liudele@youku.com","dingheng@youku.com","yaojian@youku.com","yinyuzong@youku.com","haofeng@youku.com","chenhu@youku.com","zhuxiangyang@youku.com","GaoGuangJing@youku.com","weiming@youku.com","chenying@youku.com","wangyijing@youku.com","xiongshuqin@youku.com","liyijiang@youku.com","jiabaozhen@youku.com","luwei@youku.com","wubin@youku.com"};
        message.setCc(cc);
        String subj="搜索TOP500关键词统计("+uniondate+")";
        String content="请查看附件";
        try {
        	File file = new File("/opt/log_analyze/top500/top_"+uniondate+".html");
        	if(!file.exists()){
        		TopKeywordSortPrint.printFile(uniondate,before_date);
//        	}else{
//        		System.out.println("-----当天邮件已经发送过,关键词统计邮件发送中断-----");
//        		return;
        	}
        	if(null==file||!file.exists())return;
			message.addAttachment("top_"+uniondate+".html",file,"application/octet-stream");
		} catch (Exception e1) {
			logger.error(e1);
			return ;
		}
		logger.info("-----关键词统计邮件发送开始-----"+subj);
		message.setSubject(subj);
        message.setHTMLText(true);
        message.setText(content);
        try {
			MailSender.send(message);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.info("-----关键词统计邮件发送结束-----"+subj);
	}
	
	//定时任务 每周日7点
	public static void main(String[] args) {
		try {
			LogUtil.init();
		} catch (IOException e) {
			logger.error(e);
		}
		long s=System.currentTimeMillis();
		String end = null;
		String start = null;
		String before_date = null;
		if(null!=args&&args.length==3){
			start = args[0];
			end = args[1];
			before_date = args[2];
		}else{
			end = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
			start = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
			String beforeend = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-8), DataFormat.FMT_DATE_SPECIAL);
			String beforestart = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-14), DataFormat.FMT_DATE_SPECIAL);
			before_date = new StringBuilder(beforestart).append("_").append(beforeend).toString();
		}
		logger.info(new Date()+"--start--关键词统计邮件发送,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
		TopMailSender tms=new TopMailSender();
		tms.mailSend(new StringBuilder(start).append("_").append(end).toString(),before_date);
		logger.info(new Date()+"--end--关键词统计邮件发送,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
		
	}
}
