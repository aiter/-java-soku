/**
 * 
 */
package com.youku.search.index.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.torque.TorqueException;

import com.youku.search.index.boost.Boost;
import com.youku.search.index.db.AssembleDoc;
import com.youku.search.index.db.FolderManager;
import com.youku.search.index.manager.AffectManager.Affect;
import com.youku.search.index.manager.AffectManager.Type;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public class FolderIndexManager extends BaseIndexManager{
	private int start;		//索引起始ID
	private int end;		//索引结束ID
	private static final int once_create_number = 300; //一次创建300条
	
	
	public FolderIndexManager()
	{
		indexPath = ServerManager.getFolderIndexPath();
	}
	public FolderIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	public FolderIndexManager(int start,int end,String indexPath)
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
			max = FolderManager.getInstance().getMaxFolderId();
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
		
		//删除最近一天删除过的专辑
		_log.info("删除这一天删除的专辑");
		
		AffectManager.getInstance().cleanOldFile(Type.FOLDER,48);
		
		List<Integer> affects = AffectManager.getInstance().getAllDeleteFromFile(AffectManager.Type.FOLDER);
		if (affects !=null)
		{
			_log.info("24小时内共删除专辑："+affects.size());
			for (Integer affect:affects)
			{
				try {
					indexWriter.deleteDocuments(new Term("pkfolder",affect+""));
				} catch (Exception e) {
					_log.info("delete folder error:" + affect ,e);
				} 
			}
		}
		
		optimizeWriter();
		closeWriter();
		
		_log.info("end create index");
		return total;
	}
	
	private int createSegmentIndex(int start,int end)
	{
		long time1 = System.currentTimeMillis();
		List<AssembleDoc> list = null;
		try {
			list = FolderManager.getInstance().getFolders(start,end);
		} catch (TorqueException e1) {
			return -1;
		}
		
		try
		{
			if (list != null)
			{
				for (int i=0;i<list.size();i++)
				{
					AssembleDoc doc = list.get(i);
					if (doc != null && doc.getDoc()!= null){
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.getDoc().get("pkfolder"),doc.getBoost()));
					}
				}
				_log.info("start=" + start + ",end="+end + " size="+list.size() + "  cost:" + (System.currentTimeMillis() - time1));
			}
		}catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		
		
		return list!= null?list.size() : 0;
	}
	
	private static Date lastUpdateTime = null;
	
	/**
	 * 添加索引
	 * @return 返回添加了多少条数据
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public int addIndex()
	{
		if (lastUpdateTime == null)
		{
			return addIndexFromMaxId();
		}
		
		int rows = 0;
		Date endTime = new Date();
		AffectManager.getInstance().initWriter(AffectManager.Type.FOLDER,true);
		try
		{
			List<AssembleDoc> list= null;
			list = FolderManager.getInstance().getFolders(start,end,lastUpdateTime,endTime);
			_log.info("folder新增索引：" + list.size());
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				rows = list.size();
				for (int i=0;i<list.size();i++)
				{
					AssembleDoc doc = list.get(i);
					if (doc != null & doc.getDoc()!=null){
						_log.info("add folder id=" + doc.getDoc().get("pkfolder"));
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.getDoc().get("pkfolder"),doc.getBoost()));
					}
				}
				
				_log.info("本机删除索引");
				//删除索引
				List<Affect> deleteAffects = AffectManager.getInstance().getAffects(AffectManager.Type.FOLDER,AffectManager.DELETE);
				AffectManager.getInstance().saveToFile(AffectManager.Type.FOLDER,deleteAffects);
				
				deleteFolders(deleteAffects);
				_log.info("本机删除索引完成");
				
				optimizeWriter();
			}
			lastUpdateTime = endTime;
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
				AffectManager.getInstance().closeWriter(AffectManager.Type.FOLDER);
			} catch (IOException e) {
				_log.error(e.getMessage(),e);
			}
			closeWriter();
		}
		return rows;
	}
	
	public int addIndexFromMaxId()
	{
		int rows = 0;
		Date endTime = new Date();
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try
		{
			indexReader = IndexReader.open(indexPath);
			Document maxDoc =  indexReader.document(indexReader.numDocs()-1);
			int startId = DataFormat.parseInt(maxDoc.get("pkfolder"))+1;
			
			indexSearcher =  new IndexSearcher(indexReader);
			
			List<AssembleDoc> list= null;
			
			_log.info("folder startId=" + startId + ",endTime = " + endTime);
			list = FolderManager.getInstance().getFolders(startId,endTime);
			
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				for (int i=0;i<list.size();i++)
				{
					AssembleDoc doc = list.get(i);
					if (doc != null && doc.getDoc()!= null){
						Hits hits = indexSearcher.search(new TermQuery(new Term("pkfolder",doc.getDoc().get("pkfolder"))));
						if (hits != null && hits.length() > 0)
							continue;
						_log.info("add folder id=" + doc.getDoc().get("pkfolder"));
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.getDoc().get("pkfolder"),doc.getBoost()));
						rows++;
					}
				}
				optimizeWriter();
				_log.info("folder新增索引：" + rows);
			}
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
			
		finally
		{
			lastUpdateTime = endTime;
			closeWriter();
			try
			{
				if (indexReader != null)indexReader.close();
				if (indexSearcher != null)indexSearcher.close();
			}catch(Exception e){}
		}
		return rows;
	}
	
	public int addIndex(int startid,int endid)
	{
		int rows = 0;
		Date endTime = new Date();
		
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try
		{
			List<AssembleDoc> list= new ArrayList<AssembleDoc>();
			//获取当前最大id
			indexReader = IndexReader.open(indexPath);
			indexSearcher =  new IndexSearcher(indexReader);
			for (int i=startid;i<endid;i=i+once_create_number)
				list.addAll(FolderManager.getInstance().getFolders(i,i+once_create_number));
			
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				for (int i=0;i<list.size();i++)
				{
					AssembleDoc doc = list.get(i);
					if (doc == null || doc.getDoc() == null)
						continue;
					
					Hits hits = indexSearcher.search(new TermQuery(new Term("pkfolder",doc.getDoc().get("pkfolder"))));
					if (hits != null && hits.length() > 0)
						continue;
					_log.info("add folder id=" + doc.getDoc().get("pkfolder"));
					indexWriter.addDocument(doc.getDoc());
					boostWriter.write(new Boost(doc.getDoc().get("pkfolder"),doc.getBoost()));
					rows++;
				}
				_log.info("folder新增索引：" + rows);
				optimizeWriter();
			}
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			lastUpdateTime = endTime;
			closeWriter();
			try
			{
				if (indexReader != null)indexReader.close();
				if (indexSearcher != null)indexSearcher.close();
			}catch(Exception e){}
		}
		return rows;
	}
	
	public int updateIndex(int[] fids)
	{
		int rows = 0;
		Date endTime = new Date();
		
		try
		{
			List<AssembleDoc> list= null;
			//获取当前最大id
			
			list = FolderManager.getInstance().getFolders(fids);
			
			if (list != null && list.size() > 0)
			{
//					构建硬盘存储writer
				initWriter();
				for (int i=0;i<list.size();i++)
				{
					AssembleDoc doc = list.get(i);
					if (doc != null && doc.getDoc() != null){
						indexWriter.deleteDocuments(new Term("pkfolder",doc.getDoc().get("pkfolder")));
						
						_log.info("add folder id=" + doc.getDoc().get("pkfolder"));
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.getDoc().get("pkfolder"),doc.getBoost()));
						rows++;
					}
				}
				_log.info("folder新增索引：" + rows);
				optimizeWriter();
			}
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			lastUpdateTime = endTime;
			closeWriter();
		}
		return rows;
	}
	
	public int deleteFolder(int folder_id) throws IOException
	{
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		
		Term term = new Term("pkfolder", String.valueOf(folder_id));
		try {
	    	initWriter();
	    	
	    	//删除boost
	    	indexReader = IndexReader.open(indexPath);
			indexSearcher =  new IndexSearcher(indexReader);
			Hits hits = indexSearcher.search(new TermQuery(term));
			for (int i =0;i<hits.length();i++)
			{
				boostWriter.deleteBoostToBuffer(hits.id(i));
			}
			boostWriter.flushDeleteBoost();
			
			//标记物理删除
			indexWriter.deleteDocuments(term);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			return 0;
		}
		finally{
			closeWriter();
			indexReader.close();
			indexSearcher.close();
		}
		return 1;
	}
	
	public void deleteFolders(List<Affect> folder_ids) throws IOException
	{
		Term[] terms = new Term[folder_ids.size()];
		
		boolean open = false;
		if (indexWriter == null)
		{
			open = true;
			initWriter();
		}
		
		for (int i =0;i<folder_ids.size();i++)
		{
			_log.info("delete folder id=" + folder_ids.get(i).getPk_id());
			terms[i] = new Term("pkfolder", String.valueOf(folder_ids.get(i).getPk_id()));
			
		}
		
	    try {
	    	indexWriter.deleteDocuments(terms);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally{
			if (open)closeWriter();
		}
	}
	
	public void clearLastUpdateTime()
	{
		lastUpdateTime = null;
	}
}
