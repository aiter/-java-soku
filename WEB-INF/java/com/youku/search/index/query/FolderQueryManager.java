/**
 * 
 */
package com.youku.search.index.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.index.entity.AdvanceQuery;
import com.youku.search.index.entity.Folder;
import com.youku.search.index.entity.Result;
import com.youku.search.index.entity.SpecialQuery;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class FolderQueryManager extends BaseQuery{

	public static int DEFAULT_OPERATOR=Constant.Operator.AND; //默认为AND搜索
	
	private String[] fields = new String[]{"folder_name","description","video_title_index"};//检索的字段
	private Map<String,Float> boosts = new HashMap<String,Float>();  //各字段对应的权重
	
	
	public static final Sort SORT_NEW_ASC = new Sort(new SortField("update_time",SortField.LONG,false));
	public static final Sort SORT_NEW_DESC = new Sort(new SortField("update_time",SortField.LONG,true));
	public static final Sort SORT_PV_ASC = new Sort(new SortField("total_pv",SortField.INT,false));
	public static final Sort SORT_PV_DESC = new Sort(new SortField("total_pv",SortField.INT,true));
	public static final Sort SORT_VIDEOCOUNT_ASC = new Sort(new SortField("content_total",SortField.INT,false));
	public static final Sort SORT_VIDEOCOUNT_DESC = new Sort(new SortField("content_total",SortField.INT,true));
	
	private FolderQueryManager(){
		
	}
	private static FolderQueryManager self = null;

	public  static FolderQueryManager getInstance(){
		
		if(self == null){
			self = new FolderQueryManager();
			self.init();
		}
		return self;
	}
	
	public int[] pks = null;
	private synchronized void init(){
		super.indexType = Constant.QueryField.FOLDER;
		indexPath = ServerManager.getFolderIndexPath();
		boosts.put("folder_name",5f);
		boosts.put("description",0.001f);
		boosts.put("video_title_index",0.1f);
		initReader();
		
		try {
			pks = FieldCache.DEFAULT.getInts(indexReader, "pkfolder");
		} catch (IOException e) {
			System.err.println("error:"+e.getMessage());
		}
	}

	public void reopenIndex() throws CorruptIndexException, IOException
	{
		super.reopenIndex();
		pks = FieldCache.DEFAULT.getInts(indexReader, "pkfolder");
	}
	
	/**
	 * 查询
	 * @return
	 */
	
	public Result<Folder> query(com.youku.search.index.entity.Query condition)
	{
		String oldWord = condition.keywords;
		String newWord = condition.keywords;
		
		TopDocs docs = null;
		
		if (condition.needAnalyze){
			newWord = MyUtil.prepareAndAnalyzeWord(condition.keywords);
			
			if (newWord == null || newWord.length()==0)
				return new Result<Folder> ();
		}
		
		QueryParser  parser = new MultiFieldQueryParser (fields,AnalyzerManager.getBlankAnalyzer(),boosts);
        Query query = null;
		try {
			BooleanQuery bq = new BooleanQuery();
			
			if (DEFAULT_OPERATOR == Constant.Operator.AND)
				parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			query = parser.parse(newWord);
			
			QueryParser qparser = new MultiFieldQueryParser (new String[]{"video_title_index"},AnalyzerManager.getBlankAnalyzer(),boosts); 
			if (DEFAULT_OPERATOR == Constant.Operator.AND)
				qparser.setDefaultOperator(QueryParser.AND_OPERATOR);
			Query vQuery = qparser.parse(newWord);
			
			
			BooleanQuery bq2 = new BooleanQuery();
			TermQuery tagQuery = new TermQuery(new Term("tags_index",oldWord));
			
			bq2.add(tagQuery,BooleanClause.Occur.SHOULD);
			bq2.add(query,BooleanClause.Occur.SHOULD);
			
			bq.add(bq2,BooleanClause.Occur.MUST);
			bq.add(vQuery,BooleanClause.Occur.SHOULD);
			
			if (condition.category > 0) //有分类条件
			{
				bq.add(query,BooleanClause.Occur.MUST);
				
				Term term = new Term("cate_ids",String.valueOf(condition.category));
				bq.add(new TermQuery(term),BooleanClause.Occur.MUST);
				
			}
			
			Sort sort = Constant.Sort.getFolderSort(condition.sort,condition.reverse);
			
			YoukuTopDocCollector collector = new YoukuTopDocCollector(condition.end,true,pks);
			if (sort==null){
				 indexSearcher.search(bq,null,collector);
				 docs = collector.topDocs();
			}
			else
				docs = indexSearcher.search(bq,null,condition.end,sort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hitsToResult(docs,condition.start,condition.end);
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
	public Result<Folder> queryTag(String tag,int category,int sort,boolean reverse,int start,int end)
	{
		return queryTag(tag,category,Constant.Sort.getFolderSort(sort,reverse),start,end);
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
	public Result<Folder> queryTag(String tag,int category,Sort sort,int start,int end)
	{
		TopDocs docs = null;
		try {
			BooleanQuery bq = new BooleanQuery();
			
			BooleanQuery bq2 = new BooleanQuery();
			
			String[] tags = MyUtil.toLowerCase(tag).split(" ");
			for (int i = 0;i<tags.length;i++)
			{
				bq2.add( new TermQuery(new Term("tags_index",tags[i])),BooleanClause.Occur.MUST);
			}
			bq.add(bq2,BooleanClause.Occur.MUST);
			
			if (category > 0) //有分类条件
			{
				bq.add(new TermQuery(new Term("cate_ids",String.valueOf(category))),BooleanClause.Occur.MUST);
				
			}
			
			YoukuTopDocCollector collector = new YoukuTopDocCollector(end,true,pks);
			if (sort==null){
				 indexSearcher.search(bq,null,collector);
				 docs = collector.topDocs();
			}
			else
				docs = indexSearcher.search(bq,null,end,sort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hitsToResult(docs,start,end);
	}
	
	public Result<Folder> advanceQuery(AdvanceQuery aQuery)
	{
		TopDocs docs = null;
		BooleanQuery bq = new BooleanQuery();
		
		String[] theFields;
		Map<String,Float> theBoosts = new HashMap<String,Float>();
		
		String keywords = aQuery.keywords;
		//组织查询字段以及boost值
		if (aQuery.fields == null ){
			theFields = fields;
			theBoosts = boosts;
		}
		else
		{
			theFields = aQuery.fields.split(",");
			if (theFields.length ==3 )
			{
				theFields = fields;
				theBoosts = boosts;
			}
			else
			{
				for (int i=0;i<theFields.length ;i++)
				{
					theBoosts.put(theFields[i],boosts.get(theFields[i]));
				}
			}
		}
		//开始组织query
//		System.out.println("分解后："+keywords);
		try
		{
			if (keywords != null && keywords.length() > 0){
				if (theFields.length == 1 && theFields[0].equals("tags_index")) //只有tag搜索
				{
					keywords = MyUtil.parseSyntax(keywords,false);
				}
				else
				{
					keywords = MyUtil.parseSyntax(keywords);
				}
				
				QueryParser parser = new MultiFieldQueryParser (theFields,AnalyzerManager.getBlankAnalyzer(),theBoosts);
				Query q = parser.parse(keywords);
				bq.add(q,BooleanClause.Occur.MUST);
			}
			else
				return new Result<Folder> ();
			
//			if (arr[1] != null && arr[1].length() > 0){
//				Query notQuery = null;
//				QueryParser parser = new QueryParser ("f_single_analyzed",AnalyzerManager.getBlankAnalyzer());
//				notQuery = parser.parse(arr[1]);
//				bq.add(notQuery,BooleanClause.Occur.MUST_NOT);
//			}
			//分类条件
			if (aQuery.categories != null && aQuery.categories.length() > 0)
			{
				QueryParser cateParser = new QueryParser("cate_ids",AnalyzerManager.getBlankAnalyzer());
				cateParser.setDefaultOperator(QueryParser.OR_OPERATOR);
				Query cateQuery = cateParser.parse(aQuery.categories.replaceAll(","," "));
				bq.add(cateQuery,BooleanClause.Occur.MUST);
			}
			//上传时间
			if (aQuery.limitDate !=null && !aQuery.limitDate.isEmpty())
			{
				
				Date startTime  = null;
				Date endTime = null;
				Date now = new Date();
				
				String[] arr = aQuery.limitDate.split("-");
				if (arr.length == 2)
				{
					int end = DataFormat.parseInt(arr[0]);
					int start = DataFormat.parseInt(arr[1]);
					startTime = DataFormat.getNextDate(now,0-start);
					endTime = DataFormat.getNextDate(now,0-end);
				}
				else
				{
					int start = DataFormat.parseInt(aQuery.limitDate);
					startTime = DataFormat.getNextDate(now,0-start);
				}
				
				RangeQuery timeQuery = new RangeQuery(
						startTime!= null ?new Term("create_day",DataFormat.formatDate(startTime,DataFormat.FMT_DATE_SPECIAL)):null,
						endTime!= null ?new Term("create_day",DataFormat.formatDate(endTime,DataFormat.FMT_DATE_SPECIAL)):null, 
						true); 
				
				bq.add(timeQuery,BooleanClause.Occur.MUST);
			}
			
			if (aQuery.pv > 0)
			{
				TermQuery query = new TermQuery(new Term("pv_index",String.valueOf(aQuery.pv)));
				bq.add(query,BooleanClause.Occur.MUST);
			}
			
			
			Sort sort = Constant.Sort.getFolderSort(aQuery.sort,aQuery.reverse);
			
			YoukuTopDocCollector collector = new YoukuTopDocCollector(aQuery.end,true,pks);
			if (sort==null){
				 indexSearcher.search(bq,null,collector);
				 docs = collector.topDocs();
			}
			else
				docs = indexSearcher.search(bq,null,aQuery.end,sort);
			
//			System.out.println(bq);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return hitsToResult(docs,aQuery.start,aQuery.end);
	}
	
	public Result<Folder>  queryTitleAndTag(SpecialQuery condition)
	{
		String title = MyUtil.prepareAndAnalyzeWord(condition.title);
		String[] tags = null;
		if (condition.tags!=null)
			tags = condition.tags.split(",");
			
		if ((title == null || title.length()==0) && (tags==null || tags.length ==0))
			return new Result<Folder> ();
		
		BooleanQuery bq = new BooleanQuery();
		TopDocs docs = null;
		try {
			if (title != null && title.length() > 0){
				Query titleQuery = null;
				QueryParser  parser = new QueryParser ("folder_name",AnalyzerManager.getBlankAnalyzer());
//				parser.setDefaultOperator(Constant.Operator.getOperator(operator));
				titleQuery = parser.parse(title);
				bq.add(titleQuery,BooleanClause.Occur.SHOULD);
			}
			
			if (tags != null){
				BooleanQuery tagQuery = new BooleanQuery();
				for(String tag:tags){
					TermQuery q = new TermQuery(new Term("tags_index",tag));
					tagQuery.add(q,BooleanClause.Occur.SHOULD);
				}
				bq.add(tagQuery,BooleanClause.Occur.SHOULD);
			}
			
			Sort s = Constant.Sort.getFolderSort(condition.sort,condition.reverse);
			
			if (s==null){
				docs = indexSearcher.search(bq,null,condition.end);
			}
			else
				docs = indexSearcher.search(bq,null,condition.end,s);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hitsToResult(docs,condition.start,condition.end);
	}
	
	
	private Result<Folder> hitsToResult(TopDocs docs,int start,int end)
	{
		if (docs == null) return new Result<Folder> ();
		Result<Folder> result = new Result<Folder> ();
		
		int size = docs.totalHits;
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
		
		ArrayList<Folder> list = new ArrayList<Folder>();
		ScoreDoc[] scoreDoc = docs.scoreDocs;
		for (int i=start;i<end;i++)
		{
			try {
				Folder folder = documentToFolder(scoreDoc[i]);
				folder.score = scoreDoc[i].score;
				list.add(folder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.results = list;
		return result;
	}
	
	protected Folder documentToFolder(ScoreDoc scoreDoc) throws CorruptIndexException, IOException
	{
		Document doc =  indexReader.document(scoreDoc.doc);
		Folder folder = new Folder();
		folder.pk_folder = DataFormat.parseInt(doc.get("pkfolder"));
		folder.title = doc.get("folder_name_store");
		folder.owner = DataFormat.parseInt(doc.get("fk_owner"));
		folder.owner_name = doc.get("user_name");
		folder.cate_ids = DataFormat.parseInt(doc.get("cate_ids"));
		folder.update_time = DataFormat.parseLong(doc.get("update_time"));
		folder.video_count = DataFormat.parseInt(doc.get("content_total"));
		folder.total_pv = DataFormat.parseInt(doc.get("total_pv"));
		folder.logo = doc.get("logo");
		folder.total_seconds = doc.get("total_seconds");
		folder.total_comment = DataFormat.parseInt(doc.get("total_comment"));

		String[] video_order_no = null;
		String[] video_title = null;
		String[] video_seconds = null;
		String[] video_md5 = null;
		
		String video_title1=doc.get("video_title1");
		String video_title2=doc.get("video_title2");
		
		if (video_title1 != null){
			if (video_title2 == null){
				video_title = new String[]{video_title1};
				video_seconds = new String[]{doc.get("video_seconds1")};
				video_order_no = new String[]{doc.get("order_no1")};
				video_md5 = new String[]{doc.get("md51")};
			}
			else{
				String video_title3=doc.get("video_title3");
				if (video_title3 ==null){
					video_title = new String[]{video_title1,video_title2};
					video_seconds = new String[]{doc.get("video_seconds1"),doc.get("video_seconds2")};
					video_order_no = new String[]{doc.get("order_no1"),doc.get("order_no2")};
					video_md5 = new String[]{doc.get("md51"),doc.get("md52")};
				}
				else{
					video_title = new String[]{video_title1,video_title2,video_title3};
					video_seconds = new String[]{doc.get("video_seconds1"),doc.get("video_seconds2"),doc.get("video_seconds3")};
					video_order_no = new String[]{doc.get("order_no1"),doc.get("order_no2"),doc.get("order_no3")};
					video_md5 = new String[]{doc.get("md51"),doc.get("md52"),doc.get("md53")};
				}
			}
		}
		
		folder.video_order_no = video_order_no;
		folder.video_title = video_title;
		folder.video_seconds = video_seconds;
		folder.video_md5 = video_md5;
		folder.tags = doc.get("tags");
		
		return folder;
	}
	
	
	
	
	
	public int deleteFolder(int folder_id)
	{
		int row = 0;
		Term term = new Term("pkfolder", String.valueOf(folder_id));
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
	
	public void deleteFolders(List<Integer> folder_ids)
	{
		Term[] terms = new Term[folder_ids.size()];
		for (int i =0;i<folder_ids.size();i++)
		{
			terms[i] = new Term("pkfolder", String.valueOf(folder_ids.get(i)));
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
