/**
 * 
 */
package com.youku.soku.index.query;

import java.util.ArrayList;

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
import com.youku.search.index.entity.Result;
import com.youku.search.index.entity.Stat;
import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.index.analyzer.AnalyzerManager;
import com.youku.soku.index.entity.Word;
import com.youku.soku.util.Constant;
import com.youku.soku.util.MyUtil;

/**
 * @author william
 *
 */
public class WordQueryManager extends BaseQuery{
	private WordQueryManager(){
		
	}
	private static WordQueryManager self = null;

	public  static WordQueryManager getInstance(){
		
		if(self == null){
			self = new WordQueryManager();
			self.init();
		}
		return self;
	}
	private synchronized void init(){
		indexType = Constant.QueryField.CORRECT;
		indexPath = Config.getWordIndexPath();
		initReader();
	}
	
	
	public static final Sort SORT_QureyCount = new Sort(new SortField("query_count",SortField.INT,true));
	public static final Sort SORT_ResultCount = new Sort(new SortField("result",SortField.INT,true));
	
	/**
	 * 纠错
	 * @param words
	 * @param type
	 * @return
	 */
	public Result<Word> correct(String keywords)
	{
		String newWord = CorrectorManager.getCorrector().correct(keywords);
		if (newWord != null){ 	//纠错词表直接找到，直接返回
			Word word = new Word();
			word.keyword = newWord;
			
			ArrayList<Word> words = new ArrayList<Word>();
			words.add(word);
			
			Result<Word> result = new Result<Word>();
			result.results = words;
			result.totalCount = 1;
			return result;
		}
		
        Hits hits = null;
		
		try {
			Query query = new TermQuery(new Term("keyword_py_index",MyUtil.formatFuzzyLetter(com.youku.search.hanyupinyin.Converter.convert(keywords,true))));
			hits = indexSearcher.search(query,SORT_QureyCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return correctHitsToResult(hits,keywords);
	}
	
	/**
	 * 相关搜索
	 * @param words
	 * @param type
	 * @param start
	 * @param end
	 * @return
	 * @deprecated 有代码临时使用，上线后更正
	 */
	public Result<Word> fuzzyQuery(String words,int start,int end)
	{
        Hits hits = null;
		
        Result<Word> correctHits = correct(words);
        
        if (correctHits.results!=null && correctHits.results.size() == 1)
        	words = ((Word)correctHits.results.get(0)).keyword;
        
        words = MyUtil.analyzeWord(true,words);
        
        QueryParser parser = new QueryParser("keyword",AnalyzerManager.getBlankAnalyzer());
        parser.setDefaultOperator(QueryParser.OR_OPERATOR);
        
        BooleanQuery bq = new BooleanQuery();
        try {
        	Query query = parser.parse(words);
        	bq.add(query,BooleanClause.Occur.MUST);
        	
        	bq.add(new TermQuery(new Term("query_count_index","1")),BooleanClause.Occur.MUST);//查询量在10以上
        	
        	hits = indexSearcher.search(bq,SORT_QureyCount);
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
	private Result<Word> correctHitsToResult(Hits hits,String keyword)
	{
//		for (int i=0;i<hits.length();i++){
//		try {
//			System.out.println(hits.doc(i).get("keyword") + "\t" + hits.doc(i).get("query_count"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//		
		Result<Word> result = new Result<Word>();
		
		if (hits == null) return result;

		int size = hits.length();
		if (size == 0) return result;
		int end = size >5 ? 5 :size;
		
		ArrayList<Word> list = new ArrayList<Word>();
		
		try {
			Word word = documentToStat(hits.doc(0));
			
			if (word.keyword.equals(keyword))
			{
				return result;
			}
			if (size > 1)
			{
				int i = 1;
				for (;i<end;i++)
				{
					Word owner = documentToStat(hits.doc(i));
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
	private Result<Word> fuzzyHitsToResult(Hits hits,int start,int end)
	{
		if (hits == null) return new Result<Word>();
		Result<Word> result = new Result<Word>();
		
		int size = hits.length();
		result.totalCount = size;  //存储到结果集
		
		//如果开始位置大于查询总量，直接返回result,结果集为空
		if (start > size)
			return result;
		
		if (size < end) end = size;
		
		ArrayList<Word> list = new ArrayList<Word>();
		
		for (int i=start;i<size;i++)
		{
			try {
				float score = hits.score(i);
				if (score < 0.8f)
				{
					break;
				}
				else
				{
					Word word = documentToStat(hits.doc(i));
					word.score = score;
					list.add(word);
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
	
	protected Word documentToStat(Document doc)
	{
		Word word = new Word();
		word.id = DataFormat.parseInt(doc.get("id"));
		word.keyword = doc.get("keyword");
		word.query_type = DataFormat.parseInt(doc.get("query_type"));
		word.query_count = DataFormat.parseInt(doc.get("query_count"));
		word.result = DataFormat.parseInt(doc.get("result"));
		return word;
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
