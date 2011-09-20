package com.youku.soku.library.load.map;

import org.apache.torque.TorqueException;

/**
 * This is a Torque Generated class that is used to load all database map 
 * information at once.  This is useful because Torque's default behaviour
 * is to do a "lazy" load of mapping information, e.g. loading it only
 * when it is needed.<p>
 *
 * @see org.apache.torque.map.DatabaseMap#initialize() DatabaseMap.initialize() 
 */
public class SokuLibraryMapInit
{
    public static final void init()
        throws TorqueException
    {
        com.youku.soku.library.load.SeriesPeer.getMapBuilder();
        com.youku.soku.library.load.SeriesSubjectPeer.getMapBuilder();
        com.youku.soku.library.load.ProgrammePeer.getMapBuilder();
        com.youku.soku.library.load.ProgrammeSitePeer.getMapBuilder();
        com.youku.soku.library.load.ProgrammeEpisodePeer.getMapBuilder();
        com.youku.soku.library.load.BlacklistPeer.getMapBuilder();
        com.youku.soku.library.load.MusicPeer.getMapBuilder();
        com.youku.soku.library.load.MusicSitePeer.getMapBuilder();
        com.youku.soku.library.load.FolderPeer.getMapBuilder();
        com.youku.soku.library.load.MusicFolderPeer.getMapBuilder();
        com.youku.soku.library.load.EpisodeLogPeer.getMapBuilder();
        com.youku.soku.library.load.SearchNumsPeer.getMapBuilder();
        com.youku.soku.library.load.UserOperationLogPeer.getMapBuilder();
        com.youku.soku.library.load.UrlCheckPeer.getMapBuilder();
        com.youku.soku.library.load.ProgrammeDoubanPeer.getMapBuilder();
        com.youku.soku.library.load.ProgrammeDoubanMorePeer.getMapBuilder();
        com.youku.soku.library.load.KnowledgeColumnPeer.getMapBuilder();
        com.youku.soku.library.load.ProgrammeSearchNumberPeer.getMapBuilder();
        com.youku.soku.library.load.ProgrammeForwardWordPeer.getMapBuilder();
    }
}
