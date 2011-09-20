/**
 * 
 */
package com.youku.soku.index.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.index.manager.db.WordManager;
import com.youku.soku.util.DataBase;

/**
 * @author william
 *
 */
public class WordIndexManager extends BaseIndexManager{

	private static final int once_create_number = 5000; //一次创建5000条
	
	private final String tableName = "query_";
	private String table;
	
	public WordIndexManager()
	{
		this(DataFormat.getNextDate(new Date(),-1),Config.getWordIndexPath());
	}
	public WordIndexManager(Date d)
	{
		this(d,Config.getWordIndexPath());
	}
	public WordIndexManager(String path)
	{
		this(DataFormat.getNextDate(new Date(),-1),path);//默认为昨天
	}
	public WordIndexManager(Date d,String path)
	{
		String strDate = DataFormat.formatDate(d,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + strDate;
		indexPath = path;
	}
	
	
	public int createIndex() 
	{
		int total =0;
		_log.info("create word index table:" + table);
		
		int max = 0;
		try {
			max = WordManager.getInstance().getMaxId(table);
//			initWriter(AnalyzerManager.getStandardAnalyzer(),true);
			initWriter(true);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		
		int starttimes = (once_create_number-1)/once_create_number;
		int totaltimes = (max+once_create_number-1)/once_create_number;
		_log.debug("starttimes="+starttimes + "  totaltimes="+totaltimes);
		for (int i=starttimes;i<totaltimes;i++)
		{
			if (i==starttimes)
				total += createSegmentIndex(0,i*once_create_number);
			else if (i == totaltimes-1)
				total += createSegmentIndex((i-1)*once_create_number,max);
			else
				total += createSegmentIndex((i-1)*once_create_number,i*once_create_number);
		}
		
		optimizeWriter();
		closeWriter();
		_log.info("end create index");
		return total;
	}
	
	public int createSegmentIndex(int start,int end) 
	{
		_log.info("start=" + start + ",end="+end);
		Connection conn = null;
		try {
			 conn = DataBase.getSearchStatConn();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		WordManager word = WordManager.getInstance();
		List<Document> list = word.getWords(start,end,table,conn);
		try
		{
			if (list != null)
			{
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					indexWriter.addDocument(doc);
				}
			}
		}catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
				if (conn != null)conn.close();
			} catch (SQLException e) {
			}
		}
		return list!= null ?list.size():0;
	}

	public int delete(int id)
	{
		Term term = new Term("id", String.valueOf(id));
		try {
	    	initWriter();
			//标记物理删除
			indexWriter.deleteDocuments(term);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		finally{
			closeWriter();
		}
		return 1;
	}

}
