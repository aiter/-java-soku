package com.youku.search.console.operate.juji;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;

import com.workingdogs.village.Record;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.RegexpBuilder;
import com.youku.search.console.operate.TeleplaySpideMgt;
import com.youku.search.console.pojo.EpisodeVideo;
import com.youku.search.console.pojo.PlayName;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.pojo.PlayVersionPeer;
import com.youku.search.console.teleplay.TeleplayQuery;
import com.youku.search.console.teleplay.VarietyQuery;
import com.youku.search.console.teleplay.Video;
import com.youku.search.recomend.FilterUtils;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.Utils;

public class TeleplayExecute {
	static Log logger = LogFactory.getLog(TeleplayExecute.class);

	private TeleplayExecute() {
	}

	private static TeleplayExecute instance = null;

	public static synchronized TeleplayExecute getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new TeleplayExecute();
			return instance;
		}
	}

	/**
	 * 有视屏的剧集
	 * @return
	 */
	private List<Integer[]> getAllEpisodeID() {
		
		String sql = "select a.id,a.fk_teleplay_id,a.fk_version_id,a.video_id,a.order_id from episode a,teleplay b where a.fk_teleplay_id=b.id and b.is_valid=1 and a.video_id!=''";
		PreparedStatement pt = null;
		ResultSet rs = null;
		Connection conn = null;
		List<Integer[]> vids = new ArrayList<Integer[]>();
		try {
			conn = DataConn.getTeleplayConn();
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			String s = null;
			while (rs.next()) {
				s = rs.getString("a.video_id");
				if (null != s && s.trim().length() > 0) {
					try {
						vids.add(new Integer[] { rs.getInt("a.id"),
								rs.getInt("a.fk_teleplay_id"),
								rs.getInt("a.fk_version_id"),
								rs.getInt("a.order_id"), Integer.parseInt(s) });
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			logger.error(sql,e);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
			JdbcUtil.close(conn);
		}
		return vids;
	}

	/**
	 * 检测剧集状态
	 */
	public void updateEpisode() {
		List<Integer[]> vids = null;
		try {
			vids = getAllEpisodeID();
			TeleplayQuery query = new TeleplayQuery();
			Video v = null;
			List<Video> vl = null;
			int temp=-1;
			int versionid=0;
			int subcate=0;
			VarietyQuery vq = new VarietyQuery();
			if(null==vids)return;
			for (Integer[] ids : vids) {
				v = JujiUtils.getInstance().getVideoByDB(ids[4]);
				if (null == v) {
					System.out.println("episode_id:"+ids[0]+"\tvid:"+ids[4]);
					versionid=ids[2];
					if(temp!=versionid){
						PlayVersion pv=PlayVersionMgt.getInstance().getPlayVersionByID(ids[2]);
						if(null==pv)continue;
						subcate=pv.getSubcate();
						temp=versionid;
					}
					Set<String> tns = PlayNameMgt.getInstance().getTeleplayNameByIdReturnSet(ids[1]);
					Set<String> pns = PlayVersionMgt.getInstance().getVersionNameByIdReturnSet(ids[2]);
					String regexp = RegexpBuilder.build(tns,pns, ids[3]);
					if(subcate!=2078){
						vl = query.getOneMovie(tns,pns, ids[3],regexp);
					}else{
						String exclude=ExcludeMgt.getInstance().getExclude(ids[1]);
						Set<String> excludeSet=null;
						if(!StringUtils.isBlank(exclude))
							excludeSet = Utils.parseStr2Set(exclude, "\\|");
						vl=vq.getOneVariety(tns,excludeSet, DataFormat.parseUtilDate(""+ids[3], DataFormat.FMT_DATE_SPECIAL),10,0);
					}
					for (Video video : vl) {
						if (null == video)
							continue;
						if (!BlacklistMgt.isInBlacklist(ids[2], video.getVid())) {
							v = video;
							break;
						}
					}
					if (null != v){
						EpisodeMgt.getInstance().executeSql(
								"update episode set isLock=0, source_name='"
										+ v.getTitle() + "',video_id='"
										+ v.getVid() + "',encode_video_id='"
										+ v.getEncodeVid() + "',logo='"
										+ v.getLogo() + "',seconds="
										+ v.getSeconds() +",file_id='"+v.getFile_id()
										+ "' where id="
										+ ids[0]);
						if(subcate!=2078)
							EpisodeLogMgt.getInstance().save(TeleplayService.getInstance().getfullWords(ids[0],true), ids[0]);
						else
							EpisodeLogMgt.getInstance().save(PlayNameMgt.getInstance().getTeleplayMainName(ids[1])+ids[3], ids[0]);
						EpisodeVideoMgt.getInstance().addEpisodeVideo(ids[0], v);
						
					}else{
						EpisodeMgt.getInstance().executeSql(
										"update episode set isLock=0, source_name='',video_id='',encode_video_id='',logo='',seconds=0,file_id='' where id="
												+ ids[0]);
						PlayVersionMgt.getInstance().executeSql(
								"update play_version set episode_count=episode_count-1,fixed=0 where id=" + ids[2]);
						
						EpisodeVideoMgt.getInstance().executeSql("delete from episode_video where fk_episode_id ="
										+ ids[0],"delete");
						EpisodeLogMgt.getInstance().delete(ids[0]);
					}
				}

			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 查找符合屏蔽的电视剧ID
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<Integer> searchValidTeleplayID() throws TorqueException {
		List<PlayName> tl = null;
		List<Integer> tidlist = new ArrayList<Integer>();
			tl = PlayNameMgt.getInstance().getAllPlayNames();
			if (tl != null && tl.size() > 0) {
				for (PlayName p : tl) {
					if (p.getName() == null || p.getName().trim().length() < 1)
						continue;
					if (tidlist.contains(p.getFkTeleplayId()))
						continue;
					if (FilterUtils.isFilter(p.getName())) {
						tidlist.add(p.getFkTeleplayId());
						continue;
					}
				}
			}
		return tidlist;
	}

	public void updateTeleplayStatus(int tid, int status) {
		String sql = "update teleplay set is_valid=" + status + " where id=? ";
		TeleplayMgt.getInstance().executeSql(sql);
	}

	/**
	 * 相关剧集
	 * @return
	 */
	public List<EpisodeVideo> getEpisodeVideoList(){
		List<EpisodeVideo> tl = new ArrayList<EpisodeVideo>();
		String sql = "select a.* from episode_video a,teleplay b,episode c where c.fk_teleplay_id=b.id and b.is_valid=1 and a.fk_episode_id=c.id";
		Connection conn = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		EpisodeVideo ev=null;
		try {
			conn = DataConn.getTeleplayConn();
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				ev=new EpisodeVideo();
				ev.setId(rs.getInt("a.id"));
				ev.setFkEpisodeId(rs.getInt("a.fk_episode_id"));
				ev.setVideoId(rs.getInt("a.video_id"));
				ev.setStatusNotify(rs.getInt("a.status_notify"));
				ev.setShield(rs.getInt("a.shield"));
				ev.setSourceName(rs.getString("a.source_name"));
				ev.setEncodeVideoId(rs.getString("a.encode_video_id"));
				ev.setLogo(rs.getString("a.logo"));
				ev.setSeconds(rs.getDouble("a.seconds"));
				tl.add(ev);
			}
		} catch (Exception e) {
			logger.error(sql,e);
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
			JdbcUtil.close(conn);
		}
		return tl;
		
	}
	
	/**
	 * 检测相关剧集状态
	 */
	public void updateEpisodeVideo() {
		List<EpisodeVideo> tl = null;
		try {
			tl = getEpisodeVideoList();
			Video v = null;
			for (EpisodeVideo ev : tl) {
				if (ev.getVideoId() == 0)
					continue;
				v = JujiUtils.getInstance().getVideoByDB(ev.getVideoId());
				if (null != v) {
					String hql="";
					if (ev.getShield() == 1) {
						if(ev.getEncodeVideoId()==null||ev.getEncodeVideoId().trim().length()<1){
							hql=",seconds="+v.getSeconds()+",encode_video_id='"+v.getEncodeVid()+"',source_name='"+v.getTitle()+"',logo='"+v.getLogo()+"',file_id='"+v.getFile_id()+"' ";
						}
						EpisodeVideoMgt.getInstance().executeSql("update episode_video set shield=0"+hql+" where id="+ev.getId(),"update shield");
					}
				} else {
					if (ev.getShield() == 0) {
						EpisodeVideoMgt.getInstance().executeSql("update episode_video set shield=1 where id="+ev.getId(),"update shield");
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void teleplayruntask() throws Exception {
		String hql = "select a.* from play_version a,teleplay b where a.fixed=0 and b.id=a.fk_teleplay_id and b.is_valid=1 order by a.id";
		boolean f = true;
		try {
			List<Record> res = PlayVersionPeer.executeQuery(hql,"searchteleplay");
			if(null==res) return;
			int subcate=0;
			for(Record r:res) {
				try{
				subcate=r.getValue("subcate").asInt();
				if(subcate!=2078){
					f = f
						&& EpisodeAnalyzeService.getInstance().episodeAnalyze(r.getValue("fk_teleplay_id").asInt(), r.getValue("id").asInt(),r.getValue("total_count").asInt());
				}else{
					f=f&&VarietyMgt.getInstance().varietyAnalyze(r.getValue("fk_teleplay_id").asInt(), r.getValue("id").asInt(),r.getValue("name").asString(),r.getValue("total_count").asInt());
				}
				}catch(Exception e){
					logger.error(e);
				}
			}
		} catch (Exception e) {
			logger.error(hql,e);
		}
	}
	
	public void teleplaySpide(){
		List<String> tel_names=null;
		try {
			tel_names=com.youku.search.spider.baidu.Main.get();
		} catch (Exception e) {
			logger.error("[ERROR] www.baidu.com spider error!",e);
		}
		if(null!=tel_names&&tel_names.size()>0){
			TeleplaySpideMgt t=TeleplaySpideMgt.getInstance();
			try {
				for(String name:tel_names)
					t.save(name);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
}
