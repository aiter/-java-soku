package com.youku.top;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtils {

	public static void sleep() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String buildSubjectUrlGetYearAndPerson(int i) {
		StringBuilder url = new StringBuilder("http://10.103.12.72/show/");
		url.append(i);
		url
				.append("?fd=person%20releasedate&pn=1&pl=1&state%3Anormal%20&ft=json&cl=search_out");
		return url.toString();
	}

	public static String buildPersonUrl(int i, int j) {
		StringBuilder url = new StringBuilder(
				"http://10.103.12.72/person.person?q=personid%3A");
		url.append(i);
		url.append("-");
		url.append(j);
		url
				.append("%20state%3Anormal&fc=&fd=pk_odperson%20personname%20firstletter%20personalias%20thumburl%20persondesc%20gender%20birthday%20homeplace%20height%20bloodtype%20state%20persontype%20nationality%20occupation%20showcount%20episode_showcount%20movie_showcount%20tv_showcount%20anime_showcount%20mv_showcount%20variety_showcount%20sports_showcount&pn=1&pl=1000&ob=episode_showcount%3Adesc&ft=json&cl=search_out");
		return url.toString();
	}

	public static String buildPersonUrlGetMax() {
		return "http://10.103.12.72/person.person?q=state%3Anormal&fc=&fd=&pn=1&pl=1&ob=personid%3Adesc&ft=json&cl=search_out";
	}

	public static String buildPersonUrl(String name) {
		StringBuilder url = new StringBuilder(
				"http://10.103.12.72/person.person?q=state%3Anormal%20personname%3A");
		try {
			url.append(URLEncoder.encode(name, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		url
				.append("&fc=&fd=personid%20personname%20thumburl&pn=1&pl=10&ob=personid%3Adesc&ft=json&cl=search_out");
		return url.toString();
	}
}
