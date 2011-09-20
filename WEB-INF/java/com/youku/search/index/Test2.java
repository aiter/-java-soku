/**
 * 
 */
package com.youku.search.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.youku.search.config.Config;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Request;
import com.youku.soku.util.Constant;
import com.youku.soku.util.MyUtil;


/**
 * @author 1verge
 *
 */
public class Test2 {
	
	public static String formatTags(String tags)
	{
		if (tags == null)
			return "";
//		tags = tags.replaceAll(" ", ",");
		String[] arr = tags.split(",");
		StringBuilder builder = new StringBuilder();
		int len = Math.min(arr.length, 3);
		for(int i=0;i<len;i++)
		{
			String s = arr[i];
			builder.append("<a href=\""+ Constant.Web.SEARCH_URL +"?keyword=");
			builder.append(MyUtil.urlEncode(s));
			builder.append("\" target=\"_blank\">");
			builder.append(s);
			builder.append("</a> ");
		}
		return builder.toString();
	}
	
	static String s = "http://10.101.8.103/index/index.jsp?keywords=vid%3A44485782&sort=&field=1&category=&timeless=&timemore=&sub=%CC%E1%BD%BB";
	public static void main(String[] args)
	{

		
		BufferedReader r;
		try {
			r = new BufferedReader(new InputStreamReader(new FileInputStream("e:/a.txt")));
			String line = null;
			
			while ( (line = r.readLine()) != null)
			{
				int i  = DataFormat.parseInt(line);
				
				if (i >44500000){
					
					String str = Request.requestGet("http://10.101.8.102/index/index.jsp?keywords=vid%3A"+i+"&sort=&field=1&category=&timeless=&timemore=&sub=%CC%E1%BD%BB");
					if (str.indexOf("没有找到结果")>0)
						System.out.println(i);
//					else
//						System.out.println(i+"\t"+str);
				}
				else
				{
					String str = Request.requestGet("http://10.101.8.101/index/index.jsp?keywords=vid%3A"+i+"&sort=&field=1&category=&timeless=&timemore=&sub=%CC%E1%BD%BB");
					if (str.indexOf("没有找到结果")>0)
						System.out.println(i);
//					else
//						System.out.println(i+"\t"+str);
				}
			}
			r.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
