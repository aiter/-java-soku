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
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.torque.TorqueException;

import com.youku.search.index.boost.Boost;
import com.youku.search.index.boost.BoostWriter;
import com.youku.search.index.db.AssembleDoc;
import com.youku.search.index.db.LastVvManager;
import com.youku.search.index.db.VideoManager;
import com.youku.search.index.db.VvManager;
import com.youku.search.index.db.LastVvManager.Vv;
import com.youku.search.index.entity.Video;
import com.youku.search.index.manager.AffectManager.Affect;
import com.youku.search.index.manager.AffectManager.Type;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.DataFormat;
import com.youku.search.util.boost.VideoBoost_Test;
import com.youku.search.util.mail.MailSender;

/**
 * @author william
 *
 */
public class VideoIndexManager  extends BaseIndexManager{
	
	private int start;		//索引起始ID
	private int end;		//索引结束ID
	private static final int once_create_number = 500; //一次创建50条
	
	public VideoIndexManager()
	{
		indexPath = ServerManager.getVideoIndexPath();
	}
	public VideoIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	public VideoIndexManager(int start,int end,String indexPath)
	{
		this.start = start;
		this.end = end;
		this.indexPath = indexPath;
	}
	
	
	
	/**
	 * 创建索引
	 */
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
	
		//初始化vv表名
		String table = LastVvManager.getInstance().initTable();
		_log.info("vv表："+table);
		
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
		
		//删除最近一天删除过的视频
		AffectManager.getInstance().cleanOldFile(Type.VIDEO,24);
		
