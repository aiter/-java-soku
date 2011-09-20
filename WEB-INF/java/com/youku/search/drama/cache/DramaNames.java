/**
 * 
 */
package com.youku.search.drama.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.youku.search.analyzer.WordProcessor;

public class DramaNames {
	
	private String[] names;
	private String[] versions;
	
	public DramaNames(String[] names,String[] versions)
	{
		if (names == null || versions == null)
		{
			System.err.println("Error::Drama name Exception,has null value!");
			throw new RuntimeException("Error::Drama name Exception,has null value!");
		}
		this.names = names;
		this.versions = versions;
	}
	
	public DramaNames(List<String> names,List<String> versions)
	{
		if (names == null || versions == null)
		{
			System.err.println("Error::Drama name Exception,has null value!");
			throw new RuntimeException("Error::Drama name Exception,has null value!");
		}
		this.names = names.toArray(new String[0]);
		this.versions = versions.toArray(new String[0]);
	}
	
	public String getMainKey()
	{
		return names[0]+versions[0];
	}
	
	public List<String> getCacheKey()
	{
		ArrayList<String> result = new ArrayList<String>();
		for (String name:names)
		{
			for (String version:versions)
			{
				if (null != version && version.length() > 0)
				{
					result.add(name + version);
					result.add(name + " " + version);
				}
			}
		}
		
		return result;
	}
	
	public static String formatName(String name)
	{
		Matcher m = Pattern.compile("第([^ |第]*)(季|部)$").matcher(name);

		while (m.find()) {
			if (m.groupCount() != 2)
				break;
			name = m.replaceFirst(new StringBuffer("第").append(WordProcessor.formatNumber(m.group(1))).append(m.group(2)).toString());
			break;
		}
		return name.toLowerCase();
	}
}
