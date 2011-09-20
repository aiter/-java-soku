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

import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.library.load.form.ProgrammeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;
import com.youku.soku.library.load.util.SourceUtil;


/**
 * 节目-站点 的数据库操作类
 * @author liuyunjian
 * 2011-2-23
 */
public class ProgrammeSiteDao {
	private static final Logger log = Logger.getLogger(ProgrammeSiteDao.class.getName());
	private ProgrammeSiteDao(){}
	private static ProgrammeSiteDao instance = null;
	public static synchronized ProgrammeSiteDao getInstance(){
		if(null==instance){
			instance = new ProgrammeSiteDao();
		}
		return instance;
	}
	
	/**
	 * 插入数据
	 * @param programmeDao
	 * @return
	 * @throws TorqueException 
	 */
	public boolean insert(ProgrammeSiteBo psBo) {
		if(psBo==null || psBo.fkProgrammeId<=0){
			log.error("insert DB data[ProgrammeSiteBo] error:"+psBo);
			return false;
		}
		ProgrammeSite pSite = null;
		try {
			pSite = isExist(psBo);
		} catch (TorqueException e) {
				log.error("select DB error:"+psBo+e.getMessage());
			return false;
		}
		
		try {
			return doInsert(pSite,psBo);
		} catch (TorqueException e) {
				log.error("insert DB error:"+psBo+e.getMessage());
			return false;
		}
	}
	
