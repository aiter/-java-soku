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
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

import com.youku.search.index.boost.Boost;
import com.youku.search.index.db.AssembleDoc;
import com.youku.search.index.db.UserManager;
import com.youku.search.index.db.VideoManager;
import com.youku.search.index.manager.AffectManager.Affect;
import com.youku.search.index.manager.AffectManager.Type;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;
import com.youku.search.util.mail.MailSender;

/**
 * @author william
 *
 */
public class UserIndexManager extends BaseIndexManager{
	
	private int start;	//索引起始ID
	private int end;		//索引结束ID
	private static final int once_create_number = 1000; //一次创建1000条
	
	public UserIndexManager()
	{
		indexPath = ServerManager.getUserIndexPath();
	}
	public UserIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	public UserIndexManager(int start,int end,String indexPath)
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
			max = UserManager.getInstance().getMaxId();
			_log.info("max user id=" + max);
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
		
		//删除最近4天删除过的用户
		AffectManager.getInstance().cleanOldFile(Type.USER,4 * 24);
		
		_log.info("本机删除这4天删除的用户");
		List<Integer> users = AffectManager.getInstance().getAllDeleteFromFile(AffectManager.Type.USER);
		if (users !=null){
			_log.info("共删除："+users.size());
			for (Integer id:users)
			{
				try {
					indexWriter.deleteDocuments(new Term("pkuser",id+""));
				} catch (Exception e) {
					_log.info("delete user error:" + id ,e);
				} 
			}
		}
	    _log.info("结束本机删除这4天删除的用户");
	    
		optimizeWriter();
		closeWriter();
		
		_log.info("end create index");
		return total;
	}
	
	private int createSegmentIndex(int start,int end)
	{
		_log.info("start=" + start + ",end="+end);
		Connection userconn = null;
		try {
			userconn = Database.getYoqooUserConnection();
		} catch (Exception e) {
			try {
				MailSender.send("luwei@youku.com","创建会员索引过程中连接数据库失败，start+"+start + " end="+end,e.getMessage());
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			return -1;
		}
		
		UserManager user = UserManager.getInstance();
		
		List<Document> list = user.getUsers(start,end,userconn);
		
		try
		{
			if (list != null)
			{
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					if (doc != null)
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
		        if (userconn != null) userconn.close();
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
		return list!=null ? list.size():0;
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
		IndexSearcher searcher = null;
		Connection userconn = null;
		//获取当前最大id
		
		AffectManager.getInstance().initWriter(AffectManager.Type.USER,true);
		try
		{
			indexReader = IndexReader.open(indexPath);
			searcher = new IndexSearcher(indexReader);
			Sort sort = new Sort(new SortField("pkuser",SortField.INT,true));
			Hits hits = searcher.search(new MatchAllDocsQuery(),sort);
			if (hits!=null && hits.length()>0)
			{
				startId = DataFormat.parseInt(hits.doc(0).get("pkuser"))+1;
			}
			
			userconn = Database.getYoqooUserConnection();
			_log.info("user start=" + startId + ",end = " + end);
			List<Document> list = UserManager.getInstance().getUsers(startId,end,userconn);
			_log.info("user 新增索引：" + list.size());
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				rows = list.size();
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					if (doc != null)
						indexWriter.addDocument(doc);
				}
				
				List<Affect> ids_renew = AffectManager.getInstance().getAffects(AffectManager.Type.USER,AffectManager.RENEW);
				updateUsers(ids_renew);

				
				_log.info("本机删除索引");
				//删除索引
				List<Affect> deleteAffects = AffectManager.getInstance().getAffects(AffectManager.Type.USER,AffectManager.DELETE);
				AffectManager.getInstance().saveToFile(AffectManager.Type.USER,deleteAffects);
				
				deleteUsers(deleteAffects);
				_log.info("本机删除索引完成");
				
				
				optimizeWriter();
			}
		}catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
				AffectManager.getInstance().closeWriter(AffectManager.Type.USER);
			} catch (IOException e) {
				_log.error(e.getMessage(),e);
			}
			
			closeWriter();
			try
			{
				if (searcher != null)searcher.close();
				if (indexReader != null)indexReader.close();
				if (userconn != null) userconn.close();
			}catch(Exception e){}
		}
		
		
		return rows;
	}
	
	public int addIndex(int startid,int endid)
	{
		_log.info("开始添加用户索引：start="+startid +" end="+endid);
		int rows = 0;
		
		Connection userconn = null;
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try
		{
			List<Document> list= null;
			userconn = Database.getYoqooUserConnection();
			indexReader = IndexReader.open(indexPath);
			indexSearcher =  new IndexSearcher(indexReader);
			list = UserManager.getInstance().getUsers(startid,endid,userconn);
			_log.debug("list.size="+list.size());
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				for (int i=0;i<list.size();i++)
				{
					Document doc = list.get(i);
					if (doc != null)
					{
						Hits hits = indexSearcher.search(new TermQuery(new Term("pkuser",doc.get("pkuser"))));
						if (hits != null && hits.length() > 0)
							continue;
						_log.debug("add user id=" + doc.get("pkuser"));
						indexWriter.addDocument(doc);
						rows++;
					}
				}
				
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
				if (userconn != null) userconn.close();
			}catch(Exception e){}
		}
		_log.info("user新增索引：" + rows);
		return rows;
	}
	
	public int updateUsers(List<Affect> affects) throws CorruptIndexException, IOException
	{
		int row = 0;
		if (affects !=null && affects.size()>0){
			_log.info("更新数量：" + affects.size());
			for (Affect affect:affects){
				Document doc =  UserManager.getInstance().getUserAsDocument(affect.getPk_id());
				if (doc != null){
					_log.info("update user id=" + doc.get("pkuser"));
					//先删除原来的
					indexWriter.deleteDocuments(new Term("pkuser",doc.get("pkuser")));
					//添加新的
					indexWriter.addDocument(doc);
					row++;
				}
			}
		}
		return row;
	}
	
	public int deleteUser(int user_id)
	{
		Term term = new Term("pkuser", String.valueOf(user_id));
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
	
	public void deleteUsers(List<Affect> user_ids)
	{
		boolean open = false;
		Term[] terms = new Term[user_ids.size()];
		for (int i =0;i<user_ids.size();i++)
		{
			_log.debug("delete user id=" + user_ids.get(i).getPk_id());
			terms[i] = new Term("pkuser", String.valueOf(user_ids.get(i).getPk_id()));
		}
	    try {
			if (indexWriter == null)
			{
				open = true;
				initWriter();
			}
	    	indexWriter.deleteDocuments(terms);
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally{
			if (open) closeWriter();
		}
	}
	
	public int updateIndex(int[] ids)
	{
		if (ids ==null || ids.length ==0) return 0;
		
		_log.info("开始更新会员索引");
		int rows = 0;
		
		try
		{
			initWriter();
			for (int id:ids)
			{
				Document doc = UserManager.getInstance().getUserAsDocument(id);
	//			构建硬盘存储writer
					
				if (doc != null ){
					indexWriter.deleteDocuments(new Term("pkuser",doc.get("pkuser")));
					_log.info("add user id=" +doc.get("pkuser"));
					indexWriter.addDocument(doc);
					rows++;
				}
			}
			optimizeWriter();
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
		}
		finally
		{
			closeWriter();
		}
		_log.info("video新增索引：" + rows);
		return rows;
	}
	
	/* (non-Javadoc)
	 * @see com.youku.search.index.manager.BaseIndexManager#clearLastUpdateTime()
	 */
	@Override
	public void clearLastUpdateTime() {
		// TODO Auto-generated method stub
		
	}
	
}
