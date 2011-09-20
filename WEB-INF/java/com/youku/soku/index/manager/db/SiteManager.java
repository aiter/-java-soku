/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.util.HashMap;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.index.om.Site;
import com.youku.soku.index.om.SitePeer;


/**
 * @author 1verge
 *
 */
public class SiteManager {

	public static final String ZONGHE = "综合";
	
	int[] validSites = new int[]{1,19,9,6,3,17,2,14,15};
	
	private static SiteManager self = null;
	public  HashMap<Integer,Site> siteMap = new HashMap<Integer,Site>(); 
	public  HashMap<String,Site> tableNameMap = new HashMap<String,Site>();
	
	public static SiteManager getInstance(){
		
		if(self == null){
				self = new SiteManager();
		}
		return self;
	}
	
	public synchronized void init()
	{
		System.out.println("SiteManager .init..");
		List<Site> sites = getSiteList();
		if (sites != null)
		{
//			siteMap.clear();
//			tableNameMap.clear();
			for (Site site:sites)
			{
				siteMap.put(site.getId(),site);
				tableNameMap.put(site.getTablename(),site);
			}
		}
	}
	
	public int create(String siteName,String tableName)
	{
		Site site = new Site ();
		site.setName(siteName);
		site.setTablename(tableName);
		try {
			site.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return site.getId();
	}
	
	public Site getSite(int id)
	{
		if (id <= 0 ){
			return null;
		}
		Site site = siteMap.get(id);
		if (site== null)
		{
			try {
				site = SitePeer.retrieveByPK(id);
				siteMap.put(id,site);
				tableNameMap.put(site.getTablename(),site);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
		return site;
	}
	
	public String getName(int id)
	{
		if (id <=0){
			return ZONGHE;
		}
		else
		{
			Site site = siteMap.get(id);
			if (site != null)
				return site.getName();
		}
		return "";
	}
	
	/**
	 * 通过表明获取站点id
	 * @param table
	 * @return
	 */
	public Site getSiteByTableName(String table)
	{
		Site site = tableNameMap.get(table);
		if (site == null)
		{
			try {
				Criteria c = new Criteria();
				c.add(SitePeer.TABLE_NAME,table);
				List sites = SitePeer.doSelect(c);
				if (sites!=null && sites.size()>0)
				{
					site = (Site)sites.get(0);
					siteMap.put(site.getId(),site);
					tableNameMap.put(site.getTablename(),site);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return site;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<Site> getSiteList()
	{
		Criteria c = new Criteria();
		c.add(SitePeer.STATUS_ID,1);
		try {
			return SitePeer.doSelect(c);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Site> getValidSiteList()
	{
		Criteria c = new Criteria();
		c.add(SitePeer.STATUS_ID,1);
		c.addIn(SitePeer.ID, validSites);
		try {
			return SitePeer.doSelect(c);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return null;
	}
}
