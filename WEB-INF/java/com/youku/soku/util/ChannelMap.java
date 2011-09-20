package com.youku.soku.util;

import java.util.HashMap;
import java.util.Map;

public class ChannelMap {
	public static Map<Integer,String> channelMap=new HashMap<Integer,String>();
	
	static{
		for(ChannelType channelType: ChannelType.values()){
			channelMap.put(channelType.getValue(), channelType.name());
		}
	}

}
