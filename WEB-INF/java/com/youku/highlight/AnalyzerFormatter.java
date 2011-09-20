package com.youku.highlight;


/**
 * 分词前的格式化处理
 * @author 1verge
 *
 */
public class AnalyzerFormatter {
	public static final char BLANK_OLD_CHAR = ' ';		//转换后的空格
	public static final char BLANK_NEW_CHAR = '╬';		//转换后的空格
	
	public static final char SEPARATOR_CHAR = ' '; 	//分词后词语之间的间隔符
	public static final String SEPARATOR = new String(new char[]{SEPARATOR_CHAR}); 	//分词后词语之间的间隔符
	
	/**
	 * 分词前格式化字符串
	 * @param str
	 * @return
	 */
	public static String format(String str){
		if (str == null)return null;
		return str.replace(BLANK_OLD_CHAR,BLANK_NEW_CHAR );
	}
	
	/**
	 * 恢复格式化
	 * @param str
	 * @return
	 */
	public static String unformat(String str){
		if (str == null)return null;
		return unformat(str,0);
	}
		
	
	/**
	 * 恢复格式化
	 * @param str
	 * @param   maxLength 返回字符串的长度 0 不限制
	 * @return
	 */
	public static String unformat(String str,int maxLength){
		if (str == null)return null;
		
		StringBuilder result = new StringBuilder();
		
		char[] chars = str.toCharArray();
		int len = chars.length;
		int start = 0;
		for (int i = 0 ;i < len; i++){
			if (chars[i] == AnalyzerFormatter.SEPARATOR_CHAR){
				
				if (chars[i-1] == AnalyzerFormatter.BLANK_NEW_CHAR){
					result.append(AnalyzerFormatter.BLANK_OLD_CHAR);
				}
				else{
					result.append(chars,start,i-start);
				}
				
				start = i+1;
				
				if (maxLength > 0 && result.length() >= maxLength)
					break;
				
			}
		}
		
		return result.toString();
	}
}
