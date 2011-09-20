package com.youku.soku.manage.shield;



import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.operate.log.LogBaseView;
import com.youku.search.console.vo.SingleDayDate;
import com.youku.search.util.DataFormat;
import com.youku.soku.manage.entity.ShieldWordConstants;


public class SingleDayViewAction  extends ActionSupport{
	String date=null;
	SingleDayDate sd=null;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public SingleDayDate getSd() {
		return sd;
	}
	public void setSd(SingleDayDate sd) {
		this.sd = sd;
	}
	
	
	public String list(){
		
		System.out.println("validate code is: " + code);
		if(!validateUrl(code)) {
			return Action.ERROR;
		}
		System.out.println("date: " + date);
		if(null==date||date.trim().length()<1){
			return Action.ERROR;
		}
		
		
		
		//date=date.replace("-", "_");
		sd=LogBaseView.lbvmap.get(date);
		
		if(sd!=null)
			return Action.SUCCESS;
		else{
			LogBaseView lbv=new LogBaseView();
			sd=lbv.getSingleDayDate(date);
			if(LogBaseView.lbvmap.size()>30){
					LogBaseView.lbvmap.clear();
			}
			if(null!=sd.getEm()&&sd.getEm().size()>0)
			LogBaseView.lbvmap.put(date, sd);
			return Action.SUCCESS;
		}
	}
	
	private boolean validateUrl(String url) {
		if(url == null) {
			return false;
		}
		String timeMillis = url.substring(0, 13);
		date = DataFormat.formatDate(new Date(Long.valueOf(timeMillis)), DataFormat.FMT_DATE_YYYY_MM_DD);
		
		String key = ShieldWordConstants.MAILVALIDATECODE + timeMillis;
		System.out.println("md5 in url: " + url.substring(13));
		System.out.println("date" + date);
		System.out.println("validate code: " + DigestUtils.md5Hex(key));
		if(url.substring(13).equals(DigestUtils.md5Hex(key))) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		long time = System.currentTimeMillis() - 24 * 3600 * 1000;
		String key = ShieldWordConstants.MAILVALIDATECODE + time;
		System.out.println(time + key + "==" + DigestUtils.md5Hex(key));
		
		String url = time + DigestUtils.md5Hex(key);
		System.out.println(url);
	}
	
	private String code;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
