package com.youku.top.quick;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.DataFormat;

public class FileUtils {
	public static List<String> readFile(String fileName) {
		List<String> result = new ArrayList<String>();
		try {
			BufferedReader d = new BufferedReader(new FileReader(fileName));
			String s = d.readLine();
			while (s != null) {
				s = s.trim();
				if (s.equals("")) {
					s = d.readLine();
					continue;
				}
				result.add(s);
				s = d.readLine();
			}
		} catch (Exception e) {

		}
		return result;
	}
	
	public static Map<String,Integer> list2Map(List<String> list,String split){
		Map<String,Integer> map = new HashMap<String, Integer>();
		if(null==list||list.size()<1) return map;
		String[] arr = null;
		int query_count = 0;
		String keyword = null;
		for(String str:list){
			if(StringUtils.isBlank(str))
				continue;
			arr = str.split(split);
			if(null==arr||arr.length!=2)
				continue;
			query_count = DataFormat.parseInt(arr[1]);
			if(query_count<1) continue;
			keyword = arr[0];
			if(StringUtils.isBlank(keyword)) continue;
			map.put(keyword.trim(), query_count);
		}
		return map;
	}
	
	public static void appendToFile(String fileName, String s,String encode) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName, true), encode));
			bw.write(s);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null!=bw)
					bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
