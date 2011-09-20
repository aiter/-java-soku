/**
 * 
 */
package com.youku.soku.web;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.soku.sort.Parameter;
import com.youku.soku.web.controller.Receiver.Forward;
/**
 * @author 1verge
 *
 */
public class SearchResult {
	
	Logger logger = Logger.getLogger(SearchResult.class.getName());
	private Forward forward ;
	
	private Content content;
	
	private String cost;
	private JSONObject json;
	
	private String keyword;
	private Parameter param;
	
	public SearchResult(Forward forward)
	{
		this.forward = forward;
	}
	public SearchResult(JSONObject json)
	{
		this.json = json;
		parser(json);
	}
	
	private void parser(JSONObject rootJson)
	{
		content = new Content();
		
		if (rootJson != null){
			if (rootJson.has("items")){
				content.setVideos(rootJson.optJSONArray("items"));
				int total = rootJson.optInt("total");
				content.setTotal(total);
			}
			if (rootJson.has("movie")){
				content.setMovie(rootJson.optJSONObject("movie"));
			}
			if (rootJson.has("teleplay")){
				content.setTeleplay(rootJson.optJSONObject("teleplay"));
			}
			if (rootJson.has("anime")){
				content.setCartoon(rootJson.optJSONObject("anime"));
			}
			if (rootJson.has("variety")){
				content.setVariety(rootJson.optJSONObject("variety"));
			}
			if (rootJson.has("person")){
				content.setPerson(rootJson.optJSONObject("person"));
			}
			if (rootJson.has("nameTypes")){
				content.setTabs(rootJson.optJSONArray("nameTypes"));
			}
			
			String correct_word = rootJson.optString("correct_word");
			if(correct_word!=null && !correct_word.isEmpty())
			{
				content.setCorrect_word(correct_word);
			}
			
			JSONObject jo= rootJson.optJSONObject("like_words");
			if (jo != null){
				JSONArray like_words = jo.optJSONArray("items");
				if(like_words!= null && like_words.length()>0)
				{
					content.setLike_words(like_words);
				}
			}
			
			if (rootJson.has("major_term")){
					content.setMajor(rootJson.optJSONObject("major_term"));
			}
			
			JSONArray sites = rootJson.optJSONArray("sites");
			if(sites!= null && sites.length()>0)
			{
				content.setSites(sites);
			}
//				
//				{
//					logger.info("sort cost:"+rootJson.getInt("cost"));
//					
//					JSONArray items = rootJson.optJSONArray("items");
//					if (items != null){
//						content.setVideos(items);
//					}
					JSONObject ext = rootJson.optJSONObject("ext");
					if (ext != null){
						content.setExt(ext);
						
						JSONObject teleplay = ext.optJSONObject("teleplay");
						if (teleplay != null && teleplay.length() > 0){
							content.setTeleplay(teleplay);
						}
						
						JSONObject movie = ext.optJSONObject("movie");
						if (movie != null && movie.length() > 0){
							content.setMovie(movie);
						}
						
						JSONObject anime = ext.optJSONObject("anime");
						if (anime != null && anime.length() > 0){
							content.setCartoon(anime);
						}
						
						JSONObject variety = ext.optJSONObject("variety");
						if (variety != null && variety.length() > 0){
							content.setVariety(variety);
						}
						
						JSONObject person = ext.optJSONObject("person");
						if (person != null && person.length() > 0){
							content.setPerson(person);
						}
						
						JSONArray tabs = ext.optJSONArray("nameTypes");
						if (tabs != null && tabs.length() > 0){
							content.setTabs(tabs);
						}
					}
//					
//				}
//			this.content = content;
		}
		else
			logger.error("json is null!");
	}
	
	
	
	public Forward getForward() {
		return forward;
	}

	public Content getContent() {
		return content;
	}
	
	public void setForward(Forward forward)
	{
		this.forward = forward;
	}

	public String getCost() {
		return cost;
	}


	public void setCost(String cost) {
		this.cost = cost;
	}


	public class Content{
		
		int total ;
		
		
		private JSONArray videos;	//正常搜索结果
		private JSONObject movie;	//电影信息
		private JSONObject teleplay;		//电视剧
		private JSONObject variety;	//综艺
		private JSONObject person;	//人物
		private JSONObject cartoon;	//动漫
		private String correct_word; //纠错
		private JSONArray like_words; //相关
		private JSONArray sites; //相关
		private JSONArray tabs; //tabs
		private JSONObject major;	//动漫
		
		private JSONObject ext;  //新版直达区
	
		
		public JSONArray getSites() {
			return sites;
		}
		public void setSites(JSONArray sites) {
			this.sites = sites;
		}
		public String getCorrect_word() {
			return correct_word;
		}
		public void setCorrect_word(String correct_word) {
			this.correct_word = correct_word;
		}
		public JSONArray getLike_words() {
			return like_words;
		}
		public void setLike_words(JSONArray like_words) {
			this.like_words = like_words;
		}
		public JSONObject getCartoon() {
			return cartoon;
		}
		public void setCartoon(JSONObject cartoon) {
			this.cartoon = cartoon;
		}
		public JSONObject getTeleplay() {
			return teleplay;
		}
		public void setTeleplay(JSONObject teleplay) {
			this.teleplay = teleplay;
		}
		public JSONArray getVideos() {
			return videos;
		}
		public void setVideos(JSONArray videos) {
			this.videos = videos;
		}
		public JSONObject getMovie() {
			return movie;
		}
		public void setMovie(JSONObject movie) {
			this.movie = movie;
		}
		
		
		public JSONObject getVariety() {
			return variety;
		}
		public void setVariety(JSONObject variety) {
			this.variety = variety;
		}
		public JSONObject getPerson() {
			return person;
		}
		public void setPerson(JSONObject person) {
			this.person = person;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public JSONArray getTabs() {
			return tabs;
		}
		public void setTabs(JSONArray tabs) {
			this.tabs = tabs;
		}
		public JSONObject getMajor() {
			return major;
		}
		public void setMajor(JSONObject major) {
			this.major = major;
		}
		public JSONObject getExt() {
			return ext;
		}
		public void setExt(JSONObject ext) {
			this.ext = ext;
		}
		
	}
	public JSONObject getJson()
	{
		return json;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public Parameter getParam() {
		return param;
	}
	public void setParam(Parameter param) {
		this.param = param;
	}
	public static void main(String[] args) throws JSONException
	{
//		try
//		{
//			URL url = new URL("http://10.102.23.32/extsearch?cate_id=1&h&episode_start=1&episode_limit=3&episode_id=245637");
//			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
//	
//			InputStream in = uc.getInputStream();
//			in = new BufferedInputStream(in);
//			Reader r = new InputStreamReader(in, "UTF-8");
//			int c;
//			while ((c = r.read()) != -1)
//				System.out.print((char) c);
//			in.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}

		System.out.println(DataFormat.formatDate("2010-02-01 11:42:54.0").after(DataFormat.formatDate("2010-01-05 0:0:0")));
	}
	
}
