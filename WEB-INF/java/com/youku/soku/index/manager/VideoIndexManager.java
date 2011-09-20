/**
 * 
 */
package com.youku.soku.index.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

import com.youku.search.index.boost.Boost;
import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.index.manager.db.AssembleDoc;
import com.youku.soku.index.manager.db.MovielogManager;
import com.youku.soku.index.manager.db.SiteManager;
import com.youku.soku.index.manager.db.VideoManager;
import com.youku.soku.index.manager.db.VideoManagerFactory;
import com.youku.soku.index.om.Movielog;
import com.youku.soku.index.om.Site;
import com.youku.soku.index.query.VideoQueryManager;
import com.youku.soku.index.server.ServerManager;
import com.youku.soku.util.Constant;
import com.youku.soku.util.MyUtil;

/**
 * @author 1verge
 *
 */
public class VideoIndexManager extends BaseIndexManager{

	private static final int once_create_count = 1000; //一次创建500条
	
	public VideoIndexManager()
	{
		
	}
	public VideoIndexManager(String indexPath)
	{
		this.indexPath = indexPath;
	}
	
	int docCount = 0;
	public int createIndex()
	{
		_log.info("create index start");
		int row = 0;
		int index_number = 1;
		int server_count = ServerManager.getVideoServers(1).size();
		
		//先计算每台机器存储索引量
		int count_peri_index;
		_log.info("getTotalVideoCount");
		int count_total_index = VideoManager.getInstance().getTotalVideoCount();
//		int count_total_index = 10000;
//		 server_count = 1;
		
		count_peri_index = count_total_index/(server_count-1); //最后一台存储少量视频
		_log.info("count_total_index=" + count_total_index);
		_log.info("count_peri_index=" + count_peri_index);
		
		List<Site> sites = SiteManager.getInstance().getValidSiteList();
		HashMap<Integer,Integer> startMap = new HashMap<Integer,Integer>();
		
		this.indexPath = Config.getVideoIndexPath()+index_number;
		_log.info(indexPath);
		try {
			initWriter(true);
		} catch (Exception e1) {
			e1.printStackTrace();
			return 0;
		}
		Random random = new Random();
			while (true){
				int index = random.nextInt(sites.size());
				Site site = sites.get(index);
				Integer start = startMap.get(site.getId());
				if (start == null)
					start = 1;
				long time1 = System.currentTimeMillis();
				List<AssembleDoc> videos = VideoManagerFactory.getVideoManager(site.getId()).getVideos(start,once_create_count,site);
				long time2 = System.currentTimeMillis();
				_log.info("getVideos cost:" +(time2-time1));
				if (videos!=null && videos.size()>0){
					row += videos.size();
					int id = DataFormat.parseInt(videos.get(videos.size()-1).get("vid"));
					if (id <= start){ //异常
						_log.error("异常，start="+start+",id="+id);
						break;
					}
					start = id+1;
					startMap.put(site.getId(),start);
					
					for (int i=0;i<videos.size();i++)
					{
						AssembleDoc doc = videos.get(i);
						if(doc != null && doc.getDoc() != null){
							try {
								indexWriter.addDocument(doc.getDoc());
								if (indexWriter.docCount() == docCount+1){
									boostWriter.write(new Boost(doc.get("id"),doc.getBoost()));
									docCount++;
								}
							} catch (CorruptIndexException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
//							_log.info("create " + doc.get("id"));
						}
					}
					if(row >=count_peri_index && index_number<server_count) //超出每台机器存储的数量，索引号递增
					{
						_log.info("索引："+index_number+"共索引" + row);
						//创建单个索引完毕
						optimizeWriter();
						closeWriter();
						docCount = 0;
						//递增索引号
						index_number++;
						
						//新开索引目录
						this.indexPath = Config.getVideoIndexPath()+index_number;
						_log.info(indexPath);
						try {
							initWriter(true);
						} catch (Exception e1) {
							e1.printStackTrace();
							_log.error("初始化索引目录异常:"+index_number,e1);
							return row;
						}
						//初始化数量
						row = 0;
					}
				}
				else//此站点下找不到视频，继续下一个站点
				{
					sites.remove(index);
					startMap.remove(site.getId());
					_log.info("remove site " +site.getName());
					
					//如果全都删除了就停止
					if (sites.size() == 0)
						break;
					
					continue;
				}
				long time3 = System.currentTimeMillis();
				_log.info("getVideos cost:" +(time3-time2));
				
			}
//		创建单个索引完毕
		optimizeWriter();
		closeWriter();
		
		docCount = 0;
		startMap.clear();
		return row;
	}
	public final int lastServerDocCount=10000;
	public int createIndexDesc()
	{
		_log.info("create index start");
		int row = 0;
		
		int server_count = ServerManager.getVideoServers(1).size();
		int index_number = server_count;
		
		//先计算每台机器存储索引量
		int count_peri_index;
		_log.info("getTotalVideoCount");
//		int count_total_index = VideoManager.getInstance().getTotalVideoCount()+YoukuVideoManager.getInstance().getTotalVideoCount();
		int count_total_index = VideoManager.getInstance().getTotalVideoCount();
		
		count_peri_index = (count_total_index-lastServerDocCount)/(server_count-1); //最后一台存储少量视频
		_log.info("count_total_index=" + count_total_index);
		_log.info("count_peri_index=" + count_peri_index);
		
		List<Site> sites = SiteManager.getInstance().getValidSiteList();
		HashMap<Integer,Integer> startMap = new HashMap<Integer,Integer>();
		
		this.indexPath = Config.getVideoIndexPath()+index_number;
		_log.info(indexPath);
		try {
			initWriter(true);
		} catch (Exception e1) {
			e1.printStackTrace();
			return 0;
		}
		Random random = new Random();
			while (true){
				int index = random.nextInt(sites.size());
				Site site = sites.get(index);
				Integer end = startMap.get(site.getId());
				if (end == null)
					end = 0;
				long time1 = System.currentTimeMillis();
				List<AssembleDoc> videos = VideoManagerFactory.getVideoManager(site.getId()).getVideosDesc(end,once_create_count,site);
				long time2 = System.currentTimeMillis();
				_log.info("getVideos cost:" +(time2-time1) + ",size=" + (videos!=null?videos.size():0));
				if (videos != null && videos.size()>0){
					int id = 0;
					for (int i= videos.size()-1;i>=0;i--)
					{
						AssembleDoc doc = videos.get(i);
						
						if (doc == null){
							end = 1;
							break;
						}
						
						//记录下次获取最大值
						if (id == 0)
						{
							id = doc.getId();
							if (end>0 && id > end){ //异常
								_log.error("异常，end="+end+",id="+id);
								break;
							}
							end = id;
							startMap.put(site.getId(),end);
						}
						if(doc != null && doc.getDoc() != null){
							row ++;
							try {
								indexWriter.addDocument(doc.getDoc());
								if (indexWriter.docCount() == docCount+1){
									boostWriter.write(new Boost(doc.get("id"),doc.getBoost()));
									docCount++;
								}
							} catch (CorruptIndexException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
//							_log.info("create " + doc.get("id"));
						}
					}
					if( ((index_number == server_count && row>=lastServerDocCount )|| row >=count_peri_index ) && index_number>1) //超出每台机器存储的数量，索引号递减
					{
						_log.info("索引："+index_number+"共索引" + row);
						//创建单个索引完毕
						optimizeWriter();
						closeWriter();
						docCount = 0;
						//递增索引号
						index_number--;
						
						//新开索引目录
						this.indexPath = Config.getVideoIndexPath()+index_number;
						_log.info(indexPath);
						try {
							initWriter(true);
						} catch (Exception e1) {
							e1.printStackTrace();
							_log.error("初始化索引目录异常:"+index_number,e1);
							return row;
						}
						//初始化数量
						row = 0;
					}
				}
				else//此站点下找不到视频，继续下一个站点
				{
					sites.remove(index);
					startMap.remove(site.getId());
					_log.info("remove site " +site.getName());
					
					//如果全都删除了就停止
					if (sites.size() == 0)
						break;
					
					continue;
				}
				
			}
//		创建单个索引完毕
		optimizeWriter();
		closeWriter();
		
		docCount = 0;
		startMap.clear();
		return row;
	}
	
	/**
	 * 添加新视频
	 */
	public int addIndex(Date startTime,Date endTime)
	{
		long start = System.currentTimeMillis();
		int row = 0;
		List<Site> sites = SiteManager.getInstance().getValidSiteList();
		if (sites != null && sites.size()>0)
		{
			try {
				initWriter(false);
			} catch (Exception e) {
				_log.error("initWriter",e);
				return 0;
			}
			_log.info("initwriter over");
			for (Site site:sites){
				_log.info("site:"+site.getId());
				List<AssembleDoc> videos = null;
				try {
					videos = VideoManagerFactory.getVideoManager(site.getId()).getVideos(startTime,endTime,once_create_count,site);
					if (videos!=null && videos.size()>0){
						
						//先把视频添加到索引
						int last = addVideos(videos);
						row += videos.size();
						if (last == 0) continue;
						while (true){
							 videos = VideoManagerFactory.getVideoManager(site.getId()).getVideos(last+1,once_create_count,endTime,site);
							 if (videos!=null && videos.size()>0){
								 last = addVideos(videos);
								 row += videos.size();
								 if (last == 0) break;
							 }
							 else
								 break;
						}
					}
						
				} catch (Exception e) {
					e.printStackTrace();
					_log.error("addIndex Error:"+e.getMessage(),e);
				}
			}
			
			if (row > 0)
			{
				optimizeWriter();
			}
			
			_log.info("耗时："+(System.currentTimeMillis() - start)+"毫秒，新增："+row);
			closeWriter();
		}
		return row;
	}
	
	public int addIndex(String maxids,Date endTime)
	{
		long start = System.currentTimeMillis();
		int row = 0;
		List<Site> sites = SiteManager.getInstance().getValidSiteList();
		String[] ids = maxids.split(",");
		
		if (sites.size() != ids.length)
			return 0;
		
		if (sites != null && sites.size()>0)
		{
			try {
				initWriter(false);
			} catch (Exception e) {
				_log.error("initWriter",e);
				return 0;
			}
			_log.info("initwriter over");
			for (int i=0;i<sites.size();i++){
				Site site = sites.get(i);
				_log.info("site:"+site.getId());
				List<AssembleDoc> videos = null;
				//继续添加
				int last = DataFormat.parseInt(ids[i]);
				_log.info("last="+last);
				try {
					videos = VideoManagerFactory.getVideoManager(site.getId()).getVideos(last+1,once_create_count,endTime,site);
					if (videos!=null && videos.size()>0){
						
						//先把视频添加到索引
						last = addVideos(videos);
						row += videos.size();
						
						_log.info("last="+last);
						if (last == 0) continue;
						while (true){
							try {
								 videos = VideoManagerFactory.getVideoManager(site.getId()).getVideos(last+1,once_create_count,endTime,site);
								 if (videos!=null && videos.size()>0){
									 last = addVideos(videos);
									 row += videos.size();
									 _log.info("last="+last);
									 
									 if (last == 0) break;
								 }
								 else
									 break;
							} catch (Exception e) {
								_log.error("addIndex Error:"+ e.getMessage(),e);
							}
						}
						
					}
				}
				catch(Exception e)
				{
				}
			}
			
			if (row > 0)
			{
				optimizeWriter();
			}
			_log.info("耗时："+(System.currentTimeMillis() - start)+"毫秒，新增："+row);
			closeWriter();
		}
		return row;
	}
	
	private int addVideos(List<AssembleDoc> videos)
	{
		int last = 0;
		if (videos != null && videos.size()>0)
		{
			_log.info("新增video："+videos.size());
			for (int i=0;i<videos.size();i++)
			{
				AssembleDoc doc = videos.get(i);
				if(doc != null && doc.getDoc()!=null){
					try {
						last = DataFormat.parseInt(doc.get("vid"));
						indexWriter.addDocument(doc.getDoc());
						boostWriter.write(new Boost(doc.get("id"),doc.getBoost()));
						_log.info("add video:"+doc.get("id"));
					} catch (Exception e) {
						_log.error("addIndex Error:" + e.getMessage(),e);
					}
				}
			}
		}
		return last;
	}
	
	/**
	 * 更新需要更新的视频
	 */
	public int updateVideo()
	{
		long start = System.currentTimeMillis();
		int row = 0;
		
		List<AssembleDoc> updateVideos = new ArrayList<AssembleDoc>();
		
		System.out.println("getVideos ing...");
		List<Movielog> videos = MovielogManager.getInstance().getUpdateVideos();
		
		if (videos != null && videos.size() > 0){
			 
			for (int i=0;i<videos.size();i++)
			{
					Movielog movielog = videos.get(i);
					int id = movielog.getVid();
					String table = movielog.getTablename();
					
					Site site = SiteManager.getInstance().getSiteByTableName(table);
					if (site == null){
						_log.error("updateVideo getSiteByTableName error:" + table);
						continue;
					}
					try{
						int docid = VideoQueryManager.getInstance().isInIndex(id,site.getId());
						if (docid > 0)
						{
							AssembleDoc doc = VideoManagerFactory.getVideoManager(site.getId()).getVideoAsDocument(id,site);
							if (doc!=null && doc.getDoc()!=null){
								updateVideos.add(doc);
								MovielogManager.getInstance().delete(movielog.getId());
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						continue;
					}
			}
			
			_log.info("update videos size="+updateVideos.size());
			if (!updateVideos.isEmpty() )
			{
				try {
					initWriter(false);
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
				for (AssembleDoc doc : updateVideos){
					String key = doc.get("id");
					_log.info("update video " + key);
					try {
						indexWriter.updateDocument(new Term("id",key),doc.getDoc());
						boostWriter.updateBoost(new Boost(key,doc.getBoost()));
					} catch (Exception e) {
						_log.error(e.getMessage(),e);
						continue;
					}
					row ++;
				}
				
				optimizeWriter();
				_log.info("update index cost:"+(System.currentTimeMillis() - start));
				closeWriter();
			}
		}
		return row;
	}
	
	/**
	 * 删除需要删除的视频
	 * @return
	 */
	public int deleteVideo()
	{
		_log.info("start delete video.......");
		//删除站外视频
		List<Movielog> videos = MovielogManager.getInstance().getDeleteVideos();
		
		
		if (videos != null)
			_log.info("other video count:" +videos.size());
		
		if (videos == null || videos.isEmpty())
			return 0;
		
		int row = 0;
		try {
			initWriter(false);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		
			for (Movielog Movielog:videos){
				
				int id = Movielog.getVid();
				String table = Movielog.getTablename();

				Site site = SiteManager.getInstance().getSiteByTableName(table);
				if (site == null){
					_log.error("deleteVideo not find site error:" + table);
					continue;
				}
				if(MyUtil.isIndexServer(Constant.QueryField.VIDEO))
				{
					int docid = VideoQueryManager.getInstance().isInIndex(id,site.getId());
					if (docid > 0){
						
						VideoQueryManager.getInstance().deleteVideoByDocId(docid);//从内存删除
						_log.info("delete video:"+table + "\t vid:"+id);
						try{
							
							String key = site.getId()+"_"+Movielog.getVid();
							indexWriter.deleteDocuments(new Term("id",key ) );
							
							boostWriter.deleteBoost(key);
						}catch(Exception e)
						{
							e.printStackTrace();
							continue;
						}
						
						row ++;
					}
				}
				else
				{
					_log.info("delete video:"+table + "\t vid:"+id);
					try{
						
						String key = site.getId()+"_"+Movielog.getVid();
						indexWriter.deleteDocuments(new Term("id",key ) );
						
						boostWriter.deleteBoost(key);
					}catch(Exception e)
					{
						e.printStackTrace();
						continue;
					}
				}
			}
		
		if (row >0){
			try {
				indexWriter.flush();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		closeWriter();
		_log.info("delete video complete,delete total:" + row);
		return row;
	}
	
	/**
	 * 获取每个站点最大id
	 * @param sites
	 * @return
	 */
	public static int[] getMaxIds(List<Site> sites)
	{
		int[] result = new int[sites.size()];
		int server_count = ServerManager.getVideoServers(1).size();
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for (int i=1;i<=server_count;i++)
		{
			IndexReader reader = null;
			IndexSearcher searcher = null;
				
			try {
				reader = IndexReader.open(Config.getVideoIndexPath()+i);
				searcher = new IndexSearcher(reader);
				Sort sort = new Sort(new SortField("id",new VidSortComparator(),true));
				
				for (int j=0;j<sites.size();j++)
				{
					Site site = sites.get(j);
					int site_id = site.getId();
				
					Hits hits = searcher.search(new TermQuery(new Term("site",String.valueOf(site_id))),sort);
					
					if (hits!=null && hits.length()>0)
					{
						int vid = DataFormat.parseInt(hits.doc(0).get("vid"));
						_log.info("order:"+ i +"\t site :"+ site_id +"\t max vid=" +vid);
						
						if (map.containsKey(site_id))
							map.put(site_id,Math.max(map.get(site_id), vid));
						else
							map.put(site_id,vid);
					}
				}
			
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally
			{
				try {
					if (searcher != null)
						searcher.close();
					if (reader != null)
						reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			for (int j=0;j<sites.size();j++){
				Site site = sites.get(j);
				if (map.containsKey(site.getId()))
					result[j] = map.get(site.getId());
				else
					result[j] = 0;
			}
		}
		
		return result;
	}
	
	public static class Flag{
		public static Date lastTime = null;
	}
}
