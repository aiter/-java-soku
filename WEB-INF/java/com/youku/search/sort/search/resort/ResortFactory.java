package com.youku.search.sort.search.resort;

import com.youku.search.config.Config;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.sort.util.bridge.BridgeMap;
import com.youku.search.sort.util.bridge.SearchUtil;

public class ResortFactory {
	
	private ResortFactory() {
	}
	
	public static <T, R> AbstractVideoResort<T, R> getResort(SearchContext<T> context){
		int type = context.type;
		
		if (context.p.type == SearchConstant.VIDEO_MD5) {
			return VideoNoMergeResort.I;
		}
		
		boolean isResort = (Config.getResortType() == 0 ? false : true);
		if (isResort) {	
			if (context.p.orderFieldStr.equalsIgnoreCase("null")) {
				return SearchUtil.getValue(BridgeMap.ResortInstance, type);
			} else if (context.p.orderFieldStr.equalsIgnoreCase("createtime")) {
				return SearchUtil.getValue(BridgeMap.ResortByCreatedTimeInstance, type);
			} else {
				return SearchUtil.getValue(BridgeMap.UNResortInstance, type);
			}
		} else {
			return SearchUtil.getValue(BridgeMap.UNResortInstance, type);
		}
	}

}
