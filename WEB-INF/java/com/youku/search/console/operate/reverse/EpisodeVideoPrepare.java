package com.youku.search.console.operate.reverse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.workingdogs.village.Record;
import com.youku.search.config.Config;
import com.youku.search.console.operate.juji.EpisodeMgt;
import com.youku.search.console.operate.juji.EpisodeVideoMgt;
import com.youku.search.console.operate.juji.JujiUtils;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodeVideoPeer;
import com.youku.search.console.teleplay.Video;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Wget;

public class EpisodeVideoPrepare {
	static Log logger = LogFactory.getLog(EpisodeVideoPrepare.class);

	private EpisodeVideoPrepare() {
	}

	private static EpisodeVideoPrepare instance = null;

	public static synchronized EpisodeVideoPrepare getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new EpisodeVideoPrepare();
			return instance;
		}
	}
	private List<int[]> getEpisodeVideos(){
		String sql = "select id,video_id from episode_video where shield=0 and file_id is not null order by id asc";
		List<int[]> vids = new ArrayList<int[]>();
		try {
			List<Record> res = EpisodeVideoPeer.executeQuery(sql,"searchteleplay");
			if(null==res||res.size()<1) return vids;
			int s = 0;
			for(Record r:res){
				s = r.getValue("video_id").asInt();
				if (0 != s) {
					try {
						vids.add(new int[] { r.getValue("id").asInt(),
								s});
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error(sql,e);
		}
		return vids;
	}
	
	public void updateFileID(){
		List<int[]> vids=null;
		try {
			vids=getEpisodeVideos();
			if(null==vids||vids.size()<1)return;
			Video v=null;
			for(int[] vid:vids){
				v=JujiUtils.getInstance().getVideoByDB(vid[1]);
				if(null!=v&&v.getVid()>0){
					doUpdateFileid(v.getFile_id(), vid[0]);
					Thread.sleep(35);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doUpdateFileid(String fileid,int id){
		try {
			EpisodeVideoMgt.getInstance().executeSql("update episode_video set file_id='"+fileid+"' where id="+id,"update fileid");
		} catch (Exception e) {
			logger.error("error:sql:update episode_video set file_id='"+fileid+"' where id="+id);
		}
	}
	
	public void insertEpisodeVideo(){
		try{
			List<Episode> episodes=EpisodeMgt.getInstance().getAllEpisode();
			if(null!=episodes&&episodes.size()>0){
				List<Video> vl=null;
				Video v=null;
				String url=null;
				for(Episode episode:episodes){
					v=JujiUtils.getInstance().getVideoByDB(DataFormat.parseInt(episode.getVideoId()));
					if(null==v||v.getVid()<1)continue;
					int i = 1;
					while(true){
						url="http://"+Config.getYoukuHost()+"/search?type=18&curpage="+i+"&pagesize=100&order=1&orderfield=1&md5=1&keyword="+v.getFile_id();
						vl=getVideoListByVid(url);
						if(null==vl||vl.size()<1) break;
						for(Video video:vl){
							EpisodeVideoMgt.getInstance().addEpisodeVideo(episode.getId(),video);
							Thread.sleep(35);
						}
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<Video> getVideoListByVid(String url) throws Exception{
		List<Video> videos=new ArrayList<Video>();
		byte[] bytes=Wget.get(url);
		if(null==bytes)return videos;
		String response=new String(bytes,"utf-8");
//		System.out.println(response);
		videos=jsonStrToList(response);
		return videos;
	}
	
	private List<Video> jsonStrToList(String jsonstr){
		List<Video> videos=new ArrayList<Video>();
		try {
			JSONObject json=new JSONObject(jsonstr);
			JSONObject jsondata=json.getJSONObject("items");
			Iterator iterator = jsondata.keys();
			Video v=null;
			while(iterator.hasNext()){
				JSONObject data=jsondata.getJSONObject(""+iterator.next());
				v=jsonToVideo(data);
				if(null!=v)
				videos.add(v);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			System.err.println("json :"+jsonstr);
		}
		return videos;
	}
	
	private Video jsonToVideo(JSONObject data){
		Video v = null;
		try{
			v=new Video();
			v.setVid(data.getInt("vid"));
			v.setEncodeVid(data.getString("encodeVid"));
			v.setFile_id(data.getString("md5"));
			v.setLogo(data.getString("logo"));
			v.setTitle(data.getString("title"));
			v.setSeconds(DataFormat.parseFloat(data.getString("seconds"), 0L));
		}catch(Exception e){
			System.err.println("json :"+data);
		}
		return v;
	}
	
	public static void main(String[] args) throws Exception {
		EpisodeVideoPrepare ev=new EpisodeVideoPrepare();
		byte[] bytes=Wget.get("http://"+Config.getYoukuHost()+"/search?type=18&keyword=020064060047d1a7e04efa00272e7bdd07b759-8294-cd62-72aa-c61620b25f31&curpage=1&pagesize=100&order=1&orderfield=1&md5=1");
		String res=new String(bytes,"utf-8");
		List<Video> videos=ev.jsonStrToList(res);
		for(Video v:videos)
			System.out.println(v);
	}
}
