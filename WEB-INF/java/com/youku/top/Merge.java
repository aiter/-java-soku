package com.youku.top;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.soku.pos_analysis.KeywordAnalysis;

public class Merge {
	static KeywordAnalysis ka = new KeywordAnalysis();
	
	static class Kvo{
		String user_key="";
		Map<String,Integer> map = new HashMap<String, Integer>();
		int total = 0;
		Map<String,Integer> cateQueryMap = new HashMap<String, Integer>();
		public String getUser_key() {
			return user_key;
		}
		public void setUser_key(String userKey) {
			user_key = userKey;
		}
		public Map<String, Integer> getMap() {
			return map;
		}
		public void setMap(Map<String, Integer> map) {
			this.map = map;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public Map<String, Integer> getCateQueryMap() {
			return cateQueryMap;
		}
		public void setCateQueryMap(Map<String, Integer> cateQueryMap) {
			this.cateQueryMap = cateQueryMap;
		}
		
	}
	
	public static int getPerValue(String value){
		int v =(int) DataFormat.parseFloat(value,0);
		return v/10*10;
	}
	
	public static void clear(Kvo kvo){
		if(null!=kvo){
			kvo.getCateQueryMap().clear();
			kvo.getMap().clear();
		}
	}
	
	public static int parseInt ( Object strData ,int defaultValue)
	{
		if (strData == null || strData.toString().length ( ) == 0)
		{
			return defaultValue ;
		} else
		{
			try
			{
				return Integer.parseInt(strData.toString()) ;
			}catch(Exception e)
			{
				return defaultValue;
			}
		}
	}
	
	public static String parse2Str(List<String> list) {
		if (null == list || list.size() == 0)
			return null;
		StringBuilder strs = new StringBuilder();
		int i = 0;
		for (String str : list) {
			if (!isBlank(str)) {
				if (i != 0)
					strs.append("|");
				strs.append(str.trim());
				i++;
			}
		}
		if (strs.length() > 0)
			return strs.toString();
		else
			return "";
	}
	
	public static void readLog(File file,Set<String> stopset,Map<String,Map<String,Integer>> map){
		List<String> list = readFile(file, "utf-8");
		List<String> columns = null;
		Map<String,Integer> kc = null;
		List<String> is = null;
		List<String> ks = null;
		for(String str:list){
			columns = parseStr2List(str, "\\t");
			if(null!=columns&&columns.size()==2){
				kc = map.get(columns.get(0));
				ks = parseStr2List(columns.get(1), "\\|");
				if(null!=ks&&ks.size()>0){
					for(String s:ks){
						is = parseStr2List(s, ":");
						if(null!=is&&is.size()==2){
								if(null==kc){
									kc = new HashMap<String, Integer>();
									kc.put(is.get(0), parseInt(is.get(1),0));
								}else
									kc.put(is.get(0), parseInt(is.get(1),0)+parseInt(kc.get(is.get(0)),0));
								map.put(columns.get(0),kc);
						}
					}
				}
			}
		}
	}
	
	public static void readDirectory(String dir){
//		Map<Integer,Integer> maxmap = new HashMap<Integer, Integer>();
		Map<Integer,Integer> catemap = new HashMap<Integer, Integer>();
		File[] names = null;
		File file = new File(dir);
		if(file.isFile())
			names = new File[]{file};
		else
			names = new File(dir).listFiles();
		List<String> lines = null;
		List<String> strs = null;
		List<Integer> keys = new ArrayList<Integer>();
		List<Integer> values = new ArrayList<Integer>();
		int v = 0;
		if(null!=names&&names.length>0){
			for(File f:names){
				if(null!=lines)
					lines.clear();
				lines = readFile(f,"utf-8");
				if(null!=lines){
					int j = 0;
					int k=0;
					for(String line:lines){
						strs = parseStr2List(line, "\\t");
//						if(null!=strs&&strs.size()==7){
//							v = getPerValue(strs.get(3));
//							maxmap.put(v, 1+DataFormat.parseInt(maxmap.get(v)));
//							v = getPerValue(strs.get(5));
//							catemap.put(v, 1+DataFormat.parseInt(catemap.get(v)));
//						}
						
						if(null!=strs&&strs.size()==2){
							v = DataFormat.parseInt(strs.get(0));
//							if(v<=500){
//								v = j/5+1;
//								j = j+1;
//							}else{
//								v = 101;
//							}
							
							if(k>500) break;
							k++;
							
							if(j<20){
								keys.add(v);
								values.add(DataFormat.parseInt(strs.get(1)));
								j++;
							}else{
								j=0;
								System.out.println(Arrays.toString(keys.toArray(new Integer[]{})));
								System.out.println(Arrays.toString(values.toArray(new Integer[]{})));
							}
							
							
//							catemap.put(v, DataFormat.parseInt(strs.get(1))+DataFormat.parseInt(catemap.get(v)));
						}
						
					}
				}
			}
		}
		
//		for(Entry<Integer, Integer> entry:maxmap.entrySet()){
//			Utils.appendToFile("/vol/user_log/maxmap.txt", entry.getKey()+"\t"+entry.getValue());
//		}
//		for(Entry<Integer, Integer> entry:catemap.entrySet()){
//			Utils.appendToFile("C:\\Program Files\\SecureCRT\\download\\map.txt", entry.getKey()+"\t"+entry.getValue());
//		}
	}
	
