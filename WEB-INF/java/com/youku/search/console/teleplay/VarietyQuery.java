/**
 * 
 */
package com.youku.search.console.teleplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.youku.search.util.DataFormat;
import com.youku.search.util.Request;

public class VarietyQuery {
	static Log logger = LogFactory.getLog(VarietyQuery.class);
	
	protected int min_seconds = 10;//最少时常限制,少于此值,则不满足条件
	
	protected static final int max_videos = 3000;
	
	private Rule rule = null;
	
	private Date startDate = null;
	
	public VarietyQuery(){
		startDate = DataFormat.parseUtilDate("2002-01-01",DataFormat.FMT_DATE_YYYYMMDD);
	}
	
	
	/**
	 * 获取视频
	 * @param words
	 * @param exclude
	 * @return
	 */
	public List<Video> getAllVariety(Set<String> names,Collection<String> exclude )
	{
		List<Video> result = new ArrayList<Video>();
		Date date = startDate;
		Date now = new Date();
		while (true)
		{
			Video video = getVarietyOneDay(names,exclude,date);
			if (video != null)
			{
				result.add(video);
			}
			date = DataFormat.getNextDate(date,1);
			if (!date.before(now))
				break;
			JujiUtils.sleep(20);
		}
		
		return result;
	}
	
	public List<Video> getVarietyOneYear(Set<String> names,Collection<String> exclude ,int year)
	{
		List<Video> result = new ArrayList<Video>();
		Date date = DataFormat.parseUtilDate(year+"-01-01",DataFormat.FMT_DATE_YYYYMMDD);
		Date endDate = DataFormat.parseUtilDate((year+1)+"-01-01",DataFormat.FMT_DATE_YYYYMMDD);
		Date now = new Date();
		if (endDate.after(now))
			endDate=now;
		while (true)
		{
			Video video = getVarietyOneDay(names,exclude,date);
			if (video != null)
			{
				result.add(video);
			}
			date = DataFormat.getNextDate(date,1);
			if (!date.before(endDate))
				break;
			JujiUtils.sleep(20);
		}
		
		return result;
	}
	
	/**
	 * 获取某天最匹配的视频
	 * @param words
	 * @param exclude
	 * @param date
	 * @return 只返回最匹配的一个，若要查询所有使用getOneVariety
	 */
	public Video getVarietyOneDay(Set<String> names,Collection<String> exclude ,Date date)
	{
		if (rule != null)
		{
			if (!rule.isValid(date))
				return null;
		}
		
		List<Video> vs = getOneVariety(names,exclude,date,5,0);
		if (vs != null && vs.size() > 0)
		{
			return vs.get(0);
		}
		return null;

	}
	
	/**
	 * 查询某天节目相关的视频
	 * @param name
	 * @param exclude
	 * @param alias 别名
	 * @param theDay 哪天的节目
	 * @param limit
	 * @param before 几天前上传的视频,0不限制
	 * @return
	 */
	public List<Video> getOneVariety(Set<String> names,Collection<String> exclude,Date theDay,int limit,int before)
	{
		
		List<Video> result = new ArrayList<Video>();
		List<Video> videos = null;
		String regexp = RegexpBuilder.build(names, DataFormat.parseInt(DataFormat.formatDate(theDay, DataFormat.FMT_DATE_SPECIAL)));
		if(StringUtils.isBlank(regexp)){
			logger.error("正则匹配公式出错:"+names+","+theDay);
		}
		for(String name:names){
			videos = getOneVariety(name, exclude, theDay, limit, before, regexp);
			if(null!=videos&&videos.size()>0){
				for(Video v:videos){
					if(result.contains(v))
						continue;
					result.add(v);
				}
			}
			JujiUtils.sleep(20);
		}
		return result;
	}
	
