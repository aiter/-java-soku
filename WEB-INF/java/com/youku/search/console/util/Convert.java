package com.youku.search.console.util;

import java.util.Set;

import com.youku.search.console.vo.LeftMenuVO;

public class Convert{
	public static LeftMenuVO[] setToArray(Set<LeftMenuVO> s){
		if(null!=s&&s.size()>0){
			LeftMenuVO[] lmvo=s.toArray(new LeftMenuVO[]{});
			return lmvo;
		}
		return null;
	}
}
