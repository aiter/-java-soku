/**
 * 
 */
package com.youku.soku.index.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author 1verge
 *
 */
public class UrlManager {
	private String url = null;
	
	public UrlManager(String url)
	{
		this.url = url;
	}
	
	public String getDomain()
	{
		int end = url.indexOf("/",8);
		return url.substring(7,end);
	}
	
	public List<String> getDomains()
	{
		List<String> list = new ArrayList<String>();
		int end = url.indexOf("/",8);
		String domain = url.substring(7,end);
		int last = domain.lastIndexOf(".");
		int first = domain.indexOf(".");
		list.add(domain);
		while (first<last)
		{
			String s = domain.substring(first+1);
			if (domainSuffixSet.contains(s))
			{
				break;
			}
			else
			{
				list.add(s);
				first = domain.indexOf('.', first+1);
			}
		}
		return list;
	}
	static HashSet<String> domainSuffixSet = new HashSet<String>(); 
	static {
		domainSuffixSet.add("com.cn");
		domainSuffixSet.add("gov.cn");
	}
}