		_log.info("本机删除这一天删除的视频");
		List<Integer> videos = AffectManager.getInstance().getAllDeleteFromFile(AffectManager.Type.VIDEO);
		if (videos !=null){
			_log.info("24小时内共删除视频："+videos.size());
			for (Integer id:videos)
			{
				Video video = VideoManager.getInstance().getVideo(id);
				if (video == null){
					try {
						indexWriter.deleteDocuments(new Term("vid",id+""));
					} catch (Exception e) {
						_log.info("delete video error:" + id ,e);
					} 
				}
			}
		}
	    _log.info("结束本机删除视频");
		
		
		_log.info("indexWriter.docCount()="+indexWriter.docCount());
		optimizeWriter();
		_log.info("indexWriter.docCount()="+indexWriter.docCount());
		closeWriter();
		_log.info("end create index");
		docCount = 0;
		return total;
	}
	int docCount = 0;
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
		VideoManager video = VideoManager.getInstance();
		List<AssembleDoc> list;
		try {
			list = video.getVideos(start,end);
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
					if (doc != null && doc.getDoc()!=null){
						try{
							indexWriter.addDocument(doc.getDoc());
							
							if (indexWriter.docCount() == docCount+1){
								boostWriter.write(new Boost(doc.get("vid"),doc.getBoost()));
								docCount++;
							}
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
		_log.info("start=" + start + ",end="+end + "  cost:" + (System.currentTimeMillis() - time1));
		return list!=null?list.size():0;
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
		AffectManager.getInstance().initWriter(AffectManager.Type.VIDEO,true);
		try
		{
			_log.info("video start=" + lastUpdateTime + ",end = " + endTime);
			List<AssembleDoc> list = VideoManager.getInstance().getVideos(start,end,lastUpdateTime,endTime);
			
			_log.info("video新增索引：" + list.size());
			if (list != null && list.size() > 0)
			{
				initWriter();
				for (int i=0;i<list.size();i++)
				{
					AssembleDoc doc = list.get(i);
					if (doc != null && doc.getDoc()!=null){
						_log.info("add video id=" + doc.getDoc().get("vid") + "\t boost="+doc.getBoost());
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.get("vid"),doc.getBoost()));
						rows++;
					}
				}
				
				List<Affect> vids_renew = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.RENEW);
				List<Affect> vids_mobile_complete = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.MOBILE_CODING_OVER);
				List<Affect> vids_update = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.UPDATE);
				
				
				_log.info("取消屏蔽数量：" + vids_renew.size());
				_log.info("移动格式转码完成更新数量：" + vids_mobile_complete.size());
				_log.info("视频更新数量：" + vids_update.size());
				
				updateVideos(vids_renew);
				updateVideos(vids_mobile_complete);
				updateVideos(vids_update);
				
				
				_log.info("更新最近1天内视频boost");
				//更新最近1天内视频boost
				List<Boost> boosts = getNewVideoBoost(1);
				
				if (boosts != null){
					_log.info("video count = " + boosts.size());
					boostWriter.updateBoost(boosts);
				}
				
				_log.info("本机删除索引");
				//删除索引
				List<Affect> deleteAffects = AffectManager.getInstance().getAffects(AffectManager.Type.VIDEO,AffectManager.DELETE);
				AffectManager.getInstance().saveToFile(AffectManager.Type.VIDEO,deleteAffects);
				
				try {
					deleteVideos(deleteAffects);
				} catch (IOException e) {
					_log.error(e.getMessage(),e);
				}
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
				AffectManager.getInstance().closeWriter(AffectManager.Type.VIDEO);
			} catch (IOException e) {
				_log.error(e.getMessage(),e);
			}
			closeWriter();
		}
		
		
		if (rows > 0)
			lastUpdateTime = endTime;
		
		return rows;
	}
	
	/**
	 *  更新视频
	 * @param vids
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public int updateVideos(List<Affect> vids) throws CorruptIndexException, IOException
	{
		int row = 0;
		if (vids !=null && vids.size()>0){
			_log.info("更新数量：" + vids.size());
			for (Affect affect:vids){
				
				AssembleDoc doc =  VideoManager.getInstance().getVideoAsDocument(affect.getPk_id());
				if (doc != null && doc.getDoc()!=null){
					_log.info("update video id=" + affect.getPk_id() + "\t boost=" + doc.getBoost());
					//先删除原来的
					indexWriter.deleteDocuments(new Term("vid",doc.get("vid")));
					//添加新的
					indexWriter.addDocument(doc.getDoc());
					boostWriter.write(new Boost(doc.get("vid"),doc.getBoost()));
					row++;
				}
				
				//记录下来并删除
				AffectManager.getInstance().saveToFile(AffectManager.Type.VIDEO,affect);
			}
		}
		return row;
	}
	
	public int addIndex(int startid,int endid)
	{
		_log.info("开始添加视频索引：start="+startid +" end="+endid);
		int rows = 0;
		
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try
		{
			List<AssembleDoc> list= null;
			
			indexReader = IndexReader.open(indexPath);
			indexSearcher =  new IndexSearcher(indexReader);
			int e = Math.min(startid+1000,endid);
			//构建硬盘存储writer
			initWriter();
			do{
				list = VideoManager.getInstance().getVideos(startid,e);
				_log.info("list.size="+list.size());
				if (list != null && list.size() > 0)
				{
					for (int i=0;i<list.size();i++)
					{
						AssembleDoc doc = list.get(i);
						if (doc != null && doc.getDoc()!=null){
							Hits hits = indexSearcher.search(new TermQuery(new Term("vid",doc.getDoc().get("vid"))));
							if (hits != null && hits.length() > 0)
								continue;
							_log.info("add video id=" + doc.getDoc().get("vid"));
							indexWriter.addDocument(doc.getDoc());
							boostWriter.write(new Boost(doc.get("vid"),doc.getBoost()));
							rows++;
						}
					}
					
				}
				startid = e;
				e = startid +1000;
			}
			while (e < endid);
				
			optimizeWriter();
		}
		catch(Exception e)
		{
			_log.error(e.getMessage(),e);
			try {
				MailSender.send("luwei@youku.com","addIndex添加视频过程中连接数据库失败，start+"+start + " end="+end,e.getMessage());
			} catch (Exception e2) {
			}
		}
		finally
		{
			closeWriter();
			try
			{
				if (indexSearcher != null)indexSearcher.close();
				if (indexReader != null)indexReader.close();
			}catch(Exception e){
				_log.error(e.getMessage(),e);
			}
		}
		_log.info("video新增索引：" + rows);
		return rows;
	}
	
	public int addIndexFromMaxId() 
	{
		int rows = 0;
		Date endTime = new Date();
		
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		//获取当前最大id
		try
		{
			indexReader = IndexReader.open(indexPath);
			indexSearcher =  new IndexSearcher(indexReader);
			Hits hs = indexSearcher.search(new MatchAllDocsQuery(),new Sort(new SortField("vid",SortField.LONG,true)));
			int startId = 0;
			if (hs!=null && hs.length()>0)
				startId = DataFormat.parseInt(hs.doc(0).get("vid"))+1;
			else
				return 0;
			
			_log.info("video startId=" + startId + ",endTime = " + endTime);
			initWriter();
			while (true)
			{
				List<AssembleDoc> list = VideoManager.getInstance().getVideos(startId,endTime,once_create_number);
				
				if (list != null && list.size() > 0)
				{
					
					startId = DataFormat.parseInt(list.get(list.size()-1).get("vid"))+1;
					_log.info("共搜到视频："+list.size());
					for (int i=0;i<list.size();i++)
					{
						AssembleDoc doc = list.get(i);
						if(doc == null || doc.getDoc()==null)
							continue;
						Hits hits = indexSearcher.search(new TermQuery(new Term("vid",doc.getDoc().get("vid"))));
						if (hits != null && hits.length() > 0)
							continue;
						rows++;
						_log.info("add video id=" + doc.getDoc().get("vid"));
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.get("vid"),doc.getBoost()));
					}
					_log.info("video新增索引：" + rows);
				}
				else
					break;
			}
			if (rows > 0)
				optimizeWriter();
			
			lastUpdateTime = endTime;
		}catch(Exception e)
		{
			try {
				MailSender.send("luwei@youku.com","updateIndexFromMaxId添加视频过程中连接数据库失败，start+"+start + " end="+end,e.getMessage());
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			return 0;
		}
		finally
		{
			closeWriter();
			try
			{
				if (indexReader != null)indexReader.close();
				if (indexSearcher != null)indexSearcher.close();
			}catch(Exception e){}
		}
		return rows;
	}
	
	
	public int updateIndex(int[] vids)
	{
		_log.info("开始更新视频索引");
		int rows = 0;
		
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try
		{
			List<AssembleDoc> list= null;
			
			indexReader = IndexReader.open(indexPath);
			indexSearcher =  new IndexSearcher(indexReader);
			list = VideoManager.getInstance().getVideos(vids);
			_log.info("list.size="+list.size());
			if (list != null && list.size() > 0)
			{
//				构建硬盘存储writer
				initWriter();
				for (int i=0;i<list.size();i++)
				{
					AssembleDoc doc = list.get(i);
					if (doc != null && doc.getDoc()!=null){
						indexWriter.deleteDocuments(new Term("vid",doc.getDoc().get("vid")));
						_log.info("add video id=" +doc.getDoc().get("vid"));
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.get("vid"),doc.getBoost()));
						rows++;
					}
					else
					{
						_log.info("doc is null:"+doc.get("vid"));
					}
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
				if (indexSearcher != null)indexSearcher.close();
				if (indexReader != null)indexReader.close();
			}catch(Exception e){
				_log.error(e.getMessage(),e);
			}
		}
		_log.info("video新增索引：" + rows);
		return rows;
	}
	
	public int deleteVideo(int vid) throws IOException
	{
		Term term = new Term("vid", String.valueOf(vid));
		
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
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
			e.printStackTrace();
			return 0;
		}
		finally{
			closeWriter();
			indexReader.close();
			indexSearcher.close();
		}
		return 1;
	}
	
	public void deleteVideos(List<Affect> vids) throws IOException
	{
		boolean open = false;
		if (indexWriter == null)
		{
			open = true;
			initWriter();
		}
		
		
		Term[] terms = new Term[vids.size()];
		for (int i =0;i<vids.size();i++)
		{
			_log.info("delete video id=" + vids.get(i).getPk_id());
			terms[i] = new Term("vid", String.valueOf(vids.get(i).getPk_id()));
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
	
	/**
	 * days 几天前
	 * @param days
	 */
	public List<Boost> getNewVideoBoost(int days)
	{
		List<Boost> boosts = new ArrayList<Boost>();
		Date startTime = DataFormat.getNextDate(new Date(),0-days);
		
		List<Integer> vids = null;
		try {
			vids = VideoManager.getInstance().getLastVideoIds(startTime, 40000);
		} catch (TorqueException e) {
			e.printStackTrace();
			return null;
		}
		
		for (int vid:vids)
		{
			int vv = VvManager.getInstance().getVv(vid);
			if (vv > 0){
				float boost = (float)VideoBoost_Test.getBoost(vv,0l,false,0).all();
				if (boost > 0)
					boosts.add(new Boost(String.valueOf(vid),boost));
			}
		}

		return boosts;
	}
	
	public void createBoost() throws IOException
	{
		LastVvManager.getInstance().initTable();
		int del_count = 0;
		boostWriter = new BoostWriter(indexPath,true);
		IndexReader indexReader = null;
		indexReader = IndexReader.open(indexPath);
		int max  = indexReader.numDocs();
		_log.info("max:"+max);
		for (int i=0;i<max;i++)
		{
			Document doc = null;
			try {
				doc = indexReader.document(i);
			} catch (Exception e) {
				e.printStackTrace();
				_log.error(e.getMessage(),e);
				continue;
			}
			
			if (indexReader.isDeleted(i))
				del_count++;
			
			long time = DataFormat.parseLong(doc.get("createtime"));
//			int vv = DataFormat.parseInt(doc.get("total_pv"));
//			int total_fav = DataFormat.parseInt(doc.get("total_fav"));
//			int total_comment = DataFormat.parseInt(doc.get("total_comment"));
			float seconds = DataFormat.parseFloat(doc.get("seconds"),0f);
//			String title = doc.get("title");
//			String memo = doc.get("memo");
			String st = VideoManager.getInstance().getStreamtypes(DataFormat.parseInt(doc.get("streamtypes")));
			boolean hd = st!=null && st.startsWith("1");
			
			
//			float boost = VideoBoost.getBoost(time,vv,total_fav,total_comment,seconds,title,memo,hl).all();
			
			float boost = 0 ;
			Vv lastvv = LastVvManager.getInstance().getVv(DataFormat.parseInt(doc.get("vid")));
			if (lastvv != null){
				VideoBoost_Test bo = VideoBoost_Test.getBoost(lastvv.getValue(),time,hd,seconds);
				boost = (float)bo.all();
				
				_log.info(i+"\t"+doc.get("vid")+ "\tvv="+ lastvv.getValue() + "\t vvboost=" + bo.vv +"\tboost="+boost);
			}
			
			boostWriter.write(new Boost(doc.get("vid"),boost));
		}
		indexReader.close();
		boostWriter.closeWriter();
		
		_log.info("over:doc count=" + max+" del="+del_count);
	}
	
	
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	
	public void clearLastUpdateTime()
	{
		lastUpdateTime = null;
	}
	
	public static void main(String[] rags)
	{
		System.out.println(DataFormat.getNextDate(new Date(),0-1));
	}

}
