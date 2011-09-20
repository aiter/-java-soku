/**
 * 
 */
package com.youku.search.console.teleplay;

import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 *
 */
public class TelePlayNames {
	
//	private String[] nameArr = null;
//	private String[] versionArr = null;
//	
//	public TelePlayNames(String names,String versions)
//	{
//		if (names == null )
//		{
//			System.err.println("Error::Drama name Exception,has null value!");
//			throw new RuntimeException("Error::Drama name Exception,has null value!");
//		}
//		this.nameArr  = names.replaceAll("\\-|\\+|,","_").split("\\$\\$");
//		if (versions != null && versions.length() > 0)
//			this.versionArr = versions.split("\\$\\$");
//		
//		if (nameArr != null && nameArr.length>2)
//			nameArr = new String[]{nameArr[0],nameArr[1]};
//		
//		if (versionArr != null && versionArr.length>2)
//			versionArr = new String[]{versionArr[0],versionArr[1]};
//		
//	}
//	
//	public TelePlayNames(String[] nameArr,String[] versionArr)
//	{
//		if (nameArr == null )
//		{
//			System.err.println("Error::Drama name Exception,has null value!");
//			throw new RuntimeException("Error::Drama name Exception,has null value!");
//		}
//		this.nameArr  = nameArr;
//		this.versionArr = versionArr;
//	}
//	
//	public List<String> getFullVersionNames()
//	{
//		ArrayList<String> result = new ArrayList<String>();
//		
//		for (String name:nameArr)
//		{
//			addVersions(result,name);
//		}
//		
//		return result;	
//		
//	}
//	
//	private void addVersions(List<String> result,String name)
//	{
//		if (versionArr == null) {
//			result.add(name );
//			return;
//		}
//		for (String version : versionArr)
//		{
//			result.add(name + version);
//		}
//	}
//	private void addVersions(StringBuffer result,String name)
//	{
//		if (versionArr == null) {
//			result.append(name).append("|");
//			return;
//		}
//		
//		
//		for (String version : versionArr)
//		{
//			result.append(name).append(version).append("|");
//			if (version!=null && version.length() > 0){
//				result.append(version).append(name).append("|");
//			}
//		}
//		
//	}
//	private String arrayToString(String[] array)
//	{
//		if (array == null || array.length == 0)
//			return null;
//		
//		StringBuffer sb = new StringBuffer();
//		for (String s:array)
//		{
//			sb.append(s).append(",");
//		}
//		return sb.substring(0,sb.length()-1);
//	}
//	
//	public List<String> getSearchNameList()
//	{
//		ArrayList<String> result = new ArrayList<String>();
//		
//		int len = nameArr.length;
//		
//		for (int i=0;i<len;i++)
//		{
//			result.add(nameArr[i]);
//			for (int j= i+1;j<len;j++)
//			{
//				if (j == i)
//					continue;
//				addVersions(result,nameArr[i] + "," + nameArr[j]);
//			}
//		}
//		
//		if (len > 2){
//			addVersions(result,arrayToString(nameArr));
//		}
//		
//		return result;
//	}
//	
//	public String getSearchNameString()
//	{
//		StringBuffer result =  new StringBuffer();
//		
//		int len = nameArr.length;
//		
//		for (int i=0;i<len;i++)
//		{
//			//System.out.println(i + nameArr[i]);
//			addVersions(result,nameArr[i]);
//			for (int j= i+1;j<len;j++)
//			{
//				if (j == i)
//					continue;
//				addVersions(result,nameArr[i] + "," + nameArr[j]);
//			}
//		}
//		
//		if (len > 2){
//			StringBuffer sb = new StringBuffer();
//			for (int i = 0;i<len;i++)
//			{
//				sb.append(nameArr[i]).append(",");
//			}
//			addVersions(result,sb.substring(0,sb.length()-1));
//		}
//		
//		return result.substring(0,result.length()-1);
//	}
//	
//	public static void main(String[] args)
//	{
//		System.out.println(System.currentTimeMillis()/1000);
//		String names = "越狱,prison break";
//		TelePlayNames name = new TelePlayNames(names,"第一季");
//		
//		List<String> result = name.getSearchNameList();
//		for (String o:result)
//		{
//			System.out.println(o);
//		}
//		
//		System.out.println(name.getSearchNameString());
//	}
}
