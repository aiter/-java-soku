/**
 * 
 */
package com.youku.search.console.teleplay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.console.operate.RegexpBuilder;
import com.youku.search.console.operate.juji.JujiUtils;
import com.youku.search.console.operate.juji.SearchLogMgt;
import com.youku.search.console.operate.juji.SearchNumMgt;
import com.youku.search.console.pojo.SearchNum;
import com.youku.search.util.Request;

public class TeleplayQuery {
	
	static Log logger = LogFactory.getLog(TeleplayQuery.class);
	protected static final int try_times = 4; //连续n次未找到合适的结果,跳出
	protected static final String[] versionString = new String[]{"季","部"};
	protected static final int max_videos = 3000; //每集获取最大视频数量
	
	protected int min_seconds = 10;//最少时常限制,少于此值,则不满足条件
	
	String sort;
	
	public TeleplayQuery()
	{
		min_seconds = QueryType.DIANSHIJU.seconds;
	}
	
	public TeleplayQuery(QueryType type)
	{
		min_seconds = type.seconds;
	}
	
	
	/**
	 * 根据关键词获取剧集列表
	 * 不知道一共多少集,利用程序来查找
	 * @param oldWords
	 * @return List<Video>
	 * @see getMovies(String words ,int limit)
	 */
	public List<Video> getMovies(Set<String> teleplayNames,Set<String> versionNames)
	{
		return getMovies(teleplayNames, versionNames,0);
	}
	
	
	/**
	 * 根据关键词获取剧集列表
	 * @param words
	 * @param limit 限制查找多少集,0为不限
	 * @return
	 */
	public List<Video> getMovies(Set<String> teleplayNames,Set<String> versionNames,int limit)
	{
		return getMovies(teleplayNames, versionNames, 1, limit);
	}
	
