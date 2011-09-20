/**
 * 
 */
package com.youku.soku.library.load.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.util.SourceUtil;


/**
 * 节目 的数据库操作类
 * @author liuyunjian
 * 2011-2-22
 */
public class ProgrammeDao {
	private static final Logger log = Logger.getLogger(ProgrammeBo.class.getName());
	private ProgrammeDao(){}
	private static ProgrammeDao instance = null;
	public static synchronized ProgrammeDao getInstance(){
		if(null==instance){
			instance = new ProgrammeDao();
		}
		return instance;
	}
	
	/**
	 * 插入数据
	 * @param programmeDao
	 * @return
	 * @throws TorqueException 
	 */
	public boolean insert(ProgrammeBo programmeBo) {
		if(programmeBo==null || programmeBo.contentId<=0){
			log.error("insert DB data[programmeBo] error:"+programmeBo);
			return false;
		}
		Programme programme = null;
		try {
			programme = isExist(programmeBo);
		} catch (TorqueException e) {
				log.error("select DB error:"+programmeBo+e.getMessage());
			return false;
		}
		
		try {
			return doInsert(programme,programmeBo);
		} catch (TorqueException e) {
				log.error("insert DB error:"+programmeBo+e.getMessage());
			return false;
		}
	}
	
	/**
	 * 插入or更新
	 * @param programme
	 * @param programmeBo
	 * @return
	 */
	private boolean doInsert(Programme programme, ProgrammeBo bo) throws TorqueException {
		if(programme==null||programme.getId()==0){
			programme  = new Programme();
			programme.setCreateTime(new Date());
			programme.setSource(bo.source);
			programme.setNew(true);
		}else {//数据库已经存在这条数据
			programme.setSource(SourceUtil.combine(programme.getSource(), bo.source));
			programme.setNew(false);
		}
		programme.setContentId(bo.contentId);
		programme.setName(bo.name);
		programme.setAlias(bo.alias);
		programme.setCate(bo.cate);
		programme.setEpisodeTotal(bo.episodeTotal);
		if(programme.isModified()){
			programme.setUpdateTime(new Date());
			bo.updated = true;
		}
		programme.setState(bo.state);
//		programme.setBlocked(bo.blocked);
		try {
			programme.save();
		} catch (Exception e) {
			throw new TorqueException(e.getMessage(), e);
		}
		
		bo.id = programme.getId();
		bo.source = programme.getSource();
		return programme.getId()>0;
	}

	/*
	 * 检测DB中是否存在指定contentid的记录
	 */
	private Programme isExist(ProgrammeBo bo) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(ProgrammePeer.CONTENT_ID,bo.contentId);
		List<Programme> list = ProgrammePeer.doSelect(criteria);
		Programme pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}

	/**
	 * 查找指定分类的未锁定的所有节目
	 * @param cate
	 * @throws TorqueException 
	 */
	public List<ProgrammeBo> getList(int cate) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(ProgrammePeer.CATE,cate);
		criteria.add(ProgrammePeer.BLOCKED,0);
		criteria.add(ProgrammePeer.STATE,"normal");
		//TODO 测试用 
//		criteria.setLimit(30);
		List<Programme> list = ProgrammePeer.doSelect(criteria);
		List<ProgrammeBo> boList = null;
		if(null!=list&&list.size()>0){
			boList = new ArrayList<ProgrammeBo>(list.size());
			for (int i = 0; i < list.size(); i++) {
				boList.add(copyProgramme(list.get(i)));
			}
		}
		return boList;
	}

	/**
	 * @param programme
	 * @return
	 */
	private ProgrammeBo copyProgramme(Programme programme) {
		if(programme==null){
			return null;
		}
		ProgrammeBo bo = new ProgrammeBo();
//		try {
//			BeanUtils.copyProperties(programme, bo);
//		} catch (IllegalAccessException e) {
//			return null;
//		} catch (InvocationTargetException e) {
//			return null;
//		}
		bo.id = programme.getId();
		bo.contentId = programme.getContentId();
		bo.name = programme.getName();
		bo.cate = programme.getCate();
		bo.episodeTotal = programme.getEpisodeTotal();
		bo.source = programme.getSource();
		bo.updateTime = programme.getUpdateTime();
		bo.createTime = programme.getCreateTime();
		bo.state = programme.getState();
		bo.blocked = programme.getBlocked();
		return bo;
	}

	/**
	 * @param id
	 */
	public boolean updateAudit(ProgrammeBo programmeBo) {
		if(programmeBo==null || programmeBo.contentId<=0){
			log.error("insert DB data[programmeBo] error:"+programmeBo);
			return false;
		}
		Programme programme = null;
		try {
			programme = isExist(programmeBo);
		} catch (TorqueException e) {
				log.error("select DB error:"+programmeBo+e.getMessage());
			return false;
		}
		
		try {
			return doCheck(programme,programmeBo);
		} catch (TorqueException e) {
				log.error("insert DB error:"+programmeBo+e.getMessage());
			return false;
		}
	}

	/**
	 * 如果节目已审核完，然后发现新的链接，那么就修改了需要审核的状态
	 */
	private boolean doCheck(Programme programme, ProgrammeBo bo) throws TorqueException {
		if(programme!=null){
			if(programme.getAuditAll()==1){
				programme.setAuditAll(0);
			}
			try {
				programme.save();
			} catch (Exception e) {
				throw new TorqueException(e.getMessage(), e);
			}
		}
		
		return true;
	}

	/**
	 * @param fkProgrammeId
	 * @return
	 */
	public ProgrammeBo getProgramme(int programmeId) {
		Criteria criteria = new Criteria();
		criteria.add(ProgrammePeer.ID,programmeId);
		List<Programme> list = null;;
		ProgrammeBo pr = null;
		try {
			list = ProgrammePeer.doSelect(criteria);
			if(null!=list&&list.size()>0){
				pr = copyProgramme(list.get(0));
			}
		} catch (TorqueException e) {
		}
		return pr;
	}
	
	
}
