package com.youku.search.drama.util;

import com.youku.search.drama.Drama;
import com.youku.search.drama.Episode;
import com.youku.search.drama.Version;
import com.youku.search.drama.Drama.Type;

public class DramaTypeUtil {

    private static final Type DEFAULT_TYPE = Drama.DEFAULT_TYPE;

    public static Type get(Drama drama) {

        if (drama == null || drama.getType() == null) {
            return DEFAULT_TYPE;
        }

        return drama.getType();
    }

    public static Type get(Version version) {

        if (version == null) {
            return DEFAULT_TYPE;
        }

        return get(version.getDrama());
    }

    public static Type get(Episode episode) {

        if (episode == null) {
            return DEFAULT_TYPE;
        }

        return get(episode.getVersion());
    }

    public static Type unknownType(Type type) {
        throw new IllegalArgumentException("未知的类型：" + type.name());
    }

}
