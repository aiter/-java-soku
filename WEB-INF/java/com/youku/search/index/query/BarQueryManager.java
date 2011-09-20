/**
 * 
 */
package com.youku.search.index.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.index.entity.Bar;
import com.youku.search.index.entity.Result;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public class BarQueryManager  extends BaseQuery{
	
	public static int DEFAULT_OPERATOR=Constant.Operator.OR; //默认为OR搜索
	private String fields = "bar_name";//检索的字段
	private BarQueryManager(){
		
	}
	private static BarQueryManager self = null;

	public static BarQueryManager getInstance(){
		
		if(self == null){
			self = new BarQueryManager();
			self.init();
		}
		return self;
	}
	private synchronized void init(){
		super.indexType = Constant.QueryField.BAR;
		indexPath = ServerManager.getBarIndexPath();
//		boosts.put("title",1.5f);
//		boosts.put("content",1f);
		initReader();
	}
	
	
	/**
	 * 搜索
	 * @param words 已经分好词的word
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result query(String words,int start,int end)
	{
		BarTopDocCollector collector = new BarTopDocCollector(end);
		TopDocs docs = null;
		
		QueryParser  parser = new QueryParser (fields,AnalyzerManager.getBlankAnalyzer());
        Query query = null;
		try {
			parser.setDefaultOperator(QueryParser.OR_OPERATOR);
			query = parser.parse(words);
			indexSearcher.search(query,null,collector);
			docs = collector.topDocs();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return docsToResult(docs,start,end);
	}
	
	private Result docsToResult(TopDocs docs,int start,int end)
	{
		if (docs == null) return new Result();
		Result result = new Result();
		
		int size = docs.totalHits;
		result.totalCount = size;  //存储到结果集
		
		//如果开始位置大于查询总量，直接返回result,结果集为空
		if (start > size)
			return result;
		
		if (size < end) end = size;
		
		ArrayList<Bar> list = new ArrayList<Bar>();
		ScoreDoc[] scoreDoc = docs.scoreDocs;
		
		for (int i=start;i<end;i++)
		{
			try {
				Bar bar = documentToBar(scoreDoc[i]);
				bar.score = scoreDoc[i].score;;
				list.add(bar);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.results = list;
		return result;
	}
	
	protected Bar documentToBar(ScoreDoc scoreDoc) throws CorruptIndexException, IOException
	{
		Document doc =  indexReader.document(scoreDoc.doc);
		Bar bar = new Bar();
		bar.pk_bar = DataFormat.parseInt(doc.get("pk_bar"));
		bar.bar_name = doc.get("bar_name");
		bar.count_member=DataFormat.parseInt(doc.get("count_member"));
		bar.count_subject=DataFormat.parseInt(doc.get("count_subject"));
		bar.count_video=DataFormat.parseInt(doc.get("count_video"));
		bar.total_pv=DataFormat.parseInt(doc.get("total_pv"));
		List<Integer> bar_catalog_ids=new ArrayList<Integer>();
		List<String> bar_catalog_names=new ArrayList<String>();
		for(int i=1;;i++){
			if(doc.get("bar_catalog_id_"+i)==null||doc.get("bar_catalog_id_"+i).trim().length()<1)
				break;
			bar_catalog_ids.add(DataFormat.parseInt(doc.get("bar_catalog_id_"+i)));
			bar_catalog_names.add(doc.get("bar_catalog_name_"+i));
		}
		bar.bar_catalog_ids=bar_catalog_ids;
		bar.bar_catalog_names=bar_catalog_names;
//		bar.video_ids=new int[]{DataFormat.parseInt(doc.get("videoId_1")),DataFormat.parseInt(doc.get("videoId_2")),DataFormat.parseInt(doc.get("videoId_3"))};
//		bar.encodeVids=new String[]{doc.get("encodeVid_1"),doc.get("encodeVid_2"),doc.get("encodeVid_3")};
//		bar.video_logos=new String[]{doc.get("videoLogo_1"),doc.get("videoLogo_2"),doc.get("videoLogo_3")};
//		bar.video_titles=new String[]{doc.get("videoTitle_1"),doc.get("videoTitle_2"),doc.get("videoTitle_3")};
		
		bar.subject_ids=new int[]{DataFormat.parseInt(doc.get("subject_id_1")),DataFormat.parseInt(doc.get("subject_id_2")),DataFormat.parseInt(doc.get("subject_id_3"))};
		bar.subjects=new String[]{doc.get("subject_1"),doc.get("subject_2"),doc.get("subject_3")};
//		bar.last_poster_ids=new int[]{DataFormat.parseInt(doc.get("subject_1_last_poster_id")),DataFormat.parseInt(doc.get("subject_2_last_poster_id")),DataFormat.parseInt(doc.get("subject_3_last_poster_id"))};
//		bar.last_poster_name=new String[]{doc.get("subject_1_last_poster_name"),doc.get("subject_2_last_poster_name"),doc.get("subject_3_last_poster_name")};
//		bar.last_post_time=new long[]{DataFormat.parseLong(doc.get("subject_1_last_post_time")),DataFormat.parseLong(doc.get("subject_2_last_post_time")),DataFormat.parseLong(doc.get("subject_3_last_post_time"))};
		
		return bar;
	}
	/**
	 * 删除回帖
	 * @param post_id
	 * @return
	 */
	public int deletePost(int post_id)
	{
		int row = 0;
		Term term = new Term("pkpost", String.valueOf(post_id));
		 try {
		    	//先从内存删除
		    	row = indexReader.deleteDocuments(term);
		    	indexReader.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				
			}
		return row;
	}
	/**
	 * 删除回帖
	 * @param post_id
	 * @return
	 */
	public void deletePosts(List<Integer> post_ids)
	{
		Term[] terms = new Term[post_ids.size()];
		for (int i =0;i<post_ids.size();i++)
		{
			terms[i] = new Term("pkpost", String.valueOf(post_ids.get(i)));
		}
	    try {
	    	//先从内存删除
	    	for (int i =0;i<terms.length;i++)
	    		 indexReader.deleteDocuments(terms[i]);
	    	indexReader.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
				
			}
	}
	
	
	/**
	 * 删除主题
	 * @param subject_id
	 * @return
	 */
	public int deleteSubject(int subject_id)
	{
		int row = 0;
		Term term = new Term("fk_subject", String.valueOf(subject_id));
		 try {
		    	//先从内存删除
		    	row = indexReader.deleteDocuments(term);
		    	indexReader.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				
			}
		return row;
	}
	/**
	 * 删除主题
	 * @param subject_id
	 * @return
	 */
	public void deleteSubjects(List<Integer> subject_ids)
	{
		Term[] terms = new Term[subject_ids.size()];
		for (int i =0;i<subject_ids.size();i++)
		{
			terms[i] = new Term("fk_subject", String.valueOf(subject_ids.get(i)));
		}
	    try {
	    	//先从内存删除
	    	for (int i =0;i<terms.length;i++)
	    		 indexReader.deleteDocuments(terms[i]);
	    	
	    	indexReader.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
			finally{
			
			}
	}
	
	/**
	 * 删除看吧
	 * @param bar_id
	 * @return
	 */
	public int deleteBar(int bar_id)
	{
		int row = 0;
		Term term = new Term("pk_bar", String.valueOf(bar_id));
		 try {
		    	//先从内存删除
		    	row = indexReader.deleteDocuments(term);
		    	indexReader.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				
			}
		return row;
	}
	
	/**
	 * 删除看吧
	 * @param bar_id
	 * @return
	 */
	public void deleteBars(List<Integer> bar_ids)
	{
		Term[] terms = new Term[bar_ids.size()];
		for (int i =0;i<bar_ids.size();i++)
		{
			terms[i] = new Term("pk_bar", String.valueOf(bar_ids.get(i)));
		}
	    try {
	    	//先从内存删除
	    	for (int i =0;i<terms.length;i++)
	    		 indexReader.deleteDocuments(terms[i]);
	    	indexReader.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
			finally{
				
			}
	}
	
	
	public void destroy() throws IOException
	{
		if (indexReader != null)
			indexReader.close();
	}
}
