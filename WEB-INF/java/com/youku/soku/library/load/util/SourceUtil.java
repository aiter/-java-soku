/**
 * 
 */
package com.youku.soku.library.load.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权 工具类
 * @author liuyunjian
 * 2011-2-22
 */
public class SourceUtil {
	public static final String SOURCE_DEFAULT = "000";
	private static final String DEFAULT = "unknow";
	private static Map<String, String> sourceMap = new HashMap<String, String>();
	static {
		sourceMap.put(DEFAULT, "0x1");
		sourceMap.put("authorized", "1xx");
		sourceMap.put("unauthorized", "0xx");
		sourceMap.put("expired", "0xx");
		sourceMap.put("public", "1xx");
	}

	/**
	 * 通过中间层版权信息，返回对应的版权字符串
	 * @param optString
	 * @return
	 */
	public static String parse(String optString) {
		if(optString.isEmpty()){
			optString = DEFAULT;
		}
		String tmpSource = sourceMap.get(optString);
		if(tmpSource==null){
			optString = DEFAULT;
			tmpSource = sourceMap.get(optString); 
		}
		
		return combine(tmpSource,null);
	} 
	
	/**
	 * 合并权限 
	 */
	public static String combine(String source1,String source2) {
		if((source1==null ||source1.isEmpty()) && (source2 == null ||source2.isEmpty())){
			return SOURCE_DEFAULT;
		}
		
		if(source1==null ||source1.isEmpty()){
			return source2.replace('x', '0');
		}
		if(source2 == null ||source2.isEmpty()){
			return source1.replace('x', '0');
		}
		
		source1 = source1.trim();
		source2 = source2.trim();
		if(source1.length() == source2.length()){
			char []sArr1 = source1.toCharArray();
			char []sArr2 = source2.toCharArray();
			
			StringBuilder sBuilder = new StringBuilder();
			
			for (int i = 0; i < sArr1.length; i++) {
				if(sArr1[i]=='x'){
					if(sArr2[i]=='x'){
						sBuilder.append('0');
					}else {
						sBuilder.append(sArr2[i]);
					}
				}else {
					if(sArr2[i]=='x'){
						sBuilder.append(sArr1[i]);
					}else {
						sBuilder.append((sArr1[i]=='1'||sArr2[i]=='1')?'1':'0');
					}
				}
			}
			
			return sBuilder.toString();
		}
		
		return SOURCE_DEFAULT;
	}
	
	/**
	 * 主方法，测试
	 */
	public static void main(String[] args) {
		System.out.println(combine("0x0","100"));
		System.out.println(combine("1x1","100"));
		System.out.println(combine("0x0","111"));
		
		System.out.println(parse("expired"));
		System.out.println(parse("public"));
		System.out.println(parse("liuyunjian"));
	}
}
