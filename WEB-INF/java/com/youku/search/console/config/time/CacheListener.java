package com.youku.search.console.config.time;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.config.Constants;
import com.youku.search.sort.VideoRecommend;
import com.youku.search.util.mail.MailMessage;
import com.youku.search.util.mail.MailSender;

public class CacheListener extends TimerTask {
	private static Log log = LogFactory.getLog(CacheListener.class);
	private static boolean isRunning = false;
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			//long s=System.currentTimeMillis();
			//System.out.println(new Date()+"--start--定时推荐start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			try{
			VideoRecommend queryRecommend = new VideoRecommend();
			queryRecommend.cacheRecommends();
			}catch(Exception e){
				log.error("推荐发生错误",e);
//				e.printStackTrace();
				mailSend(e);
			}
			//System.out.println(new Date()+"--end--定时推荐end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("CacheListener上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}
	
	 private static String compressClassName( String name )
	    {
		// Note that this must end in . in order to be renamed correctly.
		String prefix = "com.sun.corba.se." ;
		if (name.startsWith( prefix ) ) {
		    return "(ORB)." + name.substring( prefix.length() ) ;
		} else
		    return name ;
	    }
	 
	 private static String formatStackTraceElement( StackTraceElement ste ) 
	    {
	        return compressClassName( ste.getClassName() ) + "." + ste.getMethodName() +
	            (ste.isNativeMethod() ? "(Native Method)" :
	             (ste.getFileName() != null && ste.getLineNumber() >= 0 ?
	              "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" :
	              (ste.getFileName() != null ?  "("+ste.getFileName()+")" : "(Unknown Source)")));
	    }
	 
	private void mailSend(Exception exception) {
		MailMessage message = new MailMessage();
		message.setHost(Constants.MAIL_HOST);
        message.setPort(Constants.MAIL_PORT);
        message.setNeedAuth(true);
        message.setUsername(Constants.MAIL_USERNAME);
        message.setPassword(Constants.MAIL_PASSWORD);
        message.setFrom(Constants.MAIL_FROMADDR);
        message.setTo(Constants.MAIL_TOADDR);
        message.setCc(Constants.MAIL_CC);
        StringBuffer content = new StringBuffer("&nbsp;&nbsp;&nbsp;&nbsp;"+"推荐发生错误<br>");
        content.append(exception.getMessage()+"<br>");
        content.append("    Stack Trace:<br>");
        String subj="推荐发生错误  "+new Date();
        StackTraceElement[] trace = exception.getStackTrace();
        for ( int ctr = 1; ctr < trace.length; ctr++ ) {
        	content.append( "        >" ) ;
        	content.append( formatStackTraceElement( trace[ctr] ) ) ;
        	content.append("<br>");
    	}
        content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"#FF0000\">(系统邮件，请勿回复)</font>\n&nbsp;&nbsp;&nbsp;&nbsp;"+new Date() + "<br><hr><hr>");
        message.setSubject(subj);
        message.setHTMLText(true);
        message.setText(content.toString());
        try {
			MailSender.send(message);
		} catch (Exception e) {
//			e.printStackTrace();
			log.error("邮件发送失败",e);
		}
	}
}
