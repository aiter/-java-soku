/**
 * 
 */
package com.youku.search.index.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import com.youku.search.index.db.StatManager;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;

/**
 * @author william
 *
 */
public class StatIndexManager extends BaseIndexManager{

	private static final int once_create_number = 5000; //一次创建1000条
	
	private final String tableName = "query";
	private String table;
	
	public StatIndexManager()
	{
		Date date = DataFormat.getNextDate(new Date(),-1); //默认为昨天
		String strDate = DataFormat.formatDate(date,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		indexPath = ServerManager.getStatIndexPath();
	}
	public StatIndexManager(Date d)
	{
		String strDate = DataFormat.formatDate(d,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		indexPath = ServerManager.getStatIndexPath();
	}
	public StatIndexManager(String path)
	{
		Date date = DataFormat.getNextDate(new Date(),-1); //默认为昨天
		String strDate = DataFormat.formatDate(date,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		indexPath = path;
	}
	public StatIndexManager(Date d,String path)
	{
		String strDate = DataFormat.formatDate(d,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		indexPath = path;
	}
	
	
	public int createIndex() 
	{
		int total =0;
		_log.info("create stat index table:" + table);
		
		int max = 0;
		try {
			max = StatManager.getInstance().getMaxId(table);
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
			 conn = Database.getStatConnection();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		StatManager stat = StatManager.getInstance();
		List<Document> list = stat.getWords(start,end,table,conn);
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
	
	
	/* (non-Javadoc)
	 * @see com.youku.search.index.manager.BaseIndexManager#addIndex()
	 */
	@Override
	public int addIndex() {
		//检查纠错词表
		return 0;
	}
	/* (non-Javadoc)
	 * @see com.youku.search.index.manager.BaseIndexManager#clearLastUpdateTime()
	 */
	@Override
	public void clearLastUpdateTime() {
		// TODO Auto-generated method stub
		
	}
	

}
