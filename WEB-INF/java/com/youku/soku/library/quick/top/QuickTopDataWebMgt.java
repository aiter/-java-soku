package com.youku.soku.library.quick.top;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.util.DataFormat;
import com.youku.top.quick.QuickVO;
import com.youku.top.util.DiectVideoType;

public class QuickTopDataWebMgt {
	
	private QuickTopDataWebMgt(){}
	private static QuickTopDataWebMgt instance = new QuickTopDataWebMgt();
	public static QuickTopDataWebMgt getInstance(){
		if(null!=instance)
			return instance;
		else return new QuickTopDataWebMgt();
	}
	
	public List<QuickVO> getQuickVODataReturnList(String between_date,int limit,int cate,String orderColumn){
		List<QuickVO> list = new ArrayList<QuickVO>();
		try{
		List<Record> records = BasePeer.executeQuery("select * from quick_"+between_date+" where visible=1 and cate="+cate+" order by "+orderColumn+" limit "+limit,"soku_top");
		QuickVO qvo = null;
		for(Record record:records){
			qvo = new QuickVO();
			qvo.setKeyword(record.getValue("keyword").asString());
			qvo.setQuery_count1(record.getValue("query_count1").asInt());
			qvo.setQuery_count2(record.getValue("query_count2").asInt());
			list.add(qvo);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	public Map<String,List<QuickVO>> getQuickVODataReturnMapOrderByQuerySub(String between_date,int limit){
		Map<String,List<QuickVO>> map = new HashMap<String, List<QuickVO>>();
		List<QuickVO> list = null;
		for(DiectVideoType dvt:DiectVideoType.values()){
			list = getQuickVODataReturnList(between_date, limit, dvt.getValue(),"order_number");
			if(null!=list&&list.size()>0)
				map.put(dvt.name(),list );
			System.out.println(dvt.name()+",list:"+(null!=list?list.size():0));
		}
		return map;
	}
	
	public Map<String,List<QuickVO>> getQuickVODataReturnMapOrderByQuerySub(){
		return getQuickVODataReturnMapOrderByQuerySub(getLastMonthBetween(), 50);
	}
	
	public List<QuickVO> getQuickVODateReturnListOrderByQuerySub(int cate){
		return getQuickVODataReturnList(getLastMonthBetween(), 50, cate,"order_number");
	}
	
	public Map<String,List<QuickVO>> getQuickVODataReturnMapOrderByQueryCount(String between_date,int limit){
		Map<String,List<QuickVO>> map = new HashMap<String, List<QuickVO>>();
		List<QuickVO> list = null;
		for(DiectVideoType dvt:DiectVideoType.values()){
			list = getQuickVODataReturnList(between_date, limit, dvt.getValue(),"query_count1 desc");
			if(null!=list&&list.size()>0)
				map.put(dvt.name(),list );
			System.out.println(dvt.name()+",list:"+(null!=list?list.size():0));
		}
		return map;
	}
	
	public Map<String,List<QuickVO>> getQuickVODataReturnMapOrderByQueryCount(){
		return getQuickVODataReturnMapOrderByQueryCount(getLastMonthBetween(), 50);
	}
	
	public List<QuickVO> getQuickVODateReturnListOrderByQueryCount(int cate){
		return getQuickVODataReturnList(getLastMonthBetween(), 50, cate,"query_count1 desc");
	}
	
	public static String getLastWeekBetween(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		cal.add(Calendar.DAY_OF_WEEK, -1);
		Date enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		Date startdate = cal.getTime();
		String start = DataFormat.formatDate(startdate,DataFormat.FMT_DATE_SPECIAL);
		String end = DataFormat.formatDate(enddate,DataFormat.FMT_DATE_SPECIAL);
		return new StringBuilder(start).append("_").append(end).toString();
	}
	
	public static String getLastLastWeekBetween(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		cal.add(Calendar.DAY_OF_WEEK, -1);
		Date enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		Date startdate = cal.getTime();
		cal.add(Calendar.DAY_OF_WEEK, -1);
		enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		startdate = cal.getTime();
		String start = DataFormat.formatDate(startdate,DataFormat.FMT_DATE_SPECIAL);
		String end = DataFormat.formatDate(enddate,DataFormat.FMT_DATE_SPECIAL);
		return new StringBuilder(start).append("_").append(end).toString();
	}
	
	public static String getLastMonthBetween(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date startdate = cal.getTime();
		String start = DataFormat.formatDate(startdate,DataFormat.FMT_DATE_SPECIAL);
		String end = DataFormat.formatDate(enddate,DataFormat.FMT_DATE_SPECIAL);
		return new StringBuilder(start).append("_").append(end).toString();
	}
	
	public static String getLastLastMonthBetween(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date startdate = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		startdate = cal.getTime();
		String start = DataFormat.formatDate(startdate,DataFormat.FMT_DATE_SPECIAL);
		String end = DataFormat.formatDate(enddate,DataFormat.FMT_DATE_SPECIAL);
		return new StringBuilder(start).append("_").append(end).toString();
	}
	
	public static String getLastMonth21Between(){
		Calendar cal = Calendar.getInstance();
		if(cal.get(Calendar.DAY_OF_MONTH)<20){
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.set(Calendar.DAY_OF_MONTH, 20);
		Date enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 21);
		Date startdate = cal.getTime();
		String start = DataFormat.formatDate(startdate,DataFormat.FMT_DATE_SPECIAL);
		String end = DataFormat.formatDate(enddate,DataFormat.FMT_DATE_SPECIAL);
		return new StringBuilder(start).append("_").append(end).toString();
	}
	
	public static String getLastLastMonth21Between(){
		Calendar cal = Calendar.getInstance();
		if(cal.get(Calendar.DAY_OF_MONTH)<20){
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.set(Calendar.DAY_OF_MONTH, 20);
		Date enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 21);
		Date startdate = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		enddate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 21);
		startdate = cal.getTime();
		String start = DataFormat.formatDate(startdate,DataFormat.FMT_DATE_SPECIAL);
		String end = DataFormat.formatDate(enddate,DataFormat.FMT_DATE_SPECIAL);
		return new StringBuilder(start).append("_").append(end).toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getLastMonth21Between());
		System.out.println(getLastLastMonth21Between());
	}
}
