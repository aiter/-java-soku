package com.youku.soku.shield;

import java.util.Collection;
import java.util.regex.Pattern;

import com.youku.soku.manage.shield.ShieldInfo;

public class ShieldUtil {
	
	public static Pattern listToRegex(Collection<ShieldInfo> list) {
		if(list == null || list.isEmpty()) {
			System.out.println("No Shield Words");
			return null;
		} else {
			StringBuilder sb = new StringBuilder();
			for(ShieldInfo si : list) {
				if(sb.length() != 0) {
					sb.append('|');
				}
				sb.append(si.getKeyword());
				
			}
			//System.out.println(sb.toString());
			return Pattern.compile(sb.toString());
		}
	}
}
