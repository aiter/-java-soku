/**
 * 
 */
package com.youku.search.index.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

import com.youku.common.correct.CorrectorManager;
import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.index.entity.Result;
import com.youku.search.index.entity.Stat;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class StatQueryManager extends BaseQuery{
	private StatQueryManager(){
		
	}
	private static StatQueryManager self = null;

	public  static StatQueryManager getInstance(){
		
		if(self == null){
			self = new StatQueryManager();
			self.init();
		}
		return self;
	}
	private synchronized void init(){
		super.indexType = Constant.QueryField.STAT;
		indexPath = ServerManager.getStatIndexPath();
		initReader();
	}
	
	
	public static final Sort SORT_QureyCount = new Sort(new SortField("query_count",SortField.INT,true));
	public static final Sort SORT_ResultCount = new Sort(new SortField("result",SortField.INT,true));
	
	
	public Result<Stat> correctFromDb(String words){
		String newWord = CorrectorManager.getCorrector().correct(words);
		if (newWord != null){ 	//纠错词表直接找到，直接返回
			Stat word = new Stat();
			word.keyword = newWord;
			
			ArrayList<Stat> stats = new ArrayList<Stat>();
			stats.add(word);
			
			Result<Stat> result = new Result<Stat>();
			result.results = stats;
			result.totalCount = 1;
			return result;
		}
		return null;
	}
	
	/**
	 * 纠错
	 * @param words
	 * @param type
	 * @return
	 */
	public Result<Stat> correct(String words,int type)
	{
		Result<Stat> result = correctFromDb(words);
		if (result != null)
			return result;
		
        Hits hits = null;
		
		try {
			if (type == 0) type = Constant.QueryField.VIDEO;
				
			BooleanQuery bq = new BooleanQuery();
			Query typeQuery = new TermQuery(new Term("query_type",String.valueOf(type)));
			Query query = new TermQuery(new Term("keyword_py_index",MyUtil.formatFuzzyLetter(com.youku.search.hanyupinyin.Converter.convert(words,true))));

			bq.add(typeQuery,BooleanClause.Occur.MUST);
			bq.add(query,BooleanClause.Occur.MUST);
			
			hits = indexSearcher.search(bq,SORT_QureyCount);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		result = correctHitsToResult(hits,words);
		
		if (result != null){
			List<Stat> stats = result.results;
			if (stats!=null && stats.size()>0){
				String newWord = CorrectorManager.getCorrector().correct(stats.get(0).keyword);
				if (newWord != null){ 	//纠错词表找到再纠错的词继续纠错
					Stat word = new Stat();
					word.keyword = newWord;
					
					stats.add(0, word);
				}
			}
		}
		return result;
	}
	
	/**
	 * 相关搜索
	 * @param words
	 * @param type
	 * @param start
	 * @param end
	 * @return
	 */
	public Result<Stat> fuzzyQuery(String words,int type,int start,int end)
	{
        Hits hits = null;
		
        Result<Stat> correctHits = correct(words,type);
        
        if (correctHits.results!=null && correctHits.results.size() == 1)
        	words = ((Stat)correctHits.results.get(0)).keyword;
        
        words = MyUtil.analyzeWord(true,words);
        
        QueryParser parser = new QueryParser("keyword",AnalyzerManager.getBlankAnalyzer());
        parser.setDefaultOperator(QueryParser.OR_OPERATOR);
        
        BooleanQuery bq = new BooleanQuery();
        try {
        	if (type >0)
			{
				Query typeQuery = new TermQuery(new Term("query_type",String.valueOf(type)));
				bq.add(typeQuery,BooleanClause.Occur.MUST);
			}
        	Query query = parser.parse(words);
        	bq.add(query,BooleanClause.Occur.MUST);
        	
        	bq.add(new TermQuery(new Term("query_count_index","1")),BooleanClause.Occur.MUST);//查询量在200以上
        	
        	hits = indexSearcher.search(bq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fuzzyHitsToResult(hits,start,end);
	}
	
	/**
	 * 汇总纠错的结果
	 * @param hits
	 * @param word
	 * @return
	 */
	private Result<Stat> correctHitsToResult(Hits hits,String keyword)
	{
		
//		for (int i=0;i<hits.length();i++){
//			try {
//				System.out.println(hits.doc(i).get("keyword") + "\t" + hits.doc(i).get("query_count"));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		Result<Stat> result = new Result<Stat>();
		
		if (hits == null) return result;

		int size = hits.length();
		
		if (size == 0) return result;
		
		int end = size >5 ? 5 :size;
		
		ArrayList<Stat> list = new ArrayList<Stat>();
		
		try {
			Stat word = documentToStat(hits.doc(0));
			if (word.keyword.equals(keyword))
			{
				return result;
			}
			if (size > 1)
			{
				int i = 1;
				for (;i<end;i++)
				{
					Stat owner = documentToStat(hits.doc(i));
					if (owner.keyword.equals(keyword))
					{
						if (owner.result < 2000)
						{
							if ((word.query_count+0f) / owner.query_count > 1.5){
								list.add(word);
							}
						}
						break;
					}
				}
				if (i == end)//没找到
				{
					if (word.result>100 && word.query_count>500)
						list.add(word);
				}
			}
			else
			{
				list.add(word);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		result.results = list;
		return result;
	}
	private Result<Stat> fuzzyHitsToResult(Hits hits,int start,int end)
	{
		if (hits == null) return new Result<Stat>();
		Result<Stat> result = new Result<Stat>();
		
		int size = hits.length();
		result.totalCount = size;  //存储到结果集
		
		//如果开始位置大于查询总量，直接返回result,结果集为空
		if (start > size)
			return result;
		
		if (size < end) end = size;
		
		ArrayList<Stat> list = new ArrayList<Stat>();
		
		for (int i=start;i<size;i++)
		{
			try {
				float score = hits.score(i);
				if (score < 1f)
				{
					break;
				}
				else
				{
					Stat stat = documentToStat(hits.doc(i));
					stat.score = score;
					list.add(stat);
				}
				//如果超出就返回
				if (list.size() == (end -start))
					break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.results = list;
		return result;
	}
	
	protected Stat documentToStat(Document doc)
	{
		Stat stat = new Stat();
		stat.id = DataFormat.parseInt(doc.get("id"));
		stat.keyword = doc.get("keyword");
		stat.query_type = DataFormat.parseInt(doc.get("query_type"));
		stat.query_count = DataFormat.parseInt(doc.get("query_count"));
		stat.result = DataFormat.parseInt(doc.get("result"));
		return stat;
	}
	
	public int deleteStats(String keyword)
	{
		int row = 0;
		
		QueryParser parser = new QueryParser("keyword",AnalyzerManager.getBlankAnalyzer());
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        
        try {
			Query query = parser.parse(keyword);
			Hits hits = indexSearcher.search(query);
			
			Term[] terms = new Term[hits.length()];
			
			if (hits != null && hits.length() > 0 )
			{
				for (int i=0;i<hits.length();i++)
				{
					terms[i] = new Term("id", hits.doc(i).get("id"));
					indexReader.deleteDocuments(terms[i]);
				}
				indexReader.flush();
			}
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			
		}
		return row;
	}
}
