/**
 * 
 */
package com.youku.top.index.query;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
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
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.top.Constant;
import com.youku.top.index.analyzer.AnalyzerManager;
import com.youku.top.index.server.ServerManager;

public class LogQuickQueryManager extends BaseQuery {
	private LogQuickQueryManager() {

	}

	private static LogQuickQueryManager self = null;

	public static LogQuickQueryManager getInstance() {

		if (self == null) {
			self = new LogQuickQueryManager();
			self.init(null);
		}

		return self;
	}
	
	public static LogQuickQueryManager getInstance(String date) {

		if (self == null) {
			self = new LogQuickQueryManager();
			self.init(date);
		}

		return self;
	}

	public static LogQuickQueryManager newInstance() {
		self = new LogQuickQueryManager();
		self.init(null);
		return self;
	}
	
	public static LogQuickQueryManager newInstance(String date) {
		self = new LogQuickQueryManager();
		self.init(date);
		return self;
	}

	private synchronized void init(String strDate) {
		super.indexType = Constant.QueryField.LOG;
		if(StringUtils.isBlank(strDate)){
			Date date = DataFormat.getNextDate(new Date(), -1); // 默认为昨天
			strDate = DataFormat.formatDate(date,
				DataFormat.FMT_DATE_YYYY_MM_DD);
		}
		indexPath = ServerManager.getLogQuickIndexPath() + strDate;
		reopenIndex();
	}

	public static final Sort SORT_QureyCount = new Sort(new SortField(
			"query_count", SortField.INT, true));
	public static final Sort SORT_WeightRate = new Sort(new SortField(
			"quick_top_rate", SortField.DOUBLE, true));

	public JSONArray doQuery(String keyword, boolean isLike, boolean isQuick,
			int limit) {
		// String kword = MyUtil.analyzeWord(false,keyword);
		QueryParser parser = new QueryParser("keyword", AnalyzerManager
				.getMyAnalyzer(false));
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = null;
		try {
			query = parser.parse(keyword);
		} catch (Exception e1) {
			return new JSONArray();
		}
		BooleanQuery bq = new BooleanQuery();
		bq.add(query, BooleanClause.Occur.MUST);
		Sort s = SORT_QureyCount;
		if (isQuick) {
//			Query rateQuery = new TermQuery(new Term("quick_top_rate_index",
//					"1"));
//			bq.add(rateQuery, BooleanClause.Occur.MUST);
			Query countQuery = new TermQuery(new Term("query_count_index", "1"));
			bq.add(countQuery, BooleanClause.Occur.MUST);
			s = SORT_WeightRate;
		}
		Hits hits = null;
		try {
			hits = indexSearcher.search(bq, s);
		} catch (Exception e1) {
			return new JSONArray();
		}
		JSONArray terms = new JSONArray();
		if (hits != null) {
			Document doc = null;
			JSONObject term = null;
			int hitSize = hits.length();
			int size = limit == 0 ? hitSize : (hitSize > limit ? limit
					: hitSize);
			int b = 0;
			for (int j = 0; j < hitSize; j++) {
				if(b>= size) break;
				term = new JSONObject();
				try {
					doc = hits.doc(j);
					String word = doc.get("keyword");
					if (isLike && word.equalsIgnoreCase(keyword))
						continue;
					b = b+1;
					String query_count = doc.get("query_count");
					String result = doc.get("result");
					term.append("keyword", word);
					term.append("query_count", query_count);
					term.append("result", result);
					if (isQuick) {
						String rate = doc.get("quick_top_rate");
						term.append("rate_weight", rate);
					}
					terms.put(term);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return terms;
	}

	public JSONObject query(String keyword, boolean isLike, boolean isQuick,
			int limit) {
		JSONObject json = new JSONObject();
		if (!isLike && !isQuick)
			return json;
		try {
			if (isLike && isQuick) {
				json.put("like", doQuery(keyword, true, false, limit));
				json.put("quick", doQuery(keyword, false, true, limit));
			} else {
				String key = isQuick ? "quick" : "like";
				json.put(key, doQuery(keyword, isLike, isQuick, limit));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;

	}

	public JSONArray doQuery(int limit) {
		BooleanQuery bq = new BooleanQuery();
		Sort s = SORT_QureyCount;
		Hits hits = null;
		try {
			hits = indexSearcher.search(bq, s);
		} catch (Exception e1) {
			return new JSONArray();
		}
		JSONArray terms = new JSONArray();
		if (hits != null) {
			Document doc = null;
			JSONObject term = null;
			int hitSize = hits.length();
			int size = limit == 0 ? hitSize : (hitSize > limit ? limit
					: hitSize);
			for (int j = 0; j < size; j++) {
				term = new JSONObject();
				try {
					doc = hits.doc(j);
					String word = doc.get("keyword");
					String query_count = doc.get("query_count");
					term.append("keyword", word);
					term.append("query_count", query_count);
					terms.put(term);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return terms;
	}
	
	public JSONArray doQuickQuery(int limit) {
		BooleanQuery bq = new BooleanQuery();
		Query rateQuery = new TermQuery(new Term("quick_top_rate_index",
		"1"));
		bq.add(rateQuery, BooleanClause.Occur.MUST);
		Query countQuery = new TermQuery(new Term("query_count_index", "1"));
		bq.add(countQuery, BooleanClause.Occur.MUST);
		Sort s = SORT_WeightRate;
		Hits hits = null;
		try {
			hits = indexSearcher.search(bq, s);
		} catch (Exception e1) {
			return new JSONArray();
		}
		JSONArray terms = new JSONArray();
		if (hits != null) {
			Document doc = null;
			JSONObject term = null;
			int hitSize = hits.length();
			int size = limit == 0 ? hitSize : (hitSize > limit ? limit
					: hitSize);
			for (int j = 0; j < size; j++) {
				term = new JSONObject();
				try {
					doc = hits.doc(j);
					String word = doc.get("keyword");
					String query_count = doc.get("query_count");
					String result = doc.get("result");
					String rate = doc.get("quick_top_rate");
					term.append("keyword", word);
					term.append("query_count", query_count);
					term.append("result", result);
					term.append("rate_weight", rate);
					terms.put(term);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return terms;
	}
}
