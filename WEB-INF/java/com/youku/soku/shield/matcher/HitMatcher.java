package com.youku.soku.shield.matcher;

import com.youku.soku.manage.shield.ShieldInfo;

public interface HitMatcher {
	
	public ShieldInfo match(String keyword);
}
