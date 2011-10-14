package com.youku.soku.manage.admin.copyright.load.map;

import org.apache.torque.TorqueException;

/**
 * This is a Torque Generated class that is used to load all database map 
 * information at once.  This is useful because Torque's default behaviour
 * is to do a "lazy" load of mapping information, e.g. loading it only
 * when it is needed.<p>
 *
 * @see org.apache.torque.map.DatabaseMap#initialize() DatabaseMap.initialize() 
 */
public class FinanceMapInit
{
    public static final void init()
        throws TorqueException
    {
        com.youku.soku.manage.admin.copyright.load.ActConfigPeer.getMapBuilder();
        com.youku.soku.manage.admin.copyright.load.ActSearchPeer.getMapBuilder();
    }
}
