package com.youku.search.console.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.DateRead;
import com.youku.search.console.vo.ViewTotal;
import com.youku.search.util.DataFormat;

public class StatisticsAction extends ActionSupport {
//	HttpServletRequest request;
	ViewTotal vt;
	String date=null;
	String date_start;
	String date_end;
	String date_remove;
	
	List<ViewTotal> vtlist=new ArrayList<ViewTotal>();
	
	public String getDate_remove() {
		return date_remove;
	}

	public void setDate_remove(String date_remove) {
		this.date_remove = date_remove;
	}

	public List<ViewTotal> getVtlist() {
		return vtlist;
	}

	public void setVtlist(List<ViewTotal> vtlist) {
		this.vtlist = vtlist;
	}

	public String getDate_start() {
		return date_start;
	}

	public void setDate_start(String date_start) {
		this.date_start = date_start;
	}

	public String getDate_end() {
		return date_end;
	}

	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ViewTotal getVt() {
		return vt;
	}

	public void setVt(ViewTotal vt) {
		this.vt = vt;
	}
	
	public String dateRemove(){
		if(null==date_remove||date_remove.trim().length()<1){
			if(null!=DateRead.vtmap&&DateRead.vtmap.size()>0){
					DateRead.vtmap.clear();
			}
			return SUCCESS;
		}
		if(null!=DateRead.vtmap.get(date_remove)){
				DateRead.vtmap.remove(date_remove);
		}
		return SUCCESS;
	}
	
	public String list(){
		String table;
		if(null==date||date.trim().length()<1){
			date=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYY_MM_DD);
			vt=DateRead.vtmap.get(date);
			if(null!=vt)
				return SUCCESS;
			table=new StringBuffer("turn_").append(date).append("_s").toString();
		}else {
			vt=DateRead.vtmap.get(date);
			if(null!=vt)
				return SUCCESS;
			table=new StringBuffer("turn_").append(date).append("_s").toString();
		}
		Connection conn=null;
		try{
			conn=DataConn.getLogStatConn();
			vt=DateRead.getInstance().getView(conn,table);
		}catch(Exception e){
			vt=new ViewTotal();
		}finally{
			DataConn.releaseConn(conn);
		}
		vt.setDate(date);
		if(DateRead.vtmap.size()>10){
			DateRead.vtmap.clear();
		}
		if(null!=vt.getTurnmap()&&vt.getTurnmap().size()>0)
		DateRead.vtmap.put(date, vt);
		LogInfoWriter.operate_log.info("分页比例----"+date);
		return SUCCESS;
	}
	
	public String dateinput(){
		return SUCCESS;
	}
	
	public String toTopView(){
		return SUCCESS;
	}
	
	public String listMulti(){
		String min;
		String max;
		if(null==date_start||date_start.trim().length()<1){
			date_start=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYY_MM_DD);
			
		}
		if(null==date_end||date_end.trim().length()<1){
			date_end=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYY_MM_DD);
			
		}
		if(date_start.compareToIgnoreCase(date_end)<0){
			min=date_start;
			max=date_end;
		}else{
			min=date_end;
			max=date_start;
		}
		String table;
		Connection conn=null;
		ViewTotal view;
		while(min.compareToIgnoreCase(max)<=0){
			view=DateRead.vtmap.get(min);
			if(null!=view){
				vtlist.add(view);
				min=DataFormat.formatDate(DataFormat.getNextDate(DataFormat.parseUtilDate(min,DataFormat.FMT_DATE_YYYY_MM_DD),1), DataFormat.FMT_DATE_YYYY_MM_DD);
				continue;
			}
			view=new ViewTotal();
			table=new StringBuffer("turn_").append(min).append("_s").toString();
			view.setDate(min);
			try{
				conn=DataConn.getLogStatConn();
				DateRead.getInstance().getTotalRateByDB(conn, view, table);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DataConn.releaseConn(conn);
			}
			vtlist.add(view);
			min=DataFormat.formatDate(DataFormat.getNextDate(DataFormat.parseUtilDate(min,DataFormat.FMT_DATE_YYYY_MM_DD),1), DataFormat.FMT_DATE_YYYY_MM_DD);
		}
		LogInfoWriter.operate_log.info("分页比例----"+max+"--"+min);
		return SUCCESS;
	}
}
