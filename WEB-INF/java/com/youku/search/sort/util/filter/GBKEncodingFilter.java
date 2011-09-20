package com.youku.search.sort.util.filter;

import sun.io.CharToByteGBK;

import com.youku.search.sort.util.BaseFilter;

/**
 * 将所有不能映射到GBK范围的char过滤为空格
 * 
 * @author gaosong
 */
public class GBKEncodingFilter extends BaseFilter {

	private final CharToByteGBK ctb;
	
	public GBKEncodingFilter(){
		this.ctb = new CharToByteGBK();
	}
	
	@Override
	protected void innerFilter(char[] chars, int i) {
		boolean canConvert = ctb.canConvert(chars[i]);
		if (!canConvert) {
			chars[i] = ' ';
		}
	}

}
