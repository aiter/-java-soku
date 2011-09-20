/**
 * 
 */
package com.youku.search.index.manager;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.torque.TorqueException;

import com.youku.search.index.db.BarPostManager;
import com.youku.search.index.db.UserManager;
import com.youku.search.index.db.VideoManager;
import com.youku.search.index.entity.Video;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;
import com.youku.search.util.mail.MailSender;

/**
 * @author william
 *
 */
public class BarpostIndexManager extends BaseIndexManager{
	private int start;	//索引起始ID
	private int end;		//索引结束ID
	
	private static final int once_create_number = 1000; //一次创建1000条
	
	public BarpostIndexManager()
	{
		indexPath = ServerManager.getBarpostIndexPath();
	}
	public BarpostIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	public BarpostIndexManager(int start,int end,String indexPath)
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
			max = BarPostManager.getInstance().getMaxId();
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
			 conn = Database.getBarConnection();
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建看吧帖子索引过程中连接数据库失败，start+"+start + " end="+end,e.getMessage());
			} catch (Exception e2) {
			}
			return -1;
		}
		
		BarPostManager barpost = BarPostManager.getInstance();
		List<Document> list = barpost.getSubjects(start,end,conn);
		try {
			if (list != null)
			{
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
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
			}catch(Exception e)
			{}
		}
		return list!=null?list.size():0;
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
			startId = DataFormat.parseInt(maxDoc.get("pkpost"))+1;
			conn = Database.getBarConnection();
		
			
			_log.info("barpost start=" + startId + ",end = " + end);
			List<Document> list = BarPostManager.getInstance().getSubjects(startId,end,conn);
			_log.info("barpost 新增索引：" + list.size());
			if (list != null && list.size() > 0)
			{
				initWriter();
				//构建硬盘存储writer
				rows = list.size();
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					if (doc != null)
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
	public int deletePost(int post_id)
	{
		Term term = new Term("pkpost", String.valueOf(post_id));
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
	public int deleteSubject(int fk_subject)
	{
		Term term = new Term("fk_subject", String.valueOf(fk_subject));
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
	public void deleteAll(List<Integer> pk_bars,List<Integer> post_ids,List<Integer> fk_subjects)
	{
		 try {
		    initWriter();
		 }catch(Exception e)
		 {
			 _log.error(e.getMessage(),e);
			 return;
		 }
		Term[] terms_subject = new Term[fk_subjects.size()];
		for (int i =0;i<fk_subjects.size();i++)
		{
			terms_subject[i] = new Term("fk_subject", String.valueOf(fk_subjects.get(i)));
			_log.info("del subject:"+fk_subjects.get(i));
		}
		Term[] terms_post = new Term[post_ids.size()];
		for (int i =0;i<post_ids.size();i++)
		{
			terms_post[i] = new Term("pkpost", String.valueOf(post_ids.get(i)));
			_log.info("del subject:"+post_ids.get(i));
		}
		Term[] terms_bar = new Term[pk_bars.size()];
		for (int i =0;i<pk_bars.size();i++)
		{
			terms_bar[i] = new Term("pk_bar", String.valueOf(pk_bars.get(i)));
			_log.info("del subject:"+pk_bars.get(i));
		}
	    try {
	    	indexWriter.deleteDocuments(terms_subject);
	    	indexWriter.deleteDocuments(terms_post);
	    	indexWriter.deleteDocuments(terms_bar);
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
