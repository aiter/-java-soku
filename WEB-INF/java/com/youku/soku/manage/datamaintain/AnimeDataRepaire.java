package com.youku.soku.manage.datamaintain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.youku.soku.manage.datamaintain.SokuOldDataImport.ImportDataStruct;

public class AnimeDataRepaire {
	
	//去除动漫中已经整理过的重复的词
	
	public static void main(String[] args) throws Exception {
		File dataFile = new File("e:/MyDocument/动漫名字重复.log");
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		
		File dataFile1 = new File("e:/MyDocument/success_import_data_cate5.csv");
		BufferedReader br1 = new BufferedReader(new FileReader(dataFile1));
		
		File resultFile = new File("e:/MyDocument/动漫数据.csv");
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(resultFile));
		
		String line;
		List<ImportDataStruct> result = new ArrayList<ImportDataStruct>();
		while((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			if(st.hasMoreElements()) {
				
				ImportDataStruct data = new ImportDataStruct();
				try {
					data.setShowId(st.nextToken());
					data.setContentId(Integer.valueOf(st.nextToken()));
					data.setSokuId(Integer.valueOf(st.nextToken()));
					data.setYoukuName(st.nextToken().trim());
					
					
				} catch (Exception e) {
				}
				result.add(data);
			}
			
		}
		
		br.close();
		
		while((line = br1.readLine()) != null) {
			//StringTokenizer st = new StringTokenizer(line, ",");
			String[] contents = line.split(",");
			//if(st.hasMoreElements()) {}
			
			if(contents.length < 2) {
				continue;
			}
			
			String youkuName = contents[1].trim();
			System.out.println(youkuName);
			boolean processed = false;
			for(ImportDataStruct data : result) {
				
				if(data.getYoukuName().equals(youkuName)){
					System.out.println("=====" + data.getYoukuName());
					processed = true;
					break;
				}
			}
			
			if(!processed) {
				bw1.write(line + "\n");
			}
			
		
		}
		
		br1.close();
		bw1.flush();
		bw1.close();
		
	}

}
