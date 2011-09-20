package com.youku.search.console.operate.juji;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.RavietyExclude;
import com.youku.search.console.pojo.RavietyExcludePeer;

public class ExcludeMgt {
	static Log logger = LogFactory.getLog(ExcludeMgt.class);

	private ExcludeMgt() {
	}

	private static ExcludeMgt instance = null;

	public static synchronized ExcludeMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new ExcludeMgt();
			return instance;
		}
	}
	
	public void saveExclude(int tid,String excludes){
			if(excludes!=null&&excludes.trim().length()>0){
				RavietyExclude ravExclude=new RavietyExclude();
				ravExclude.setFkTeleplayId(tid);
				ravExclude.setName(excludes);
				try {
					ravExclude.save();
				} catch (Exception e) {
					logger.error(ravExclude,e);
				}
			}
	}
	
	public String getExclude(int tid){
		Criteria criteria = new Criteria();
		criteria.add(RavietyExcludePeer.FK_TELEPLAY_ID,tid);
		List<RavietyExclude> relist=null;
		try {
			relist=RavietyExcludePeer.doSelect(criteria);
			if(relist!=null&&relist.size()>0&&relist.get(0)!=null&&relist.get(0).getName()!=null&&relist.get(0).getName().trim().length()>0){
				return relist.get(0).getName().trim();
			}
			else return null;
		} catch (Exception e) {
			logger.error(tid,e);
		}
		return null;
	}
	
	public void updateExclude(int tid,String excludes){
		if(excludes==null||excludes.trim().length()<1)
			deleteExclude(tid);
		if(excludes!=null&&excludes.trim().length()>0){
			executeSql("update raviety_exclude set name='"+excludes+"' where fk_teleplay_id="+tid);
		}
	}
	
	public void executeSql(String sql){
		try {
			RavietyExcludePeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public void deleteExclude(int tid){
		Criteria criteria = new Criteria();
		criteria.add(RavietyExcludePeer.FK_TELEPLAY_ID,tid);
		try {
			RavietyExcludePeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(tid,e);
		}
	}
}
