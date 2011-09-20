package com.youku.soku.shield.matcher;

import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.ShieldWordsInfo;

public class AccurateHitMatcher implements HitMatcher {
	
	private ShieldWordsInfo wordsInfo;
	
	public AccurateHitMatcher(ShieldWordsInfo wordsInfo) {
		this.wordsInfo = wordsInfo;
	}
	
	public ShieldInfo match(String keyword) {
		if(wordsInfo.getAccurateHitWordsMap() == null) {
			return null;
		}
		return wordsInfo.getAccurateHitWordsMap().get(keyword);
	}
}
