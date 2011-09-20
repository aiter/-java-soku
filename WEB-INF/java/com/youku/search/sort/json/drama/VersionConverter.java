package com.youku.search.sort.json.drama;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.drama.Episode;
import com.youku.search.drama.Version;
import com.youku.search.drama.Drama.Type;
import com.youku.search.drama.util.DramaTypeUtil;
import com.youku.search.util.StringUtil;

public class VersionConverter {

    static class VersionInfo {
        public String logo;
        public String vidEncoded;
        public int episodeCount;
    }

    static Log logger = LogFactory.getLog(VersionConverter.class);

    public static JSONObject convert(Version v) {
        return convert(v, DramaTypeUtil.get(v));
    }

    public static JSONObject convert(Version v, Type asType) {

        if (v == null) {
            return null;
        }

        try {
            JSONObject o = new JSONObject();

            o.put("id", v.getId());
            o.put("name", StringUtil.filterNull(v.getName()));
            o.put("alias", StringUtil.filterNull(v.getAlias()));

            VersionInfo info = findVersionInfo(v, asType);
            o.put("logo", StringUtil.filterNull(info.logo));
            o.put("vidEncoded", StringUtil.filterNull(info.vidEncoded));
            o.put("episodeCount", info.episodeCount);

            o.put("is_show", v.getIs_show());
            o.put("episodes", convertEpisodes(v, asType));

            return o;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private static Object convertEpisodes(Version version, Type asType)
            throws Exception {

        switch (asType) {
        case DRAMA:
            return convertEpisodesWithDramaType(version.getEpisodes());

        case ZONGYI:
            return convertEpisodesWithZongyiType(version.getEpisodes());
        }

        return DramaTypeUtil.unknownType(asType);
    }

    private static Object convertEpisodesWithDramaType(List<Episode> episodes) {

        if (episodes == null || episodes.isEmpty()) {
            return "";
        }

        JSONArray array = new JSONArray();
        for (Episode e : episodes) {
            JSONObject episode = EpisodeConverter.convert(e);
            if (episode != null) {
                array.put(episode);
            }
        }
        return array;
    }

    private static Object convertEpisodesWithZongyiType(List<Episode> episodes)
            throws Exception {

        if (episodes == null || episodes.isEmpty()) {
            return "";
        }

        JSONObject jsonObject = new JSONObject();
        for (Episode episode : episodes) {
            if (!jsonObject.has(episode.getName())) {
                jsonObject.put(episode.getName(), new JSONArray());
            }

            JSONArray array = jsonObject.getJSONArray(episode.getName());
            JSONObject episodeObject = EpisodeConverter.convert(episode);
            if (episodeObject != null) {
                array.put(episodeObject);
            }
        }

        return jsonObject;
    }

    /**
     * version默认的显示logo是第一个视频的logo；
     * 
     * 如果当前version是综艺，那么就显示最后一个视频的logo。
     */
    private static VersionInfo findVersionInfo(Version v, Type asType) {
        VersionInfo info = new VersionInfo();

        List<Episode> episodes = v.getEpisodes();
        if (episodes == null || episodes.isEmpty()) {
            return info;
        }

        int index = 0;
        int episodeCount = 0;

        switch (asType) {
        case DRAMA:
            index = 0;
            episodeCount = episodes.size();
            break;

        case ZONGYI:
            index = episodes.size() - 1;

            Set<String> episodeNames = new HashSet<String>();
            for (Episode episode : episodes) {
                episodeNames.add(episode.getName());
            }
            episodeCount = episodeNames.size();
            break;

        default:
            DramaTypeUtil.unknownType(asType);
        }

        // ok
        info.logo = episodes.get(index).getLogo();
        info.vidEncoded = episodes.get(index).getVidEncoded();
        info.episodeCount = episodeCount;

        return info;
    }
}