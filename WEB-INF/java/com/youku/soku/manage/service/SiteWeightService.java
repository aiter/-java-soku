package com.youku.soku.manage.service;

import java.util.List;

import com.youku.soku.manage.entity.SiteWeight;
import com.youku.soku.manage.sql.SiteWeightPersistence;

public class SiteWeightService {

	private static SiteWeightPersistence swp = new SiteWeightPersistence();
	
	public static List<SiteWeight> getAllSiteWeight() {
		return swp.getAllSiteWeight();
	}
	
	public static SiteWeight getSiteWeight(int siteId) {
		return swp.getSiteWeight(siteId);
	}
	
	public static void changeSiteWeight(SiteWeight siteWeight) {
		swp.changeSiteWeight(siteWeight);
	}
}
