package com.youku.soku.shield.matcher;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.ShieldWordsHolder;
import com.youku.soku.shield.ShieldWordsInfo;

public class ContainHitMatcher implements HitMatcher {

	private ShieldWordsInfo wordsInfo;
	
	public ContainHitMatcher(ShieldWordsInfo wordsInfo) {
		this.wordsInfo = wordsInfo;
	}
	
	
	public ShieldInfo match1(String keyword) {
		Pattern pattern = wordsInfo.getContainHitPattern();
		
		Matcher matcher = pattern.matcher(keyword);
		
		System.out.println(matcher.matches());
		for(int i = 0; i < matcher.groupCount(); i++) {
			System.out.println(matcher.group());
		}
		
		return null;
	}
	
	@Override
	public ShieldInfo match(String keyword) {
		if(wordsInfo.getContainHitWordsMap() == null) {
			return null;
		}
		Collection<String> words = wordsInfo.getContainHitWordsMap().keySet();
		for(String s : words) {
			if(keyword.contains(s)) {
				return wordsInfo.getContainHitWordsMap().get(s);
			}
		}
		return null;
	}
	

	
	

}
