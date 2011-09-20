package com.youku.soku.manage.torque.map;

import org.apache.torque.TorqueException;

/**
 * This is a Torque Generated class that is used to load all database map
 * information at once. This is useful because Torque's default behaviour is to
 * do a "lazy" load of mapping information, e.g. loading it only when it is
 * needed.
 * <p>
 * 
 * @see org.apache.torque.map.DatabaseMap#initialize() DatabaseMap.initialize()
 */
public class SokuMapInit {
	public static final void init() throws TorqueException {
		com.youku.soku.manage.torque.UserPeer.getMapBuilder();
		com.youku.soku.manage.torque.AuthPermissionPeer.getMapBuilder();
		com.youku.soku.manage.torque.UserPermissionPeer.getMapBuilder();
		com.youku.soku.manage.torque.ItemPeer.getMapBuilder();
		com.youku.soku.manage.torque.VideoPeer.getMapBuilder();
		com.youku.soku.manage.torque.HotwordPeer.getMapBuilder();
		com.youku.soku.manage.torque.KeywordIntervenPeer.getMapBuilder();
		com.youku.soku.manage.torque.KeywordIntervenVideoPeer.getMapBuilder();
		com.youku.soku.manage.torque.CorrectionPeer.getMapBuilder();
		com.youku.soku.manage.torque.SokuFeedbackPeer.getMapBuilder();
		com.youku.soku.manage.torque.SokuFeedbackUnionPeer.getMapBuilder();
	}
}
