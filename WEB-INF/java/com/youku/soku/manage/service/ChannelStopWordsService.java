package com.youku.soku.manage.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.manage.entity.ChannelStopWords;
import com.youku.soku.manage.sql.ChannelStopWordPersistence;

public class ChannelStopWordsService {
	
	private static ChannelStopWordPersistence cswp = new ChannelStopWordPersistence();
	
	public static List<ChannelStopWords> getAllChannelStopWords() {
		return cswp.getChannelStopWords();
	}
	
	public static ChannelStopWords getChannelStopWords(String channelName) {
		return cswp.getChannelStopWords(channelName);
	}
	
	public static void changeChannelStopWords(String channelName, String stopwords) {
		cswp.changeChannelStopWords(channelName, stopwords);
	}
	
	public static void changeChannelDeleteWords(String channelName, String deletewords) {
		cswp.changeChannelDeleteWords(channelName, deletewords);
	}
	
	public static void addChannelStopWords(String channelName, String stopword) {
		ChannelStopWords csw = cswp.getChannelStopWords(channelName);
		System.out.println(channelName);
		String stopwords = csw.getKeywords();
		if(StringUtils.isBlank(stopwords)) {
			stopwords = stopword;
		} else if(stopwords.indexOf("|") > 0) {
			String[] wordArray = stopwords.split("[|]");
			for(String aWord : wordArray) {
				if(aWord.equals(stopword)) {
					return;
				}
			}
			
			stopwords += "|" + stopword;
		} else {
			if(stopwords.equals(stopword)) {
				return;
			} else {
				stopwords += "|" + stopword;
			}
		}
		cswp.changeChannelStopWords(channelName, stopwords);
	}
	
	public static void addChannelDeleteWords(String channelName, String deleteword) {
		ChannelStopWords csw = cswp.getChannelStopWords(channelName);
		String deltewords = csw.getDeleteWords();
		if(StringUtils.isBlank(deltewords)) {
			deltewords = deleteword;
		} else if(deltewords.indexOf("|") > 0) {
			String[] wordArray = deltewords.split("[|]");
			for(String aWord : wordArray) {
				if(aWord.equals(deleteword)) {
					return;
				}
			}
			
			deltewords += "|" + deleteword;
		} else {
			if(deltewords.equals(deleteword)) {
				return;
			} else {
				deltewords += "|" + deleteword;
			}
		}
		cswp.changeChannelDeleteWords(channelName, deltewords);
	}
	
	public static void deleteChannelStopWords(String channelName, String stopword) {
		ChannelStopWords csw = cswp.getChannelStopWords(channelName);
		String stopwords = csw.getKeywords();
		if(StringUtils.isBlank(stopwords)) {
			return;
		} else if(stopwords.indexOf("|") > 0) {
			String[] wordArray = stopwords.split("[|]");
			stopwords = "";
			for(String aWord : wordArray) {
				if(!aWord.equals(stopword)) {
					stopwords += aWord + "|";
				}
			}
			
			stopwords.substring(0, stopwords.length() - 1);
		} else {
			if(stopwords.equals(stopword)) {
				stopwords = "";
			} else {
				return;
			}
		}
		cswp.changeChannelStopWords(channelName, stopwords);
	}

}
