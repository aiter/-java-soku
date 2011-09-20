<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.index.boost.*,
				com.youku.search.*
				com.youku.search.analyzer.*
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				java.sql.*,
				java.util.*,
				org.apache.lucene.analysis.*,
				org.apache.lucene.index.*,
				java.io.*,
				org.apache.lucene.queryParser.*,
				org.apache.lucene.search.*,
				org.apache.lucene.document.Document"%>
<%
Result result = null;
long start=0 ,end = 0;
com.youku.search.index.entity.Query query = null;
String keywords = null;


if (request.getParameter("keywords") != null)
{
	int sort = DataFormat.parseInt(request.getParameter("sort"));
	keywords = (String)request.getParameter("keywords");
	query = new com.youku.search.index.entity.Query();
	query.field=DataFormat.parseInt(request.getParameter("field"));
	query.keywords = keywords;
	query.start=0;
	query.end=10;
	
	query.sort=sort;
	int needAnalyze = DataFormat.parseInt(request.getParameter("needAnalyze"));
	if (needAnalyze == 0)
		query.needAnalyze = false;
	start = System.currentTimeMillis();

	if (query.field == 15 || query.field == 16)
	{
		query.category=1;
	}
	
	//result = (Result)SearchManager.getInstance().processQuery(query);
}
	end = System.currentTimeMillis();
%>
视频索引数：<%=VideoQueryManager.getInstance().getIndexReader()!=null?VideoQueryManager.getInstance().getIndexReader().numDocs()+"":""%><br/>

<form name="" action="" method="get">
搜索关键字：<input type="text" name="keywords">
排序：<input type="text" name="sort" size=4>（不填按相关度）<br>
<select name="field">
<option value="1">搜视频</option>
<option value="8">搜视频TAG</option>
<option value="2">搜专辑</option>
<option value="3">搜会员</option>
<option value="4">搜看吧</option>
<option value="5">按标题搜帖子</option>
<option value="6">按作者搜帖子</option>
<option value="7">搜PK</option>
<option value="15">智能纠错</option>
<option value="16">相关搜索</option>
<option value="17">搜索铃声</option>
</select>
<br>
<input type="checkbox" value="1" name="needAnalyze" checked>需要分词
<input type="submit" value="提交">
</form>

<br>
<%=query!=null?query.toString():""%>
<br>
<%
if (request.getParameter("keywords") != null)
{
	if (query.field == 1 || query.field == 8)
	{

		 BooleanQuery bq = new BooleanQuery();
			String newWord = WordProcessor.formatVideoQueryString(keywords);
			newWord = MyUtil.prepareAndAnalyzeWord(newWord);//词语后加空格再分词 修复词语歧义问题
			
			QueryParser  titleparser = new QueryParser ("title",AnalyzerManager.getBlankAnalyzer());
			titleparser.setDefaultOperator(QueryParser.AND_OPERATOR);
			org.apache.lucene.search.Query titlequery = titleparser.parse(newWord);
			
			QueryParser  memoparser = new QueryParser ("memo",AnalyzerManager.getBlankAnalyzer());
			memoparser.setDefaultOperator(QueryParser.AND_OPERATOR);
			org.apache.lucene.search.Query memoquery = memoparser.parse(newWord);
			
			TermQuery tagQuery = new TermQuery(new Term("tags_index",keywords));
			TermQuery userQuery = new TermQuery(new Term("owner_username",keywords));
			
			BooleanQuery bq2 = new BooleanQuery();
			
			titlequery.setBoost(5f);
			tagQuery.setBoost(0.000001f);
			userQuery.setBoost(0.0000000001f);
			memoquery.setBoost(0.0000000001f);
			
			
			
			bq2.add(titlequery,BooleanClause.Occur.SHOULD);
			bq2.add(memoquery,BooleanClause.Occur.SHOULD);
			bq2.add(tagQuery,BooleanClause.Occur.SHOULD);
			bq2.add(userQuery,BooleanClause.Occur.SHOULD);
			
			bq.add(bq2,BooleanClause.Occur.MUST);
		
		
		
		Hits hits = VideoQueryManager.getInstance().getIndexSearcher().search(bq);
			
		int len = 50;
		if (hits.length() <= 50) len = hits.length();

			for (int i =0;i<len;i++)
			{
				Document doc = hits.doc(i);
				out.println("vid="+doc.get("vid")+"    title:"+hits.doc(i).get("title_store")+"<br>");
				int docid=hits.id(i);
				float boost = BoostReader.getBoost(docid);
				out.println("docid="+docid+"<br>");
				out.println("total score:"+hits.score(i)+"=lucene("+ (hits.score(i)-boost) +")+boost("+boost+")"+"<br>");
				out.println(VideoQueryManager.getInstance().getIndexSearcher().explain(bq,hits.id(i))+"<br>");
			}
	}
	else if (query.field == 2)
	{
		String[] fields = new String[]{"folder_name","description"};//检索的字段
		Map<String,Float> boosts = new HashMap<String,Float>();  //各字段对应的权重
		boosts.put("folder_name",5f);
		boosts.put("description",0.0001f);

		String oldWords = query.keywords;
		keywords = AnalyzerManager.analyzeWord(query.keywords);

		QueryParser  parser = new MultiFieldQueryParser (fields,AnalyzerManager.getBlankAnalyzer(),boosts);
		org.apache.lucene.search.Query q = null;
		Hits hits = null;
		BooleanQuery bq = new BooleanQuery();
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		q = parser.parse(keywords);
		
		BooleanQuery bq2 = new BooleanQuery();
		TermQuery tagQuery = new TermQuery(new Term("tags_index",oldWords));
		bq2.add(tagQuery,BooleanClause.Occur.SHOULD);
		bq2.add(q,BooleanClause.Occur.SHOULD);
		
		bq.add(bq2,BooleanClause.Occur.MUST);
			
		System.out.println(bq);
		hits = FolderQueryManager.getInstance().getIndexSearcher().search(bq);
		
		out.println("返回结果集："+hits.length() + "<br>");

		int len = 50;
		if (hits.length() <= 50) len = hits.length();

			for (int i =0;i<len;i++)
			{
				Document doc = hits.doc(i);
				out.println("pkfolder="+doc.get("pkfolder")+"    title:"+hits.doc(i).get("folder_name_store")+"<br>");
				int docid=hits.id(i);
				float boost = BoostReader.getBoost(docid);
				out.println("docid="+docid+"<br>");
				out.println("total score:"+hits.score(i)+"=lucene("+ (hits.score(i)-boost) +")+boost("+boost+")"+"<br><br>");
				//out.println(FolderQueryManager.getInstance().getIndexSearcher().explain(bq,hits.id(i))+"<br>");
			}
	}
}
%>