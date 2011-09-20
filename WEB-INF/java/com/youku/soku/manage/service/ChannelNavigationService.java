package com.youku.soku.manage.service;

import java.util.List;

import com.youku.soku.manage.entity.ChannelNavigation;
import com.youku.soku.manage.sql.ChannelNavigationPersistence;

public class ChannelNavigationService {
	
	private static ChannelNavigationPersistence cswp = new ChannelNavigationPersistence();
	
	public static List<ChannelNavigation> getAllChannelNavigation() {
		return cswp.getChannelNavigation();
	}
	
	public static ChannelNavigation getChannelNavigation(String channelName) {
		return cswp.getChannelNavigation(channelName);
	}
	
	
	public static void changeChannelNavigation(String channelName, String navigationText) {
		cswp.changeChannelNavigation(channelName, navigationText);
	}
	
	

}
