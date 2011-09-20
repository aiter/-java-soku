package com.youku.search.index.query.parser;

public abstract class BaseParser implements Parser{
	
	public ParserResult parse(String input) {   
		ParserResult result = new ParserResult();
		String[] fields = getFields();
		char[] chars = input.toCharArray();
		int lastPoint = 0;
		String curField = null;
		String lastValue = null;
		int i = 0;
		for (i = 0;i < chars.length; i++)
		{
			char c = chars[i];
			if (c == ':'){
				int min = Math.min(i-lastPoint, 5);
				String tmp = new String(chars,i-min,min);
				for (String field:fields){
					if (tmp.endsWith(field)){
						if (curField != null){
							lastValue = new String(chars,lastPoint,i-field.length()-lastPoint);
							result.setAttribute(curField, lastValue);
						}
						else{
							if (i - field.length() > 0){ // 说明有普通词
								result.setKeyword(new String(chars,0,i-field.length()));
							}
						}
						curField = field;
						break;
					}
				}
				lastPoint = i+1;
			}
		}
		
		if (curField == null)
			return null;
		else{
			if (lastPoint < chars.length-1){
				lastValue = new String(chars,lastPoint,chars.length - lastPoint);
				result.setAttribute(curField, lastValue);
			}
			if (lastValue != null)
				return result;
			else
				return null;
		}
		
	 }
	
	public abstract String[] getFields();
	
}
