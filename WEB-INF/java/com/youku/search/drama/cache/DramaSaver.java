package com.youku.search.drama.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.drama.Drama;
import com.youku.search.drama.Episode;
import com.youku.search.drama.Version;
import com.youku.search.drama.Drama.Type;
import com.youku.search.drama.util.DramaTypeUtil;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.MemCache.StoreResult;

public class DramaSaver {

    static Log logger = LogFactory.getLog(DramaSaver.class);

    /**
     * 存入Drama和版本的映射
     * 
     * @param list
     */
    public static void saveDramaAndVersionMap(List<Drama> list) {

        Map<StoreResult, Integer> stat = new HashMap<StoreResult, Integer>();

        if (list == null || list.isEmpty()) {
            logMemcacheStat(stat);
            return;
        }

        final int seconds = CacheConstant.CACHE_SECONDS;

        for (Drama drama : list) {

            final Drama.Type type = drama.getType();

            String name = drama.getName();
            List<String> alias = drama.getAlias();

            boolean isNameOK = isNameOK(name);
            if (!isNameOK || drama.getVersions() == null
                    || drama.getVersions().isEmpty()) {
                continue;
            }

            final String dramaCacheKey = nameCacheSet(type, name, drama,
                    seconds, stat);

            for (int i = 0; alias != null && i < alias.size(); i++) {
                String a = alias.get(i);
                if (isNameOK(a)) {
                    nameCacheSet(type, a, dramaCacheKey, seconds, stat);
                }
            }

            // 加载剧集
            List<String> d_names = new ArrayList<String>();
            d_names.add(name);// 把主名字添加到第一个的位置
            if (alias != null && !alias.isEmpty()) {
                d_names.addAll(alias);
            }

            for (Version version : drama.getVersions()) {
                // 构建cache对象
                CacheAssemble c = new CacheAssemble();
                c.drama_cache_key = dramaCacheKey;
                c.version_id = version.getId();

                if (drama.getVersionCount() > 1) {
                    // 按version名称构造key
                    List<String> v_names = buildVersionNames(version);
                    DramaNames dn = new DramaNames(d_names, v_names);

                    List<String> keys = dn.getCacheKey();
                    // 为每个key做映射
                    for (String key : keys) {
                        if (isNameOK(key)) {
                            nameCacheSet(type, key, c, seconds, stat);
                        }
                    }
                }
            }
        }

        logMemcacheStat(stat);
    }

    /**
     * 存入电视剧和剧集映射
     */
	public static void saveDramaAndEpisodeMap(List<Drama> list,
            boolean saveEpisodeVideo) {
        DramaCache.init(list, saveEpisodeVideo);
    }

    // 返回实际存储的key
    private static String nameCacheSet(Drama.Type type, String key, Object o,
            int cacheTimeOut, Map<StoreResult, Integer> stat) {

        final String newKey = CacheConstant.CACHE_KEY(type)
                + DramaNames.formatName(key);

        StoreResult result = MemCache.cacheSet(newKey, o, cacheTimeOut);

        // 统计
        Integer count = stat.get(result);
        if (count == null) {
            stat.put(result, 1);
        } else {
            stat.put(result, count + 1);
        }

        // log
        if (result != StoreResult.success) {
            logger.warn("保存到MemCache失败, key: " + newKey + ", value: " + o
                    + ", 错误描述: " + result);
        }

        return newKey;
    }

    private static boolean isNameOK(String name) {
        if (name == null || name.trim().length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * 构造版本的名字
     */
    private static List<String> buildVersionNames(Version version) {
        Set<String> v_names = new LinkedHashSet<String>();

        // 公用名字
        v_names.add(version.getName());
        if (version.getAlias() != null) {
            String alias = version.getAlias().trim();
            if (!alias.equals("")) {
                v_names.add(alias);
            }
        }

        // 分情况
        final Type type = DramaTypeUtil.get(version);
        switch (type) {
        case DRAMA:
            break;

        case ZONGYI:
            for (Episode episode : version.getEpisodes()) {
                String order = "" + episode.getOrder();
                if (order.length() > 6) {
                    v_names.add(order.substring(0, 6));
                }
                v_names.add(order);
            }
            break;

        default:
            DramaTypeUtil.unknownType(type);
            break;
        }

        List<String> version_names = new LinkedList<String>();
        version_names.addAll(v_names);
        return version_names;
    }

    private static void logMemcacheStat(Map<StoreResult, Integer> stat) {
        if (logger.isInfoEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("保存到Memcache的统计情况: ");

            for (StoreResult result : StoreResult.values()) {
                Integer count = stat.get(result);
                if (count == null) {
                    count = 0;
                }
                builder.append(result + ": " + count + "; ");
            }

            logger.info(builder.toString());
        }
    }
}