	/**
	 * 根据关键词获取剧集列表
	 * @param words
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<Video> getMovies(Set<String> teleplayNames,Set<String> versionNames,int start,int limit)
	{
		List<Video> result = new ArrayList<Video>();
		int i = 0;

		if (limit == 0) limit=10000; //如果不指定多少集,自动搜索
		int empty=0;
		for (;i<limit;i++)
		{
			List<Video> videos = getOneMovie(teleplayNames, versionNames, (start+i),RegexpBuilder.build(teleplayNames, versionNames, start+i),5,0);
			if (videos == null || videos.size() == 0)
			{
				empty++;
				if (empty >= try_times)
					return result;  
			}
			else
			{
				if (empty>0)
				{
					for (int m=empty;m>0;m--){
						result.add(null);
					}
					empty = 0;
				}
				result.add(videos.get(0));
			}
		}
		if (i == 1){
			//没有找到完全匹配的视频
		}
		return result;
	}
	
	public List<Video> getOneMovie(Set<String> teleplayNames,Set<String> versionNames,int order,String regexp)
	{
		return getOneMovie( teleplayNames, versionNames, order,regexp,0,0);
	}
	
	public List<Video> getOneMovie(Set<String> teleplayNames,Set<String> versionNames,int order,String regexp,int limit,int date){
		List<Video> result = new ArrayList<Video>();
		List<Video> temp = null;
		for(String tn:teleplayNames){
			if(null!=versionNames&&versionNames.size()>0){
				for(String vn:versionNames){
					temp = getOneMovie(tn, vn, order,regexp,limit,date);
					if(null!=temp&&temp.size()>0){
						for(Video v:temp){
							if(result.contains(v))
								continue;
							result.add(v);
						}
					}
					JujiUtils.sleep(20);
				}
			}else{
				temp = getOneMovie(tn, "", order,regexp,limit,date);
				if(null!=temp&&temp.size()>0){
					for(Video v:temp){
						if(result.contains(v))
							continue;
						result.add(v);
					}
				}
				JujiUtils.sleep(20);
			}
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param teleplayName 加上电视剧名称的版本全称，如“越狱第一季”
	 * @param episodeName 某集的名称 如“第一集”
	 * @param limit 限多少个
	 * @return
	 */
	public List<Video> getOneMovie(String teleplayName,String versionName,int order,String regexp,int limit,int date)
	{
		if(StringUtils.isBlank(regexp)){
			logger.error("正则匹配公式为空");
			return null;
		}
		List<Video> result = new ArrayList<Video>();
		String words = teleplayName+versionName+order;
		SearchNum sn = SearchNumMgt.getInstance().getSearchNumByWords(words.trim());
		if(null!=sn){
			if(sn.getNum()>2){
				SearchNumMgt.getInstance().executeSql("update search_num set num=1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
				return result;
			}
		}
		String url = null;
		String content = null;
		int stop = 0;
		JSONObject json = null;
		while(null==json&&stop<20){
			if(!StringUtils.isBlank(url))
				System.out.println("url:"+url+",result:false");
			
			SearchLogMgt.getInstance().insert(words, url, stop);
			
			url = UrlManager.getQueryTeleplayUrl(words,sort,limit,date); //对名称先处理
			content = Request.requestGet(url,100000);
			stop++;
			if(StringUtils.isBlank(content)){
				System.out.println("url:"+url+",result:false");
				if(null!=sn){
					SearchNumMgt.getInstance().executeSql("update search_num set num=num+1 where id="+sn.getId());
					logger.info("searchNum:"+sn.toString());
				}else{
					SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+words+"',1)");
				}
				return result;
			}
			try {
				json = new JSONObject(content);
				Thread.sleep(20);
			} catch (Exception e) {
				json = null;
				content = null;
			}
		}
		if(StringUtils.isBlank(content)||null==json){
			logger.error("连续请求20次失败url:"+url+"\tteleplayName:"+teleplayName+"\tversionName:"+versionName+"\torder:"+order);
			System.out.println("url:"+url+",result:false");
			if(null!=sn){
				SearchNumMgt.getInstance().executeSql("update search_num set num=num+1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
			}else{
				SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+words+"',1)");
			}
			return result;
		}else
			System.out.println("url:"+url+",result:true");
		
		try {
			
			int total = json.getInt("total");
			
			if (total < 1){
				if(null!=sn){
					SearchNumMgt.getInstance().executeSql("update search_num set num=num+1 where id="+sn.getId());
					logger.info("searchNum:"+sn.toString());
				}else{
					SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+words+"',1)");
				}
				return result;
			}
			else
				total = total > max_videos ? max_videos:total;
				
			if (limit > 0)
				total  = total >= limit ? limit : total;
			JSONObject items = json.getJSONObject("items");
			if (items != null)
			{
				int j=0;
				for (;j<total;j++)
				{
					JSONObject item = items.getJSONObject(String.valueOf(j));
					
					int seconds = item.getInt("seconds");
					if (seconds < min_seconds*60 || item.getInt("public_type")!=0)
						continue ;
					String title = item.getString("title");
					if(StringUtils.isBlank(title)||!RegexpBuilder.allMatch(title,regexp)){
						System.out.println("完全匹配失败,title:"+title+",regexp:"+regexp);
						continue;
					}
					result.add(jsonToVideo(item));
				}
			}
		}catch(Exception e)
		{
			logger.error("teleplayName="+teleplayName+"\tversionName="+versionName+"\torder="+order+"\tlimit="+limit+"\te:"+e.getMessage());
		}
		if(result.size()<1){
			if(null!=sn){
				SearchNumMgt.getInstance().executeSql("update search_num set num=num+1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
			}else{
				SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+words+"',1)");
			}
		}else{
			if(null!=sn){
				SearchNumMgt.getInstance().executeSql("update search_num set num=1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
			}
		}
		return result;
	}
	
	private Video jsonToVideo(JSONObject item) throws JSONException
	{
		Video video = new Video();
		video.setVid(item.getInt("vid"));
		video.setEncodeVid(item.getString("encodeVid"));
		video.setTitle(item.getString("title"));
		video.setLogo(item.getString("logo"));
		video.setSeconds((float)item.getDouble("seconds"));
		video.setFile_id(item.getString("md5"));
		return video;
	}
	
	/**
	 * 查询所有版本集合
	 * @param keyword
	 * @return
	 */
	public QueryResult queryAllVersionMovies(Set<String> teleplayNames)
	{
		return queryAllVersionMovies(teleplayNames,0);
	}
	
