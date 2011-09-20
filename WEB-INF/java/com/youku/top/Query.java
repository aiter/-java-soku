package com.youku.top;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.quick.FileUtils;
import com.youku.top.quick.QueryTopWordsMerger;
import com.youku.top.quick.QuickVO;
import com.youku.top.quick.QuickVOComparator;

public class Query {
	
	private static int getQueryCount(Map<String,Integer> map,Set<String> ks){
		int sum = 0;
		for(String s:ks){
			sum = sum + DataFormat.parseInt(map.get(s));
		}
		return sum;
	}
	
	public static void getQueryCountByIndex(Map<String,Integer> map,String filepath,String newName){
		List<String> list = FileUtils.readFile(filepath);
		Map<String,Set<String>> keywords = new HashMap<String,Set<String>>();
		Set<String> ks = null;
		for(String str:list){
			ks = new HashSet<String>();
			ks.add(str);
			ks.add(str.toLowerCase());
			ks.add(str.toUpperCase());
			ks.add(Utils.stopWordsFilter(str).trim());
			ks.add(Utils.stopWordsFilter(str.toLowerCase()).trim());
			ks.add(Utils.stopWordsFilter(str.toUpperCase()).trim());
			ks.add(Utils.analyzerForSearch(str).trim());
			keywords.put(str, ks);
		}
		List<QuickVO> qvos = new ArrayList<QuickVO>();
		QuickVO qvo = null;
		for(Entry<String, Set<String>> entry:keywords.entrySet()){
			qvo = new QuickVO();
			qvo.setKeyword(entry.getKey());
			qvo.setQuery_count_sub(getQueryCount(map, entry.getValue()));
			qvos.add(qvo);
		}
		Collections.sort(qvos, new QuickVOComparator());
		
		for(QuickVO qv:qvos){
			FileUtils.appendToFile(newName, qv.getKeyword()+"\t"+qv.getQuery_count_sub()+"\n", "utf-8");
		}
	}
	
	public static void main(String[] args) {
		String startdate = null;
		String enddate = null;
		
		if(null!=args){
			if(args.length==2&&args[0].matches("\\d{8}")&&args[1].matches("\\d{8}")){
				startdate = args[0];
				enddate = args[1];
			}	
		}
		
		if(StringUtils.isBlank(startdate)){
			startdate = "20100703";
		}
		if(StringUtils.isBlank(enddate)){
			enddate = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_SPECIAL);
		}
		
		List<String> dates = new ArrayList<String>();
		Date date = DataFormat.parseUtilDate(startdate, DataFormat.FMT_DATE_SPECIAL);
		String date2 = DataFormat.formatDate(DataFormat.parseUtilDate(enddate, DataFormat.FMT_DATE_SPECIAL),DataFormat.FMT_DATE_YYYY_MM_DD);
		String date1 = null;
		for(int i=0;;i++){
			date1 = DataFormat.formatDate(DataFormat.getNextDate(date, i), DataFormat.FMT_DATE_YYYY_MM_DD);
			dates.add(date1);
			if(date1.equalsIgnoreCase(date2))
				break;
		}
		Map<String,Integer> map = QueryTopWordsMerger.getInstance().getQueryMap(dates);
		String dir = "/opt/log_analyze/tmp/";
		File file = new File(dir);
		if(file.isDirectory()){
			String[] names = file.list();
			if(null!=names){
				for(String fn:names){
					getQueryCountByIndex(map,dir+fn,"new_"+fn);
				}
			}
		}
	}
}
