package com.youku.search.console.operate.juji;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.EpisodePeer;
import com.youku.search.console.teleplay.TeleplayQuery;
import com.youku.search.console.teleplay.VarietyQuery;
import com.youku.search.console.teleplay.Video;
import com.youku.search.console.vo.EpisodeSingleVO;
import com.youku.search.console.vo.EpisodeVO;
import com.youku.search.console.vo.TeleName;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;

public class VersionEpisodeSpiderService {
	static Log logger = LogFactory.getLog(VersionEpisodeSpiderService.class);

	private VersionEpisodeSpiderService() {
	}

	private static VersionEpisodeSpiderService instance = null;

	public static synchronized VersionEpisodeSpiderService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new VersionEpisodeSpiderService();
			return instance;
		}
	}
	
	public EpisodeVO reEpisodeAnalyze(EpisodeVO epvo,int tid,int pid,int subcate,int count,int collect_count){
		if(tid==-1)epvo= new EpisodeVO();
		Set<String> tns = PlayNameMgt.getInstance().getTeleplayNameByIdReturnSet(tid);
		Set<String> pns = PlayVersionMgt.getInstance().getVersionNameByIdReturnSet(pid);
		
		TeleName telename=PlayNameMgt.getInstance().getTeleplayNameByIdReturnTeleName(tid);
		String title = telename.getName();
		String pn = null;
		if(null!=pns&&pns.size()>0){
			pn = PlayVersionMgt.getInstance().getVersionName(pid);
			if(!StringUtils.isBlank(pn)&&!pn.trim().toLowerCase().equalsIgnoreCase("null"))
				title=title+pn;
		}
		if(subcate!=2078){
			epvo=reEpisodeAnalyze(tid, pid, tns, pns, title, count,collect_count,subcate);
		}else{
			epvo=reVarietyAnalyze(tid, pid, telename.getName(),tns,count,pn,ExcludeMgt.getInstance().getExclude(tid));
		}
		return epvo;
	}
	
	public EpisodeVO reEpisodeAnalyze(int tid, int pid, Set<String> tns, Set<String> pns,
			String title, int count, int collect_count,int subcate) {
		EpisodeVO ev =null;
		List<Video> lv = null;
		TeleplayQuery tq = new TeleplayQuery();
		if (count < 0)
			count = 0;
		lv = tq.getMovies(tns,pns, count);
		Criteria criteria = null;
		int collect = 0;
		try {
			EpisodeVideoMgt evmgt=EpisodeVideoMgt.getInstance();
			Episode e = null;
			boolean f = true;
			Video v=null;
			List<Episode> eplist=null;
			String firstlogo = "";
			synchronized (lv) {
			for (int i = 0; i < lv.size(); i++) {
				v = lv.get(i);
				criteria = new Criteria();
				criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
				criteria.add(EpisodePeer.FK_VERSION_ID, pid);
				criteria.add(EpisodePeer.ORDER_ID, i+1);
				eplist=EpisodePeer.doSelect(criteria);
				if(eplist!=null&&eplist.size()>0){
					e=eplist.get(0);
					if(e.getVideoId()!=null&&e.getVideoId().trim().length()>0&&e.getIslock()==1){
						collect = collect + 1;
						continue;
					}
					e.setIslock(0);
				}else{
					e=new Episode();
					e.setFkTeleplayId(tid);
					e.setFkVersionId(pid);
					e.setOrderId(i+1);
					e.setName("第" + (i + 1) + "集");
					e.setIslock(0);
				}
				if (null != v) {
					collect = collect + 1;
					e.setSourceName(v.getTitle());
					e.setVideoId("" + v.getVid());
					e.setEncodeVideoId(v.getEncodeVid());
					e.setLogo(v.getLogo());
					e.setSeconds(v.getSeconds());
					e.setFileId(v.getFile_id());
					if(i==0&&(firstlogo==null||firstlogo.trim().length()<1))firstlogo=v.getLogo();
				} else {
					f = false;
					e.setSourceName("");
					e.setVideoId("");
					e.setEncodeVideoId("");
					e.setLogo("");
					e.setSeconds(0);
				}
				e.save();
				if(null!=v){
					evmgt.addEpisodeVideo(e.getId(), v);
				}
			}
			}
			EpisodeMgt.getInstance().executeSql("update episode set source_name='',video_id='',encode_video_id='',logo='',seconds=0,file_id='' where fk_teleplay_id="+tid+" and fk_version_id="+pid+" and order_id>"+lv.size()+" and isLock=0");
			ev=getEpisodeVO(tid, pid, title, count, subcate);
			StringBuffer shql = new StringBuffer("episode_count=");
			shql.append(ev.getCollect_count());
			if (!f)
				shql.append(",fixed=0");
			shql.append(",firstlogo='");
			shql.append(ev.getFirstlogo());
			shql.append("'");
			PlayVersionMgt.getInstance().executeSql("update play_version set "
						+ shql.toString()+ " where id=" + pid);
		} catch (Exception e) {
			System.out
					.println("[ERROR] update episode  date error  in function reEpisodeAnalyze");
			e.printStackTrace();
		}
		return ev;
	}
	
	public EpisodeVO reVarietyAnalyze(int tid, int pid, String words, Set<String> names,int count,String keyword,String exclude) {
		//System.out.println("tid="+tid+"--pid"+pid+"---words"+words+"---alias"+alias.toString()+"---count"+count);
		EpisodeVO ev = new EpisodeVO();
		if(StringUtils.isBlank(keyword))
			ev.setTitle(words);
		else
			ev.setTitle(words+keyword);
		ev.setTid(tid);
		ev.setPid(pid);
		List<Video> lv = null;
		Set<String> excludes=null;
		if(null!=exclude&&exclude.trim().length()>0)
			excludes=Utils.parseStr2Set(exclude, "\\|");
		VarietyQuery vq = new VarietyQuery();
		if(null!=keyword&&keyword.trim().length()>0)
			lv = vq.getVarietyOneYear(names,excludes,DataFormat.parseInt(keyword));
		else
			lv = vq.getAllVariety(names, excludes);
		Criteria criteria = null;
		EpisodeSingleVO esvo = null;
		try {
			ev.setSubcate(2078);
			EpisodeVideoMgt evmgt=EpisodeVideoMgt.getInstance();
			criteria = new Criteria();
			criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
			criteria.add(EpisodePeer.FK_VERSION_ID, pid);
			List<Episode> eplist=EpisodePeer.doSelect(criteria);
			for(Episode e:eplist){
				evmgt.deleteEpisodeVideo(e.getId());
			}
			EpisodePeer.doDelete(criteria);
			PlayVersionMgt.getInstance().executeSql("update play_version set episode_count=0 where id=" + pid);
			Episode e = null;
			Video v=null;
			Set<Integer> list = new HashSet<Integer>();
			for (int i = 0; i < lv.size(); i++) {
				v = lv.get(i);
				e = new Episode();
				e.setFkTeleplayId(tid);
				e.setFkVersionId(pid);
				e.setSourceName(v.getTitle());
				e.setVideoId("" + v.getVid());
				e.setEncodeVideoId(v.getEncodeVid());
				e.setLogo(v.getLogo());
				e.setSeconds(v.getSeconds());
				e.setName(StringUtils.substring(v.getIndex(), 4, 6));
				e.setOrderId(DataFormat.parseInt(v.getIndex()));
				e.setFileId(v.getFile_id());
				e.save();
				if(null != v){
					evmgt.addEpisodeVideo(e.getId(), v);
				}
				esvo = new EpisodeSingleVO();
				esvo.setId(e.getId());
				esvo.setName(v.getIndex());
				esvo.setOrder(e.getOrderId());
				esvo.setSubcate(2078);
				if (null != v)
					esvo.setUrl("http://v.youku.com/v_show/id_"
							+ v.getEncodeVid() + ".html");
				else
					esvo.setUrl("");
				ev.getEvo().add(esvo);
				list.add(e.getOrderId());
			}
			if(count<0)
				count=0;
			ev.setCollect_count(lv.size());
			ev.setTotal_count(count);
			String firstlogo = "";
			for (Video video : lv) {
				if (video != null) {
					firstlogo = video.getLogo();
					break;
				}
			}
			if (lv.size() > 0)
				PlayVersionMgt.getInstance().executeSql("update play_version set firstlogo='" + firstlogo
						+ "',episode_count=" + lv.size()+",total_count="+count + " where id=" + pid);
			if (count > 0) {
				for (int i = 1; i <= count; i++) {
					if(list.contains(i))
						continue;
					esvo = new EpisodeSingleVO();
					esvo.setName(v.getIndex());
					esvo.setOrder(DataFormat.parseInt(v.getIndex()));
					esvo.setUrl("");
					esvo.setSubcate(2078);
					ev.getVevo().add(esvo);
				}
			}
			
		} catch (Exception e) {
			System.out
					.println("[ERROR] update episode  date error  in function reEpisodeAnalyze");
			e.printStackTrace();
		}
		return ev;
	}
	
	public EpisodeVO getEpisodeVO(int tid, int pid,
			String title, int count,int subcate) throws TorqueException {
		List<Episode> el = new ArrayList<Episode>();
		EpisodeVO ev = new EpisodeVO();
		ev.setTitle(title);
		ev.setTid(tid);
		ev.setPid(pid);
		Criteria criteria = new Criteria();
		criteria.add(EpisodePeer.FK_TELEPLAY_ID, tid);
		criteria.add(EpisodePeer.FK_VERSION_ID, pid);
		criteria.addAscendingOrderByColumn(EpisodePeer.ORDER_ID);
			ev.setSubcate(subcate);
			el = EpisodePeer.doSelect(criteria);
			int collect=0;
			EpisodeSingleVO esvo = null;
			String firstlogo="";
			Set<Integer> list = new HashSet<Integer>();
			for (int i = 0; i < el.size(); i++) {
				esvo = new EpisodeSingleVO();
				esvo.setId(el.get(i).getId());
				esvo.setIsLock(el.get(i).getIslock());
				if(ev.getSubcate()!=2078)
					esvo.setName(el.get(i).getName());
				else esvo.setName(""+el.get(i).getOrderId());
				esvo.setSubcate(ev.getSubcate());
				if (el.get(i).getEncodeVideoId() == null
						|| el.get(i).getEncodeVideoId().length() < 1)
					esvo.setUrl("");
				else{
					collect=collect+1;
					esvo.setUrl("http://v.youku.com/v_show/id_"
							+ el.get(i).getEncodeVideoId() + ".html");
					if(firstlogo==null||firstlogo.trim().length()<1)firstlogo=el.get(i).getLogo();
				}
				ev.getEvo().add(esvo);
				esvo.setOrder(el.get(i).getOrderId());
				list.add(el.get(i).getOrderId());
			}
			ev.setCollect_count(collect);
			ev.setTotal_count(count);
			ev.setFirstlogo(firstlogo);
			if (count > 0) {
				for (int i = 1; i <=count; i++) {
					if(list.contains(i))
						continue;
					esvo = new EpisodeSingleVO();
					if(ev.getSubcate()!=2078)
						esvo.setName("第" + i + "集");
					esvo.setUrl("");
					esvo.setOrder(i);
					esvo.setSubcate(ev.getSubcate());
					ev.getVevo().add(esvo);
				}
			}
		return ev;
	}
}
