package com.youku.soku.manage.deadlink;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.library.load.UrlCheck;
import com.youku.soku.library.load.UrlCheckPeer;

public class OtherSiteUrlCheck extends BaseUrlCheck{
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public static final int STATUS_NOT_HANDLE = 0;
	
	public static final int STATUS_HANDLED = 1;
	
	public void saveDeadLink(String url, int programmeId) throws Exception{
		handleDeadLink(url, programmeId);		
	}
	
	public void handleDeadLink(String url, int programmeId) {
		Criteria critPe = new Criteria();
		critPe.add(ProgrammeEpisodePeer.URL, url);
		int orderStage = -1;
		
		try {
			List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(critPe);
			logger.info("peList size: " + peList.size());
			if(peList != null && !peList.isEmpty()) {
				for(ProgrammeEpisode pe : peList) {
					logger.info("delete dead link: " + pe.getTitle() + pe.getUrl());
					orderStage = pe.getOrderStage();					
					if(orderStage == 0) {
						orderStage = pe.getOrderId();
					}
					
					if(programmeId == 0) {
						ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(pe.getFkProgrammeSiteId());
						programmeId = ps.getFkProgrammeId();
					}
					
					markDeadLinkEpisode(pe);
					
					try {
						//判断是否已经存在该url
						Criteria uccrit = new Criteria();
						uccrit.add(UrlCheckPeer.URL,url);
						List<UrlCheck> ucList=UrlCheckPeer.doSelect(uccrit);
						if(null==ucList||ucList.size()==0){
							UrlCheck uc = new UrlCheck();
							uc.setUrl(url);
							uc.setStatus(STATUS_HANDLED);
							uc.setCreateTime(new Date());
							uc.setOrderStage(orderStage);
							uc.setProgrammeId(programmeId);
							uc.setProgrammeSiteId(pe.getFkProgrammeSiteId());
							uc.save();
						}else{
							UrlCheck uc = ucList.get(0);
							uc.setStatus(STATUS_HANDLED);
							UrlCheckPeer.doUpdate(uc);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void checkAll() {
		try {
			Criteria crit = new Criteria();
			crit.add(UrlCheckPeer.STATUS, STATUS_NOT_HANDLE);
			
			List<UrlCheck> urlList = UrlCheckPeer.doSelect(crit);
			if(urlList != null) {
				for(UrlCheck url : urlList) {
					try {
						logger.info("check url: " + url);
						handleDeadLink(url.getUrl(), 0);
						url.setStatus(STATUS_HANDLED);
						UrlCheckPeer.doUpdate(url);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		} catch (TorqueException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
