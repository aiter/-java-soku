/**
 * 
 */
package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.util.Database;

/**
 * @author 1verge
 *
 */
public class SynonymManager {
	
	private HashMap<String,String[]> wordMap = new HashMap<String,String[]>();
	
	public static SynonymManager instance = null;
	
	public static SynonymManager getInstance()
	{
		if ( null == instance ){
			instance = new SynonymManager();
		}
		return instance;
	}
	
	private SynonymManager(){
		
	}
	
	public synchronized void init()
	{
		System.out.println("synonym init start!");
		wordMap.clear();
		Connection conn = null;
		try {
			conn = Database.getConsoleConnection();
			List<Record> list = BasePeer.executeQuery("select keywords from synonym where state=1",false,conn);
			if (list != null && !list.isEmpty())
			{
				for (int i =0;i<list.size();i++)
				{
					Record record=  list.get(i);
					String keyword = record.getValue("keywords").asString();
					String[] words = keyword.split(";");
					for(String word:words){
						if (word != null && !word.isEmpty()){
							wordMap.put(word,words);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		finally
		{
			if (conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
				}
				conn = null;
			}
		}
		
		System.out.println("synonym init over!");
	}
	
	public String[] getWords(String word)
	{
		return wordMap.get(word);
	}
	
	public HashMap<String, String[]> getWordMap() {
		return wordMap;
	}

}
