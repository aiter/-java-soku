/**
 * 
 */
package com.youku.soku.index.query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SokuTopFieldDocCollector;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.LockObtainFailedException;

import com.youku.search.index.entity.Result;
import com.youku.search.index.query.SecondFilter;
import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.index.analyzer.AnalyzerManager;
import com.youku.soku.index.analyzer.WordProcessor;
import com.youku.soku.index.entity.Video;
import com.youku.soku.index.manager.db.DomainManager;
import com.youku.soku.util.Constant;
import com.youku.soku.util.MyUtil;

/**
 * @author william
 *
 */
public class VideoQueryManager extends BaseQuery{

	public static int DEFAULT_OPERATOR=Constant.Operator.AND; //默认为AND搜索
	
	public static final Sort SORT_NEW_ASC = new Sort(new SortField("create_day",SortField.INT,false));
	public static final Sort SORT_NEW_DESC = new Sort(new SortField("create_day",SortField.INT,true));
	
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
	
	public String[] pks = null;
	public String[] days = null;
	public float[] seconds = null;
	
	private synchronized void init(){
		indexType = Constant.QueryField.VIDEO;
		indexPath = Config.getVideoIndexPath();
		initReader();
		try {
			pks = FieldCache.DEFAULT.getStrings(indexReader, "id");
			days = FieldCache.DEFAULT.getStrings(indexReader, "create_day");
			seconds = FieldCache.DEFAULT.getFloats(indexReader, "seconds");
		} catch (IOException e) {
			System.err.println("error:"+e.getMessage());
		}
	}
	/**
	 * 索引加载到内存
	 *
	 */
	public synchronized void initReader()
	{
		super.initReader();
		prepare();
		System.out.println("prepare reader over!");
	}
	
	
	
