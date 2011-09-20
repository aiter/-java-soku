/**
 * 
 */
package com.youku.search.index.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.index.entity.Result;
import com.youku.search.index.entity.User;
import com.youku.search.index.server.ServerManager;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public class UserQueryManager extends BaseQuery{

	public static int DEFAULT_OPERATOR=Constant.Operator.AND; //默认为AND搜索
	
	public static final Sort SORT_NEW_REG_ASC = new Sort(new SortField("reg_date",SortField.LONG,false));
	public static final Sort SORT_NEW_REG_DESC = new Sort(new SortField("reg_date",SortField.LONG,true));
	public static final Sort SORT_NEW_UPDATE_ASC = new Sort(new SortField("last_content_date",SortField.LONG,false));
	public static final Sort SORT_NEW_REG_UPDATE_DESC = new Sort(new SortField("last_content_date",SortField.LONG,true));
	public static final Sort SORT_VIDEOCOUNT_ASC = new Sort(new SortField("video_count",SortField.INT,false));
	public static final Sort SORT_VIDEOCOUNT_DESC = new Sort(new SortField("video_count",SortField.INT,true));
	public static final Sort SORT_FAV_ASC = new Sort(new SortField("fav_count",SortField.INT,false));
	public static final Sort SORT_FAV_DESC = new Sort(new SortField("fav_count",SortField.INT,true));
	public static final Sort SORT_USERSCORE_ASC = new Sort(new SortField("score",SortField.INT,false));
	public static final Sort SORT_USERSCORE_DESC = new Sort(new SortField("score",SortField.INT,true));
	
	private UserQueryManager(){
		
	}
	private static UserQueryManager self = null;

	public  static UserQueryManager getInstance(){
		
		if(self == null){
			self = new UserQueryManager();
			self.init();
		}
		return self;
	}
	private synchronized void init(){
		super.indexType = Constant.QueryField.MEMBER;
		indexPath = ServerManager.getUserIndexPath();
		initReader();
	}
	
	/**
	 * 查询
	 * @param words 已经分好词的word
	 * @param sort  排序，见Constant.Sort类
	 * @param reverse 倒序，默认true，表示倒序
	 * @param start  结果集开始位置
	 * @param end	 结果集结束位置
	 * @return
	 */
	public Result query(String words,int sort,boolean reverse,int start,int end)
	{
		return query(words,Constant.Sort.getUserSort(sort,reverse),start,end);
	}
	
	/**
	 * 搜索，在username中搜索
	 * @param words 已经分好词的word
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	public Result query(String words,Sort sort,int start,int end)
	{
		QueryParser parser = new QueryParser("user_name",AnalyzerManager.getBlankAnalyzer());
		if (DEFAULT_OPERATOR == Constant.Operator.AND)
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        Query query = null;
        Hits hits = null;
		try {
			query = parser.parse(words);
			hits = indexSearcher.search(query,sort);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hitsToResult(hits,start,end);
	}
	
	/**
	 * 根据专辑id取视频信息
	 * @return
	 */
	public User getUser(int pk_user)
	{
		User user = null;
		Term term = new Term("pkuser",String.valueOf(pk_user));
		TermQuery query = new TermQuery(term);
		Hits hits = null;
		try {
			hits = indexSearcher.search(query);
			if (hits != null && hits.length() > 0)
			{
				Document doc = hits.doc(0);
				return documentToUser(doc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	/**
	 * 批量取专辑信息
	 * @return
	 */
	public List<User> getUsers(int[] pk_users)
	{
		List<User> list = new ArrayList<User>();
		
		for (int i = 0 ;i<pk_users.length;i++)
		{
			Term term = new Term("pkuser",String.valueOf(pk_users[i]));
			TermQuery query = new TermQuery(term);
			Hits hits = null;
			try {
				hits = indexSearcher.search(query);
				if (hits != null && hits.length() > 0)
				{
					Document doc = hits.doc(0);
					list.add(documentToUser(doc));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	private Result hitsToResult(Hits hits,int start,int end)
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
		ArrayList<User> list = new ArrayList<User>();
		
		for (int i=start;i<end;i++)
		{
			try {
				User user = documentToUser(hits.doc(i));
				user.score = hits.score(i);
				list.add(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.results = list;
		return result;
	}
	
	protected User documentToUser(Document doc)
	{
		User user = new User();
		user.pk_user = DataFormat.parseInt(doc.get("pkuser"));
		user.user_name = doc.get("user_name");
		user.gender = DataFormat.parseInt(doc.get("gender"));
		user.city = DataFormat.parseInt(doc.get("city"));
		user.icon64 = doc.get("icon64");
		user.user_score = DataFormat.parseInt(doc.get("score"));
		user.video_count = DataFormat.parseInt(doc.get("video_count"));
		user.order_count = DataFormat.parseInt(doc.get("order_count"));
		user.last_login_date = DataFormat.parseLong(doc.get("last_login_date"));
		user.last_content_date = DataFormat.parseLong(doc.get("last_content_date"));
		user.reg_date = DataFormat.parseLong(doc.get("reg_date"));
		user.fav_count = DataFormat.parseInt(doc.get("fav_count"));
		
		return user;
	}
	
	
	public int deleteUser(int user_id)
	{
		int row = 0;
		Term term = new Term("pkuser", String.valueOf(user_id));
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
	
	public void deleteUsers(List<Integer> user_ids)
	{
		Term[] terms = new Term[user_ids.size()];
		for (int i =0;i<user_ids.size();i++)
		{
			terms[i] = new Term("pkuser", String.valueOf(user_ids.get(i)));
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
