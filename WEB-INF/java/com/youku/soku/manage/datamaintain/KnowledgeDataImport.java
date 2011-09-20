package com.youku.soku.manage.datamaintain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.youku.soku.library.load.KnowledgeColumn;

public class KnowledgeDataImport {
	
	
	private int step = 0;
	
	private void readDataFromFile(int depth, File file, List<KnowledgeColumn> dataList) throws Exception {
		if(file.isDirectory()) {
			step++;
			File[] subFiles = file.listFiles();
			
			for(File aFile : subFiles) {
				if(aFile.isDirectory()) {
					KnowledgeColumn kc = new KnowledgeColumn();
					kc.setName(aFile.getName());
					kc.setLevel(depth);
					dataList.add(kc);
				}
				readDataFromFile(depth + 1, aFile, dataList);
			}
		} else {
			//BufferedReader reader = new BufferedReader(new FileReader(file, ));
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()),"GB2312"));
			String line = null;
			while((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				if(st.hasMoreElements()) {
					String name = st.nextToken();
					KnowledgeColumn kc = new KnowledgeColumn();
					kc.setName(name);
					kc.setLevel(depth - 1);
					dataList.add(kc);
				}
			}
		}
	}

	public void imporData() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/opt/soku_data_import_log/knowledge.txt"),"utf-8"));
		String line = null;
		int parentId1 = 0;
		int parentId2 = 0;
		int parentId3 = 0;
		while((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			String level = st.nextToken();
			String name = st.nextToken();
			if(name != null) {
				name = name.trim();
			}
			KnowledgeColumn kc = new KnowledgeColumn();
			kc.setName(name);
			kc.setLevel(Integer.valueOf(level));
			kc.setPic("");
			if(Integer.valueOf(level) == 1) {
				kc.setParentId(0);
			}
			if(Integer.valueOf(level) == 2) {
				kc.setParentId(parentId1);
			}
			if(Integer.valueOf(level) == 3) {
				kc.setParentId(parentId2);
			}
			if(Integer.valueOf(level) == 4) {
				kc.setParentId(parentId3);
			}
			kc.save();
			if(Integer.valueOf(level) == 1) {
				parentId1 = kc.getId();
			}
			if(Integer.valueOf(level) == 2) {
				parentId2 = kc.getId();
			}
			if(Integer.valueOf(level) == 3) {
				parentId3 = kc.getId();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<KnowledgeColumn> dataList = new ArrayList<KnowledgeColumn>();
		File directory = new File("E:/MyDocument/zhishi");
		new KnowledgeDataImport().readDataFromFile(1, directory, dataList);
		int i = 0;
		for(KnowledgeColumn kc : dataList) {
			i++;
			System.out.format("%d, %s \n", kc.getLevel(), kc.getName());
		}
		System.out.println(i);
	}
}