	/**
	 * 搜索，在title,tags,memo中搜索
	 * @param words 已经分好词的word
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result<Video> query(com.youku.soku.Query condition)
	{
		long start_time = System.currentTimeMillis();
		
		boolean isCollectSite = true;
		String oldWord = condition.keywords;
		String newWord = condition.keywords;
		
		BooleanQuery bq = new BooleanQuery();
		
		if (oldWord!= null){
			int start = oldWord.indexOf("site:");
			if (start > -1){
				int end = oldWord.indexOf(" ",start+5);
				if (end == -1)
					end = oldWord.length();
				
				String domain = newWord.substring(start+5,end);
				int id = DomainManager.getInstance().getDomainId(domain);
				if (id > 0)
				{
					bq.add( new TermQuery(new Term("domain_index",id+"")),BooleanClause.Occur.MUST);
				}
				else{
					return null;
				}
				oldWord = oldWord.replaceFirst(newWord.substring(start,end), "");
				
				isCollectSite = false;
			}
			
			newWord = WordProcessor.formatVideoQueryString(oldWord);
			newWord = MyUtil.prepareAndAnalyzeWord(newWord);
		}
		
		try {
			if (newWord != null && newWord.length() >0)
			{			
				BooleanQuery bq2 = new BooleanQuery();
				QueryParser  parser = new QueryParser ("title",AnalyzerManager.getBlankAnalyzer());
				parser.setDefaultOperator(Constant.Operator.getOperator(condition.operator));
				Query query = parser.parse(newWord);
				
				
				TermQuery tagQuery = new TermQuery(new Term("tags_index",oldWord));
				bq2.add(tagQuery,BooleanClause.Occur.SHOULD);
				bq2.add(query,BooleanClause.Occur.SHOULD);
				bq.add(bq2,BooleanClause.Occur.MUST);
			}
			
			if (condition.hd > 0)
				bq.add(new TermQuery(new Term("hd",String.valueOf(condition.hd))),BooleanClause.Occur.MUST);
			
			if (condition.site > 0){
				isCollectSite = false;
				bq.add(new TermQuery(new Term("site",""+condition.site)),BooleanClause.Occur.MUST);
			}
			
			if (condition.limitDate > 0){
				Date startTime = DataFormat.getNextDate(new Date(),0-condition.limitDate);
				RangeQuery timeQuery = new RangeQuery(
						startTime!= null ?new Term("create_day",DataFormat.formatDate(startTime,DataFormat.FMT_DATE_SPECIAL)):null,
						null, true);
				bq.add(timeQuery,BooleanClause.Occur.MUST);
			}
			
//			if (condition.timeLength > 0){
//				bq.add(new TermQuery(new Term("timelength_index",""+condition.timeLength)),BooleanClause.Occur.MUST);
//			}
			if (condition.exclude_sites != null){
				for (int site:condition.exclude_sites)
					bq.add(new TermQuery(new Term("site",site+"")),BooleanClause.Occur.MUST_NOT);
			}
			if (condition.site <= 0 && condition.include_sites != null && condition.include_sites.length>0){
				if(condition.include_sites.length==1){
					bq.add(new TermQuery(new Term("site",condition.include_sites[0]+"")),BooleanClause.Occur.MUST);
				} else {
					BooleanQuery bq2 = new BooleanQuery();
					
					for (int site:condition.include_sites)
						bq2.add(new TermQuery(new Term("site",site+"")),BooleanClause.Occur.SHOULD);
					
					bq.add(bq2,BooleanClause.Occur.MUST);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if (bq == null)
			return new Result<Video>();
		
		SecondFilter filter = null;
		if (condition.timeLength > 0){
			TimeLength time = new TimeLength(condition.timeLength);
			filter = new SecondFilter(time.getLess(),time.getMore());
		}
//		System.out.println(bq); 
		
		TopDocs docs = null;
		HashSet<String> siteSet = null;
		Sort sort = Constant.QuerySort.getSort(condition.sort,condition.reverse);
		try
		{
			if (sort == null){
				boolean useBoost = condition.operator == Constant.Operator.AND;
				YoukuTopDocCollector collector = new YoukuTopDocCollector(condition.end,useBoost,pks,isCollectSite,seconds,filter);
				indexSearcher.search(bq,null,collector);
				docs = collector.topDocs();
				siteSet = collector.getSiteSet();
			}
			else{
				SokuTopFieldDocCollector collector = new SokuTopFieldDocCollector(indexReader,sort,condition.end,pks,isCollectSite,seconds,filter);
				indexSearcher.search(bq,null,collector);
				docs = collector.topDocs();
				siteSet = collector.getSiteSet();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return new Result<Video>();
		}
		
		//如果没有查到结果直接返回
		if (docs == null || docs.totalHits==0)
			return new Result<Video>();
		
		
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
		
			hlight = new HighlightCondition(
					new int[]{HighlightCondition.TITLE,HighlightCondition.TAGS}
					,highlighter);
		}
		Result<Video> r = docsToResult(hlight,docs,condition.start,condition.end);
		
		//组织站点
		if (siteSet != null && siteSet.size()>0)
		{
			HashSet<Integer> nameSet = new HashSet<Integer>();
			for (String id:siteSet){
				nameSet.add(DataFormat.parseInt(id));
			}
			r.extra = nameSet;
		}
		
		r.timecost = (int)(System.currentTimeMillis() - start_time);

		if (r.timecost>=1000)
			System.out.println("QUERY WAINING:use long time:"+r.timecost+",queryObject:"+condition.toString());
		
		return r;
	}
	

	private Result<Video> docsToResult(HighlightCondition hlight,TopDocs docs,int start,int end)
	{
		if (docs == null) 
			return new Result<Video>();
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
			result.hasNext=true;
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
//		System.out.println("list.size()="+list.size());
		return result;
	}
	
	
	protected Video documentToVideo(HighlightCondition hlight,ScoreDoc scoreDoc) throws CorruptIndexException, IOException
	{
		Document doc =  indexReader.document(scoreDoc.doc);
		Video video = new Video();
		
		video.id = DataFormat.parseInt(doc.get("vid"));
		video.logo = getImageUrl(doc.get("logo"));
	    video.seconds = doc.get("seconds");
	    video.title = doc.get("title_store");
		video.tags = doc.get("tags");
		video.cate = doc.get("cate");
		video.site = doc.get("site");
		video.uploadTime = DataFormat.parseLong(doc.get("uploadTime"));
		if (!video.site.equals("14"))
			video.uploadTime = video.uploadTime*1000;
		
//		video.site = SiteManager.getInstance().getName(site);
		String domain = DomainManager.getInstance().getUrl(DataFormat.parseInt(doc.get("domain")));
		
		
		
		StringBuffer url = new StringBuffer(100);
		url.append("http://")
			.append(domain)
			.append(doc.get("url"));
		
		video.url =  url.toString() ;
		video.hd = DataFormat.parseInt(doc.get("hd"));
		
		String newTitle = null;
		String newTag = null;
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
				if (hlight.isHighTag())
				{
					hlight.getHighlighter().setTextFragmenter(new SimpleFragmenter(40));    
					 TermPositionVector tpv_tags = (TermPositionVector)indexReader.getTermFreqVector(scoreDoc.doc,"tags_index");
					 if (tpv_tags != null){
						 TokenStream tokenStream=TokenSources.getTokenStream(tpv_tags);
						 newTag = hlight.getHighlighter().getBestFragments(tokenStream,video.tags,1,fragmentSeparator);
					 }
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if (newTitle != null && !newTitle.isEmpty())
			video.title_hl = newTitle;
		if (newTag != null && !newTag.isEmpty()){
			video.tags_hl = newTag;
		}
		
		return video;
	}
	
	
	public int isInIndex(int id,int site)
	{
		try {
			TermDocs docs = indexReader.termDocs(new Term("id", site+"_"+id));
			if (docs!=null&& docs.next())
				return docs.doc();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getLastId(int site)
	{
		TermQuery query = new TermQuery(new Term("site",String.valueOf(site)));
		Sort sort = new Sort(new SortField("id",SortField.STRING,true));
		try {
			TopDocs docs = indexSearcher.search(query,null,1,sort);
			ScoreDoc[] scoreDoc = docs.scoreDocs;
			if (scoreDoc.length >0)
			{
				Document maxDoc = indexReader.document(scoreDoc[0].doc);
				return DataFormat.parseInt(maxDoc.get("id"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void destroy() throws IOException
	{
		if (indexReader != null)
			indexReader.close();
	}
	
	
	public void reopenIndex() throws CorruptIndexException, IOException
	{
		super.reopenIndex();
		try {
			pks = FieldCache.DEFAULT.getStrings(indexReader, "id");
			days = FieldCache.DEFAULT.getStrings(indexReader, "create_day");
			seconds = FieldCache.DEFAULT.getFloats(indexReader, "seconds");
		} catch (IOException e) {
			System.err.println("error:"+e.getMessage());
		}
	}
	
	public void deleteVideoByDocId(int docid)
	{
		if(indexReader != null)
			try {
				indexReader.deleteDocument(docid);
			} catch (StaleReaderException e) {
				e.printStackTrace();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockObtainFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public void deleteVideoByDocVideoId(int site,int videoId)
	{
		if(indexReader != null)
			try {
				indexReader.deleteDocuments(new Term("id",site+"_"+videoId ));
			} catch (StaleReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockObtainFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	public void prepare()
	{
		if (null == indexReader)
			return;
		
		QueryParser  parser = new QueryParser ("title",AnalyzerManager.getBlankAnalyzer());
		Query query = null;
		try {
				query = parser.parse("mm");
				indexSearcher.search(query);
				indexSearcher.search(query,SORT_NEW_DESC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Random r = new Random();
	private String getImageUrl(String logo)
	{
		if (logo == null)
			return "";
		if (logo.startsWith("http"))
		{
			return logo;
		}
		else
		{
			StringBuilder builder = new StringBuilder("http://g");
			builder.append(r.nextInt(4)+1);
			builder.append(".ykimg.com/");
			builder.append(logo);
			return builder.toString();
		}
	}

	public static class TimeLength{
		int less;
		int more;
		
		public TimeLength(int index){
			switch (index){
				case 1:
					less = 10*60;
					break;
				case 2:
					less = 30*60;
					more = 10*60;
					break;
				case 3:
					less = 60*60;
					more = 30*60;
					break;
				case 4:
					more = 60*60;
					break;
			}
		}
		
		public int getLess()
		{
			return less;
		}
		public int getMore()
		{
			return more;
		}
	}
	
}
