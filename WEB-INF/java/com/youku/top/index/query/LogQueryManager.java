/**
 * 
 */
package com.youku.top.index.query;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.top.MyUtil;
import com.youku.top.Constant;
import com.youku.top.index.analyzer.AnalyzerManager;
import com.youku.top.index.server.ServerManager;

public class LogQueryManager extends BaseQuery{
	private LogQueryManager(){
		
	}
	public  LogQueryManager(String path,String strDate){
		init(path,strDate);
	}
	
	private void init(String path,String strDate){
		super.indexType = Constant.QueryField.LOG;
		if(StringUtils.isBlank(path)){
			if(StringUtils.isBlank(strDate)){
				Date date = DataFormat.getNextDate(new Date(),-1); //默认为昨天
				strDate = DataFormat.formatDate(date,DataFormat.FMT_DATE_YYYY_MM_DD);
			}
			indexPath = ServerManager.getLogIndexPath()+strDate;
		}else 
			indexPath = path;
			initReader();
	}
	
	public static final Sort SORT_QureyCount = new Sort(new SortField("query_count",SortField.INT,true));
	public static final Sort SORT_ResultCount = new Sort(new SortField("result",SortField.INT,true));
	
	public JSONArray query(String keyword){
		return query(keyword,0);
	}
	
	public JSONArray query(String keyword,int limit){
		String kword = MyUtil.analyzeWord(true,keyword);
		QueryParser parser = new QueryParser("keyword",AnalyzerManager.getStandardAnalyzer());
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = null;
		try {
			query = parser.parse(kword);
		} catch (Exception e1) {
			return new JSONArray();
		}
		BooleanQuery bq = new BooleanQuery();
		bq.add(query,BooleanClause.Occur.MUST);
		Sort s = SORT_QureyCount;
		Hits hits = null;
		try {
			hits = indexSearcher.search(bq,s);
		} catch (Exception e1) {
			return new JSONArray();
		}
		JSONArray terms = new JSONArray();
		if (hits!= null){
		        Document doc = null;
		        JSONObject term = null;
		        int hitSize = hits.length();
		        int size = limit==0?hitSize:(hitSize>limit?limit:hitSize);
		        for (int j=0;j<size;j++){
		                term = new JSONObject();
		                try {
		                        doc = hits.doc(j);
		                        String word = doc.get("keyword");
		                        String query_count = doc.get("query_count");
		                        String result = doc.get("result");
		                        term.append("keyword",word);
		                        term.append("query_count",query_count);
		                        term.append("result",result);
		                        terms.put(term);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
		        }
		}
		return terms;
	}
}