	/**
	 * 插入or更新
	 * @param pSite
	 * @param programmeBo
	 * @return
	 */
	private boolean doInsert(ProgrammeSite pSite, ProgrammeSiteBo bo) throws TorqueException {
		if(pSite==null||pSite.getId()==0){
			pSite  = new ProgrammeSite();
			pSite.setCreateTime(new Date());
			pSite.setSource(bo.source);
			pSite.setCompleted(bo.completed);
			pSite.setNew(true);
		}else {//数据库已经存在这条数据
			pSite.setSource(SourceUtil.combine(pSite.getSource(), bo.source));
			if(pSite.getCompleted()==0){//结合自动发现的结果，再设置这个值 
				pSite.setCompleted(bo.completed);
			}
			pSite.setNew(false);
		}
		pSite.setFkProgrammeId(bo.fkProgrammeId);
//		if((bo.firstLogo==null || bo.firstLogo.length()==0) && pSite.getFirstLogo().length()>0){
//			System.out.println(pSite.getFirstLogo());
//		}
		pSite.setOrderId(bo.orderId);
		
		if(bo.firstLogo!=null && bo.firstLogo.length()>0 && !isSameLogo(pSite.getFirstLogo(),bo.firstLogo)){
//			System.out.println(pSite.getFirstLogo()+"::::"+bo.firstLogo);
			pSite.setFirstLogo(bo.firstLogo);
		}
//		if(pSite.isModified()){
//			System.out.println("firstLogo:"+bo.firstLogo);
//		}
		pSite.setSourceSite(bo.sourceSite);
//		if(pSite.isModified()){
//			System.out.println("sourceSite:"+bo.sourceSite);
//		}
		pSite.setMidEmpty(bo.midEmpty);
//		if(pSite.isModified()){
//			System.out.println("sourceSite:"+bo.sourceSite);
//		}//2011.5.13 不同步中间层的收录集数，由单独线程计算
//		pSite.setEpisodeCollected(bo.episodeCollected);
//		if(pSite.isModified()){
//			System.out.println("episodeCollected:"+bo.episodeCollected);
//		}
		if(pSite.isModified()){
			pSite.setUpdateTime(new Date());
			bo.updated = true;
		}
//		programme.setBlocked(bo.blocked);
		try {
			pSite.save();
		} catch (Exception e) {
			throw new TorqueException(e.getMessage(), e);
		}
		
		bo.id = pSite.getId();
		bo.source = pSite.getSource();
		return pSite.getId()>0;
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
	 * 检测DB中是否存在指定programme_id;source_site的记录
	 */
	private ProgrammeSite isExist(ProgrammeSiteBo bo) throws TorqueException{
		return isExist(bo.sourceSite, bo.fkProgrammeId);
	}
	private ProgrammeSite isExist(int site, int programmeId) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeSitePeer.FK_PROGRAMME_ID,programmeId);
		criteria.add(ProgrammeSitePeer.SOURCE_SITE,site);
		List<ProgrammeSite> list = ProgrammeSitePeer.doSelect(criteria);
		ProgrammeSite pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}

	/**
	 * @param siteYouku
	 * @param contentId
	 * @return
	 */
	public ProgrammeSiteBo getProgrammeSiteBo(int site, ProgrammeBo pBo) {
		try {
			ProgrammeSite programmeSite = isExist(site,pBo.id);
			if(programmeSite!=null){
				ProgrammeSiteBo psBo = copyProgrammeSite(programmeSite);
				psBo.programmeBo = pBo;
				return psBo;
			}
		} catch (TorqueException e) {
			log.error("get programmeSite error:programmeId="+pBo.id);
		}
		return null;
	}

	/**
	 * @param programmeSite
	 * @return
	 */
	private ProgrammeSiteBo copyProgrammeSite(ProgrammeSite ps) {
		if(ps==null){
			return null;
		}
		ProgrammeSiteBo psBo = new ProgrammeSiteBo();
		psBo.id = ps.getId();
		psBo.fkProgrammeId=ps.getFkProgrammeId();
		psBo.sourceSite=ps.getSourceSite();
		psBo.orderId = ps.getOrderId();
		psBo.firstLogo=ps.getFirstLogo();
		psBo.completed = ps.getCompleted();
		psBo.blocked = ps.getBlocked();
		psBo.midEmpty = ps.getMidEmpty();
		psBo.episodeCollected = ps.getEpisodeCollected();
		psBo.source = ps.getSource();
		psBo.otherSiteCompleted = ps.getOtherSiteCompleted();
		psBo.updateTime = ps.getUpdateTime();
		psBo.createTime = ps.getCreateTime();
		
		return psBo;
	}

	/**
	 * @param progSiteId
	 * @return
	 */
	public List<ProgrammeSiteBo> getProgrammeSites(int progSiteId) {
		ProgrammeSite programmeSite = null;
		try {
			programmeSite = isExist(progSiteId);
		} catch (TorqueException e) {
			log.error("get programmeSite error:id="+progSiteId);
		}
		
		if(programmeSite==null){
			return null;
		}
		
		List<ProgrammeSite> list =  null;
		try {
			list = getProgrammeSitesByPid(programmeSite.getFkProgrammeId());
		} catch (TorqueException e) {
			log.error("get programmeSite-list error:programmeId="+programmeSite.getFkProgrammeId());
		}
		
		if(list!=null && list.size()>0){
			List<ProgrammeSiteBo> tmpList = new ArrayList<ProgrammeSiteBo>(list.size());
			for (ProgrammeSite ps : list) {
				tmpList.add(copyProgrammeSite(ps));
			}
			
			return tmpList;
		}
		
		return null;
	}
	
	/**
	 * @param fkProgrammeId
	 * @return
	 * @throws TorqueException 
	 */
	private List<ProgrammeSite> getProgrammeSitesByPid(int fkProgrammeId) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeSitePeer.FK_PROGRAMME_ID,fkProgrammeId);
		List<ProgrammeSite> list = ProgrammeSitePeer.doSelect(criteria);
		return list;
	}

	private ProgrammeSite isExist(int progSiteId) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeSitePeer.ID,progSiteId);
		List<ProgrammeSite> list = ProgrammeSitePeer.doSelect(criteria);
		ProgrammeSite pr = null;
		if(null!=list&&list.size()>0){
			pr = list.get(0);
		}
		return pr;
	}

	/**
	 * @return
	 */
	public List<Integer> getGroupSiteIds() {
		Criteria criteria = new Criteria();
		criteria.addGroupByColumn(ProgrammeSitePeer.FK_PROGRAMME_ID);
		List<Integer> siteIds = null;
		try {
			List<ProgrammeSite> list = ProgrammeSitePeer.doSelect(criteria);
			if(null!=list&&list.size()>0){
				siteIds = new ArrayList<Integer>(list.size());
				for (ProgrammeSite programmeSite : list) {
					siteIds.add(programmeSite.getId());
				}
			}
		} catch (TorqueException e) {
		}
		return siteIds;
	}

	/**
	 * @param pSiteId
	 * @return
	 */
	public ProgrammeSiteBo getProgrammeSiteBo(int pSiteId) {
		try {
			ProgrammeSite programmeSite = isExist(pSiteId);
			if(programmeSite!=null){
				ProgrammeSiteBo psBo = copyProgrammeSite(programmeSite);
				return psBo;
			}
		} catch (TorqueException e) {
			log.error("get programmeSite error:programmeId="+pSiteId);
		}
		return null;
	}
	
	
}
