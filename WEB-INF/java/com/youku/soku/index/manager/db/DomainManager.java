/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.index.manager.UrlManager;
import com.youku.soku.index.om.Domain;
import com.youku.soku.index.om.DomainPeer;

/**
 * @author 1verge
 *
 */
public class DomainManager {

	private static DomainManager self = null;
	private static HashMap<String,Domain> domainMap = new HashMap<String,Domain>();
	private static HashMap<Integer,String> urlMap = new HashMap<Integer,String>();
	
	private static HashMap<String,List<Domain>> domainCache = new HashMap<String,List<Domain>>();
	
	public static DomainManager getInstance(){
		
		if(self == null){
				self = new DomainManager();
				self.init();
		}
		return self;
	}

	public synchronized void init()
	{
		List<Domain> domains = getDomainList();
		if (domains!=null){
			domainMap.clear();
			urlMap.clear();
			for (Domain domain:domains)
			{
				domainMap.put(domain.getUrl(),domain);
				urlMap.put(domain.getId(),domain.getUrl());
			}
		}
	}
	@SuppressWarnings("unchecked")
	public List<Domain> getDomainList()
	{
		Criteria c = new Criteria();
		c.add(DomainPeer.ID,(Object)" is not null");
		try {
			return DomainPeer.doSelect(c);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getUrl(int id)
	{
		String domain = urlMap.get(id);
		if (domain== null)
		{
			try {
				Domain dom = DomainPeer.retrieveByPK(id);
				urlMap.put(id,dom.getUrl());
				domainMap.put(dom.getUrl(),dom);
				
				domain = dom.getUrl();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
		return domain;
	}
	
	@SuppressWarnings("unchecked")
	public Domain getDomain(String url)
	{
		Domain domain = domainMap.get(url);
		if (domain==null)
		{
			Criteria c = new Criteria();
			c.add(DomainPeer.URL,url);
			try {
				List<Domain> domains = DomainPeer.doSelect(c);
				if (domains!= null && domains.size()>0)
				{
					domain = domains.get(0);
					domainMap.put(domain.getUrl(),domain);
					urlMap.put(domain.getId(),domain.getUrl());
				}
			} catch (TorqueException e) {
				e.printStackTrace();
			}
		}
		return domain;
	}
	
	/**
	 * 通过url获取站点id
	 * @param url
	 * @return
	 */
	public List<Domain> getDomainsByUrl(String url)
	{
		UrlManager u = new UrlManager(url);
		List<String> domain = u.getDomains();
		if (domain != null)
		{
			List<Domain> result = new ArrayList<Domain>();
			for (String str:domain){
				Domain site = getDomain(str);
				result.add(site);
			}
			return result;
		}
		return null;
	}
	public int getDomainId(String url)
	{
		Domain domain = getDomain(url);
		if(domain != null)
			return domain.getId();
		else 
			return 0;
	}
	
	/**
	 * 通过url获取站点id，如果没有此域名，则插入
	 * @param url
	 * @return
	 */
	public List<Domain> getDomainsByUrlWithInsert(int site_id,String url)
	{
		UrlManager um = new UrlManager(url);
		String maxDomain = um.getDomain();
		if (domainCache.containsKey(maxDomain))
			return domainCache.get(maxDomain);
		
		List<String> domain = um.getDomains();
		if (domain != null)
		{
			List<Domain> result = new ArrayList<Domain>();
			for (String str:domain){
				Domain site = getDomain(str);
				if (site == null)
				{
					site = new Domain ();
					site.setUrl(str);
					site.setSiteId(site_id);
					try {
						site.save();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				result.add(site);
			}
			domainCache.put(maxDomain, result);
			return result;
		}
		return null;
	}
}
