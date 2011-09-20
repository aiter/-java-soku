package com.youku.search.console.action;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.LogBaseView;
import com.youku.search.console.vo.SingleDayDate;


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
		if(null==date||date.trim().length()<1){
			return Action.ERROR;
		}
		LogInfoWriter.operate_log.info("分类统计----"+date);
		date=date.replace("-", "_");
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
}
