/**
 * 
 */
package com.youku.top.index.manager;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.torque.TorqueException;

import com.youku.top.DataBase;
import com.youku.top.index.analyzer.AnalyzerManager;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;
import com.youku.top.index.db.LogManager;
import com.youku.top.index.server.ServerManager;

public class LogIndexManager extends BaseIndexManager{

	private static final int once_create_number = 5000; //一次创建1000条
	
	private final String tableName = "query";
	private String table;
	private String beforetable;
	int max = 0;
	
	public void init(){
		try {
			max = LogManager.getInstance().getMaxId(table);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}
	
	public LogIndexManager(boolean quick)
	{
		Date date = DataFormat.getNextDate(new Date(),-1); //默认为昨天
		Date beforedate = DataFormat.getNextDate(new Date(),-2); //默认为前天
		String strDate = DataFormat.formatDate(date,DataFormat.FMT_DATE_YYYY_MM_DD);
		String beforestrDate = DataFormat.formatDate(beforedate,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		beforetable = tableName + "_" +beforestrDate;
		init();
		if(1>max) return;
		if(quick)
			indexPath = ServerManager.getLogQuickIndexPath()+strDate;
		else indexPath = ServerManager.getLogIndexPath()+strDate;
		System.out.println(indexPath);
	}
	public LogIndexManager(Date d,Date before,boolean quick)
	{
		String strDate = DataFormat.formatDate(d,DataFormat.FMT_DATE_YYYY_MM_DD);
		String beforestrDate = DataFormat.formatDate(before,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		beforetable = tableName + "_" +beforestrDate;
		init();
		if(1>max) return;
		if(quick)
			indexPath = ServerManager.getLogQuickIndexPath()+strDate;
		else indexPath = ServerManager.getLogIndexPath()+strDate;
	}
	public LogIndexManager(String path,boolean quick)
	{
		Date date = DataFormat.getNextDate(new Date(),-1); //默认为昨天
		Date beforedate = DataFormat.getNextDate(new Date(),-2); //默认为前天
		String strDate = DataFormat.formatDate(date,DataFormat.FMT_DATE_YYYY_MM_DD);
		String beforestrDate = DataFormat.formatDate(beforedate,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		beforetable = tableName + "_" +beforestrDate;
		init();
		if(1>max) return;
		indexPath = path + strDate;
	}
	public LogIndexManager(Date d,Date before,String path,boolean quick)
	{
		String strDate = DataFormat.formatDate(d,DataFormat.FMT_DATE_YYYY_MM_DD);
		String beforestrDate = DataFormat.formatDate(before,DataFormat.FMT_DATE_YYYY_MM_DD);
		table = tableName + "_" +strDate;
		beforetable = tableName + "_" +beforestrDate;
		init();
		if(1>max) return;
		indexPath = path+strDate;
	}
	
	
	public int createIndex(boolean quick) 
	{
		init();
		if(1>max) return 0;
		int total =0;
		_log.info("create stat index table:" + table);
		try {
			if(quick)
				initWriter(AnalyzerManager.getMyAnalyzer(false),true);
			else initWriter(true);
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
				total += createSegmentIndex(0,i*once_create_number,quick);
			else if (i == totaltimes-1)
				total += createSegmentIndex((i-1)*once_create_number,max,quick);
			else
				total += createSegmentIndex((i-1)*once_create_number,i*once_create_number,quick);
		}
		
		optimizeWriter();
		closeWriter();
		_log.info("end create index");
		return total;
	}
	
	public int createSegmentIndex(int start,int end,boolean quick) 
	{
		_log.info("start=" + start + ",end="+end);
		Connection conn = null;
		try {
			 conn = DataBase.getSearchStatConn();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		LogManager stat = LogManager.getInstance();
		List<Document> list = null;
		if(quick)
			list = stat.getWordsAndQuick(start,end,table,beforetable,conn);
		else 
			list = stat.getWords(start,end,table,beforetable,conn);
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
			JdbcUtil.close(conn);
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
		// TODO Auto-generated method stub
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
