/**
 * 
 */
package com.youku.top.index.boost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author william
 *
 */
public class BoostReader<T> {
	static File  directory = null;
	private static int version = 0;
	
	private static HashMap<String,Float> boostMap = new HashMap<String,Float>();
	
	
	public static float getBoost(String key)
	{
		if (boostMap.containsKey(key))
			return boostMap.get(key);
		return 0;
	}
	
	public static float getBoost(int key)
	{
		String k = String.valueOf(key);
		if (boostMap.containsKey(k ))
			return boostMap.get(k );
		return 0;
	}
	
	public static void reRead(String boostDir) throws NumberFormatException, IOException
	{
		boostMap.clear();
		boostMap = new HashMap<String,Float>();
		read(boostDir);
	}
	
	
	public static void read(String boostDir) throws NumberFormatException, IOException
	{
		System.out.println("BoostReader.read");
		long start = System.currentTimeMillis();
		
		Directory directory = FSDirectory.getDirectory(boostDir);
		
		if (!directory.fileExists(BoostNames.boostFileName) ||!directory.fileExists(BoostNames.boostFile_version) )
		{
			return;
		}
		
		//get last version
		File boost_version = new File(boostDir,BoostNames.boostFile_version);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(boost_version)));
		String line = null;
		try{
			
			while ( (line = r.readLine()) != null)
			{
				if (line.startsWith("version=")){
					version = Integer.parseInt(line.substring(8,line.length()));
					break;
				}
			}
		}
		finally
		{
			r.close();
		}
		System.out.println("version="+version);
		File boostFile = new File(boostDir,BoostNames.boostFileName);
		int startId = fileToMap(boostFile,0);
		
		//把更新的文件也加载到map中
		if (version > 1)
		{
			for (int i = 2; i<= version ;i++)
			{
				File file = new File(boostDir,BoostNames.boostFileName_new_prefix+i);
				startId = fileToMap(file,startId);
			}
		}
		
		System.out.println("boostreader open use :"+(System.currentTimeMillis() - start));
	}
	
	/**
	 * 返回结束点docid
	 * @param file
	 * @param start
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static int fileToMap(File file,int start) throws NumberFormatException, IOException
	{
		System.out.println("fileToMap file "+ file.getAbsolutePath() + " start = "+ start);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		
		try
		{
			while ( (line = r.readLine()) != null)
			{
				String[] arr = line.split("=");
				if (arr != null && arr.length==2){
					boostMap.put(arr[0],Float.valueOf(arr[1]));
				}
				start ++;
			}
		}
		finally{
			r.close();
		}
		return start;
	}
	
}
