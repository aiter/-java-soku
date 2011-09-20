package com.youku.search.spider.mdbchina;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.youku.search.spider.mdbchina.entity.SimpleTVDrama;
import com.youku.search.spider.mdbchina.entity.TVDrama;

public class Parser {

	/**
	 * <a href="/movies/63319/" title="甜蜜的人生" target="_blank">甜蜜的人生</a>
	 */
	public static List<SimpleTVDrama> parseTVId(String s) {
		List<SimpleTVDrama> list = new ArrayList<SimpleTVDrama>();

		try {

//			String regex = "<[aA]\\s+[hH][rR][eE][fF]=\"/movies/(\\d+)/\".+?title=\"([^\"]*)\".*?>";
			String regex = "(?:.*)<span><A href=\"(?:.*)\" target=\"_blank\">(.*)</A>(?:.*)</span>";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(s);
			while (matcher.find()) {
				
				SimpleTVDrama simpleTVDrama = new SimpleTVDrama();
				simpleTVDrama.name = matcher.group(1);
				list.add(simpleTVDrama);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return list;
	}


	public static TVDrama parseTVDrama(String s) {
		TVDrama drama = new TVDrama();
		return drama;
	}
	
	public static void main(String[] args)
	{
		String s = "<span><A href=\"/movies/61674/\" target=\"_blank\">攻壳机动队2.0 </A>(2008)</span>";
		parseTVId(s);
	}

}
