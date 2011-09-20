/**
 * 
 */
package com.youku.search.index.manager;

import java.sql.Connection;
import java.util.List;

import org.apache.lucene.document.Document;

import com.youku.search.index.db.RingManager;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Database;

/**
 * @author william
 *
 */
public class RingIndexManager extends BaseIndexManager{
	private int start;		//索引起始ID
	private int end;		//索引结束ID
	private static final int once_create_number = 1000; //一次创建1000条
	
	
	public RingIndexManager()
	{
		indexPath = ServerManager.getRingIndexPath();
	}
	public RingIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	public RingIndexManager(int start,int end,String indexPath)
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
			max = RingManager.getInstance().getMaxId();
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
			if (i==starttimes)
				total += createSegmentIndex(start,i*once_create_number);
			else if (i == totaltimes-1)
				total += createSegmentIndex((i-1)*once_create_number,end);
			else
				total += createSegmentIndex((i-1)*once_create_number,i*once_create_number);
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
			 conn = Database.getRingConnection();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		
		RingManager folder = RingManager.getInstance();
		List<Document> list = folder.getRings(start,end,conn);
		try
		{
			if (list != null)
			{
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					System.out.println(doc.get("cname"));
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
		return list!= null ?list.size():0;
	}
	
	/**
	 * 暂时没有更新操作
	 */
	public int addIndex() {
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
