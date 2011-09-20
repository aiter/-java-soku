package com.youku.search.console.action;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.LogBaseView;
import com.youku.search.console.vo.View30Days;
import com.youku.search.util.DataFormat;

public class LogTotalViewAction {
	protected List<View30Days> lvd;
	String startdate;
	String enddate;
	String start;
	String dateend;
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	
	public String getDateend() {
		return dateend;
	}
	public void setDateend(String dateend) {
		this.dateend = dateend;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getStartdate() {
		return startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public List<View30Days> getLvd() {
		return lvd;
	}
	public void setLvd(List<View30Days> lvd) {
		this.lvd = lvd;
	}
	public String execute() {
		LogBaseView lv=new LogBaseView();
		Pattern m = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
		if(!StringUtils.isBlank(startdate)){
			if(!m.matcher(startdate.trim()).find()){
				startdate = null;
			}else 
				startdate = startdate.trim();
		}else 
			startdate = null;
		if(!StringUtils.isBlank(enddate)){
			if(!m.matcher(enddate.trim()).find()){
				enddate = null;
			}else
				enddate = enddate.trim();
		}else
			enddate = null;
		if(!(StringUtils.isBlank(startdate)&&StringUtils.isBlank(enddate))){
			if(StringUtils.isBlank(startdate))
				startdate = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);
			if(StringUtils.isBlank(enddate))
				enddate = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);
			if(startdate.compareToIgnoreCase(enddate)>0){
				String temp = startdate;
				startdate = enddate;
				enddate = temp;
			}
		}
		if(StringUtils.isBlank(startdate)&&StringUtils.isBlank(enddate)){
			start = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -30), DataFormat.FMT_DATE_YYYYMMDD);
			dateend = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);
		}else{
			start = startdate;
			dateend = enddate;
		}
		lvd=lv.getUnionDate(startdate,enddate);
		
		LogInfoWriter.operate_log.info("分类统计");
		return Action.SUCCESS;
	}
	
	public static void main(String[] args) {
		String startdate ="2010-02-02";
		String enddate ="2010-02-16";
		String start,dateend;
		Pattern m = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
		if(!StringUtils.isBlank(startdate)){
			if(!m.matcher(startdate.trim()).find()){
				startdate = null;
			}else 
				startdate = startdate.trim();
		}else 
			startdate = null;
		if(!StringUtils.isBlank(enddate)){
			if(!m.matcher(enddate.trim()).find()){
				enddate = null;
			}else
				enddate = enddate.trim();
		}else
			enddate = null;
		if(!(StringUtils.isBlank(startdate)&&StringUtils.isBlank(enddate))){
			if(StringUtils.isBlank(startdate))
				startdate = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);
			if(StringUtils.isBlank(enddate))
				enddate = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);
			if(startdate.compareToIgnoreCase(enddate)>0){
				String temp = startdate;
				startdate = enddate;
				enddate = temp;
			}
		}
		if(StringUtils.isBlank(startdate)&&StringUtils.isBlank(enddate)){
			start = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -30), DataFormat.FMT_DATE_YYYYMMDD);
			dateend = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);
		}else{
			start = startdate;
			dateend = enddate;
		}
		System.out.println(start+","+dateend);
	}
}
