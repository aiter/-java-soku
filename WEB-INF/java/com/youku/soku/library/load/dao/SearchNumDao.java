/**
 * 
 */
package com.youku.soku.library.load.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.SearchNums;
import com.youku.soku.library.load.SearchNumsPeer;
import com.youku.soku.library.load.form.ProgrammeSiteBo;

/**
 * 自动搜索（自动发现） 的搜索次数记录
 * @author liuyunjian
 * 2011-3-2
 */
public class SearchNumDao {
	private static final Logger log = Logger.getLogger(SearchNumDao.class.getName());
	private SearchNumDao(){}
	private static SearchNumDao instance = null;
	public static synchronized SearchNumDao getInstance(){
		if(null==instance){
			instance = new SearchNumDao();
		}
		return instance;
	}
	
	
	public boolean insert(Integer orderId,ProgrammeSiteBo psBo) {
		if(psBo==null ||psBo.id<=0){
			log.error("insert DB data[SearchNums] error:"+psBo.id+":orderid:"+orderId);
			return false;
		}
		SearchNums searchNums = null;
		try {
			searchNums = isExist(psBo.id,orderId);
		} catch (TorqueException e) {
				log.error("select DB error:"+psBo+e.getMessage());
			return false;
		}
		
		try {
			return doInsert(searchNums, orderId,psBo);
		} catch (TorqueException e) {
				log.error("insert DB error:"+psBo+e.getMessage());
			return false;
		}
	}
	
	/**
	 * 插入or更新
	 * @param searchUnms
	 * @param programmeBo
	 * @return
	 */
	private boolean doInsert(SearchNums searchUnms, Integer orderId, ProgrammeSiteBo psBo) throws TorqueException {
		if(searchUnms==null||searchUnms.getId()==0){
			searchUnms  = new SearchNums();
			searchUnms.setCreateTime(new Date());
			searchUnms.setNum(1);
			searchUnms.setNew(true);
		}else {//数据库已经存在这条数据
			searchUnms.setNew(false);
			searchUnms.setNum(searchUnms.getNum()+1);
		}
		searchUnms.setOrderId(orderId);
		searchUnms.setFkProgrammeSiteId(psBo.id);
		
		if(searchUnms.isModified()){
			searchUnms.setUpdateTime(new Date());
		}
		try {
			searchUnms.save();
		} catch (Exception e) {
			throw new TorqueException(e.getMessage(), e);
		}
		
		return searchUnms.getId()>0;
	}

	/*
	 * 检测DB中是否存在指定siteId,orderId的记录
	 */
	private SearchNums isExist(int siteId,int orderId) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(SearchNumsPeer.FK_PROGRAMME_SITE_ID,siteId);
		criteria.add(SearchNumsPeer.ORDER_ID,orderId);
		List<SearchNums> list = SearchNumsPeer.doSelect(criteria);
		SearchNums pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}


	/**
	 * @return
	 */
	public int getSearchNum(int siteId, int orderId) {
		SearchNums searchNums= null;;
		try {
			searchNums = isExist(siteId, orderId);
		} catch (TorqueException e) {
		}
		
		if(searchNums!=null){
			return searchNums.getNum();
		}
		return 0;
	}


	/**
	 * 更新搜索次数
	 */
	public void update(int orderId, ProgrammeSiteBo psBo, int num) {
		SearchNums searchNums= null;;
		try {
			searchNums = isExist(psBo.id, orderId);
			if(searchNums!=null){
				searchNums.setNum(num);
				searchNums.save();
			}
		} catch (TorqueException e) {
		} catch (Exception e) {
		}
	}
}
