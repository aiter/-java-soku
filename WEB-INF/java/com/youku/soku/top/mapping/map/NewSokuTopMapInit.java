package com.youku.soku.top.mapping.map;

import org.apache.torque.TorqueException;

/**
 * This is a Torque Generated class that is used to load all database map 
 * information at once.  This is useful because Torque's default behaviour
 * is to do a "lazy" load of mapping information, e.g. loading it only
 * when it is needed.<p>
 *
 * @see org.apache.torque.map.DatabaseMap#initialize() DatabaseMap.initialize() 
 */
public class NewSokuTopMapInit
{
	public static final void init()
		throws TorqueException
	{
        com.youku.soku.top.mapping.TopWordsPeer.getMapBuilder();
        com.youku.soku.top.mapping.TypeWordsPeer.getMapBuilder();
    }
}
