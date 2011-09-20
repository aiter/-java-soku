package com.youku.soku.manage.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.entity.SiteWeight;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.manage.service.SiteWeightService;
public class SiteWeightAction extends BaseActionSupport {
	
	public String list() throws Exception {
		Map<Integer, String> siteMap = SiteService.getNoIntegrateSitesMap();
		List<SiteWeight> swList = new ArrayList<SiteWeight>();
		for(int siteId : siteMap.keySet()) {
			SiteWeight sw = SiteWeightService.getSiteWeight(siteId);
			if(sw != null) {
				sw.setSiteName(siteMap.get(siteId));
			} else {
				sw = new SiteWeight();
				sw.setFkSiteId(siteId);
				sw.setSiteName(siteMap.get(siteId));
			}
			swList.add(sw);
		}
		
		System.out.println(swList);
		setSwList(swList);
		
		return Constants.LIST;
	}
	
	public String save() throws Exception {
		List<SiteWeight> swList = getSwList();
		int normalSiteWeight = 0;
		int librarySiteWeight = 0;
		for(SiteWeight sw : swList) {
			normalSiteWeight += sw.getNormalWeight();
			librarySiteWeight += sw.getLibraryWeight();
		}
		
		if(normalSiteWeight > 100 || librarySiteWeight > 100) {
			addActionError(getText("个站点所占比例之和不能大于100"));
			return Constants.LIST;
		}
	
		for(SiteWeight sw : swList) {
			SiteWeightService.changeSiteWeight(sw);
		}
		return SUCCESS;
	}
	
	private List<SiteWeight> swList;
	
	public List<SiteWeight> getSwList() {
		return swList;
	}

	public void setSwList(List<SiteWeight> swList) {
		this.swList = swList;
	}
	
	private Map<Integer, String> siteMap;

	public Map<Integer, String> getSiteMap() {
		return siteMap;
	}

	public void setSiteMap(Map<Integer, String> siteMap) {
		this.siteMap = siteMap;
	}

		

}
