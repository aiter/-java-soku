/**
 * 
 */
package com.youku.soku.web.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.json.JSONArray;
import org.json.JSONException;

import com.youku.soku.sort.Parameter;
import com.youku.soku.util.Constant;
import com.youku.soku.util.MyUtil;

/**
 * @author 1verge
 *
 */
public class WebUtil {
	public static String formatTags(String tags)
	{
		if (tags == null)
			return "";
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
	
	public static String formatTags(String tags,String hl_tags)
	{
		if (tags == null)
			return "";
		if (hl_tags == null || hl_tags.isEmpty())
			return formatTags(tags);
		
		String[] arr = tags.split(",");
		String[] hltags = hl_tags.split(",");
		
		if (arr.length != hltags.length)
			return formatTags(tags);
		
		StringBuilder builder = new StringBuilder();
		int len = Math.min(Math.min(arr.length,hltags.length), 3);
		for(int i=0;i<len;i++)
		{
			String s = arr[i];
			String hs = hltags[i];
			builder.append("<a href=\""+ Constant.Web.SEARCH_URL +"?keyword=");
			builder.append(MyUtil.urlEncode(s));
			builder.append("\" target=\"_blank\">");
			builder.append(hs);
			builder.append("</a> ");
		}
		return builder.toString();
	}
	
	public static String getSecondAsString(float second)
	{
		if (second == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		int sec = Math.round(second);
		if (second < 10){
			builder.append("00:0").append(sec);
		}
		else if (second < 60){
			builder.append("00:").append(sec);
		}
		else if (second >= 60)
		{
			int s = sec%60;

			int m = sec/60;
			if (m < 10)
				builder.append("0");
			
			builder.append(m).append(":");
			
			if (s < 10)
				builder.append("0");
			
			builder.append(s);
		}
		return builder.toString();
	}
	
	public static String formatCost(int millisecond)
	{
		if (millisecond < 9)
			return "0.00" + millisecond;
		else if (millisecond < 99)
			return "0.0" + millisecond;
		else if (millisecond < 999)
			return "0." + millisecond;
		else
			return "0.014";
	}
	
	public static String subString(String s,int start,int end){
		if (null == s)
			return "";
		if (s.length() > end){
			return s.substring(start,end);
		}
		else{
			return s;
		}
	}
	
	public static String getPagePrefixString(Parameter param,HttpServletRequest request)
	{
		if (param == null)
			return "";
		StringBuilder builder = new StringBuilder(Constant.Web.SEARCH_URL);
		builder.append("?");
		if (param.keyword != null)
		{
			builder.append("keyword=");
			builder.append(MyUtil.urlEncode(param.keyword));
			builder.append("&");
		}
		
		if (param.sort > 0)
		{
			builder.append("orderfield=");
			builder.append(param.sort);
			builder.append("&");
		}
		if (request.getParameter("time_length")!=null)
		{
			builder.append("time_length=");
			builder.append(param.time_length);
			builder.append("&");
		}
		if (request.getParameter("limit_date")!=null)
		{
			builder.append("limit_date=");
			builder.append(param.limit_date);
			builder.append("&");
		}
		if (request.getParameter("hd")!=null)
		{
			builder.append("hd=");
			builder.append(param.hd);
			builder.append("&");
		}
		if (request.getParameter("site")!=null)
		{
			builder.append("site=");
			builder.append(param.site);
			builder.append("&");
		}
//		if (param.ext )
//		{
//			builder.append("ext=");
//			builder.append("2&");
//		}
		String total_sites = request.getParameter("ts");
		if (total_sites != null)
		{
			builder.append("ts=");
			builder.append(total_sites);
			builder.append("&");
		}
		String cate = request.getParameter("ca");
		if (cate != null)
		{
			builder.append("ca=");
			builder.append(cate);
			builder.append("&");
		}
		String ver = request.getParameter("ver");
		if (ver != null)
		{
			builder.append("ver=");
			builder.append(ver);
			builder.append("&");
		}
		
		
		builder.append( "ext=2&curpage=");
		
		return builder.toString();
	}
	
	public static String getPagePrefixString(String urlPrefix, Parameter param,HttpServletRequest request)
	{
		if (param == null)
			return "";
		StringBuilder builder = new StringBuilder(urlPrefix);
		builder.append("?");
		if (param.keyword != null)
		{
			builder.append("keyword=");
			builder.append(MyUtil.urlEncode(param.keyword));
			builder.append("&");
		}
		
		if (param.sort > 0)
		{
			builder.append("orderfield=");
			builder.append(param.sort);
			builder.append("&");
		}
		if (param.time_length > 0)
		{
			builder.append("time_length=");
			builder.append(param.time_length);
			builder.append("&");
		}
		if (param.limit_date > 0)
		{
			builder.append("limit_date=");
			builder.append(param.limit_date);
			builder.append("&");
		}
		if (param.hd > 0)
		{
			builder.append("hd=");
			builder.append(param.hd);
			builder.append("&");
		}
		if (param.site > 0)
		{
			builder.append("site=");
			builder.append(param.site);
			builder.append("&");
		}
//		if (param.ext )
//		{
//			builder.append("ext=");
//			builder.append("2&");
//		}
		String total_sites = request.getParameter("ts");
		if (total_sites != null)
		{
			builder.append("ts=");
			builder.append(total_sites);
			builder.append("&");
		}
		String cate = request.getParameter("ca");
		if (cate != null)
		{
			builder.append("ca=");
			builder.append(cate);
			builder.append("&");
		}
		String ver = request.getParameter("ver");
		if (ver != null)
		{
			builder.append("ver=");
			builder.append(ver);
			builder.append("&");
		}
		
		
		builder.append( "ext=2&curpage=");
		
		return builder.toString();
	}
	
	public static void formatJSONArray(JSONArray performer,JspWriter out) throws IOException
	{
		int max = Math.min(performer.length(),3);
		for (int j=0;j<max;j++){
			try {
				String n = performer.getString(j);
				out.println("<a href=\"/v?keyword="+MyUtil.urlEncode(n)+"\">"+n+"</a> ");
			} catch (JSONException e) {
			}
			
		}
	}
	
	public static String formatHtml(String plainText)
	{
		if (plainText == null || plainText.length() == 0)
		{
			return null;
		}
		StringBuilder result = new StringBuilder();

		for (int index=0; index<plainText.length(); index++) 
		{
			char ch = plainText.charAt(index);

			switch (ch) 
			{
			case '"':
				result.append("&quot;");
				break;

			case '\'':
				result.append("&#039;");
				break;

			case '<':
				result.append("&lt;");
				break;

			case '>':
				result.append("&gt;");
				break;

			default:
				result.append(ch);
			}
		}

		return result.toString();
	}
	static Random randGen = new Random();
	public static String formatLogo(String logo)
	{
		if (logo == null || logo.equals("NA") || logo.equals("null")){
			return Constant.DEFAULT_VIDEO_LOGO;
		}
		
		if (!logo.startsWith("http"))
		{
			StringBuilder sb = new StringBuilder("http://g");
			sb.append(randGen.nextInt(3)+1);
			sb.append(".ykimg.com/");
			sb.append(logo);
			return sb.toString();
		}
		return logo;
		
	}
	
	/**
	 * 加密html代码，只适用于用javascript解析
	 * @param str
	 * @return
	 */
	public static String encodingHtmlTag(String str) {

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			switch (c) {
			case '"':
			case '&':
			case '>':
			case '=':
			case '<':
			case '\'':
			case '\\':
				result.append("\\x" + Integer.toHexString(c));
				break;
			default:
				result.append(c);
				break;
			}

		}
	
		return result.toString();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
	}
}
