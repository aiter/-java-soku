package com.youku.search.index.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.youku.search.util.DataFormat;

public class LastUpdateContainer {
	
	static String path = "/tmp/";
	
	static Map<Integer,Date> map = new ConcurrentHashMap<Integer,Date>();
	
	public static void put(int type,Date date){
		map.put(type, date);
		try {
			touchFile(type,date);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Date get(int type){
		Date date = map.get(type);
		
		if (date == null){
			date = getDateFromFile(type);
			if (date == null){
				date = new Date();
			}
			
			map.put(type, date);
		}
		
		return date;
	}
	
	public static void touchFile(int type,Date date) throws IOException{
		File file = new File(path  + "INDEX_LAST_UPDATE_" +type);
		if (!file.exists()){
			file.createNewFile();
		}
		Writer writer = new FileWriter(file,false);
		writer.write(String.valueOf(date.getTime()));
		writer.close();
	}
	
	public static Date getDateFromFile(int type){
		File file = new File(path  + "INDEX_LAST_UPDATE_" + type);
		
		BufferedReader r;
		try {
			r = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			
			if ( (line = r.readLine()) != null && !line.trim().isEmpty())
			{
				long time = DataFormat.parseLong(line);
				if (time > 0)
					return new Date(time);
			}
			r.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}
