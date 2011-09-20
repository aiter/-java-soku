package com.youku.search.console.operate.juji;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.console.pojo.Teleplay;
import com.youku.search.console.pojo.TeleplayPeer;
import com.youku.search.console.vo.TeleUpdateVO;

public class TeleplayMgt {
	static Log logger = LogFactory.getLog(TeleplayMgt.class);

	private TeleplayMgt() {
	}

	private static TeleplayMgt instance = null;

	public static synchronized TeleplayMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new TeleplayMgt();
			return instance;
		}
	}
	
	public List<Teleplay> getAllTeleplay(){
		Criteria criteria = new Criteria();
		try {
			return TeleplayPeer.doSelect(criteria);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	public void executeSql(String sql){
		try {
			TeleplayPeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public void updateTeleplay(TeleUpdateVO tvo){
		executeSql(
				"update teleplay set is_valid=" + tvo.getIs_valid()
						+ ",version_count=" + tvo.getVersion_count()
						+ ",cate=" + tvo.getTc().getCate() + ",subcate="
						+ tvo.getTc().getSubcate() + " where id="
						+ tvo.getTid());
	}
	
	public void addVersionCount(int tid){
		executeSql("update teleplay set version_count=version_count+1 where id="+ tid);
	}
	
	public void deleteById(int tid){
		Criteria criteria = new Criteria();
		criteria.add(TeleplayPeer.ID,tid);
		try {
			TeleplayPeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(tid,e);
		}
	}
	
	public List<Teleplay> getHasValidTeleplay() throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(TeleplayPeer.IS_VALID,0);
		return TeleplayPeer.doSelect(criteria);
	}
	
	public List<Integer> getHasValidTeleplayId() throws TorqueException{
		List<Teleplay>  list = getHasValidTeleplay();
		List<Integer> ids = new ArrayList<Integer>();
		for(Teleplay t:list){
			ids.add(t.getId());
		}
		return ids;
	}
	
	public void subVersionCount(int tid){
		executeSql("update teleplay set version_count=version_count-1 where id="+ tid);
	}
	
	public void teleplaySave(Teleplay t, int isValid, int versionCount,int cate, int subcate) {
		t.setIsValid(isValid);
		t.setVersionCount(versionCount);
		t.setCate(cate);
		t.setSubcate(subcate);
		try {
			t.save();
		} catch (Exception e) {
			logger.error(t,e);
		}
	}
	
	public int searchAllTeleplaySize() {
		String sql = "select count(*) as num from teleplay ";
		return searchTeleplaySizeReturnNum(sql);
	}
	
	public int searchTeleplaySizeReturnNum(String sql) {
		try {
			List<Record> res = TeleplayPeer.executeQuery(sql,"searchteleplay");
			if(null!=res&&res.size()>0)
				return res.get(0).getValue("num").asInt();
		} catch (Exception e) {
			logger.error(sql,e);
		}
		return 0;
	}
}
