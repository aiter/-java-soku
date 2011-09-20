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
public class SearchconsoleMapInit
{
	public static final void init()
		throws TorqueException
	{
        com.youku.search.console.pojo.UserPeer.getMapBuilder();
        com.youku.search.console.pojo.RolePeer.getMapBuilder();
        com.youku.search.console.pojo.ResourcePeer.getMapBuilder();
        com.youku.search.console.pojo.MenuPeer.getMapBuilder();
        com.youku.search.console.pojo.MenuResourcePeer.getMapBuilder();
        com.youku.search.console.pojo.UserRolePeer.getMapBuilder();
        com.youku.search.console.pojo.RoleMenuPeer.getMapBuilder();
        com.youku.search.console.pojo.ErrorinfoPeer.getMapBuilder();
        com.youku.search.console.pojo.FeedbackSearchPeer.getMapBuilder();
        com.youku.search.console.pojo.SynonymPeer.getMapBuilder();
    }
}
