package com.youku.common.correct;

import java.util.HashMap;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.Record;

public class Corrector {
		private HashMap<String,String> words = new HashMap<String,String>();
		
		public String correct(String key){
			return words.get(key);
		}
		
		@SuppressWarnings("unchecked")
		public void initWords(){
			String sql = "select * from correction where status = 1 order by update_time,id desc ";
			
			try {
				List<Record> records = BasePeer.executeQuery(sql,"soku");
				if (records!= null){
					for (Record record:records){
						String keywords = record.getValue("keyword").asString();
						String correct_words = record.getValue("correct_keyword").asString();
						if (keywords != null){
							String[] keywordArray = keywords.split("\\|");
							for (String keyword:keywordArray){
								words.put(keyword,correct_words);
							}
						}
					}
				}
			} catch (TorqueException e) {
				e.printStackTrace();
			} catch (DataSetException e) {
				e.printStackTrace();
			}
		}
		
		protected void finalize() throws Throwable {
			words.clear();
			words = null;
		}
}
