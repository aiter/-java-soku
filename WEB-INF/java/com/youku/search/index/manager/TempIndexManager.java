/**
 * 
 */
package com.youku.search.index.manager;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;

import com.youku.search.index.db.VideoManager;
import com.youku.search.index.server.ServerManager;

/**
 * @author 1verge
 *
 */
public class TempIndexManager extends BaseIndexManager{
	
	private static final int once_create_number = 500; //一次创建50条
	
	public TempIndexManager()
	{
		indexPath = ServerManager.getVideoIndexPath();
	}
	public TempIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	
	
	public int createIndex()
	{
		int total = 0;
		int start =64709660;
		int end = 69471419;
		_log.info("create index start=" + start + " end=" +end);
		
		int max = 0;
		
		try {
			max = VideoManager.getInstance().getMaxId();
			initWriter(true);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		
		end = end>max?max:end;
		
		int starttimes = (start+once_create_number-1)/once_create_number;
		int totaltimes = (end+once_create_number-1)/once_create_number;
		_log.info("starttimes="+starttimes + "  totaltimes="+totaltimes);
		for (int i=starttimes;i<=totaltimes;i++)
		{
			int count = 0;
			if (i==starttimes){
				count = createSegmentIndex(start,i*once_create_number>end?end:i*once_create_number);
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
		_log.info("indexWriter.docCount()="+indexWriter.docCount());
		optimizeWriter();
		_log.info("indexWriter.docCount()="+indexWriter.docCount());
		closeWriter();
		_log.info("end create index");
		return total;
	}
	/**
	 * 创建索引
	 * @param start 索引起始位置
	 * @param end  索引结束位置
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public int createSegmentIndex(int start,int end) 
	{
		 long time1 =System.currentTimeMillis();
		_log.info("start=" + start + ",end="+end);
		
		VideoManager video = VideoManager.getInstance();
		List<Document> list;
		list = video.getInvalidVideos(start,end);
		try
		{
			if (list != null)
			{
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					if (doc != null ){
						try{
							indexWriter.addDocument(doc);
						}
						catch(Exception e)
						{
							_log.error("addDocument error",e);
						}
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			_log.error(e.getMessage(),e);
		}
		
		_log.info("createSegmentIndex cost:" + (System.currentTimeMillis() - time1));
		return list!=null?list.size():0;
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
