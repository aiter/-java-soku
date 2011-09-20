package com.youku.soku.library.quick;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.topn.KeywordWeekVOMapper;
import com.youku.top.topn.entity.KeywordWeekVO;

public class QuickDateMgt {
	static Logger logger = Logger.getLogger(QuickDateMgt.class);
	
	private QuickDateMgt(){}
	private static QuickDateMgt instance = new QuickDateMgt();
	public static  QuickDateMgt getInstance(){
		if(null==instance)
			return new QuickDateMgt();
		else return instance;
	}
	
	final static int VIEW_SIZE = 100;
	final static int MAP_SIZE = 6;
	
	private List<KeywordWeekVO> getInsiteUnionQueryKeywords(String date,String type,String order,int offset,int limit,String cate){
		String sql="select * from week_query_youku_"+date+ " where query_count>15 and query_type='"+type+"' ";
		if(StringUtils.isBlank(cate)||cate.trim().equalsIgnoreCase("topconcern")){
			sql = sql + " order by "+order+" desc limit "+offset+","+limit;
		}else
			sql = sql + "and types like '"+cate+"' order by "+order+" desc limit "+offset+","+limit;
		try{
			System.out.println(sql);
			return JdbcTemplateFactoray.mergeLogYoukuDataSource.query(sql, KeywordWeekVOMapper.kwvomapper);
		}catch(Exception e){
			logger.error(e);
		}
		return null;
	}
	
	public QuickDataVO getQuickDataVO(String date,String order,String type,String cate,int lsize){
		QuickDataVO qvo = new QuickDataVO();
		qvo.setDate(date);
		qvo.getMap().put(cate, new ArrayList<KeywordWeekVO>());
		List<KeywordWeekVO> kvos = null;
		int offset = 0;
		int limit = 2000;
		Set<String> types = null;
		while(!isComplete(qvo,lsize)){
			if(offset>=20000) break;
			kvos = getInsiteUnionQueryKeywords(date, type, order, offset, limit,cate);
			offset = offset+limit;
			if(null==kvos||kvos.size()<1) break;
			for(KeywordWeekVO kvo:kvos){
				if(isComplete(qvo,lsize)) break;
				if(StringUtils.isBlank(cate)||cate.trim().equalsIgnoreCase("topconcern")){
					qvo.getMap().get("topconcern").add(kvo);
				}else{
					types = kvo.getTypes();
					if(null!=types&&types.size()>0){
						for(String t:types){
							if(t.equalsIgnoreCase("topconcern"))
								continue;
							qvo.getMap().get(t).add(kvo);
						}
					}
				}
			}
		}
		return qvo;
	}
	
	public QuickDataVO getQuickDataVO(String t,int limit){
		Calendar cal = Calendar.getInstance();
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, -day_of_week);
		Date end_date = cal.getTime();
		cal.add(Calendar.DATE, -6);
		Date start_date = cal.getTime();
		String start = DataFormat.formatDate(start_date,DataFormat.FMT_DATE_SPECIAL);
		String end = DataFormat.formatDate(end_date,DataFormat.FMT_DATE_SPECIAL);
		return getQuickDataVO(new StringBuilder(start).append("_").append(end).toString(), "quick_rate", "video",t,limit);
		
	}
	
	public boolean isComplete(QuickDataVO qvo,int lsize){
		for(Entry<String,List<KeywordWeekVO>> entry:qvo.getMap().entrySet()){
			if(entry.getValue().size()<lsize) return false;
		}
		return true;
	}
}
