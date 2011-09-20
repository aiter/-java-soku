package com.youku.soku.shield.matcher;

import java.util.Collection;

import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.ShieldWordsInfo;

public class FuzzyHitMatcher implements HitMatcher {
	
	private ShieldWordsInfo wordsInfo;
	
	public FuzzyHitMatcher(ShieldWordsInfo wordsInfo) {
		this.wordsInfo = wordsInfo;
	}
	
	@Override
	public ShieldInfo match(String keyword) {
		if( wordsInfo.getFuzzyHitWordsMap() == null) {
			return null;
		}
		Collection<String> words = wordsInfo.getFuzzyHitWordsMap().keySet();
		
		for(String word : words) {
			boolean matched = true;
			for(char c : word.toCharArray()) {
				if(keyword.indexOf(c) < 0) {
					matched = false;
					break;
				}
			}
			if(matched) {
				return wordsInfo.getFuzzyHitWordsMap().get(word);
			}
		}
		return null;
	}
}
