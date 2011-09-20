package com.youku.soku.manage.admin.library;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.EpisodeLogPeer;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.service.AuditEpisodeLogService;
import com.youku.soku.manage.service.ProgrammeSiteService;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.manage.util.ManageUtil;
import com.youku.soku.manage.util.SearchParameter;

public class AuditEpisodeLogAction extends BaseActionSupport {
	
	private Logger logger = Logger.getLogger(this.getClass());

	public String list() throws Exception {
		try {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(20);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			int categoryId = -1;
			if (getCategoryFilter() != null && !getCategoryFilter().equals("")) {
				categoryId = Integer.valueOf(getCategoryFilter());
			}
			String searchWord = null;
			if(getSearchWord() != null) {
				searchWord = getSearchWord().trim();
			}
			
			
			SearchParameter param = new SearchParameter();
			param.setSearchWord(searchWord);
			param.setAccuratelyMatched(getAccuratelyMatched() == 1 ? true : false);
			param.setCategoryId(categoryId);
			param.setSiteId(siteId);
			param.setConcernLevel(getConcernLevel());

			AuditEpisodeLogService.searchVideoInfo(param, pageInfo);
			
			setPageInfo(pageInfo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return Constants.LIST;
	}
	
	public String auditPass() throws Exception {
		
		EpisodeLog el = EpisodeLogPeer.retrieveByPK(getLogId());
		el.setStatus(Constants.EPISODE_LOG_HANDLEDSUCCESS);
		el.setUpdateTime(new Date());
		el.setOperator(getUserName());
		
		
		ProgrammeEpisode pe = new ProgrammeEpisode();
		Criteria crit = new Criteria();
		crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, el.getFkProgrammeSiteId());
		crit.add(ProgrammeEpisodePeer.ORDER_STAGE, el.getOrderStage());
		crit.add(ProgrammeEpisodePeer.VIEW_ORDER, Constants.VIEW_ORDER_NOT_YOUKU);
		List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(crit);
		
		boolean isUpdate = false;
		
		if(peList != null && !peList.isEmpty()) {
			pe = peList.get(0);
			isUpdate = true;
		}
		pe.setUrl(el.getUrl());
		pe.setLogo(el.getLogo());
		pe.setHd(el.getHd());
		pe.setTitle(el.getTitle());
		pe.setSeconds(el.getSeconds());
		pe.setFkProgrammeSiteId(el.getFkProgrammeSiteId());
		pe.setOrderId(el.getOrderId());
		pe.setOrderStage(el.getOrderStage());
		pe.setViewOrder(Constants.VIEW_ORDER_NOT_YOUKU);

		if(isUpdate) {
			ProgrammeEpisodePeer.doUpdate(pe);
		} else {
			pe.save();
		}
		
		EpisodeLogPeer.doUpdate(el);
		chagneProgrammSource(el.getFkProgrammeSiteId());
		AuditEpisodeLogService.checkProgrammeAuditAll(el.getFkProgrammeSiteId());
		return SUCCESS;
	}
	
	private void chagneProgrammSource(int programmeSiteId) throws Exception {
		
		ProgrammeSite programmeSite = ProgrammeSitePeer.retrieveByPK(programmeSiteId);
		Programme p = ProgrammePeer.retrieveByPK(programmeSite.getFkProgrammeId());
		p.setSource(ManageUtil.changeSourceAutoSearch(p.getSource()));
		ProgrammePeer.doUpdate(p);
	}
	
	public String auditFail() throws Exception {
		
		EpisodeLog el = EpisodeLogPeer.retrieveByPK(getLogId());
		el.setStatus(Constants.EPISODE_LOG_HANDLEDFAIL);
		el.setUpdateTime(new Date());
		el.setOperator(getUserName());
		EpisodeLogPeer.doUpdate(el);
		
		AuditEpisodeLogService.checkProgrammeAuditAll(el.getFkProgrammeSiteId());
		return SUCCESS;
	}
	
	public String fixProgrammAuditFlag() throws Exception {

		AuditEpisodeLogService.fixAuditFlag();
		return Constants.LIST;
	}

	private int pageNumber;

	private PageInfo pageInfo;
	
	private int accuratelyMatched;
	
	private int siteId;
	
	private int logId;
	
	private int concernLevel;

	public int getConcernLevel() {
		return concernLevel;
	}

	public void setConcernLevel(int concernLevel) {
		this.concernLevel = concernLevel;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getAccuratelyMatched() {
		return accuratelyMatched;
	}

	public void setAccuratelyMatched(int accuratelyMatched) {
		this.accuratelyMatched = accuratelyMatched;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	
	/**
	 * the value for the word for searching
	 */
	private String searchWord;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	/**
	 * the value for category filter
	 */
	private String categoryFilter;

	/**
	 * get the value of categoryFilter
	 */
	public String getCategoryFilter() {
		return categoryFilter;
	}
	
	public void setCategoryFilter(String categoryFilter) {
		this.categoryFilter = categoryFilter;
	}
	
	public Map<Integer, String> getSiteFilterMap() {
		return SiteService.getSitesFilterMap();	
	}
	
	public Map<Integer, String> getDirectCategory() throws Exception {
		return SearchParameter.getDirectCategory();
	}
}
