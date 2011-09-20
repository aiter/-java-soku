package com.youku.search.drama.cache;

import java.io.Serializable;

import org.json.JSONObject;

import com.youku.search.util.StringUtil;

/**
 * Drama 和 Version 的 id pair
 */
public class EpisodeVideoInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public String drama_id;
    public String version_id;
    public int episode_id;
    public int episode_order;
    public int islock;

    public EpisodeVideoInfo(String drama_id, String version_id, int episode_id,
            int episode_order, int islock) {
        this.drama_id = drama_id;
        this.version_id = version_id;
        this.episode_id = episode_id;
        this.episode_order = episode_order;
        this.islock = islock;
    }

    public JSONObject toJsonObject() {
        try {
            JSONObject object = new JSONObject();
            object.put("drama_id", StringUtil.filterNull(drama_id));
            object.put("version_id", StringUtil.filterNull(version_id));
            object.put("episode_id", episode_id);
            object.put("episode_order", episode_order);
            object.put("islock", islock);
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }
}
