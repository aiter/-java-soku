package com.youku.search.console.operate.juji;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.workingdogs.village.Record;
import com.youku.search.console.pojo.PlayNamePeer;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.pojo.PlayVersionPeer;
import com.youku.search.console.vo.SingleVersion;

public class PlayVersionMgt {

	static Log logger = LogFactory.getLog(PlayVersionMgt.class);

	private PlayVersionMgt() {
	}

	private static PlayVersionMgt instance = null;

	public static synchronized PlayVersionMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new PlayVersionMgt();
			return instance;
		}
	}
	
	public List<PlayVersion> getAllPlayVersion(){
		Criteria criteria = new Criteria();
		try {
			return PlayVersionPeer.doSelect(criteria);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	public List<PlayVersion> getPlayVersionByLogo(){
		Criteria criteria = new Criteria();
		criteria.add(PlayVersionPeer.FIRSTLOGO,(Object)"",SqlEnum.EQUAL);
		criteria.or(PlayVersionPeer.FIRSTLOGO,new Object(),SqlEnum.ISNULL);
		try {
			return PlayVersionPeer.doSelect(criteria);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	public int getVersionCount(int teleplay_id){
		Criteria criteria = new Criteria();
		criteria.add(PlayVersionPeer.FK_TELEPLAY_ID,teleplay_id);
		try {
			return PlayVersionPeer.doSelect(criteria).size();
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}

	public boolean addVersion(SingleVersion sv) {
		try {
			Criteria criteria = new Criteria();
			criteria.add(PlayVersionPeer.NAME, sv.getVersionname());
			criteria.add(PlayVersionPeer.FK_TELEPLAY_ID, sv.getFkTeleplayId());
			List<PlayVersion> l = PlayVersionPeer.doSelect(criteria);
			if (l != null && l.size() > 0)
				return false;
			PlayVersion pv = new PlayVersion();
			pv.setFkTeleplayId(sv.getFkTeleplayId());
			pv.setName(sv.getVersionname());
			pv.setCate(sv.getCate());
			pv.setSubcate(sv.getSubcate());
			pv.setAlias(sv.getAlias());
			pv.setTotalCount(sv.getTotal_Count());
			pv.setOrderId(sv.getOrderId());
			pv.setFixed(sv.getFixed());
			pv.setReverse(sv.getReverse());
			pv.setFirstlogo(sv.getFirstlogo());
			pv.setViewName(sv.getViewname());
			pv.save();
			return true;
		} catch (Exception e) {
			logger.error(sv, e);
		}
		return false;
	}

	public boolean updateVersion(SingleVersion sv) {
		String sql = "update play_version set cate=" + sv.getCate()
				+ ",subcate=" + sv.getSubcate() + ",name='"
				+ sv.getVersionname() + "',alias='" + sv.getAlias()
				+ "',order_id=" + sv.getOrderId() + ",reverse="
				+ sv.getReverse() + ",total_count=" + sv.getTotal_Count()
				+ ",fixed=" + sv.getFixed() + ",firstlogo='"
				+ sv.getFirstlogo() + "',view_name='" + sv.getViewname()
				+ "' where id=" + sv.getPid();
		try {
			PlayVersionPeer.executeStatement(sql, "searchteleplay");
			return true;
		} catch (Exception e) {
			logger.error(sv, e);
		}
		return false;
	}
	
	public void executeSql(String sql){
		try {
			PlayVersionPeer.executeStatement(sql, "searchteleplay");
		} catch (Exception e) {
			logger.error(sql, e);
		}
	}
	
	public PlayVersion getPlayVersionByID(int id){
		try {
			return PlayVersionPeer.retrieveByPK(id);
		} catch (Exception e) {
			logger.error(id,e);
		} 
		return null;
	}
	
	public void deletePlayVersionByID(int id){
		try {
			Criteria criteria = new Criteria();
			criteria.add(PlayVersionPeer.ID,id);
			PlayVersionPeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(id,e);
		}
	}
	
	public void deletePlayVersionByTeleplayID(int tid){
		try {
			Criteria criteria = new Criteria();
			criteria.add(PlayVersionPeer.FK_TELEPLAY_ID,tid);
			PlayVersionPeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(tid,e);
		}
	}
	
	public int getSubcate(int pid) {
		String sql = "select subcate from play_version where id="+pid;
		try {
			List<Record> res = PlayVersionPeer.executeQuery(sql,"searchteleplay");
			if(res!=null&&res.size()>0){
				return res.get(0).getValue("subcate").asInt();
			}
		} catch (Exception e) {
			logger.error(pid,e);
		}
		return 0;
	}
	
	/**
	 * 竖线分开
	 * @param id
	 * @return
	 */
	public String getPlayVersionName(int id) {
		String sql = "select name,alias from play_version where id=" + id;
		StringBuffer sbf = new StringBuffer();
		try {
			List<Record> res = PlayNamePeer.executeQuery(sql,"searchteleplay");
			for (Record r:res) {
				String name = r.getValue("name").asString();
				if (!StringUtils.isBlank(name)&&!name.trim().toLowerCase().equalsIgnoreCase("null")) {
					sbf.append(name);
				}
				String alias = r.getValue("alias").asString();
				if (!StringUtils.isBlank(alias)&&!alias.trim().toLowerCase().equalsIgnoreCase("null")) {
					if (null != sbf && sbf.length() > 0)
						sbf.append("|");
					sbf.append(alias);
				}
			}
		} catch (Exception e) {
			logger.error(sql,e);
		}
		if (null != sbf && sbf.length() > 0)
			return sbf.toString();
		else
			return null;
	}
	
	public String getVersionName(int pid) {
		PlayVersion pv = getPlayVersionByID(pid);
		if(null==pv||StringUtils.isBlank(pv.getName())) return null;
		return pv.getName().trim();
	}
	
	public String getViewNameFromVersion(int pid) {
		PlayVersion pv = getPlayVersionByID(pid);
		if(null==pv||StringUtils.isBlank(pv.getViewName())) return null;
		return pv.getViewName().trim();
	}
	
	public Set<String> getVersionNameByIdReturnSet(int pid){
		PlayVersion pv = getPlayVersionByID(pid);
		if(null==pv||StringUtils.isBlank(pv.getName())) return null;
		Set<String> set = new HashSet<String>();
		set.add(pv.getName());
		if(!StringUtils.isBlank(pv.getAlias())){
			//TODO 版本别名就一个?
			set.add(pv.getAlias());
		}
		return set;
	}
}
