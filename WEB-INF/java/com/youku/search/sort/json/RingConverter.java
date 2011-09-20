package com.youku.search.sort.json;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.index.entity.Ring;
import com.youku.search.util.StringUtil;

public class RingConverter extends AbstractConverter {

    static Log logger = LogFactory.getLog(RingConverter.class);

    public static JSONObject convert(Ring ring) {

        if (ring == null) {
            return null;
        }

        try {
            JSONObject object = new JSONObject();

            object.put("cid", StringUtil.filterNull(ring.cid));
            object.put("cname", StringUtil.filterNull(ring.cname));
            object.put("csinger", StringUtil.filterNull(ring.csinger));
            object.put("cprice", ring.cprice);
            object.put("cdate", ring.cdate);
            object.put("score", ring.score);

            return object;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static JSONObject convert(List<Ring> list, int total) {

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
