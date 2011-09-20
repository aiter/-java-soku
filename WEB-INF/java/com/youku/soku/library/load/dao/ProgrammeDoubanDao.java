/**
 * 
 */
package com.youku.soku.library.load.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.ProgrammeDouban;
import com.youku.soku.library.load.ProgrammeDoubanMore;
import com.youku.soku.library.load.ProgrammeDoubanMorePeer;
import com.youku.soku.library.load.ProgrammeDoubanPeer;
import com.youku.soku.library.load.form.ProgrammeDoubanBo;


/**
 * 节目和豆瓣节目的对应关系数据库操作
 * @author liuyunjian
 * 2011-4-1
 */
public class ProgrammeDoubanDao {
	private static final Logger log = Logger.getLogger(ProgrammeDoubanDao.class.getName());
	private ProgrammeDoubanDao(){}
	private static ProgrammeDoubanDao instance = null;
	public static synchronized ProgrammeDoubanDao getInstance(){
		if(null==instance){
			instance = new ProgrammeDoubanDao();
		}
		return instance;
	}
	
	/**
	 * 只要对应表、待审核表任一个表有一个节目的记录，都算是搜索过的
	 * @param cId 节目ID
	 * @return
	 */
	public boolean checkSearched(int cId) {
		try {
			if(isExist(cId)!=null){
				return true;
			}else {
				if(isExistMore(cId)!=null){
					return true;
				}
			}
		} catch (TorqueException e) {
		}
		return false;
	}
	
