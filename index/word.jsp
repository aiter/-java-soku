<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				org.apache.lucene.search.*,
				com.youku.search.index.server.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.config.*,
				org.apache.lucene.search.IndexSearcher,
				org.apache.lucene.queryParser.QueryParser,
				com.youku.search.analyzer.AnalyzerManager,
				org.apache.lucene.document.Document,
				org.apache.lucene.index.Term,
				org.json.*"%>
<%
String word =  (String)request.getParameter("word");
word = MyUtil.analyzeWord(true,word);
 StatQueryManager statQueryManager = SearchManager.getInstance().getStatQueryManager();
IndexSearcher indexSearcher = statQueryManager.getIndexSearcher();
QueryParser parser = new QueryParser("keyword",AnalyzerManager.getBlankAnalyzer());
parser.setDefaultOperator(QueryParser.AND_OPERATOR);
Query query = parser.parse(word);

BooleanQuery bq = new BooleanQuery();
Query typeQuery = new TermQuery(new Term("query_type","1"));//视频
bq.add(typeQuery,BooleanClause.Occur.MUST);
bq.add(query,BooleanClause.Occur.MUST);

//SORT_QureyCount 按查询数倒序排序
//SORT_ResultCount 按结果数倒序排序

Hits hits = indexSearcher.search(bq,statQueryManager.SORT_QureyCount);

if (hits!= null)
{
	JSONObject root = new JSONObject();
	JSONArray terms = new JSONArray();
	for (int j=0;j<hits.length();j++){
		JSONObject term = new JSONObject();
		Document doc;
		try {
			doc = hits.doc(j);
			String keyword = doc.get("keyword");
			String query_count = doc.get("query_count");
			String result = doc.get("result");
			
			term.append("keyword",keyword);
			term.append("query_count",query_count);
			term.append("result",result);
			terms.put(term);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	root.put("terms",terms);
	out.clear();
	out.print(root);
}
%>
