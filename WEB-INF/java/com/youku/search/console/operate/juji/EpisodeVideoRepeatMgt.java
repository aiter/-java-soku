package com.youku.search.console.operate.juji;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodePeer;
import com.youku.search.console.pojo.EpisodeVideo;
import com.youku.search.console.pojo.EpisodeVideoPeer;
import com.youku.search.console.vo.EpisodeRepeatVO;
import com.youku.search.console.vo.EpisodeRepeatVO.Repeat;

public class EpisodeVideoRepeatMgt {
	
	static Log logger = LogFactory.getLog(EpisodeVideoRepeatMgt.class);

	private EpisodeVideoRepeatMgt() {
	}

	private static EpisodeVideoRepeatMgt instance = null;

	public static synchronized EpisodeVideoRepeatMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new EpisodeVideoRepeatMgt();
			return instance;
		}
	}
	
	public List<EpisodeRepeatVO> getEpisodeArrs() {
		List<EpisodeRepeatVO> arrslist=new ArrayList<EpisodeRepeatVO>();
		EpisodeRepeatVO ervo=null;
		Repeat r=null;
		List<Integer[]> idlist=null;
		List<String> fileids=null;
		try{
			 fileids=getEpisodeVideos();
			 for(String file_id:fileids){
				 idlist=getEpisodeVideos(file_id);
				 ervo=new EpisodeRepeatVO();
//				 arrlist=new ArrayList<String[]>();
				 if(null!=idlist&&idlist.size()>0){
				 for(Integer[] ids:idlist){
					r=ervo.getRepeat();
					r.setTname(PlayNameMgt.getInstance().getTeleplayMainName(ids[1]));
					r.setPvname(PlayVersionMgt.getInstance().getViewNameFromVersion(ids[2]));
					r.setOrder(ids[3]);
					r.setUrlstr(EpisodeMgt.getInstance().getEpisodeUrl(ids[0]));
					ervo.getRepeatVideo().add(r);
					}
				 arrslist.add(ervo);
				 }
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		return arrslist;
	}
	
	/**
	 * 找出episode表中所有重复的视频
	 * @param conn
	 * @return
	 */
	private List<String> getEpisodeVideos() {
		String hql = "select file_id from episode where video_id!='' group by file_id having count(*)>1";
		List<String> idlist=new ArrayList<String>();
		try {
			List<Record> res = EpisodePeer.executeQuery(hql,"searchteleplay");
			for(Record r:res){
				idlist.add(r.getValue("file_id").asString());
			}
		} catch (Exception e) {
			System.out
					.println("[ERROR] get episode_video id ERROR in function getEpisodeVideoIds!");
			e.printStackTrace();
		} 
		return idlist;
	}
	
	
	private List<Integer[]> getEpisodeVideos(String file_id) {
		String hql = "select id,fk_teleplay_id,fk_version_id,order_id from episode where file_id= '"+file_id+"'";
		List<Integer[]> idlist=new ArrayList<Integer[]>();
		try {
			List<Record> res = EpisodePeer.executeQuery(hql,"searchteleplay");
			for(Record r:res){
				idlist.add(new Integer[]{r.getValue("id").asInt(),r.getValue("fk_teleplay_id").asInt(),r.getValue("fk_version_id").asInt(),r.getValue("order_id").asInt()});
			}
		} catch (Exception e) {
			System.out
					.println("[ERROR] get episode_video id ERROR in function getEpisodeVideoIds!");
			e.printStackTrace();
		}
		return idlist;
	}
	
	public void deleteevs(){
		try{
			deleteEpisodes();
		}catch(Exception e){
			System.out.println("[ERROR] delete episode_video error in function deleteEpisodeVideo");
		}
	}
	
	public List<Episode> deleteEpisodes() throws TorqueException{
		List<Integer> videos=getVideos();
		List<EpisodeVideo> episodeVideos=null;
		Criteria criteria=null;
		List<Episode> episodes=null;
		for(int video_id:videos){
			episodeVideos=getEpisodeVideos(video_id);
			for(EpisodeVideo ev:episodeVideos){
				criteria=new Criteria();
				criteria.add(EpisodePeer.VIDEO_ID,ev.getVideoId());
				criteria.add(EpisodePeer.ID,ev.getFkEpisodeId());
				episodes=EpisodePeer.doSelect(criteria);
				if(null==episodes||episodes.size()<1)
					EpisodeVideoMgt.getInstance().executeSql("delete from episode_video where id="+ev.getId(),"delete");
			}
		}
		return episodes;
	}
	
	public List<Integer> getVideos(){
		List<Integer> videos=new ArrayList<Integer>();
		String sql="select video_id from episode_video group by video_id having count(*)>1";
		try {
			List<Record> res = EpisodeVideoPeer.executeQuery(sql,"searchteleplay");
			for(Record r:res){
				videos.add(r.getValue("video_id").asInt());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return videos;
	}
	
	public List<EpisodeVideo> getEpisodeVideos(int video_id) throws TorqueException{
		Criteria criteria=new Criteria();
		criteria.add(EpisodeVideoPeer.VIDEO_ID,video_id);
		return EpisodeVideoPeer.doSelect(criteria);
	}
}
