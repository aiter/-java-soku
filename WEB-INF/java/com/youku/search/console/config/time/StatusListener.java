package com.youku.search.console.config.time;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.danga.MemCached.MemCachedClient;
import com.youku.search.console.config.Constants;
import com.youku.search.console.config.MemcachedInit;
import com.youku.search.console.vo.ConnInfo;
import com.youku.search.console.vo.Detail;
import com.youku.search.util.mail.MailMessage;
import com.youku.search.util.mail.MailSender;

public class StatusListener extends TimerTask {
	private static Log log = LogFactory.getLog(StatusListener.class);
	private static boolean isRunning = false;
	private static String[] connips = Constants.iparr;
	private static final int CONNEXCEPTION=0;
	private static final int CONNPOOL=1;
	private static final int MEMCACHED=2;
	
	private void connListener() {
		String conpath = "/pool/status";
		HttpClient httpClient = new HttpClient();
		String connip;
		ConnInfo cinfo = null;
		String resp="";
		for (int i = 0; i < connips.length; i++) {
			connip = connips[i];
			GetMethod getMethod = new GetMethod("http://" + connip + conpath);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			try {
				// 执行getMethod
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: "
							+ getMethod.getStatusLine());
				}
				// 读取内容
				byte[] responseBody = getMethod.getResponseBody();
				// 处理内容
				resp=new String(responseBody);
				//System.out.println(resp);
				JSONObject jo = JSONObject.fromObject(resp);
				if(null!=jo) resp=jo.toString(4);
				cinfo = (ConnInfo) jo.toBean(jo, ConnInfo.class);

				// 分析数据，异常则发送邮件
				Map<String, Integer> tmap = new HashMap<String, Integer>();
				tmap = cinfo.getTotal();
				Detail[] d=cinfo.getDetail();
				if(tmap.get("max_total")!=-1){
					if (tmap.get("max_total") == tmap.get("idle")
									+ tmap.get("active")){
							mailSend(connip,resp,CONNPOOL);
					}
				}
				if(d!=null&&d.length>0){
					Map<String, Integer> stmap = new HashMap<String, Integer>();
					for(int j=0;j<d.length;j++){
						stmap=d[j].getStatus();
						if(stmap.get("max_active")!=-1){
							if(stmap.get("max_active") ==stmap.get("active")){
								mailSend(d[j].getIp(),resp,CONNPOOL);
							}
						}
					}
				}
			} catch (HttpException e) {
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				System.err.println("Please check your provided http address!");
				e.printStackTrace();
					mailSend(connip,e.getMessage(),CONNEXCEPTION);
			} catch (IOException e) {
				// 发生网络异常
				e.printStackTrace();
					mailSend(connip,e.getMessage(),CONNEXCEPTION);
			} catch (Exception e) {
				e.printStackTrace();
					mailSend(connip,e.getMessage(),CONNEXCEPTION);
			} finally {
				// 释放连接
				getMethod.releaseConnection();
			}
			
			
			
		}
	}

	private void memcachedListener() {
		MemcachedInit.getInstance().init("memcached.properties");
		MemCachedClient mcc = new MemCachedClient();
		Map statsmap = mcc.stats();
		Set entries = statsmap.keySet();
		Iterator entryIter = entries.iterator();
		Object k;
		Object v;
		Map s;
		long limit_maxbytes=0;
		long bytes=0;
		while(entryIter.hasNext()){
			k=entryIter.next();
			v=statsmap.get(k);
			//System.out.println("V="+v);
			if(v!=null){
				s=(Map)v;
				if(s.get("limit_maxbytes")!=null&&s.get("limit_maxbytes")!="")
					limit_maxbytes=Long.valueOf(""+s.get("limit_maxbytes"));
				if(s.get("bytes")!=null&&s.get("bytes")!="")
					bytes=Long.valueOf(""+s.get("bytes"));
				if(bytes>=limit_maxbytes){
						mailSend(""+k,""+v,MEMCACHED);
				}
			}
		}
	}

	private void mailSend(String ip,String cont,int type) {
		MailMessage message = new MailMessage();
		message.setHost(Constants.MAIL_HOST);
        message.setPort(Constants.MAIL_PORT);
        message.setNeedAuth(true);
        message.setUsername(Constants.MAIL_USERNAME);
        message.setPassword(Constants.MAIL_PASSWORD);
        message.setFrom(Constants.MAIL_FROMADDR);
        message.setTo(Constants.MAIL_TOADDR);
        message.setCc(Constants.MAIL_CC);
        String subj="";
        String content="";
        if(type==CONNEXCEPTION){
        	subj="连接池异常";
        	content="&nbsp;&nbsp;&nbsp;&nbsp;"+subj+",发生的服务器ip为"+ip+",返回的json为:\n"+cont+"\n" + "&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"#FF0000\">(系统邮件，请勿回复)</font>\n&nbsp;&nbsp;&nbsp;&nbsp;"+new Date() + "\n<hr><hr>";
        }
        else if(type==MEMCACHED){
        	subj="memcached缓存池异常";
        	content="&nbsp;&nbsp;&nbsp;&nbsp;"+subj+",发生的服务器ip为"+ip+",返回的json为:\n"+cont+"\n" + "&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"#FF0000\">(系统邮件，请勿回复)</font>\n&nbsp;&nbsp;&nbsp;&nbsp;"+new Date() + "\n<hr><hr>";
        }
        else if(type==CONNPOOL){
        	subj="连接池异常";
        	content="&nbsp;&nbsp;&nbsp;&nbsp;"+subj+",发生的服务器ip为"+ip+",返回的json为:\n"+cont+"\n" + "&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"#FF0000\">(系统邮件，请勿回复)</font>\n&nbsp;&nbsp;&nbsp;&nbsp;"+new Date() + "\n<hr><hr>";
        }
        message.setSubject(subj+"  "+ new Date());

        message.setHTMLText(true);
        message.setText(content);
        try {
			MailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("邮件发送失败");
		}
	}

	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
//			long s=System.currentTimeMillis();
//			System.out.println(new Date()+"--start--连接池和memcached侦听start,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			// 侦听连接池
			connListener();
			// 侦听memcached缓存
			memcachedListener();
//			System.out.println(new Date()+"--end--连接池和memcached侦听end,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
			isRunning = false;
		} else {
			log.warn("上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}
}
