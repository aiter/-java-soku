package com.youku.search.sort.json.drama;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.drama.Episode;
import com.youku.search.util.StringUtil;

public class EpisodeConverter {

    static Log logger = LogFactory.getLog(EpisodeConverter.class);

    public static JSONObject convert(Episode e) {

        if (e == null) {
            return null;
        }

        try {
            JSONObject o = new JSONObject();

            // o.put("id", e.getId());
            // o.put("name", StringUtil.filterNull(e.getName()));
            o.put("order", getNumberString(e.getOrder()));
            // o.put("sourceName", StringUtil.filterNull(e.getSourceName()));
            // o.put("vid", e.getVid());
            o.put("vidEncoded", StringUtil.filterNull(e.getVidEncoded()));
            o.put("seconds", e.getSeconds());

            o.put("islock", e.getIslock());

            return o;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    private static String getNumberString(int number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }
}
