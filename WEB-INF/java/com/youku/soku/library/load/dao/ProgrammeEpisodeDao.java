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

import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.form.ProgrammeEpisodeBo;
import com.youku.soku.library.load.util.SourceUtil;


/**
 * 节目-站点 的数据库操作类
 * @author liuyunjian
 * 2011-2-23
 */
public class ProgrammeEpisodeDao {
	private static final Logger log = Logger.getLogger(ProgrammeEpisodeDao.class.getName());
	private ProgrammeEpisodeDao(){}
	private static ProgrammeEpisodeDao instance = null;
	public static synchronized ProgrammeEpisodeDao getInstance(){
		if(null==instance){
			instance = new ProgrammeEpisodeDao();
		}
		return instance;
	}
	
	/**
	 * 插入数据
	 * @param programmeDao
	 * @return
	 * @throws TorqueException 
	 */
	public boolean insert(ProgrammeEpisodeBo peBo) {
		if(peBo==null || peBo.fkProgrammeSiteId<=0){
			log.error("insert DB data[ProgrammeEpisodeBo] error:"+peBo);
			return false;
		}
		ProgrammeEpisode pEpisode = null;
		try {
			pEpisode = isExist(peBo);
		} catch (TorqueException e) {
				log.error("select DB error:"+peBo+e.getMessage());
			return false;
		}
		
		try {
			return doInsert(pEpisode,peBo);
		} catch (TorqueException e) {
				log.error("insert DB error:"+peBo+e.getMessage());
			return false;
		}
		
	}
	
	/**
	 * 插入or更新
	 * @param pEpisode
	 * @param programmeBo
	 * @return
	 */
	private boolean doInsert(ProgrammeEpisode pEpisode, ProgrammeEpisodeBo bo) throws TorqueException {
		if(pEpisode==null||pEpisode.getId()==0){
			pEpisode  = new ProgrammeEpisode();
			pEpisode.setCreateTime(new Date());
			pEpisode.setNew(true);
		}else {//数据库已经存在这条数据
			pEpisode.setSource(SourceUtil.combine(pEpisode.getSource(), bo.source));
			pEpisode.setNew(false);
		}
		
		pEpisode.setFkProgrammeSiteId(bo.fkProgrammeSiteId);
		pEpisode.setTitle(bo.title);
		pEpisode.setOrderId(bo.orderId);
		pEpisode.setOrderStage(bo.orderStage);
		pEpisode.setViewOrder(bo.viewOrder);
		pEpisode.setSource(bo.source);
		if(!isSameLogo(pEpisode.getLogo(),bo.logo)){
			pEpisode.setLogo(bo.logo);
		}
		
		if(((int)pEpisode.getSeconds())!=((int)bo.seconds)){//整数部分相同，就不更新
			pEpisode.setSeconds(bo.seconds);
		}
		pEpisode.setHd(bo.hd);
		pEpisode.setUrl(bo.url);
		if(pEpisode.isModified()){
			pEpisode.setUpdateTime(new Date());
			bo.updated = true;
		}
		
		try {
			pEpisode.save();
		} catch (Exception e) {
			throw new TorqueException(e.getMessage(), e);
		}
		
		bo.id = pEpisode.getId();
		bo.source = pEpisode.getSource();
		return pEpisode.getId()>0;
	}

	/**
	 * 是否是相同的logo地址，下面两个是同一个图，分布在不同的机器，算作相同图
	 * http://g3.ykimg.com/01270F1F464D6445CCAF4C00000000A2E639E4-9041-D396-46B1-B7489522393A
	 * http://g2.ykimg.com/01270F1F464D6445CCAF4C00000000A2E639E4-9041-D396-46B1-B7489522393A
	 * @param firstLogo
	 * @param firstLogo2
	 * @return
	 */
	private boolean isSameLogo(String firstLogo, String firstLogo2) {
		if(firstLogo==null && firstLogo2==null){
			return true;
		}
		if(firstLogo==null){
			return false;
		}
		if(firstLogo2==null){
			return false;
		}
		
		String [] tmp = null;
		tmp=firstLogo.split("/");
		String imgId1 = tmp.length==4?tmp[3]:"";
		tmp=firstLogo2.split("/");
		String imgId2 = tmp.length==4?tmp[3]:"";
		
		return imgId1.equalsIgnoreCase(imgId2);
	}

	/*
	 * 检测DB中是否存在指定programme_site_id;orderid;viewOrder的记录
	 */
	private ProgrammeEpisode isExist(ProgrammeEpisodeBo bo) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID,bo.fkProgrammeSiteId);
		criteria.add(ProgrammeEpisodePeer.ORDER_ID,bo.orderId);
		criteria.add(ProgrammeEpisodePeer.VIEW_ORDER,bo.viewOrder);
//		criteria.add(ProgrammeEpisodePeer.ORDER_STAGE,bo.orderStage);
		List<ProgrammeEpisode> list = ProgrammeEpisodePeer.doSelect(criteria);
//		System.out.println(list.size());
		ProgrammeEpisode pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}

	/**
	 * @param psId
	 * @return
	 * @throws TorqueException 
	 */
	public List<ProgrammeEpisodeBo> getList(int psId) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID,psId);
		criteria.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
		
		List<ProgrammeEpisode> list = ProgrammeEpisodePeer.doSelect(criteria);
		List<ProgrammeEpisodeBo> peList = null;
		if(null!=list&&list.size()>0){
			peList = new ArrayList<ProgrammeEpisodeBo>(list.size());
			for (int i = 0; i < list.size(); i++) {
				peList.add(copyProgrammeEpisode(list.get(i)));
			}
		}
		return peList;
	}
	
	public List<ProgrammeEpisodeBo> getList(int psId,int viewOrder) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID,psId);
		criteria.add(ProgrammeEpisodePeer.VIEW_ORDER,viewOrder);
		criteria.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
		
		List<ProgrammeEpisode> list = ProgrammeEpisodePeer.doSelect(criteria);
		List<ProgrammeEpisodeBo> peList = null;
		if(null!=list&&list.size()>0){
			peList = new ArrayList<ProgrammeEpisodeBo>(list.size());
			for (int i = 0; i < list.size(); i++) {
				peList.add(copyProgrammeEpisode(list.get(i)));
			}
		}
		return peList;
	}

	/**
	 * @param programmeEpisode
	 * @return
	 */
	private ProgrammeEpisodeBo copyProgrammeEpisode(
			ProgrammeEpisode pe) {
		if(pe==null){
			return null;
		}
		
		ProgrammeEpisodeBo peBo = new ProgrammeEpisodeBo();
		peBo.id = pe.getId();
		peBo.fkProgrammeSiteId = pe.getFkProgrammeSiteId();
		peBo.title = pe.getTitle();
		peBo.orderId = pe.getOrderId();
		peBo.orderStage = pe.getOrderStage();
		peBo.viewOrder = pe.getViewOrder();
		peBo.logo = pe.getLogo();
		peBo.seconds = pe.getSeconds();
		peBo.hd = pe.getHd();
		peBo.url = pe.getUrl();
		peBo.source = pe.getSource();
		peBo.updateTime = pe.getUpdateTime();
		peBo.createTime = pe.getCreateTime();
		
		return peBo;
	}

	/**
	 * @param id
	 * @return
	 */
	public boolean delete(int id) {
		try {
			 Criteria criteria = new Criteria();
			 criteria.add(ProgrammeEpisodePeer.ID,id);
			 
			 ProgrammeEpisodePeer.doDelete(criteria);
		} catch (TorqueException e) {
			return false;
		}
		return true;
	}
}
