/**
 * 
 */
package com.youku.search.index.query;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositionVector;
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
import org.apache.lucene.search.YoukuTopFieldDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.analyzer.WordProcessor;
import com.youku.search.config.Config;
import com.youku.search.index.boost.BoostReader;
import com.youku.search.index.entity.AdvanceQuery;
import com.youku.search.index.entity.Result;
import com.youku.search.index.entity.Video;
import com.youku.search.index.query.parser.ParserFactory;
import com.youku.search.index.query.parser.ParserResult;
import com.youku.search.index.query.parser.VideoParser;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class VideoQueryManager  extends BaseQuery{

	public static int DEFAULT_OPERATOR=Constant.Operator.AND; //默认为AND搜索
	
	private String[] fields = new String[]{"title"};//检索的字段
	private String[] fullFields = new String[]{"title","memo","owner_username_index"};//检索的字段
	private Map<String,Float> boosts = new HashMap<String,Float>();  //各字段对应的权重
	
	private static List<String> keywordsList = null;
	
	public static final Sort SORT_NEW_ASC = new Sort(new SortField("createtime",SortField.LONG,false));
	public static final Sort SORT_NEW_DESC = new Sort(new SortField("createtime",SortField.LONG,true));
	public static final Sort SORT_PV_ASC = new Sort(new SortField("total_pv",SortField.INT,false));
	public static final Sort SORT_PV_DESC = new Sort(new SortField("total_pv",SortField.INT,true));
	public static final Sort SORT_COMMENT_ASC = new Sort(new SortField("total_comment",SortField.INT,false));
	public static final Sort SORT_COMMENT_DESC = new Sort(new SortField("total_comment",SortField.INT,true));
	public static final Sort SORT_FAV_ASC = new Sort(new SortField("total_fav",SortField.INT,false));
	public static final Sort SORT_FAV_DESC = new Sort(new SortField("total_fav",SortField.INT,true));
	
	private VideoQueryManager(){
		
	}
	private static VideoQueryManager self = null;

	public  static VideoQueryManager getInstance(){
		
		if(self == null){
			self = new VideoQueryManager();
			self.init();
		}
		return self;
	}
	public HashMap<Integer,Video> videoMap =  new HashMap<Integer,Video>();
	
	public int[] vids = null;
	float[] seconds = null;
	
	private synchronized void init(){
		super.indexType = Constant.QueryField.VIDEO;
		indexPath = ServerManager.getVideoIndexPath();
		boosts.put("title",5f);
		boosts.put("memo",0.00000001f);
		boosts.put("owner_username_index",0.00000001f);
		initReader();
		try {
			vids = FieldCache.DEFAULT.getInts(indexReader, "vid");
			seconds = FieldCache.DEFAULT.getFloats(indexReader, "seconds");
			
			
		} catch (IOException e) {
			System.err.println("error:"+e.getMessage());
		}
//		
//		for (int i=0;i<vids.length;i++)
//		{
//			
//			Object o = MemCached.cacheGet(String.valueOf(vids[i]));
//			
//			if (o!=null){
//				System.out.println(i + "\t" + vids[i] );
//				videoMap.put(vids[i], (Video)o);
//			}
//		}
	}
	
	
	public void reopenIndex() throws CorruptIndexException, IOException
	{
		super.reopenIndex();
		vids = FieldCache.DEFAULT.getInts(indexReader, "vid");
		seconds = FieldCache.DEFAULT.getFloats(indexReader, "seconds");
	}
	/**
	 * 搜索，在title,tags,memo中搜索
	 * @param words 已经分好词的word
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result<Video> query(com.youku.search.index.entity.Query condition)
	{
		String newWord = condition.keywords;
		
		
        TopDocs docs = null;
        BooleanQuery bq = new BooleanQuery();
		try {
			if (newWord != null && !newWord.isEmpty())
			{
				if (condition.needAnalyze){
					ParserResult result = ParserFactory.getVideoParser().parse(newWord);
					
					//如果有指定范围搜索
					if (result != null)
					{
						newWord = result.getKeyword();
						
						if (result.contains(VideoParser.TITLE)){
							String wordInTitle = WordProcessor.formatVideoQueryString(result.getValue(VideoParser.TITLE));
							wordInTitle = MyUtil.prepareAndAnalyzeWord(wordInTitle);
							
							QueryParser  titleparser = new QueryParser ("title",AnalyzerManager.getBlankAnalyzer());
							titleparser.setDefaultOperator(Constant.Operator.getOperator(condition.operator));

							Query titlequery = titleparser.parse(wordInTitle);
							titlequery.setBoost(5f);
							bq.add(titlequery,BooleanClause.Occur.MUST);
						}
						
						if (result.contains(VideoParser.TAG)){
							String wordInTag = result.getValue(VideoParser.TAG);
							
							TermQuery tagQuery = new TermQuery(new Term("tags_index",wordInTag));

							tagQuery.setBoost(0.0000001f);
							bq.add(tagQuery,BooleanClause.Occur.MUST);
						}
						
						if (result.contains(VideoParser.MEMO)){
							String wordInMemo = result.getValue(VideoParser.MEMO);
							
							QueryParser  memoparser = new QueryParser ("memo",AnalyzerManager.getBlankAnalyzer());
							memoparser.setDefaultOperator(Constant.Operator.getOperator(condition.operator));
							Query memoquery = memoparser.parse(wordInMemo);

							memoquery.setBoost(0.0000000001f);
							bq.add(memoquery,BooleanClause.Occur.MUST);
						}
						
						if (result.contains(VideoParser.USER)){
							String wordInUserName = result.getValue(VideoParser.USER);
							
							TermQuery userQuery = new TermQuery(new Term("owner_username_index",MyUtil.toLowerCase(wordInUserName)));

							userQuery.setBoost(0.0000001f);
							bq.add(userQuery,BooleanClause.Occur.MUST);
						}
					}
					
					if (newWord != null)
					{
						BooleanQuery bq2 = new BooleanQuery();
						
						newWord = WordProcessor.formatVideoQueryString(newWord);
						newWord = MyUtil.prepareAndAnalyzeWord(newWord);
						
						QueryParser  titleparser = new QueryParser ("title",AnalyzerManager.getBlankAnalyzer());
						titleparser.setDefaultOperator(Constant.Operator.getOperator(condition.operator));
						Query titlequery = titleparser.parse(newWord);
						
						QueryParser  memoparser = new QueryParser ("memo",AnalyzerManager.getBlankAnalyzer());
						memoparser.setDefaultOperator(Constant.Operator.getOperator(condition.operator));
						Query memoquery = memoparser.parse(newWord);
						
						String oldWord = result == null ? condition.keywords : result.getKeyword() != null ? result.getKeyword() :  null;
						
						if (oldWord != null){
							TermQuery tagQuery = new TermQuery(new Term("tags_index",oldWord));
							TermQuery userQuery = new TermQuery(new Term("owner_username_index",MyUtil.toLowerCase(oldWord)));
							
							tagQuery.setBoost(0.0000001f);
							userQuery.setBoost(0.0000000001f);
							bq2.add(tagQuery,BooleanClause.Occur.SHOULD);
							bq2.add(userQuery,BooleanClause.Occur.SHOULD);
						}
						
						titlequery.setBoost(5f);
						memoquery.setBoost(0.0000000001f);
						
						bq2.add(titlequery,BooleanClause.Occur.SHOULD);
						bq2.add(memoquery,BooleanClause.Occur.SHOULD);
						
						
						bq.add(bq2,BooleanClause.Occur.MUST);
					}
					
				}
			}
			
			
			if (condition.category > 0)
			{
				Term term = new Term("categories",String.valueOf(condition.category));
				TermQuery cateQuery = new TermQuery(term);
				cateQuery.setBoost(0.000000001f);
				bq.add(cateQuery,BooleanClause.Occur.MUST);
			}
			if (condition.categories != null && condition.categories.length() > 0)
			{
				QueryParser cateParser = new QueryParser("categories",AnalyzerManager.getBlankAnalyzer());
				cateParser.setDefaultOperator(QueryParser.OR_OPERATOR);
				Query cateQuery = cateParser.parse(condition.categories.replaceAll(","," "));
				bq.add(cateQuery,BooleanClause.Occur.MUST);
			}
			if (condition.partner > 0)
			{
				Term term = new Term("partnerId",String.valueOf(condition.partner));
				TermQuery partnerQuery = new TermQuery(term);
				partnerQuery.setBoost(0.000000001f);
				bq.add(partnerQuery,BooleanClause.Occur.MUST);
			}
			if (condition.ftype != null && !condition.ftype.isEmpty()){
				String[] arr = condition.ftype.split(",");
				for (String s:arr){
					if (s!= null && s.length()>0)
					{
						Query streamQuery = new TermQuery(new Term("streamtypes",s));
						streamQuery.setBoost(0.000000001f);
						bq.add(streamQuery,BooleanClause.Occur.MUST);
					}
				}
			}
			
			if (condition.exclude_cates != null && !condition.exclude_cates.isEmpty()){
				String[] arr = condition.exclude_cates.split(",");
				for (String s:arr)
					bq.add(new TermQuery(new Term("categories",s)),BooleanClause.Occur.MUST_NOT);
			}
			
			//上传时间
			Date startTime = null, endTime = null;
			if (condition.date_start > 0) {
				 startTime  = new Date(((long)condition.date_start) * 1000);
			}
			if (condition.date_end > 0) {
				 endTime  = new Date(((long)condition.date_end) * 1000);
			}
			if(startTime != null || endTime != null){
				RangeQuery timeQuery = new RangeQuery(
						startTime!= null ?new Term("create_day",DataFormat.formatDate(startTime,DataFormat.FMT_DATE_SPECIAL)):null,
						endTime!= null ?new Term("create_day",DataFormat.formatDate(endTime,DataFormat.FMT_DATE_SPECIAL)):null, 
						true); 
				
				bq.add(timeQuery,BooleanClause.Occur.MUST);
			}
			SecondFilter filter = null;
			if (condition.timeless > 0 || condition.timemore > 0){
				filter = new SecondFilter(condition.timeless*60, condition.timemore*60);
			}
			
			if (bq.getClauses() == null || bq.getClauses().length == 0){
				return new Result<Video>();
			}
			
			Sort sort = Constant.Sort.getVideoSort(condition.sort,condition.reverse);
			
			if (sort ==null){
				VideoTopDocCollector collector = new VideoTopDocCollector(condition.end,condition.operator==Constant.Operator.AND,vids,seconds,filter);
				getLocalSearcher().search(bq,null,collector);
				docs = collector.topDocs();
			}
			else
			{
				YoukuTopFieldDocCollector collector = new YoukuTopFieldDocCollector(indexReader,sort,condition.end,seconds,filter);
				getLocalSearcher().search(bq,null,collector);
				docs = collector.topDocs();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			removeLocalSearcher();
		}
		
		//高亮
		HighlightCondition hlight =  null;
		if (condition.highlight){
			Highlighter highlighter = null;
			if (condition.hl_prefix!=null && condition.hl_suffix != null)
			{
				SimpleHTMLFormatter smf = new SimpleHTMLFormatter(condition.hl_prefix,condition.hl_suffix);
				highlighter = new Highlighter(smf,new QueryScorer(bq));
			}
			else
			{
				highlighter = new Highlighter(sHtmlF,new QueryScorer(bq));
			}
			highlighter.setTextFragmenter(new SimpleFragmenter(22));
		
//			int[] hlightfield = new int[]{HighlightCondition.TITLE,HighlightCondition.MEMO,	HighlightCondition.TAGS,HighlightCondition.USERNAME};
			int[] hlightfield = new int[]{HighlightCondition.TITLE,HighlightCondition.TAGS};
			hlight = new HighlightCondition( hlightfield ,highlighter);
		}
		Result<Video> result = docsToResult(hlight,docs,condition.start,condition.end);
		return result;
	}
	/**
	 * 相关视频获取
	 * @param words
	 * @param oldWords
	 * @param category
	 * @param partner
	 * @param operator
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result<Video> queryTitleAndTag(com.youku.search.index.entity.SpecialQuery condition)
	{
		String title = MyUtil.prepareAndAnalyzeWord(condition.title);
		String[] tags = null;
		if (condition.tags!=null)
			tags = condition.tags.split(",");
			
		BooleanQuery bq = new BooleanQuery();
		TopDocs docs = null;
		BooleanQuery keyQuery = new BooleanQuery();
		try {
			if (title != null && title.length() > 0){
				Query titleQuery = null;
				QueryParser  parser = new QueryParser ("title",AnalyzerManager.getBlankAnalyzer());
				parser.setDefaultOperator(QueryParser.OR_OPERATOR);
				titleQuery = parser.parse(title);
				keyQuery.add(titleQuery,BooleanClause.Occur.SHOULD);
				
//				keyQuery.add(new TermQuery(new Term("title_index_full",title)),BooleanClause.Occur.MUST_NOT);
				
			}
			
			if (tags != null){
				for(String tag:tags){
					TermQuery q = new TermQuery(new Term("tags_index",tag));
					keyQuery.add(q,BooleanClause.Occur.SHOULD);
				}
			}
			bq.add(keyQuery,BooleanClause.Occur.MUST);
			
			if (condition.ftype != null){
				String[] arr = condition.ftype.split(",");
				BooleanQuery typeQuery = new BooleanQuery();
				for (String s:arr)
					typeQuery.add(new TermQuery(new Term("streamtypes",s)),BooleanClause.Occur.SHOULD);
				
				bq.add(typeQuery,BooleanClause.Occur.MUST);
			}
			if (condition.category > 0)
			{
				Term term = new Term("categories",String.valueOf(condition.category));
				bq.add(new TermQuery(term),BooleanClause.Occur.MUST);
			}
			
			SecondFilter filter = null;
			if (condition.timeless > 0 || condition.timemore > 0)
			{
				filter = new SecondFilter(condition.timeless*60, condition.timemore*60);
			}
		
			Sort sort = Constant.Sort.getVideoSort(condition.sort,condition.reverse);
			if (sort ==null){
				VideoTopDocCollector collector = new VideoTopDocCollector(condition.end,false,vids,seconds,filter);
				getLocalSearcher().search(bq,null,collector);
				docs = collector.topDocs();
			}
			else
			{
				YoukuTopFieldDocCollector collector = new YoukuTopFieldDocCollector(indexReader,sort,condition.end,seconds,filter);
				getLocalSearcher().search(bq,null,collector);
				docs = collector.topDocs();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			removeLocalSearcher();
		}
		
		return docsToResult(null,docs,condition.start,condition.end);
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
	public Result<Video> queryTag(com.youku.search.index.entity.Query condition)
	{
		TopDocs docs = null;
		BooleanQuery bq = new BooleanQuery();
		BooleanClause.Occur occur = null;
		if (condition.operator == Constant.Operator.AND)
			occur = BooleanClause.Occur.MUST;
		else
			occur = BooleanClause.Occur.SHOULD;
		String[] tags = MyUtil.toLowerCase(condition.keywords).split(" ");
		try {
			BooleanQuery bq2 = new BooleanQuery();
			for (int i = 0;i<tags.length;i++)
			{
				bq2.add( new TermQuery(new Term("tags_index",tags[i])),occur);
			}
			
			bq.add(bq2,BooleanClause.Occur.MUST);
			if (condition.category > 0)
			{
				Term term = new Term("categories",String.valueOf(condition.category));
				bq.add(new TermQuery(term),BooleanClause.Occur.MUST);
			}
			if (condition.partner > 0)
			{
				Term term = new Term("partnerId",String.valueOf(condition.partner));
				bq.add(new TermQuery(term),BooleanClause.Occur.MUST);
			}
				
			if (condition.ftype != null){
				String[] arr = condition.ftype.split(",");
				BooleanQuery typeQuery = new BooleanQuery();
				for (String s:arr)
					typeQuery.add(new TermQuery(new Term("streamtypes",s)),BooleanClause.Occur.SHOULD);
				
				bq.add(typeQuery,BooleanClause.Occur.MUST);
			}
			
			SecondFilter filter = null;
			if (condition.timeless > 0 || condition.timemore > 0)
			{
				filter = new SecondFilter(condition.timeless*60, condition.timemore*60);
			}
			
			
			Sort sort = Constant.Sort.getVideoSort(condition.sort,condition.reverse);
			if (sort==null){
				YoukuTopDocCollector collector = new VideoTopDocCollector(condition.end,true,vids,seconds,filter);
				getLocalSearcher().search(bq,null,collector);
				docs = collector.topDocs();
			}
			else
			{
				YoukuTopFieldDocCollector collector = new YoukuTopFieldDocCollector(indexReader,sort,condition.end,seconds,filter);
				getLocalSearcher().search(bq,null,collector);
				docs = collector.topDocs();
			}
			
		} catch (Exception e) {
			System.err.println("error::::::::::::::"+condition.keywords);
			e.printStackTrace();
		}
		finally
		{
			removeLocalSearcher();
		}
		
		//高亮
		HighlightCondition hlight =  null;
		if (condition.highlight){
			Highlighter highlighter = null;
			if (condition.hl_prefix!=null && condition.hl_suffix != null)
			{
				SimpleHTMLFormatter smf = new SimpleHTMLFormatter(condition.hl_prefix,condition.hl_suffix);
				highlighter = new Highlighter(smf,new QueryScorer(bq));
			}
			else
			{
				highlighter = new Highlighter(sHtmlF,new QueryScorer(bq));
			}
			highlighter.setTextFragmenter(new SimpleFragmenter(30));
		
			hlight = new HighlightCondition(
					new int[]{HighlightCondition.TAGS}
					,highlighter);
		}
		
		return docsToResult(hlight,docs,condition.start,condition.end);
	}
	
	
	/**
	 * 高级搜索
	 * @param aQuery
	 * @return
	 */
	public Result<Video> advanceQuery(AdvanceQuery aQuery)
	{
//		System.out.println("Query::::::::::::::::::::::"+aQuery);
		TopDocs docs = null;
		BooleanQuery bq = new BooleanQuery();
		
		String keywords = aQuery.keywords;
		String[] theFields;
		
		Sort sort = Constant.Sort.getVideoSort(aQuery.sort,aQuery.reverse);
		
		//组织查询字段以及boost值
		if (aQuery.fields == null ){
			theFields = fields;
		}
		else
		{
			theFields = aQuery.fields.split(",");
		}
		//开始组织query
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
//				System.out.println("分解后："+keywords);
				QueryParser parser = new MultiFieldQueryParser (theFields,AnalyzerManager.getBlankAnalyzer(),boosts);
				Query q = parser.parse(keywords);
				bq.add(q,BooleanClause.Occur.MUST);
			}
			
//			if (arr[1] != null && arr[1].length() > 0){
//				Query notQuery = null;
//				QueryParser parser = new QueryParser ("f_single_analyzed",AnalyzerManager.getBlankAnalyzer());
//				notQuery = parser.parse(arr[1]);
//				bq.add(notQuery,BooleanClause.Occur.MUST_NOT);
//			}
			
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
			//分类条件
			if (aQuery.categories != null && aQuery.categories.length() > 0)
			{
				QueryParser cateParser = new QueryParser("categories",AnalyzerManager.getBlankAnalyzer());
				cateParser.setDefaultOperator(QueryParser.OR_OPERATOR);
				Query cateQuery = cateParser.parse(aQuery.categories.replaceAll(","," "));
				bq.add(cateQuery,BooleanClause.Occur.MUST);
			}
			if (aQuery.pv > 0)
			{
				TermQuery query = new TermQuery(new Term("pv_index",String.valueOf(aQuery.pv)));
				bq.add(query,BooleanClause.Occur.MUST);
			}
			if (aQuery.comments > 0)
			{
				TermQuery query = new TermQuery(new Term("comment_index",String.valueOf(aQuery.comments)));
				bq.add(query,BooleanClause.Occur.MUST);
			}
			if (aQuery.hd >0)
			{
				TermQuery query = new TermQuery(new Term("streamtypes",String.valueOf(aQuery.hd)));
				bq.add(query,BooleanClause.Occur.MUST);
			}
			SecondFilter filter = null;
			if (aQuery.timeless > 0 || aQuery.timemore > 0)
			{
				filter = new SecondFilter(aQuery.timeless*60, aQuery.timemore*60);
			}
//				bq.add(new TermQuery(new Term("timelength",TimeLength.getIndex(aQuery.timeless,aQuery.timemore)+"")),BooleanClause.Occur.MUST);
			
			if (aQuery.ftype != null&& aQuery.ftype.length() > 0){
				String[] arr = aQuery.ftype.split(",");
				BooleanQuery typeQuery = new BooleanQuery();
				for (String s:arr)
					typeQuery.add(new TermQuery(new Term("streamtypes",s)),BooleanClause.Occur.SHOULD);
				
				bq.add(typeQuery,BooleanClause.Occur.MUST);
			}
			
			if (aQuery.exclude_cates != null && !aQuery.exclude_cates.isEmpty()){
				String[] arr = aQuery.exclude_cates.split(",");
				for (String s:arr)
					bq.add(new TermQuery(new Term("categories",s)),BooleanClause.Occur.MUST_NOT);
			}
			
			if (bq.getClauses() == null || bq.getClauses().length == 0){
				return new Result<Video>();
			}
			
//			System.out.println(bq);
			
			
			if (sort==null){
				YoukuTopDocCollector collector = new VideoTopDocCollector(aQuery.end,true,vids,seconds,filter);
				 getLocalSearcher().search(bq,null,collector);
				 docs = collector.topDocs();
			}
			else{
				YoukuTopFieldDocCollector collector = new YoukuTopFieldDocCollector(indexReader,sort,aQuery.end,seconds,filter);
				getLocalSearcher().search(bq,null,collector);
				docs = collector.topDocs();
			}
//				docs = getLocalSearcher().search(bq,null,aQuery.end,sort);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			removeLocalSearcher();
		}
		return docsToResult(null,docs,aQuery.start,aQuery.end);
	}
	
	
	private Result<Video> docsToResult(HighlightCondition hlight,TopDocs docs,int start,int end)
	{
		if (docs == null) return new Result<Video>();
		Result<Video> result = new Result<Video>();
		
		int size = docs.totalHits;
		result.totalCount = size;  //存储到结果集
		
		//如果开始位置大于查询总量，直接返回result,结果集为空
		if (start > size){
			return result;
		}
		
		if (size <= end){
			end = size;
		}
		else{
			result.hasNext = true;
		}
		ArrayList<Video> list = new ArrayList<Video>();
		ScoreDoc[] scoreDoc = docs.scoreDocs;
		for (int i=start;i<end;i++)
		{
			try {
				Video video = documentToVideo(hlight,scoreDoc[i]);
				video.score = scoreDoc[i].score;
				list.add(video);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.results = list;
		return result;
	}
	
	/**
	 * 根据视频id取视频信息
	 * @param vid
	 * @return
	 */
	public Video getVideo(int vid)
	{
		Video video = null;
		Term term = new Term("vid",String.valueOf(vid));
		TermQuery query = new TermQuery(term);
		TopDocs docs = null;
		try {
			docs = getLocalSearcher().search(query,null,1);
			if (docs != null && docs.totalHits >0)
			{
				ScoreDoc[] scoreDoc = docs.scoreDocs;
				return documentToVideo(null,scoreDoc[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			removeLocalSearcher();
		}
		return video;
	}
	
	/**
	 * 批量取视频信息
	 * @param vids
	 * @return
	 */
	public List<Video> getVideos(int[] vids)
	{
		List<Video> list = new ArrayList<Video>();
		
		try {
			for (int i = 0 ;i<vids.length;i++)
			{
				Term term = new Term("vid",String.valueOf(vids[i]));
				TermQuery query = new TermQuery(term);
				TopDocs docs = null;
			
				docs = getLocalSearcher().search(query,null,1);
				if (docs != null && docs.totalHits >0)
				{
					ScoreDoc[] scoreDoc = docs.scoreDocs;
					list.add(documentToVideo(null,scoreDoc[0]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			removeLocalSearcher();
		}
		return list;
	}
	
//	private Result<Video> hitsToResult(Hits hits,int start,int end)
//	{
//		if (hits == null) return new Result();
//		Result<Video> result = new Result();
//		
//		int size = hits.length();
//		result.totalCount = size;  //存储到结果集
//		
//		//如果开始位置大于查询总量，直接返回result,结果集为空
//		if (start > size)
//			return result;
//		
//		if (size <= end){
//			end = size;
//			result.hasNext = false;
//		}
//		
//		ArrayList<Video> list = new ArrayList<Video>();
//		
//		for (int i=start;i<end;i++)
//		{
//			try {
//				Video video = documentToVideo(hits.doc(i));
//				video.score = hits.score(i);
//				list.add(video);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		result.results = list;
//		return result;
//	}
	
	protected Video documentToVideo(HighlightCondition hlight,ScoreDoc scoreDoc) throws CorruptIndexException, IOException
	{
		Document doc =  indexReader.document(scoreDoc.doc);
		Video video = new Video();
		video.vid = DataFormat.parseInt(doc.get("vid"));
		video.score = scoreDoc.score;
		video.youkuscore = BoostReader.getBoost(video.vid);
		video.lucenescore = scoreDoc.score-video.youkuscore;
		
		video.encodeVid = MyUtil.encodeVideoId(video.vid);
		
		video.ftype = doc.get("streamtypes");
		video.createtime = DataFormat.parseLong(doc.get("createtime"));
		video.total_pv = DataFormat.parseInt(doc.get("total_pv"));
		video.total_comment = DataFormat.parseInt(doc.get("total_comment"));
		video.total_fav = DataFormat.parseInt(doc.get("total_fav"));
		video.cate_ids = DataFormat.parseInt(doc.get("categories"));
		video.logo = doc.get("logo");
		video.size = DataFormat.parseInt(doc.get("size"));
	    video.seconds = doc.get("seconds");
	    video.owner = doc.get("owner");
	    video.owner_username = doc.get("owner_username");
	    video.md5 = doc.get("md5");
	    video.public_type = DataFormat.parseInt(doc.get("public_type"));
	    
	    video.title = doc.get("title_store");
		video.tags = doc.get("tags");
		String memo = doc.get("memo_store");
		
		String newTitle = null;
		String newMemo = null;
		String newTags = null;
		String newUsername = null;
		
		if (hlight != null)
		{
			try
			{
				if (hlight.isHighTitle())
				{
					hlight.getHighlighter().setTextFragmenter(new SimpleFragmenter(40));    
					 TermPositionVector tpv_title = (TermPositionVector)indexReader.getTermFreqVector(scoreDoc.doc,"title");
					 if (tpv_title != null){
						 TokenStream tokenStream=TokenSources.getTokenStream(tpv_title);
						 newTitle = hlight.getHighlighter().getBestFragments(tokenStream,video.title,1,fragmentSeparator);
					 }
				}
				if (hlight.isHighMemo())
				{
					hlight.getHighlighter().setTextFragmenter(new SimpleFragmenter(35));
					 TermPositionVector tpv_memo = (TermPositionVector)indexReader.getTermFreqVector(scoreDoc.doc,"memo");
					 if (tpv_memo != null){
						 TokenStream tokenStream=TokenSources.getTokenStream(tpv_memo);
						 newMemo  = hlight.getHighlighter().getBestFragments(tokenStream,memo,1,fragmentSeparator);
					 }
				}
				if (hlight.isHighTag())
				{
					 TermPositionVector tpv_tags = (TermPositionVector)indexReader.getTermFreqVector(scoreDoc.doc,"tags_index");
					 if (tpv_tags != null){
						 TokenStream tokenStream=TokenSources.getTokenStream(tpv_tags);
						 newTags = hlight.getHighlighter().getBestFragments(tokenStream,video.tags,2,fragmentSeparator);
					 }
				}
				
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
		}
		if (newTitle != null && !newTitle.isEmpty())
			video.title_hl = newTitle;
		
		if (newMemo != null  && !newMemo.isEmpty()){
			video.memo = newMemo;
		}
		else{
			video.memo = DataFormat.htmlEncode(memo,30);
		}
			
		if (newTags != null  && !newTags.isEmpty()){
			video.tags_hl = newTags;
		}
		if (newUsername != null  && !newUsername.isEmpty()){
			video.username_hl = newUsername;
		}
		return video;
	}
	
//	
//	public Result<Video> query2(com.youku.search.index.entity.Query condition)
//	{
//		long start_date = System.currentTimeMillis();
//		String newWord = condition.keywords;
//		
//		YoukuTopDocCollector collector = new YoukuTopDocCollector(condition.end,condition.operator==Constant.Operator.AND);
//		
//        TopDocs docs = null;
//        BooleanQuery bq = new BooleanQuery();
//		try {
//			if (newWord != null && !newWord.isEmpty())
//			{
//				if (condition.needAnalyze){
//					newWord = WordProcessor.formatVideoQueryString(condition.keywords);
//					newWord = MyUtil.prepareAndAnalyzeWord(newWord);
//				}
//				
//				QueryParser  parser = new MultiFieldQueryParser (fields,AnalyzerManager.getBlankAnalyzer(),boosts);
//		        Query query = null;
//				parser.setDefaultOperator(Constant.Operator.getOperator(condition.operator));
//				query = parser.parse(newWord);
//				
//				BooleanQuery bq2 = new BooleanQuery();
//				TermQuery tagQuery = new TermQuery(new Term("tags_index",condition.keywords));
//				bq2.add(tagQuery,BooleanClause.Occur.SHOULD);
//				bq2.add(query,BooleanClause.Occur.SHOULD);
//				
//				bq.add(bq2,BooleanClause.Occur.MUST);
//			}
//			
//			
//			if (condition.category > 0)
//			{
//				Term term = new Term("categories",String.valueOf(condition.category));
//				bq.add(new TermQuery(term),BooleanClause.Occur.MUST);
//			}
//			if (condition.partner > 0)
//			{
//				Term term = new Term("partnerId",String.valueOf(condition.partner));
//				bq.add(new TermQuery(term),BooleanClause.Occur.MUST);
//			}
//			if (condition.ftype != null){
//				String[] arr = condition.ftype.split(",");
//				BooleanQuery typeQuery = new BooleanQuery();
//				for (String s:arr)
//					typeQuery.add(new TermQuery(new Term("streamtypes",s)),BooleanClause.Occur.SHOULD);
//				
//				bq.add(typeQuery,BooleanClause.Occur.MUST);
//			}
//			
//			if (condition.timeless > 0)
//				bq.add(new TermQuery(new Term("second_less_index",String.valueOf(condition.timeless))),BooleanClause.Occur.MUST);
//			//临时，因为只有1种情况，所以用less字段
//			if (condition.timemore > 0)
//				bq.add(new TermQuery(new Term("second_less_index","1")),BooleanClause.Occur.MUST);
//			
//			
//			if (bq.getClauses() == null || bq.getClauses().length == 0){
//				return new Result<Video>();
//			}
//			
//			Sort sort = Constant.Sort.getVideoSort(condition.sort,condition.reverse);
//			
//			if (sort ==null){
//				getLocalSearcher().search(bq,null,collector);
//				docs = collector.topDocs();
//			}
//			else{
//				YoukuCountTopDocCollector col = new YoukuCountTopDocCollector(condition.end);
//				getLocalSearcher().search(bq,null,col);
//				docs = col.topDocs();
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		long query_date = System.currentTimeMillis();
//		
//		//高亮
//		HighlightCondition hlight =  null;
//		if (condition.highlight){
//			Highlighter highlighter = null;
//			if (condition.hl_prefix!=null && condition.hl_suffix != null)
//			{
//				SimpleHTMLFormatter smf = new SimpleHTMLFormatter(condition.hl_prefix,condition.hl_suffix);
//				highlighter = new Highlighter(smf,new QueryScorer(bq));
//			}
//			else
//			{
//				highlighter = new Highlighter(sHtmlF,new QueryScorer(bq));
//			}
//			highlighter.setTextFragmenter(new SimpleFragmenter(22));
//		
//			hlight = new HighlightCondition(
//					new int[]{HighlightCondition.TITLE,HighlightCondition.MEMO,HighlightCondition.TAGS,HighlightCondition.USERNAME}
//					,highlighter);
//		}
//		
//		Result<Video> result = new Result();
//		int start = condition.start;
//		int end = condition.end;
//		
//		int size = docs.totalHits;
//		result.totalCount = size;  //存储到结果集
//		
//		//如果开始位置大于查询总量，直接返回result,结果集为空
//		if (start > size)
//			return result;
//		
//		if (size <= end){
//			end = size;
//			result.hasNext = false;
//		}
//		
//		ArrayList<Video> list = new ArrayList<Video>();
//		ScoreDoc[] scoreDoc = docs.scoreDocs;
//		for (int i=start;i<end;i++)
//		{
//			Video video = videoMap.get(vids[scoreDoc[i].doc]);
////			Video video = (Video)VideoMemCache.cacheGet(String.valueOf(vids[scoreDoc[i].doc]));
//			if (video  == null){
//				System.out.println("vid:" + vids[scoreDoc[i].doc]);
//				continue;
//			}
//			video.score = scoreDoc[i+start].score;
//			list.add(video);
//		}
//		
//		long end_date = System.currentTimeMillis();
//		System.out.println("list.size:" + list.size());
//		System.out.println("total cost:" + (end_date - start_date) +"\t lucene query cost:" + (query_date - start_date) + "\t get video cost:" + (end_date-query_date));
//		
//		
//		result.results = list;
//		return result;
//	}
	
	public void setDefaultOperator(int operator)
	{
		DEFAULT_OPERATOR = operator;
	}
	public int getDefaultOperator()
	{
		return DEFAULT_OPERATOR;
	}
	
	/**
	 * 预搜索关键词
	 */
	public void prepare()
	{
		if (null == indexReader)
			return;
		System.out.println("keywordsList.size() = "+keywordsList.size());
		QueryParser  parser = new MultiFieldQueryParser (fields,AnalyzerManager.getMyAnalyzer(),boosts);
		Query query = null;
		try {
			for (int i = 0;i<keywordsList.size();i++)
			{
				if (DEFAULT_OPERATOR == Constant.Operator.AND)
					parser.setDefaultOperator(QueryParser.AND_OPERATOR);
				String words = AnalyzerManager.analyzeWord(keywordsList.get(i));
				if (words == null)continue;
				query = parser.parse(words);
				this.indexSearcher.search(query);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 初始化关键词列表
	 */
	public void initKeywords()
	{
		if (null == keywordsList)
		{
			keywordsList = new ArrayList<String>();
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(Config.getWordsFile())));
				String line = null;
				
				while ( (line = r.readLine()) != null)
				{
					keywordsList.add(line);
				}
				r.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getKeywordsList() {
		return keywordsList;
	}
	
	
	public int deleteVideo(int vid)
	{
		int row = 0;
		Term term = new Term("vid", String.valueOf(vid));
	    try {
	    	//先从内存删除
	    	row = indexReader.deleteDocuments(term);
	    	if (row > 0)
	    	{
	    		indexReader.flush();
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
		
		}
		return row;
	}
	
	public void deleteVideos(List<Integer> vids)
	{
		Term[] terms = new Term[vids.size()];
		for (int i =0;i<vids.size();i++)
		{
			terms[i] = new Term("vid", String.valueOf(vids.get(i)));
			System.out.println("delete video:"+vids.get(i));
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
	
//	public Result<Video> testSet(String[] arr)
//	{
//		List list = new ArrayList();
//		try
//		{
//			for (int i=0;i<arr.length;i++)
//			{
//				List list1 = new ArrayList();
//				Term t = new Term("title",arr[i]);
//				Hits hits = getLocalSearcher().search(new TermQuery(t));
//				for (int j= 0;j < hits.length();j++)
//				{
//					Document doc = hits.doc(j);
//					String tags = doc.get("tags");
//					if(tags != null)
//					{
//						String[] tagarr = tags.split(",");
//						for (String m:tagarr)
//						{
//							if (!list1.contains(m))
//								list1.add(m);
//						}
//					}
//				}
//				if (list.size() == 0) 
//					list.addAll(list1);
//				else
//					list.retainAll(list1);
//			}
//		}catch(Exception e)
//		{
//			
//		}
//		
//		
//		
//		return null;
//	}
}
