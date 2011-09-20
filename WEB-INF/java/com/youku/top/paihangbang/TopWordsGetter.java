package com.youku.top.paihangbang;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.youku.search.util.DataFormat;
import com.youku.top.paihangbang.entity.TopWordsEntity;
import com.youku.top.util.TopDateType.TopDate;

public class TopWordsGetter {
	
	public static JSONArray getTopWordsReturnJSONArray(int cate,int limit){
		
		Map<String,String> map = TopDateMgt.getInstance().getTopDate();
		
		String date_str = map.get(TopDate.top.name());
		
		Date date = DataFormat.parseUtilDate(date_str, DataFormat.FMT_DATE_YYYY_MM_DD);
		try{
		List<TopWordsEntity> twes = TopWordsMgt.getInstance().doGetTopWords(date, cate, limit);
		
		return TopWordsConverter.convert(twes);
		}catch(Exception e){}
		
		return new JSONArray();
	}
	
}
