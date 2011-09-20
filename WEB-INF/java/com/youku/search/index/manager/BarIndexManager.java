/**
 * 
 */
package com.youku.search.index.manager;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.torque.TorqueException;

import com.youku.search.index.db.BarManager;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;
import com.youku.search.util.mail.MailSender;

/**
 * @author william
 *
 */
public class BarIndexManager extends BaseIndexManager{
	private int start;	//索引起始ID
	private int end;		//索引结束ID
	
	private static final int once_create_number = 1000; //一次创建1000条
	
	public BarIndexManager()
	{
		indexPath = ServerManager.getBarIndexPath();
	}
	public BarIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	public BarIndexManager(int start,int end,String indexPath)
	{
		this.start = start;
		this.end = end;
		this.indexPath = indexPath;
	}
	
	
	public int createIndex()
	{
		return createIndex(start,end);
	}
	/**
	 * 创建索引
	 * @param start 索引起始位置
	 * @param end  索引结束位置
	 */
	public int createIndex(int start,int end)
	{
		int total = 0;
		_log.info("create index start=" + start + " end=" +end);
		
		int max = 0;
		
		try {
			max = BarManager.getInstance().getMaxId();
			initWriter(true);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		
		end = end>max?max:end;
		
		int starttimes = (start+once_create_number-1)/once_create_number;
		int totaltimes = (end+once_create_number-1)/once_create_number;
		for (int i=starttimes;i<totaltimes;i++)
		{
			int count = 0;
			if (i==starttimes){
				count = createSegmentIndex(start,i*once_create_number);
			}
			else if (i == totaltimes-1){
				count = createSegmentIndex((i-1)*once_create_number,end);
			}
			else{
				count = createSegmentIndex((i-1)*once_create_number,i*once_create_number);
			}
			if (count == -1)
			{
				//数据库异常
				total = -1;
				break;
			}
			else
			{
				total += count;
			}
		}
		
		optimizeWriter();
		closeWriter();
		_log.info("end create index");
		
		return total;
	}
	
	
	private int createSegmentIndex(int start,int end)
	{
		_log.info("start=" + start + ",end="+end);
		Connection conn = null;
		Connection yoqooconn = null;
		try {
			 conn = Database.getBarConnection();
			 yoqooconn = Database.getYoqooConnection();
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建看吧索引过程中连接数据库失败，start+"+start + " end="+end,e.getMessage());
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			return -1;
		}
		
		BarManager bar = BarManager.getInstance();
		List<Document> list = bar.getBars(start,end,conn);
		try {
			if (list != null)
			{
				Document doc=null;
				for (int i=0;i<list.size();i++)
				{
					doc = list.get(i);
					bar.getBarStatus(doc, conn);
					bar.barCatalogToDocument(doc, conn);
//					bar.getLastSubject(doc, conn);
//					bar.getLastVideo(doc, conn, yoqooconn);
					indexWriter.addDocument(doc);
				}
			}
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try
			{
				if (conn!=null)conn.close();
				if (yoqooconn!=null)yoqooconn.close();
			}catch(Exception e)
			{}
		}
		return list!= null ? list.size() :0;
	}
	
	
	public int addIndex(int startid,int endid)
	{
		_log.info("开始添加看吧索引：start="+startid +" end="+endid);
		int rows = 0;
		
		Connection conn = null;
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try
		{
			List<Document> list= null;
			
			conn = Database.getBarConnection();
			indexReader = IndexReader.open(indexPath);
			indexSearcher =  new IndexSearcher(indexReader);
			BarManager bar = BarManager.getInstance();
			list = bar.getBars(startid,endid,conn);
			_log.debug("list.size="+list.size());
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					Hits hits = indexSearcher.search(new TermQuery(new Term("pk_bar",doc.get("pk_bar"))));
					if (hits != null && hits.length() > 0)
						continue;
					bar.getBarStatus(doc, conn);
					bar.barCatalogToDocument(doc, conn);
//					bar.getLastSubject(doc, conn);
//					bar.getLastVideo(doc, conn);
					_log.debug("add bar id=" + doc.get("pk_bar"));
					indexWriter.addDocument(doc);
					rows++;
				}
				optimizeWriter();
			}
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			closeWriter();
			try
			{
				if (indexReader != null)indexReader.close();
				if (indexSearcher != null)indexSearcher.close();
				if (conn != null) conn.close();
			}catch(Exception e){}
		}
		_log.info("看吧新增索引：" + rows);
		return rows;
	}
	
	/**
	 * 添加索引
	 * @return 返回添加了多少条数据
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public int addIndex()
	{
		int rows = 0;
		int startId =0;
		//获取当前最大id
		
		IndexReader indexReader= null;
		Connection conn = null;
		try
		{
			indexReader = IndexReader.open(indexPath);
			Document maxDoc =  indexReader.document(indexReader.numDocs()-1);
			startId = DataFormat.parseInt(maxDoc.get("pk_bar"))+1;
			conn = Database.getBarConnection();
		
			
			_log.info("bar start=" + startId + ",end = " + end);
			BarManager bar = BarManager.getInstance();
			List<Document> list = bar.getBars(startId,end,conn);
			_log.info("bar 新增索引：" + list.size());
			if (list != null && list.size() > 0)
			{
				initWriter();
				//构建硬盘存储writer
				rows = list.size();
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					bar.getBarStatus(doc, conn);
					bar.barCatalogToDocument(doc, conn);
//					bar.getLastSubject(doc, conn);
					indexWriter.addDocument(doc);
				}
				optimizeWriter();
			}
			
		}catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			closeWriter();
			try
			{
				if (indexReader != null)indexReader.close();
				if (conn != null) conn.close();
			}catch(Exception e){}
		}
		return rows;
	}
	
	public int deleteBar(int pk_bar)
	{
		Term term = new Term("pk_bar", String.valueOf(pk_bar));
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
	public void deleteBars(List<Integer> pk_bars)
	{
		Term[] terms = new Term[pk_bars.size()];
		for (int i =0;i<pk_bars.size();i++)
		{
			terms[i] = new Term("pk_bar", String.valueOf(pk_bars.get(i)));
		}
	    try {
	    	initWriter();
	    	indexWriter.deleteDocuments(terms);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally{
			closeWriter();
		}
	}
	/* (non-Javadoc)
	 * @see com.youku.search.index.manager.BaseIndexManager#clearLastUpdateTime()
	 */
	@Override
	public void clearLastUpdateTime() {
		// TODO Auto-generated method stub
		
	}
	
}
