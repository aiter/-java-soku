package com.youku.search.sort.servlet.search_page.util;

import java.util.HashSet;
import java.util.Set;

public class KeywordFilter {

	public static String SYMBOL = "`~!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
	public static Set<Character> SYMBOL_CHAR = new HashSet<Character>();
	static {
		for (char ch : SYMBOL.toCharArray()) {
			SYMBOL_CHAR.add(ch);
		}
	}

	public static String filterSymbol(String input) {
		if (input == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (char ch : input.toCharArray()) {
			if (SYMBOL_CHAR.contains(Character.valueOf(ch))) {
				builder.append(" ");
			} else {
				builder.append(ch);
			}
		}

		return builder.toString().replaceAll("\\s{2,}", " ").trim();
	}
}
