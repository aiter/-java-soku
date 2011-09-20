package com.youku.search.console.operate.juji;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.workingdogs.village.Record;
import com.youku.search.console.pojo.EpisodeVideoUpdateInfo;
import com.youku.search.console.pojo.EpisodeVideoUpdateInfoPeer;
import com.youku.search.util.DataFormat;

public class EpisodeVideoUpdateInfoMgt {
	
static Log logger = LogFactory.getLog(EpisodeVideoUpdateInfoMgt.class);
	
	private static EpisodeVideoUpdateInfoMgt instance=null;
	public static synchronized EpisodeVideoUpdateInfoMgt getInstance(){
		if(null!=instance) return instance;
		else {
			instance = new EpisodeVideoUpdateInfoMgt();;
			return instance;
		} 
	}
	
	public void insert(String name,int status,int num){
		EpisodeVideoUpdateInfo info = new EpisodeVideoUpdateInfo();
		info.setNum(num);
		info.setState(""+status);
		info.setOperateName(name);
		info.setStampDate(DataFormat.formatDate(new Date(System.currentTimeMillis()), DataFormat.FMT_DATE_YYYYMMDD_HHMMSS));
		info.setDayDate(DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD));
		try {
			info.save();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public List<EpisodeVideoUpdateInfo> getInfoByDayDate(String day_date){
		List<EpisodeVideoUpdateInfo> infos = new ArrayList<EpisodeVideoUpdateInfo>();
		List<Record> res = null;
		try {
			res = EpisodeVideoUpdateInfoPeer.executeQuery("select operate_name,day_date,state,sum(num) as num from episode_video_update_info where day_date='"+day_date+"' group by operate_name,state","searchteleplay");
		} catch (Exception e) {
			logger.error(e);
		}
		if(null!=res){
			EpisodeVideoUpdateInfo info = null;
			for(Record r:res){
				info = new EpisodeVideoUpdateInfo();
				try {
					info.setNum(r.getValue("num").asInt());
					info.setState(r.getValue("state").asString());
					info.setOperateName(r.getValue("operate_name").asString());
					info.setDayDate(day_date);
					infos.add(info);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		return infos;
	}
}
