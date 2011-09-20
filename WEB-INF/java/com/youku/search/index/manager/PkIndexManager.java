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

import com.youku.search.index.db.PkManager;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;

/**
 * @author william
 *
 */
public class PkIndexManager  extends BaseIndexManager{

	private int start;	//索引起始ID
	private int end;		//索引结束ID
	
	private static final int once_create_number = 500; //一次创建1000条
	
	public PkIndexManager()
	{
		indexPath = ServerManager.getPkIndexPath();
	}
	public PkIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	public PkIndexManager(int start,int end,String indexPath)
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
			max = PkManager.getInstance().getMaxId();
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
		try {
			 conn = Database.getYoqooConnection();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		PkManager pk = PkManager.getInstance();
		List<Document> list = pk.getPks(start,end,conn);
		try
		{
			if (list != null)
			{
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					doc = pk.getLastVideo(doc,conn);
					indexWriter.addDocument(doc);
				}
			}
		}catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try{
				if (conn != null)conn.close();
			}catch(Exception e)
			{}
		}
		return list != null?list.size():0;
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
		IndexReader indexReader = null;
		Connection conn = null;
		//获取当前最大id
		try
		{
			indexReader = IndexReader.open(indexPath);
			Document maxDoc =  indexReader.document(indexReader.numDocs()-1);
			startId = DataFormat.parseInt(maxDoc.get("pkid"))+1;
			conn = Database.getYoqooConnection();
			_log.info("pk start=" + startId + ",end = " + end);
			List<Document> list = PkManager.getInstance().getPks(startId,end,conn);
			_log.info("pk 新增索引：" + list.size());
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				rows = list.size();
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					doc = PkManager.getInstance().getLastVideo(doc,conn);
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
	
	
	public int deletePk(int pk_id)
	{
		Term term = new Term("pkid", String.valueOf(pk_id));
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

	public void deletePks(List<Integer> pk_ids)
	{
		Term[] terms = new Term[pk_ids.size()];
		for (int i =0;i<pk_ids.size();i++)
		{
			terms[i] = new Term("pkid", String.valueOf(pk_ids.get(i)));
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