	/**
	 * 查询所有版本集合
	 * @param keyword
	 * @param limit 限制取多少条,如果多个版本,则每个版本都限制
	 * @return 
	 */
	public QueryResult queryAllVersionMovies(Set<String> teleplayNames,int limit)
	{
		QueryResult result = new QueryResult();
		
			List<Integer> versions = null;
			String versionSuffix = null;
			for (String qs:versionString){
				versions  = getVersion(teleplayNames,qs);
				if (versions != null && versions.size() >0){
					versionSuffix = qs;
					break;
				}
			}
			System.out.println("versions="+versions.size());
			if (versions == null || versions.size() <= 1)
			{
				List<Video> videos = getMovies(teleplayNames,null,limit);
				result.setVersion(1);
				result.putVersionName("");
				result.putOneVersion(videos);
			}
			else
			{
				result.setVersion(versions.get(versions.size()-1));
				Set<String> vs = null;
				for (int i=1; i<=result.getVersion();i++)
				{
					vs = new HashSet<String>();
					vs.add("第" + i + versionSuffix);
					List<Video> videos = getMovies(teleplayNames , vs,limit);
					result.putVersionName("第" + i + versionSuffix);
					result.putOneVersion(videos);
				}
			}
			
	
		return result;
	}
	
	public String[] getVersionNames(Set<String> teleplayNames)
	{
		List<Integer> versions = null;
		String versionSuffix = null;
		for (String qs:versionString){
			versions  = getVersion(teleplayNames,qs);
			if (versions != null && versions.size() >0){
				versionSuffix = qs;
				break;
			}
		}
		
		String[] result = new String[versions.size()];
		for (int i=0;i<result.length;i++)
		{
			result[i] = "第"+versions.get(i) + versionSuffix;
		}
		
		return result;
	}
	

	//获取季数
	public List<Integer> getVersion (Set<String> teleplayNames,String suffix)
	{
		List<Integer> versions = new ArrayList<Integer>();
		
		for (String word:teleplayNames)
		{
			int i=1;
			int empty_count =0;
in:			for (;i<50;i++)
			{
				String url = UrlManager.getQueryVersionUrl(word + "第" + i +suffix);
				
				String content = null;
				while(content==null){
					content = Request.requestGet(url,100000);
				}
				JSONObject json = null;
				int total = 0;
				try {
					json = new JSONObject(content);
					total = json.getInt("total");
					
					if (total < 20)
						empty_count++;
					else{
						if (!versions.contains(i))
							versions.add(i);
					}
					
				} catch (JSONException e) {
					empty_count++;
					e.printStackTrace();
				}
				if (empty_count > 5)
					break in;
			}
		}
		return versions;
	}
	
	
	static final class QueryType
	{
		int seconds; //以后可能针对不同的类型用此字段
		private QueryType(int i)
		{
			switch(i)
			{
				case 1: 
					seconds = 10;
					break;
				case 2:
					seconds = 5;
					break;
			}
		}
		
		public static QueryType DIANSHIJU = new QueryType(1); 	//电视剧
		public static QueryType ZONGYI= new QueryType(2);		//综艺节目
		
		
	}
	
	
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public static void main(String[] args) throws Exception {
		
//		System.out.println(Utils.analyzer("越狱第一季第一集"));
//		TelePlayNames names = new TelePlayNames("蜗居",null);
//		
		TeleplayQuery query = new TeleplayQuery();
		Set<String> teleplays= new HashSet<String>();
		teleplays.add("O记实录");
		teleplays.add("南方四贱客");
		teleplays.add("south park");
		Set<String> versions= new HashSet<String>();
		versions.add("第1部");
		
		QueryResult qr = query.queryAllVersionMovies(teleplays);
//		List<Video> videos  = query.getOneMovie(teleplays,versions,1,RegexpBuilder.build(teleplays,versions, 1),0,0);
//		for (String a:arr)
//		{
//			System.out.println(a);
//		}
		
//		List<Video> ll = query.getMovies(names.getSearchNameString());
		
//		for (Video video:videos)
//		{
//////			if(video.getEncodeVid().equalsIgnoreCase("XMjE4MDUyNjIw"))
//			if (video != null)
//				System.out.println(video);
//			else
//				System.out.println("空缺");
//		}
//		
//		System.out.println(videos.size());
			
//		QueryResult result = query.queryAllVersionMovies(names.getSearchNameString());
//		System.out.println(result.getVersion());
//		
		List<List<Video>> list = qr.getContent();
		for (List<Video> videos:list)
		{
			for (Video video:videos)
			{
				if (video != null)
					System.out.println(video);
				else
					System.out.println("空缺");
			}
		}
	}
}
