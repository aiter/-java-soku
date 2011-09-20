package com.youku.soku.manage.service.paihangbang;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.soku.top.mapping.TopWordsPeer;

public class RankInfoService {
	private static Logger logger = Logger.getLogger(RankInfoService.class);
	
	private static RankInfoService instance = null;
	private RankInfoService(){
		super();
	}
	
	
	public synchronized static RankInfoService getInstance() {
		if(null==instance)
			instance = new RankInfoService();
		return instance;
	}
	
	public Set<Integer> getMuluProgrammeId(String date,int version_no){
		Set<Integer> set = new HashSet<Integer>();
		try {
			List<Record> list = BasePeer.executeQuery("select distinct fk_programme_id from rankinfo_"+date+" where fk_programme_id>0 and version_no="+version_no,TopWordsPeer.DATABASE_NAME);
			for(Record r:list){
				set.add(r.getValue("fk_programme_id").asInt());
			}
			return set;
		} catch (Exception e) {
			logger.error(e);
		}
		return set;
	}
}
