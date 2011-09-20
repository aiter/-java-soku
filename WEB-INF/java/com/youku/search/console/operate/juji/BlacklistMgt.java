package com.youku.search.console.operate.juji;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.Blacklist;
import com.youku.search.console.pojo.BlacklistPeer;
import com.youku.search.console.pojo.Episode;
import com.youku.search.util.DataFormat;

public class BlacklistMgt {
	static Log logger = LogFactory.getLog(BlacklistMgt.class);
	
	private static BlacklistMgt instance=null;
	public static synchronized BlacklistMgt getInstance(){
		if(null!=instance) return instance;
		else {
			instance = new BlacklistMgt();;
			return instance;
		} 
	}
	
	public void executeSql(String sql){
		try {
			BlacklistPeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public void add2Blacklist(int pid,int vid){
		if(!isInBlacklist(pid, vid)){
				Blacklist b=new Blacklist();
				b.setFkVersionId(pid);
				b.setVideoId(vid);
				try {
					b.save();
				} catch (Exception e) {
					logger.error(e);
				}
			}
	}
	
	public void add2Blacklist(Episode e){
		if(e==null||e.getVideoId()==null||e.getVideoId().trim().length()<1)return;
			if(!isInBlacklist(e.getFkVersionId(), DataFormat.parseInt(e.getVideoId()))){
				Blacklist b=new Blacklist();
				b.setFkVersionId(e.getFkVersionId());
				b.setVideoId(DataFormat.parseInt(e.getVideoId()));
				try {
					b.save();
				} catch (Exception e1) {
					logger.error(e,e1);
				}
			}
	}
	
	public static boolean isInBlacklist(int versionid,int vid){
			List<Blacklist> bl=null;
			Criteria criteria = new Criteria();
			criteria.add(BlacklistPeer.FK_VERSION_ID,versionid);
			criteria.add(BlacklistPeer.VIDEO_ID,vid);
			try {
				bl=BlacklistPeer.doSelect(criteria);
			} catch (Exception e) {
				logger.error(e);
			}
			if(null!=bl&&bl.size()>0)
				return true;
			else return false;
	}
}