	public static void main(String[] args) {
		
		readDirectory("C:\\Program Files\\SecureCRT\\download\\tt.txt");
		
//		File file = new File("/vol/user_log/sort_s");
//		parserFile(file, "utf-8");
		
//		parserFile("C:\\Program Files\\SecureCRT\\download\\s");
		
//		System.out.println(338663+9593661+1508661+5831150+5055971+1179096+2407824+2737766+271483+3812957+5459175);
//		System.out.println(338663*100.0/38196407);
//		System.out.println(2407824*100.0/38196407);
//		System.out.println(5055971*100.0/38196407);
//		System.out.println(5459175*100.0/38196407);
//		System.out.println(2737766*100.0/38196407);
//		System.out.println(9593661*100.0/38196407);
//		System.out.println(3812957*100.0/38196407);
//		System.out.println(1508661*100.0/38196407);
//		System.out.println(1179096*100.0/38196407);
//		System.out.println(271483*100.0/38196407);
//		System.out.println(5831150*100.0/38196407);
		
//		System.out.println(10780+7131736+2592087+13381628+1550314+2494557+280515+2375671+2835555+783508+4760056);
//		System.out.println(10780*100.0/38196407);
//		System.out.println(280515*100.0/38196407);
//		System.out.println(1550314*100.0/38196407);
//		System.out.println(2835555*100.0/38196407);
//		System.out.println(2375671*100.0/38196407);
//		System.out.println(7131736*100.0/38196407);
//		System.out.println(4760056*100.0/38196407);
//		System.out.println(2592087*100.0/38196407);
//		System.out.println(2494557*100.0/38196407);
//		System.out.println(783508*100.0/38196407);
//		System.out.println(13381628*100.0/38196407);
	}
	
	public static void parserFile(String path) {
		File file = new File(path);
		parserFile(file, "utf-8");
	}
	
