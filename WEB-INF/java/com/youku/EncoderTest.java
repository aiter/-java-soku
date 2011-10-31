package com.youku;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncoderTest {

	// change default string to utf8
	public static String chineseToUtf8(String str) {
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static String chineseToUnicode(String str){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<str.length();i++)
			sb.append("\\u"+Integer.toHexString(str.charAt(i)));
		return sb.toString();
	}
	
	// change unicode to chinese
	public static String unicodeToChinese(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}

	public static void main(String[] args) {
		//System.out.println(chineseToUtf8("登出"));
		//System.out.println(unicodeToChinese("\u767b\u51fa"));
		//System.out.println(chineseToUnicode("屏蔽系统"));
		System.out.print("test".matches("(t|{\\S+})"));
		//System.out.print("test".matches("(巨翼|{\"type\":\"0\",\"alias\":\"Big Wing\"})[ ]*[ ]*[第]*[ ]*[0]*5[ ]*[集话回]"));
	}
}