	public ProgrammeDouban isExist(int cId) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeDoubanPeer.FK_CONTENT_ID,cId);
		List<ProgrammeDouban> list = ProgrammeDoubanPeer.doSelect(criteria);
		ProgrammeDouban pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}
	
	public ProgrammeDoubanMore isExistMore(int cId) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeDoubanMorePeer.FK_CONTENT_ID,cId);
		List<ProgrammeDoubanMore> list = ProgrammeDoubanMorePeer.doSelect(criteria);
		ProgrammeDoubanMore pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}
	
	public ProgrammeDoubanMore isExistMore(int cId,int doubanId) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeDoubanMorePeer.FK_CONTENT_ID,cId);
		criteria.add(ProgrammeDoubanMorePeer.DOUBAN_ID,doubanId);
		List<ProgrammeDoubanMore> list = ProgrammeDoubanMorePeer.doSelect(criteria);
		ProgrammeDoubanMore pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}

	/**
	 * @param programmeBo
	 * @param searchItems
	 */
	public boolean insertMore(ProgrammeDoubanBo pdBo) {
		if(pdBo==null){
			log.error("insert DB data[ProgrammeDouban] error:"+pdBo);
			return false;
		}
		ProgrammeDoubanMore pdMore = null;
		try {
			pdMore = isExistMore(pdBo.pId,pdBo.doubanId);
		} catch (TorqueException e) {
				log.error("select DB error:"+e.getMessage());
			return false;
		}
		
		try {
			return doInsert(pdMore,pdBo);
		} catch (TorqueException e) {
				log.error("insert DB error:"+e.getMessage());
			return false;
		}
	}
	
	public boolean insert(ProgrammeDoubanBo pdBo) {
		if(pdBo==null){
			log.error("insert DB data[ProgrammeDouban] error:"+pdBo);
			return false;
		}
		ProgrammeDouban pd = null;
		try {
			pd = isExist(pdBo.pId);
		} catch (TorqueException e) {
				log.error("select DB error:"+e.getMessage());
			return false;
		}
		
		return doInsert(pd,pdBo);
	}
	
	/**
	 * 插入or更新
	 * @param programme
	 * @param programmeBo
	 * @return
	 */
	private boolean doInsert(ProgrammeDoubanMore pdMore, ProgrammeDoubanBo pdBo) throws TorqueException {
		if(pdMore==null||pdMore.getId()==0){
			pdMore  = new ProgrammeDoubanMore();
			pdMore.setCreateTime(new Date());
			pdMore.setNew(true);
		}else {//数据库已经存在这条数据
			pdMore.setNew(false);
			pdMore.setStatus(1);//把状态更新为正常
		}
		
		pdMore.setFkContentId(pdBo.pId);
		pdMore.setName(pdBo.pName);
		pdMore.setDoubanId(pdBo.doubanId);
		pdMore.setDoubanName(pdBo.doubanName);
		pdMore.setCate(pdBo.cate);
		if(pdMore.isModified()){
			pdMore.setUpdateTime(new Date());
		}
		
		try {
			pdMore.save();
		} catch (Exception e) {
			throw new TorqueException(e.getMessage(), e);
		}
		
		pdBo.id = pdMore.getId();
		
		return pdMore.getId()>0;
	}

	
	/**
	 * 
	 * @param cId 节目ID
	 * @param doubanId 豆瓣ID
	 * @return
	 */
	public boolean comfirm(int cId,int doubanId){
		ProgrammeDouban programmeDouban = null;
		ProgrammeDoubanMore programmeDoubanMore = null;
		try {
			programmeDoubanMore = isExistMore(cId, doubanId);
			programmeDouban = isExist(cId);
		} catch (TorqueException e) {
		}
		
		if(programmeDoubanMore!=null){
			doInsert(programmeDouban,programmeDoubanMore);
		}else {
			return false;
		}
		
		return true;
	}

	/**
	 * @param programmeDouban
	 * @param programmeDoubanMore
	 */
	private boolean doInsert(ProgrammeDouban programmeDouban,
			ProgrammeDoubanMore pdMore) {
		if(pdMore==null){
			return false;
		}
		
		if(programmeDouban==null){
			programmeDouban  = new ProgrammeDouban();
			programmeDouban.setCreateTime(new Date());
			programmeDouban.setNew(true);
		}else {//数据库已经存在这条数据
			programmeDouban.setNew(false);
		}
		
		programmeDouban.setFkContentId(pdMore.getFkContentId());
		programmeDouban.setName(pdMore.getName());
		programmeDouban.setDoubanId(pdMore.getDoubanId());
		programmeDouban.setDoubanName(pdMore.getDoubanName());
		programmeDouban.setCate(pdMore.getCate());
		programmeDouban.setStatus(1);
		
		if(programmeDouban.isModified()){
			programmeDouban.setUpdateTime(new Date());
		}
		
		try {
			programmeDouban.save();
		} catch (Exception e) {
			return false;
		}
		
		
		return true;
	}
	
	private boolean doInsert(ProgrammeDouban programmeDouban,
			ProgrammeDoubanBo bo) {
		if(bo==null){
			return false;
		}
		
		if(programmeDouban==null){
			programmeDouban  = new ProgrammeDouban();
			programmeDouban.setCreateTime(new Date());
			programmeDouban.setNew(true);
		}else {//数据库已经存在这条数据
			programmeDouban.setNew(false);
		}
		
		programmeDouban.setFkContentId(bo.pId);
		programmeDouban.setName(bo.pName);
		programmeDouban.setDoubanId(bo.doubanId);
		programmeDouban.setDoubanName(bo.doubanName);
		programmeDouban.setStatus(bo.status);
		programmeDouban.setCate(bo.cate);
		
		if(programmeDouban.isModified()){
			programmeDouban.setUpdateTime(new Date());
		}
		
		try {
			programmeDouban.save();
		} catch (Exception e) {
			return false;
		}
		
		
		return true;
	}

	/**
	 * @param pId
	 */
	public boolean deleteByPid(int pId) {
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeDoubanMorePeer.FK_CONTENT_ID,pId);
		
		try {
			ProgrammeDoubanMorePeer.doDelete(criteria);
		} catch (TorqueException e) {
			return false;
		}
		
		return true;
	}
}