	public static void parserFile(File file,String encode) {
		List<String> result = new ArrayList<String>();
		List<String> lines = new ArrayList<String>();
		try {
			String user_key="a";
			String keyword = "";
			int num=0;
			int flag = 0;
			Kvo kvo = null;
			Kvo temp = new Kvo();
			InputStreamReader read =new InputStreamReader(new FileInputStream(file),encode);
			BufferedReader d = new BufferedReader(read);
			String s = d.readLine();
			Map<Integer,Integer> map = new HashMap<Integer, Integer>();
			while (s != null) {
				synchronized (s) {
				if (!isBlank(s)) {
					s = s.trim();
					result = parseStr2List(s, "\\t");
					if(null!=result&&result.size()==3){
						user_key = result.get(0);
						keyword = result.get(1);
						num = DataFormat.parseInt(result.get(2));
						if(num>0){
							if(flag>0){
								temp = kvo;
							}
							if(!user_key.equalsIgnoreCase(temp.getUser_key())){
								
								if(lines.size()>2000000){
									appendToFile("/vol/user_log/out.txt", lines);
									
//									appendToFile("C:\\Program Files\\SecureCRT\\download\\out"+j+".txt", lines);
									
									lines.clear();
									lines = new ArrayList<String>();
								}
								
								if(flag!=0){
									
									if(kvo.getTotal()>1){
									//输出kvo
										int max = 0;
										for(Entry<String, Integer> entry:kvo.getMap().entrySet()){
											if(entry.getValue()>max)
												max = entry.getValue();
										}
										int catemax = 0;
										String catename = "";
										for(Entry<String, Integer> entry:kvo.getCateQueryMap().entrySet()){
											if(entry.getValue()>catemax){
												catemax = entry.getValue();
												catename = entry.getKey();
											}
										}
										
										if(catemax<max)
											catemax=max;
										
										lines.add(kvo.getUser_key()+"\t"+max+"\t"+kvo.getTotal()+"\t"+100.0*max/kvo.getTotal()+"\t"+catemax+"\t"+100.0*catemax/kvo.getTotal()+"\t"+catename);
										
										
									}
									
									map.put(kvo.getTotal(), 1+DataFormat.parseInt(map.get(kvo.getTotal())));
									
								}
								kvo = new Kvo();
								kvo.setUser_key(user_key);
								kvo.setTotal(num);
								kvo.getMap().put(keyword, num);
								List<String> types = ka.parseType(keyword);
								if(null!=types){
									for(String type:types){
										kvo.getCateQueryMap().put(type, num+DataFormat.parseInt(kvo.getCateQueryMap().get(type)));
									}
								}
								
							}else{
								kvo.setTotal(kvo.getTotal()+num);
								kvo.getMap().put(keyword, num+DataFormat.parseInt(kvo.getMap().get(keyword)));
								List<String> types = ka.parseType(keyword);
								if(null!=types){
									for(String type:types){
										kvo.getCateQueryMap().put(type, num+DataFormat.parseInt(kvo.getCateQueryMap().get(type)));
									}
								}
							}
						}
					}
					flag +=1;
				}
				}
				s = d.readLine();
			}
			//输出kvo
			int max = 0;
			for(Entry<String, Integer> entry:kvo.getMap().entrySet()){
				if(entry.getValue()>max)
					max = entry.getValue();
			}
			int catemax = 0;
			String catename = "";
			for(Entry<String, Integer> entry:kvo.getCateQueryMap().entrySet()){
				if(entry.getValue()>catemax){
					catemax = entry.getValue();
					catename = entry.getKey();
				}
			}
			if(catemax<max)
				catemax=max;
			
			if(kvo.getTotal()>1){
				lines.add(kvo.getUser_key()+"\t"+max+"\t"+kvo.getTotal()+"\t"+100.0*max/kvo.getTotal()+"\t"+catemax+"\t"+100.0*catemax/kvo.getTotal()+"\t"+catename);
			}
			appendToFile("/vol/user_log/out.txt", lines);
			
			map.put(kvo.getTotal(), 1+DataFormat.parseInt(map.get(kvo.getTotal())));
			for(Entry<Integer, Integer> entry:map.entrySet()){
				Utils.appendToFile("/vol/user_log/map.txt", entry.getKey()+"\t"+entry.getValue());
			}
			
			Map<Integer,Integer> nummap = new HashMap<Integer, Integer>();
			int key = 0;
			for(Entry<Integer, Integer> entry:map.entrySet()){
				key = entry.getKey();
				if(key==1||key==2)
					nummap.put(key, entry.getValue());
				else if(key>2&&key<6)
					nummap.put(3, entry.getValue()+DataFormat.parseInt(nummap.get(3)));
				else if(key>5&&key<11)
					nummap.put(4, entry.getValue()+DataFormat.parseInt(nummap.get(4)));
				else
					nummap.put(5, entry.getValue()+DataFormat.parseInt(nummap.get(5)));
			}
			
			for(Entry<Integer, Integer> entry:nummap.entrySet()){
				Utils.appendToFile("/vol/user_log/mergemap.txt", entry.getKey()+"\t"+entry.getValue());
			}
			
			
//			appendToFile("C:\\Program Files\\SecureCRT\\download\\out"+j+".txt", lines);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> readFile(File file,String encode) {
		List<String> result = new ArrayList<String>();
		try {
			InputStreamReader read =new InputStreamReader(new FileInputStream(file),encode);
			BufferedReader d = new BufferedReader(read);
			String s = d.readLine();
			while (s != null) {
				if (!isBlank(s)) {
					result.add(s.trim());
				}
				s = d.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void readFileToSet(File file,String encode,Set<String> set) {
		try {
			InputStreamReader read =new InputStreamReader(new FileInputStream(file),encode);
			BufferedReader d = new BufferedReader(read);
			String s = d.readLine();
			while (s != null) {
				if (!isBlank(s)) {
					set.add(s.trim());
				}
				s = d.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Set<String> readFileToSet(String dir,String encode) {
		Set<String> result = new HashSet<String>();
		File[] names = null;
		File file = new File(dir);
		if(file.isFile())
			names = new File[]{file};
		else
			names = new File(dir).listFiles();
		if(null!=names&&names.length>0){
			for(File f:names){
				readFileToSet(f, "utf-8", result);
			}
		}
		return result;
	}
	
	public static List<String> parseStr2List(String str, String split) {
		List<String> alias = new ArrayList<String>();
		String[] aliasArr = str.split(split);
		if (null != aliasArr) {
			for (String a : aliasArr) {
				if (!isBlank(a))
					alias.add(a.trim());
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}
	
	public static void appendToFile(String fileName, Map<String,Map<String,Integer>> map) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName, true), "utf-8"));
			for(Entry<String, Map<String,Integer>> entry:map.entrySet()){
				for(Entry<String, Integer> v:entry.getValue().entrySet()){
					bw.write(entry.getKey()+"\t"+v.getKey()+"\t"+v.getValue());
					bw.write("\r\n");
					bw.flush();
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception ie) {
				}
			}
		}
	}
	
	public static void appendToFile(String fileName, List<String> list) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName, true), "utf-8"));
				for(String res:list){
					bw.write(res);
					bw.write("\r\n");
					bw.flush();
				}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception ie) {
				}
			}
		}
	}
	
	public static boolean isBlank(String str){
		if(null==str)
			return true;
		if(str.trim().length()<1)
			return true;
		if(str.trim().toLowerCase().equalsIgnoreCase("null"))
			return true;
		return false;
	}
}
