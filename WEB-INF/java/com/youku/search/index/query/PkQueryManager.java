/**
 * 
 */
package com.youku.search.index.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.index.entity.Pk;
import com.youku.search.index.entity.Result;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public class PkQueryManager  extends BaseQuery{

	public static int DEFAULT_OPERATOR=Constant.Operator.AND; //默认为AND搜索
	
	public static final Sort SORT_NEW_ASC = new Sort(new SortField("begintime",SortField.LONG,false));
	public static final Sort SORT_NEW_DESC = new Sort(new SortField("begintime",SortField.LONG,true));
	public static final Sort SORT_VIDEOCOUNT_ASC = new Sort(new SortField("video_count",SortField.INT,false));
	public static final Sort SORT_VIDEOCOUNT_DESC = new Sort(new SortField("video_count",SortField.INT,true));
	public static final Sort SORT_PV_ASC = new Sort(new SortField("total_pv",SortField.INT,false));
	public static final Sort SORT_PV_DESC = new Sort(new SortField("total_pv",SortField.INT,true));
	public static final Sort SORT_VOTECOUNT_ASC = new Sort(new SortField("vote_count",SortField.INT,false));
	public static final Sort SORT_VOTECOUNT_DESC = new Sort(new SortField("vote_count",SortField.INT,true));
	public static final Sort SORT_ACTORCOUNT_ASC = new Sort(new SortField("actor_count",SortField.INT,false));
	public static final Sort SORT_ACTORCOUNT_DESC = new Sort(new SortField("actor_count",SortField.INT,true));
	
	private String[] fields = new String[]{"pk_name","tags_index","description"};//检索的字段
	private Map<String,Float> boosts = new HashMap<String,Float>();  //各字段对应的权重
	
	private PkQueryManager(){
		
	}
	private static PkQueryManager self = null;

	public  static PkQueryManager getInstance(){
		
		if(self == null){
			self = new PkQueryManager();
			self.init();
		}
		return self;
	}
	private synchronized void init(){
		super.indexType = Constant.QueryField.PK;
		indexPath = ServerManager.getPkIndexPath();
		boosts.put("title",1.5f);
		boosts.put("tags_index",1f);
		boosts.put("memo",0.5f);
		
		initReader();
	}
	
	/**
	 * 查询
	 * @param words 已经分好词的word
	 * @param sort  排序，见Constant.Sort类
	 * @param reverse 倒序，默认true，表示倒序
	 * @param start  结果集开始位置
	 * @param end	 结果集结束位置
	 * @return
	 */
	public Result query(String words,int sort,boolean reverse,int start,int end)
	{
		return query(words,Constant.Sort.getPkSort(sort,reverse),start,end);
	}
	
	/**
	 * 搜索，在title,tags,memo中搜索
	 * @param words 已经分好词的word
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result query(String words,Sort sort,int start,int end)
	{
		
		QueryParser  parser = new MultiFieldQueryParser (fields,AnalyzerManager.getBlankAnalyzer(),boosts);
        Query query = null;
        Hits hits = null;
		try {
			if (DEFAULT_OPERATOR == Constant.Operator.AND)
				parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			query = parser.parse(words);
			hits = indexSearcher.search(query,sort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hitsToResult(hits,start,end);
	}
	
	/**
	 * 单tag查询
	 * @param tag
	 * @param category
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result queryTag(String tag,int sort,boolean reverse,int start,int end)
	{
		return queryTag(tag,Constant.Sort.getPkSort(sort,reverse),start,end);
	}
	/**
	 * 单tag查询
	 * @param tag
	 * @param category
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result queryTag(String tag,Sort sort,int start,int end)
	{
		Hits hits = null;
		Query query = null;
		try {
			QueryParser parser = new QueryParser("tags_index",AnalyzerManager.getBlankAnalyzer());
			query = parser.parse(tag);
			hits = indexSearcher.search(query,sort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hitsToResult(hits,start,end);
	}
	
	private Result hitsToResult(Hits hits,int start,int end)
	{
		if (hits == null) return new Result();
		Result result = new Result();
		
		int size = hits.length();
		result.totalCount = size;  //存储到结果集
		
		//如果开始位置大于查询总量，直接返回result,结果集为空
		if (start > size)
			return result;
		
		if (size <= end){
			end = size;
			result.hasNext = false;
		}
		
		ArrayList<Pk> list = new ArrayList<Pk>();
		
		for (int i=start;i<end;i++)
		{
			try {
				Pk pk = documentToPk(hits.doc(i));
				pk.score = hits.score(i);
				list.add(pk);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.results = list;
		return result;
	}
	
	protected Pk documentToPk(Document doc)
	{
		Pk pk = new Pk();
		pk.pk_pk = DataFormat.parseInt(doc.get("pkid"));
		pk.pk_name = doc.get("pk_name");
		pk.description = doc.get("description");
		pk.logo = doc.get("logo");
		pk.status = doc.get("status");
		pk.begintime = DataFormat.parseLong(doc.get("begintime"));
		pk.endtime = DataFormat.parseLong(doc.get("endtime"));
		pk.owner = DataFormat.parseInt(doc.get("owner"));
		pk.user_name = doc.get("user_name");
		pk.video_count = DataFormat.parseInt(doc.get("video_count"));
		pk.vote_count = DataFormat.parseInt(doc.get("vote_count"));
		pk.actor_count = DataFormat.parseInt(doc.get("actor_count"));
		pk.total_pv = DataFormat.parseInt(doc.get("total_pv"));
		pk.tags = doc.get("tags");
		
		String[] video_id = null;
		String[] video_title = null;
		String[] video_seconds = null;
		
		String video_id1=doc.get("video1_id");
		String video_id2=doc.get("video2_id");
		
		if (video_id1 != null){
			if (video_id2 == null){
				video_id = new String[]{video_id1};
				video_title = new String[]{doc.get("video1_title")};
				video_seconds = new String[]{doc.get("video1_seconds")};
			}
			else{
				String video_id3=doc.get("video3_id");
				if (video_id3 ==null){
					video_id = new String[]{video_id1,video_id2};
					video_title = new String[]{doc.get("video1_title"),doc.get("video2_title")};
					video_seconds = new String[]{doc.get("video1_seconds"),doc.get("video2_seconds")};
				}
				else{
					video_id = new String[]{video_id1,video_id2,video_id3};
					video_title = new String[]{doc.get("video1_title"),doc.get("video2_title"),doc.get("video3_title")};
					video_seconds = new String[]{doc.get("video1_seconds"),doc.get("video2_seconds"),doc.get("video3_seconds")};
				}
			}
		}
		
		
		
		pk.video_id = video_id;
		pk.video_title = video_title;
		pk.video_seconds = video_seconds;
		
		return pk;
	}
	
	
	
	public int deletePk(int pk_id)
	{
		int row = 0;
		Term term = new Term("pkid", String.valueOf(pk_id));
		IndexWriter indexWriter = null;
		 try {
		    	//先从内存删除
		    	row = indexReader.deleteDocuments(term);
		    	indexWriter = new IndexWriter(indexPath, AnalyzerManager.getMyAnalyzer(),false);
		    	indexWriter.deleteDocuments(term);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				try {
					if (indexWriter != null){
						indexWriter.close();
						indexWriter = null;
					}
				} catch (Exception e) {
				}
			}
		return row;
	}
	
	
	public void deletePks(List<Integer> pk_ids)
	{
		Term[] terms = new Term[pk_ids.size()];
		IndexWriter indexWriter = null;
		for (int i =0;i<pk_ids.size();i++)
		{
			terms[i] = new Term("pkid", String.valueOf(pk_ids.get(i)));
		}
	    try {
	    	//先从内存删除
	    	for (int i =0;i<terms.length;i++)
	    		 indexReader.deleteDocuments(terms[i]);
	    	
	    	indexWriter = new IndexWriter(indexPath, AnalyzerManager.getMyAnalyzer(),false);
	    	indexWriter.deleteDocuments(terms);
		} catch (Exception e) {
			e.printStackTrace();
		}
			finally{
				try {
					if (indexWriter != null){
						indexWriter.close();
						indexWriter = null;
					}
				} catch (Exception e) {
				}
			}
	}
	
	public void destroy() throws IOException
	{
		if (indexReader != null)
			indexReader.close();
	}

	
}
