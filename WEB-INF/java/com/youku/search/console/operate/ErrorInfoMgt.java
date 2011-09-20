package com.youku.search.console.operate;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.Errorinfo;
import com.youku.search.console.pojo.ErrorinfoPeer;

public class ErrorInfoMgt {
	private static Logger log = Logger.getLogger(ErrorInfoMgt.class);

	private static ErrorInfoMgt instance = null;

	public static synchronized ErrorInfoMgt getInstance() {
		if (instance != null)
			return instance;
		else{
			instance = new ErrorInfoMgt();
			return instance;
		}
	}
	
	/**
	 * 保存
	 * 
	 * @param err
	 */
	public void save(Errorinfo err) {
		try {
			err.save();
		} catch (Exception e) {
			// log.error("user save error! the user is
			// [id="+u.getId()+",name="+u.getName()+",password="+u.getPassword()+",true_name="+u.getTrueName()+",team="+u.getTeam()+",sex="+u.getSex()+",birth="+u.getBirth()+",creator="+u.getCreator()+",createDate="+u.getCreatedate()+",flag="+u.getFlag()+",lastLoginDate="+u.getLastlogindate()+"]",
			// e);
			log.error("errorinfo save error! " + err.toString(), e);
			e.printStackTrace();
		}
	}

	/**
	 * 删除
	 * 
	 * @param e
	 */
	public void delete(String errurl) {
		try {
			Criteria criteria = new Criteria();
			criteria.add(ErrorinfoPeer.URL, errurl);
			ErrorinfoPeer.doDelete(criteria);
		} catch (TorqueException e) {
			e.printStackTrace();
		}

	}
	
	public boolean check(String errurl){
		Criteria criteria = new Criteria();
		criteria.add(ErrorinfoPeer.URL, errurl);
		List<Errorinfo> errlist = null;
		try {
			errlist=ErrorinfoPeer.doSelect(criteria);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		if(null!=errlist&&errlist.size()>0)
			return true;
		return false;
	}
	
	
	/**
	 * 查找出所有
	 * 
	 * @return
	 */
	public List<Errorinfo> findAll() {
		List<Errorinfo> el = new ArrayList<Errorinfo>();
		Criteria criteria = new Criteria();
		criteria.add(ErrorinfoPeer.ID, (Object) ("ID is not null"), Criteria.CUSTOM);
		try {
			el = ErrorinfoPeer.doSelect(criteria);
		} catch (TorqueException e) {
			log.error("Errorinfo find all error! ", e);
			e.printStackTrace();
		}
		return el;
	}
	
	public void update(String lasttime,int id){
		try {
			ErrorinfoPeer.executeStatement("update errorinfo set lasttime='"+lasttime+"',num=num+1 where id="+id);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}
}
