package com.youku.search.sort.json;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.index.entity.Pk;
import com.youku.search.util.StringUtil;

public class PKConverter extends AbstractConverter {

    static Log logger = LogFactory.getLog(PKConverter.class);

    public static JSONObject convert(Pk pk) {

        if (pk == null) {
            return null;
        }

        try {
            JSONObject object = new JSONObject();

            // object
            object.put("pk_pk", pk.pk_pk);
            object.put("pk_name", StringUtil.filterNull(pk.pk_name));
            object.put("logo", StringUtil.filterNull(pk.logo));
            object.put("status", StringUtil.filterNull(pk.status));
            object.put("description", StringUtil.filterNull(pk.description));
            object.put("begintime", formatDate(pk.begintime));
            object.put("endtime", formatDate(pk.endtime));
            object.put("owner", pk.owner);
            object.put("user_name", StringUtil.filterNull(pk.user_name));
            object.put("video_count", pk.video_count);
            object.put("vote_count", pk.vote_count);
            object.put("actor_count", pk.actor_count);
            object.put("total_pv", pk.total_pv);
            object.put("tags", StringUtil.filterNull(pk.tags));

            // videosObject
            JSONObject videosObject = new JSONObject();
            object.put("videos", videosObject);

            int subVideoCount = 0;
            if (pk.video_id != null) {
                subVideoCount = pk.video_id.length;
            }

            if (pk.video_count > 0 && subVideoCount < 1) {
                if (logger.isDebugEnabled()) {
                    logger.debug("*WARN* pk_pk: " + pk.pk_pk
                            + ", video_count: " + pk.video_count
                            + ", video_id.length: " + subVideoCount);
                }
            }

            for (int i = 0; i < subVideoCount; i++) {
                // videoObject
                JSONObject videoObject = new JSONObject();
                videosObject.put(i + "", videoObject);

                String video_id = StringUtil.filterNull(pk.video_id[i]);

                String video_title = "";
                if (pk.video_title != null && i < pk.video_title.length) {
                    video_title = StringUtil.filterNull(pk.video_title[i]);
                }

                String video_seconds = "";
                if (pk.video_seconds != null && i < pk.video_seconds.length) {
                    video_seconds = StringUtil.filterNull(pk.video_seconds[i]);
                }

                videoObject.put("id", video_id);
                videoObject.put("title", video_title);
                videoObject.put("seconds", video_seconds);
            }

            return object;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject convert(List<Pk> list, int total) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject itemsObject = new JSONObject();

            jsonObject.put("total", total);
            jsonObject.put("items", itemsObject);

            if (list == null) {
                return jsonObject;
            }

            int index = 0;
            for (int i = 0; i < list.size(); i++) {
                JSONObject object = convert(list.get(i));
                if (object != null) {
                    itemsObject.put(index + "", object);
                    index++;
                }
            }

            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
