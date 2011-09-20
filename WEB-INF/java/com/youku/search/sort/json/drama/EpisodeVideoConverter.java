package com.youku.search.sort.json.drama;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.drama.EpisodeVideo;
import com.youku.search.util.StringUtil;

public class EpisodeVideoConverter {

    static Log logger = LogFactory.getLog(EpisodeVideoConverter.class);

    public static JSONObject convert(EpisodeVideo e) {

        if (e == null) {
            return null;
        }

        try {
            JSONObject o = new JSONObject();

            o.put("vid", e.getVideo_id());
            o.put("vidEncoded", StringUtil.filterNull(e.getVidEncoded()));
            o.put("sourceName", StringUtil.filterNull(e.getSourceName()));
            o.put("logo", StringUtil.filterNull(e.getLogo()));
            o.put("seconds", e.getSeconds());

            return o;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }
}
