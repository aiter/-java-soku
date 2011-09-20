/**
 * 
 */
package com.youku.search.index.boost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.youku.search.util.DataFormat;


/**
 * @author william
 *
 */
public class BoostWriter {
	
	private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	private Directory directory = null;
	private String boostPath = null;
	private int version = 0;
	private int base = 0;
	private int rows = 0;  //the rows of current add 
	private String newIndexName = null; //new boost file
	
	private final int BUFFER_SIZE = 1000;
	
	public BoostWriter(String boostPath,boolean create)throws IOException
	{
		directory = FSDirectory.getDirectory(boostPath);
		this.boostPath = boostPath;
		if (!create && isLock()){
			_log.error("boost.writer is in used");
			return ;
		}
		doLock();
		
		if (create)
		{
			String[] files = new File(boostPath).list();
			for (int i=0;i<files.length;i++)
			{
				if (files[i].startsWith("index") && !files[i].equals(BoostNames.boostFileLock)){
					directory.deleteFile(files[i]);
				}
			}
		}
		else
		{
			if (directory.fileExists(BoostNames.boostFile_version))
			{
				initVer();
			}
			else
			{
				_log.error(BoostNames.boostFile_version + " is not exist");
				unLock();
				return ;
			}
		}
		
		int newVers = version + 1 ;
		
		if (newVers == 1)
			newIndexName = BoostNames.boostFileName;
		else
			newIndexName = BoostNames.boostFileName_new_prefix + newVers;

		try {
			writer = new FileWriter(new File(boostPath,newIndexName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	private boolean isLock()
	{
		try {
			return directory.fileExists(BoostNames.boostFileLock);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void doLock() throws IOException
	{
		if (directory.fileExists(BoostNames.boostFileLock))
			directory.touchFile(BoostNames.boostFileLock);
		else
			new File(boostPath,BoostNames.boostFileLock).createNewFile();
	}
	
	private void unLock()
	{
		try {
			if (directory.fileExists(BoostNames.boostFileLock))
				directory.deleteFile(BoostNames.boostFileLock);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	FileWriter writer = null;
	StringBuffer writerBuffer = new StringBuffer();
	
	
	public void write (Boost boost)
	{
//		_log.info("vid:"+vid+"\t boost:"+boost);
		if (writer == null)
		{
			_log.error("error:boost writer is not opened!!");
			return;
		}
		if (boost.getBoost() <= 0)
			return;
		
		 try {
			writerBuffer.append(boost.getKey()+"="+ boost.getBoost() +"\r\n");

			if (rows % BUFFER_SIZE == 0) //flush 
			{
				writer.write(writerBuffer.toString());
				writerBuffer = new StringBuffer();
			}
			rows++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void closeWriter () throws IOException
	{
		if (writer == null)
		{
			_log.error("error:boost writer is null!!");
			
			if (isLock())unLock();
			
			return;
		}
		
		if (writerBuffer.length() > 0)
		{
			writer.write(writerBuffer.toString());
			writerBuffer = new StringBuffer();
		}
//		update version to new
		writer.close();
		writer = null;
		
		if (rows > 0)
			updateVersion(version+1,base+rows);
		else
			directory.deleteFile(newIndexName);
		
		unLock();
	}
	
	public void initVer() throws IOException
	{
		File boost_version = new File(this.boostPath,BoostNames.boostFile_version);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(boost_version)));
		String line = null;
		try{
			
			while ( (line = r.readLine()) != null)
			{
				if (line.startsWith("version="))
					version = Integer.parseInt(line.substring(8,line.length()));
				else if (line.startsWith("docCount="))
					base = Integer.parseInt(line.substring(9,line.length()));
			}
		}
		finally
		{
			r.close();
		}
	}
	
	/**
	 * update the boost version and the max docid
	 * @param version
	 * @throws IOException 
	 */
	public void updateVersion( int vers,int docCount) throws IOException
	{
//		_log.info("version="+vers);
//		_log.info("docCount="+docCount);
		File boost_version = new File(this.boostPath,BoostNames.boostFile_version);
		FileWriter versWriter = null;
		try {
			versWriter = new FileWriter(boost_version,false);
			versWriter.write("version="+vers +"\r\n");
			versWriter.write("docCount="+docCount);
		}
		finally
		{
			try {
				versWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	HashSet<String> boostDeleteSet = new HashSet<String>();
	HashMap<String,Float > boostUpdateMap = new HashMap<String,Float >();
	/**
	 * delete the boost in memory
	 * @throws IOException 
	 *
	 */
	public void flushDeleteBoost() throws IOException
	{
		if (boostDeleteSet.size() > 0)
		{
			File boost_delte = new File(this.boostPath,BoostNames.boostFileDelete);
			FileWriter delWriter = null;
			try {
				delWriter = new FileWriter(boost_delte,true);
				Iterator<String> it = boostDeleteSet.iterator();
				while (it.hasNext())
				{
					delWriter.write(it.next().toString() +"\r\n");
				}
			}
			finally
			{
				try {
					delWriter.close();
				} catch (IOException e) {
				}
			}
			
			boostDeleteSet.clear(); //clear the delete boost buffer 
		}
	}
	
	public void deleteBoostToBuffer(int docid)
	{
		_log.info("delete " + docid);
		boostDeleteSet.add(String.valueOf(docid));
	}
	
	public void deleteBoost(String vid) throws IOException
	{
		File boost_delte = new File(this.boostPath,BoostNames.boostFileDelete);
		FileWriter delWriter = null;
		try {
			delWriter = new FileWriter(boost_delte,true);
			delWriter.write(vid +"\r\n");
		}
		finally
		{
			try {
				delWriter.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void deleteBoost(String[] vids) throws IOException
	{
		File boost_delte = new File(this.boostPath,BoostNames.boostFileDelete);
		FileWriter delWriter = null;
		try {
			delWriter = new FileWriter(boost_delte,true);
			for (int i = 0; i <vids.length;i++)
				delWriter.write(vids[i] +"\r\n");
		}
		finally
		{
			try {
				delWriter.close();
			} catch (IOException e) {
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void optimize() throws IOException
	{
		//加载删除
		if (directory.fileExists(BoostNames.boostFileDelete))
		{
			_log.info("file exist "+ BoostNames.boostFileDelete);
			if (boostDeleteSet.size() == 0)
				readDeleteFileToMemory(); //read the delete file to memory
			
			_log.info("delete size = "+boostDeleteSet.size());
//			delete the delete file
			directory.deleteFile(BoostNames.boostFileDelete);
		}
		//加载更新
		if (directory.fileExists(BoostNames.boostFileUpdate))
		{
			_log.info("file exist "+ BoostNames.boostFileUpdate);
			if (boostUpdateMap.size() == 0)
				readUpdateFileToMemory(); //read the update file to memory
			
			_log.info("update size = "+boostUpdateMap.size());
//			delete the delete file
			directory.deleteFile(BoostNames.boostFileUpdate);
		}
		
		if (writer!=null)
		{
			if (writerBuffer.length() >0)
			{
				writer.write(writerBuffer.toString());  //flush boost from memory to boost doc
			}
			
			writer.close();
			writer = null;
			writerBuffer = new StringBuffer();
			version++;
		}
		
			int docCount = 0;
			String newFileName = BoostNames.boostFileName + "." + (int)System.currentTimeMillis();
//			_log.info("newFile="+newFile.getAbsolutePath());
//			_log.info("version="+version);
			FileWriter opWriter = new FileWriter(new File(this.boostPath,newFileName));
			StringBuffer sb = new StringBuffer();
//			_log.info("version="+version);

			for (int i = 1; i <= version ;i++)
			{
				String name = null;
				if (i == 1)
					name = BoostNames.boostFileName;
				else
					name = BoostNames.boostFileName_new_prefix+i;
				
//				_log.info("DEBUG: get file:" + name);
				File boostFile = new File(this.boostPath,name);
				if(!boostFile.exists())
					continue;
				BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(boostFile)));
				String line = null;
				
				try
				{
					while ( (line = r.readLine()) != null)
					{
						String[] arr = line.split("=");
						String key = arr[0];
						float boost = Float.valueOf(arr[1]);
						
						if (!boostDeleteSet.contains(key))
						{
							if (boostUpdateMap.containsKey(key))
							{
								_log.info("vid:" + key + "\t old boost="+ boost );
								boost = boostUpdateMap.get(key);
								boostUpdateMap.remove(key);
								
								_log.info("vid:" + key +  "\t new boost=" + boost);
							}
							sb.append(key + "=" + boost + "\r\n");
							docCount++;
							
							if (docCount % BUFFER_SIZE == 0) //flush 
							{
								opWriter.write(sb.toString());
								sb = new StringBuffer();
							}
						}
					}
				}
				finally{
					r.close();
					directory.deleteFile(name);
				}
				
			}
			if (boostUpdateMap .size() > 0)
			{
				Iterator<String> it = boostUpdateMap.keySet().iterator();
				while (it.hasNext())
				{
					String key = it.next();
					float boost = boostUpdateMap.get(key);
					sb.append(key + "=" + boost+"\r\n");
					docCount++;
				}
			}
			
			if (sb.length() > 0){
				
				opWriter.write(sb.toString());
				sb = null;
				opWriter.close();
				opWriter = null;
			}
			
			//close the writer
			if (directory.fileExists(BoostNames.boostFileName))
				directory.deleteFile(BoostNames.boostFileName);
			//rename
			_log.info("delete file "+BoostNames.boostFileName);
			directory.renameFile(newFileName,BoostNames.boostFileName);
			_log.info("renameFile file "+newFileName+"\t" + BoostNames.boostFileName);
//			newFile.renameTo(new File(this.boostPath,BoostNames.boostFileName));  //rename the boost file 
			
			updateVersion(1,docCount);
			
			rows =0 ;
			version = 1;
			
	}
	
	private void readDeleteFileToMemory() throws NumberFormatException, IOException
	{
		File boost_delete = new File(this.boostPath,BoostNames.boostFileDelete);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(boost_delete)));
		String line = null;
		try{
			
			while ( (line = r.readLine()) != null)
			{
				boostDeleteSet.add(line);
			}
		}
		finally
		{
			r.close();
		}
	}
	
	private void readUpdateFileToMemory() throws NumberFormatException, IOException
	{
		File boost_update = new File(this.boostPath,BoostNames.boostFileUpdate);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(boost_update)));
		String line = null;
		try{
			
			while ( (line = r.readLine()) != null)
			{
				String[] arr = line.split("=");
				if(arr.length != 2)
					continue;
				int vid = DataFormat.parseInt(arr[0]);
				float boost = Float.valueOf(arr[1]);
				
				boostUpdateMap.put(String.valueOf(vid),boost);
			}
		}
		finally
		{
			r.close();
		}
	}
	
	public void updateBoost(Boost boost) throws IOException
	{
		File boost_update = new File(this.boostPath,BoostNames.boostFileUpdate);
		FileWriter updateWriter = null;
		try {
			updateWriter = new FileWriter(boost_update,true);
			updateWriter.write(boost.getKey() + "=" + boost.getBoost() +"\r\n");
		}
		finally
		{
			try {
				updateWriter.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void updateBoost(List<Boost> boosts) throws IOException
	{
		File boost_update = new File(this.boostPath,BoostNames.boostFileUpdate);
		_log.info("update boost = " + BoostNames.boostFileUpdate);
		FileWriter updateWriter = null;
		try {
			updateWriter = new FileWriter(boost_update,true);
			StringBuffer buffer = new StringBuffer();
			int docCount =0 ;
			for(Boost boost:boosts){
				if (boost.getBoost() > 0)
				{
					buffer.append(boost.getKey() + "=" + boost.getBoost() +"\r\n");
					docCount++;
					
					if (docCount % BUFFER_SIZE == 0)
					{
						updateWriter.write(buffer.toString());
						buffer = new StringBuffer();
					}
				}
			}
			if (buffer.length() > 0){
				updateWriter.write(buffer.toString());
				buffer = null;
			}
			
		}
		finally
		{
			try {
				updateWriter.close();
			} catch (IOException e) {
			}
		}
	}
	
	public int getRow()
	{
		return rows;
	}
	
	public static void main(String[] args) throws IOException
	{
		BoostWriter bw = new BoostWriter("e:/testindex",true);
		bw.write(new Boost("1",2.1f));
		bw.write(new Boost("2",3.1f));
		bw.write(new Boost("3",4.1f));
		bw.optimize();
		bw.closeWriter();
		
		
	}
}
