package com.youku.search.log_analyze;

import com.youku.search.hanyupinyin.Converter;
import com.youku.search.sort.KeywordFilter;

public class KeywordHandler {

	public static String filter(String keyword) {

		keyword = KeywordFilter.filter(keyword);

		keyword = keyword.replace("內", "内");

		return keyword;
	}

	public static String convertPY(String keyword) {
		return Converter.convert(keyword);
	}

	public static void main(String[] args) {
		String s = "{(sdAAAAdfj   快递发货        dl)}";
		String new_s = filter(s);
		String new_s_py = Converter.convert(new_s);

		System.out.println(s);
		System.out.println(new_s);
		System.out.println(new_s_py);

	}
}
