package com.youku.search.console.pojo.map;

import org.apache.torque.TorqueException;

/**
 * This is a Torque Generated class that is used to load all database map 
 * information at once.  This is useful because Torque's default behaviour
 * is to do a "lazy" load of mapping information, e.g. loading it only
 * when it is needed.<p>
 *
 * @see org.apache.torque.map.DatabaseMap#initialize() DatabaseMap.initialize() 
 */
public class SearchteleplayMapInit
{
	public static final void init()
		throws TorqueException
	{
        com.youku.search.console.pojo.TeleplayPeer.getMapBuilder();
        com.youku.search.console.pojo.PlayNamePeer.getMapBuilder();
        com.youku.search.console.pojo.PlayVersionPeer.getMapBuilder();
        com.youku.search.console.pojo.EpisodePeer.getMapBuilder();
        com.youku.search.console.pojo.EpisodeVideoPeer.getMapBuilder();
        com.youku.search.console.pojo.BlacklistPeer.getMapBuilder();
        com.youku.search.console.pojo.FeedbackPeer.getMapBuilder();
        com.youku.search.console.pojo.TeleplaySpidePeer.getMapBuilder();
        com.youku.search.console.pojo.EpisodeLogPeer.getMapBuilder();
        com.youku.search.console.pojo.RavietyExcludePeer.getMapBuilder();
        com.youku.search.console.pojo.TempPlayVersionPeer.getMapBuilder();
        com.youku.search.console.pojo.EpisodeVideoUpdateInfoPeer.getMapBuilder();
        com.youku.search.console.pojo.SearchNumPeer.getMapBuilder();
        com.youku.search.console.pojo.SearchLogPeer.getMapBuilder();
    }
}
