/**
 * 
 */
package com.youku.search.index.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.index.entity.BarPost;
import com.youku.search.index.entity.Result;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public class BarpostQueryManager  extends BaseQuery{
	
	public static int DEFAULT_OPERATOR=Constant.Operator.AND; //默认为AND搜索
	private String[] fields = new String[]{"subject","content"};//检索的字段
	private Map<String,Float> boosts = new HashMap<String,Float>();  //各字段对应的权重
	
	public static final Sort SORT_NEW_ASC = new Sort(new SortField("post_time",SortField.LONG,false));
	public static final Sort SORT_NEW_DESC = new Sort(new SortField("post_time",SortField.LONG,true));
	
	private BarpostQueryManager(){
		
	}
	private static BarpostQueryManager self = null;

	public static BarpostQueryManager getInstance(){
		
		if(self == null){
			self = new BarpostQueryManager();
			self.init();
		}
		return self;
	}
	private synchronized void init(){
		super.indexType = Constant.QueryField.BARPOST_SUBJECT;
		indexPath = ServerManager.getBarpostIndexPath();
		boosts.put("title",1.5f);
		boosts.put("content",1f);
		initReader();
	}
	
	
	
	public Result querySubject(String words,int sort ,boolean reverse,int start,int end)
	{
		return querySubject(words,Constant.Sort.getBarPostSort(sort,reverse),start,end);
	}
	
	/**
	 * 搜索
	 * @param words 已经分好词的word
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result querySubject(String words,Sort sort ,int start,int end)
	{
		QueryParser  parser = new MultiFieldQueryParser (fields,AnalyzerManager.getBlankAnalyzer(),boosts);
        Query query = null;
        Hits hits = null;
        HighlightCondition hlight = null;
		try {
			if (DEFAULT_OPERATOR == Constant.Operator.AND)
				parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			query = parser.parse(words);
			
			Highlighter highlighter = new Highlighter(sHtmlF,new QueryScorer(query));
			highlighter.setTextFragmenter(new SimpleFragmenter(22));
			
			int[] hlightfield = new int[]{HighlightCondition.TITLE,HighlightCondition.MEMO};
			hlight = new HighlightCondition( hlightfield ,highlighter);
			
			hits = indexSearcher.search(query,sort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hitsToResult(hlight,hits,start,end);
	}
	
	
	public Result queryAuthor(String words,int sort ,boolean reverse,int start,int end)
	{
		return queryAuthor(words,Constant.Sort.getBarPostSort(sort,reverse),start,end);
	}
	
	/**
	 * 搜索
	 * @param words 已经分好词的word
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result queryAuthor(String words,Sort sort ,int start,int end)
	{
		Hits hits = null;
		try {
			Term term = new Term("poster_name",words);
			TermQuery query = new TermQuery(term);
			hits = indexSearcher.search(query,sort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hitsToResult(null,hits,start,end);
	}
	
	private Result hitsToResult(HighlightCondition hlight,Hits hits,int start,int end)
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
		}
		else{
			result.hasNext=true;
		}
		ArrayList<BarPost> list = new ArrayList<BarPost>();
		
		for (int i=start;i<end;i++)
		{
			try {
				BarPost barpost = documentToBarpost(hlight,hits.id(i),hits.doc(i));
				barpost.score = hits.score(i);
				list.add(barpost);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.results = list;
		return result;
	}
	
	protected BarPost documentToBarpost(HighlightCondition hlight,int doc_id,Document doc)
	{
		BarPost barpost = new BarPost();
		barpost.pk_post = DataFormat.parseInt(doc.get("pkpost"));
		barpost.pk_bar = DataFormat.parseInt(doc.get("pk_bar"));
		barpost.fk_subject = DataFormat.parseInt(doc.get("fk_subject"));
		barpost.bar_name = doc.get("bar_name");
		barpost.subject = doc.get("subject");
		barpost.content = doc.get("content");
		barpost.videoId = DataFormat.parseInt(doc.get("videoId"));
		barpost.encodeVid  = doc.get("encodeVid");
		barpost.videologo  = doc.get("videoLogo");
		barpost.first = DataFormat.parseInt(doc.get("first"));
		barpost.floor = DataFormat.parseInt(doc.get("floor"));
		barpost.poster_id = DataFormat.parseInt(doc.get("poster_id"));
		barpost.poster_name = doc.get("poster_name");
		barpost.post_time = DataFormat.parseLong(doc.get("post_time"));
		barpost.last_post_time = DataFormat.parseLong(doc.get("last_post_time"));
		
//		String newSubject=null,newContent=null;
//		if (hlight != null)
//		{
//			try
//			{
//				if (hlight.isHighTitle())
//				{
//					hlight.getHighlighter().setTextFragmenter(new SimpleFragmenter(40));    
//					 TermPositionVector tpv_title = (TermPositionVector)indexReader.getTermFreqVector(doc_id,"subject");
//					 if (tpv_title != null){
//						 TokenStream tokenStream=TokenSources.getTokenStream(tpv_title);
//						 newSubject = hlight.getHighlighter().getBestFragments(tokenStream,barpost.subject,1,fragmentSeparator);
//					 }
//				}
//				if (hlight.isHighMemo())
//				{
//					hlight.getHighlighter().setTextFragmenter(new SimpleFragmenter(60));
//					 TermPositionVector tpv_memo = (TermPositionVector)indexReader.getTermFreqVector(doc_id,"content");
//					 if (tpv_memo != null){
//						 TokenStream tokenStream=TokenSources.getTokenStream(tpv_memo);
//						 newContent  = hlight.getHighlighter().getBestFragments(tokenStream,barpost.content,1,fragmentSeparator);
//					 }
//				}
//			}
//			catch(Exception e)
//			{
//				System.out.println(e.getMessage());
//				e.printStackTrace();
//			}
//			
//		}
//		if (newSubject != null && !newSubject.isEmpty())
//			barpost.subject = newSubject;
//		
//		if (newContent != null  && !newContent.isEmpty()){
//			barpost.content = newContent;
//		}
		
		return barpost;
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
		}
			finally{
			
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