	public List<Video> getOneVariety(String name,Collection<String> exclude,Date theDay,int limit,int before,String regexp)
	{
		String s1 = DataFormat.formatDate(theDay,DataFormat.FMT_DATE_SPECIAL);
		List<Video> result = new ArrayList<Video>();
		String url = null; //对名称先处理
		String content = null;
		int stop = 0;
		JSONObject json = null;
		SearchNum sn = SearchNumMgt.getInstance().getSearchNumByWords((name+s1).trim());
		if(null!=sn){
			if(sn.getNum()>2){
				SearchNumMgt.getInstance().executeSql("update search_num set num=1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
				return result;
			}
		}
		while(null==json&&stop<20){
			if(!StringUtils.isBlank(url))
				System.out.println("url:"+url+",result:false");
			url = UrlManager.getQueryVarietyUrl(name+s1,null,limit,before);
			
			SearchLogMgt.getInstance().insert(name+s1, url, stop);
			
			content = Request.requestGet(url,100000);
			stop++;
			if(StringUtils.isBlank(content)){
				System.out.println("url:"+url+",result:false");
				if(null!=sn){
					SearchNumMgt.getInstance().executeSql("update search_num set num=num+1 where id="+sn.getId());
					logger.info("searchNum:"+sn.toString());
				}else{
					SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+(name+s1).trim()+"',1)");
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
			logger.error("连续请求20次失败url:"+url+"\tvarietyName:"+name+"\tdate:"+theDay);
			System.out.println("url:"+url+",result:false");
			if(null!=sn){
				SearchNumMgt.getInstance().executeSql("update search_num set num=num+1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
			}else{
				SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+(name+s1).trim()+"',1)");
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
					SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+(name+s1).trim()+"',1)");
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
					if(StringUtils.isBlank(title)||!RegexpBuilder.varietyAllMatch(title,regexp,exclude)){
						System.out.println("完全匹配失败,title:"+title+",regexp:"+regexp+",exclude:"+exclude);
						continue;
					}
					
					Video video = jsonToVideo(item);
					video.setIndex(s1);
					result.add(video);
				}
			}
		}catch(Exception e)
		{
			
		}
		if(result.size()<1){
			if(null!=sn){
				SearchNumMgt.getInstance().executeSql("update search_num set num=num+1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
			}else{
				SearchNumMgt.getInstance().executeSql("insert search_num (words,num) values ('"+(name+s1).trim()+"',1)");
			}
		}else{
			if(null!=sn){
				SearchNumMgt.getInstance().executeSql("update search_num set num=1 where id="+sn.getId());
				logger.info("searchNum:"+sn.toString());
			}
		}
		return result;
	}
	
	//获取有视频的年份
	public List<Integer> getVersions (Set<String> names)
	{
		List<Integer> versions = new ArrayList<Integer>();
		
		for (String word:names)
		{
			int i = 2002;
			int now = DataFormat.getYear(new Date());
			int empty_count =0;
in:			for (;i<=now;i++)
			{
				String url = UrlManager.getQueryVersionUrl(word + i );
				String content = Request.requestGet(url,30000);
				
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
	 * 改变开始日期
	 * @param start
	 */
	public void setStartDate(Date start)
	{
		startDate = start;
	}
	/**
	 * 设置节目播放日期规则
	 * @param r
	 */
	public void setRule(Rule r)
	{
		rule = r;
	}
	
	public static void main(String[] args)
	{
//		System.out.println(MyUtil.urlEncode(" "));
		VarietyQuery query = new VarietyQuery();
		Set<String> teleplays= new HashSet<String>();
		teleplays.add("康熙来了");
		teleplays.add("康熙");
		System.out.println(RegexpBuilder.allMatch("091211 康熙来了", "((康熙来了|康熙)[ ]*(([0 ]*9|2009)[-,_,/,年, ]*[0 ]*12[-,_,/,月, ]*[0 ]*11))|((([0 ]*9|2009)[-,_,/,年, ]*[0 ]*12[-,_,/,月, ]*[0 ]*11)[ ]*(康熙来了|康熙))"));
		List<Video> videos = query.getOneVariety(teleplays, null,DataFormat.parseUtilDate("20091214",DataFormat.FMT_DATE_SPECIAL),0,20);
//
		for (Video video:videos)
		{
			System.out.println(video.getIndex()+"\t"+video.getTitle()+"\t"+video.getVid());
		}
//		System.out.println(videos.size());
//		System.out.println(videos.size());
//		System.out.println(new Date(2010-1900,10-1,6));
//		System.out.println(query.allMatch("康熙来了 061211", "康熙来了", null, new Date(2006-1900,12-1,11)));
		
//		query.setStartDate(DataFormat.parseUtilDate("2008-01-01",DataFormat.FMT_DATE_YYYYMMDD));
//		Rule rule = new Rule(new int[]{1,2,3,4});//星期1，2，3，4播出
//		query.setRule(rule);
//		List<Video> videos = query.getVarietyOneYear("星光大道", null,new String[]{"超級星光大道"}, 2009);
//
//		for (Video video:videos)
//		{
//			System.out.println(video.getIndex()+"\t"+video.getTitle());
//		}
//		System.out.println(videos.size());
	}
}
