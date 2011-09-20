package com.youku.search.sort.json.drama;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.drama.Drama;
import com.youku.search.drama.Version;
import com.youku.search.drama.Drama.Type;
import com.youku.search.drama.util.DramaTypeUtil;
import com.youku.search.util.StringUtil;

public class DramaConverter {

    static Log logger = LogFactory.getLog(DramaConverter.class);

    public static JSONObject convert(Drama d) {
        return convert(d, DramaTypeUtil.get(d));
    }

    public static JSONObject convert(Drama d, Type asType) {

        if (d == null) {
            return null;
        }

        try {
            JSONObject o = new JSONObject();

            o.put("type", d.getType());
            o.put("name", StringUtil.filterNull(d.getName()));
            o.put("alias", convertAlias(d.getAlias()));
            o.put("versionCount", d.getVersionCount());
            o.put("realVersionCount", d.getRealVersionCount());
            o.put("versions", convertVersions(d, asType));

            return o;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private static Object convertAlias(List<String> alias) {
        if (alias == null || alias.isEmpty()) {
            return "";
        }

        JSONArray array = new JSONArray();
        for (String name : alias) {
            array.put(StringUtil.filterNull(name));
        }
        return array;
    }

    private static Object convertVersions(Drama d, Type asType) {

        List<Version> versions = d.getVersions();
        String version_key_show = d.getShowVersion();

        if (versions == null || versions.isEmpty()) {
            return "";
        }

        final int versionCount = versions.size();
        JSONArray array = new JSONArray();
        for (int i = 0; i < versionCount; i++) {

            Version v = versions.get(i);

            if (isShowThisVersion(asType, v, i, versionCount, version_key_show)) {
                v.setIs_show(1);
            } else {
                v.setIs_show(0);
            }

            JSONObject version = VersionConverter.convert(v, asType);
            if (version != null) {
                array.put(version);
            }
        }
        return array;
    }

    private static boolean isShowThisVersion(Type asType, Version v, int index,
            int versionCount, String version_key_show) {

        // 有key
        if (version_key_show != null) {
            return version_key_show.equals(v.getId());
        }

        // 没有key
        switch (asType) {
        case DRAMA:
            return index == 0;

        case ZONGYI:
            return index == versionCount - 1;

        default:
            DramaTypeUtil.unknownType(asType);
            return false;// 上面的语句会抛出异常
        }
    }

}
