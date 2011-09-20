package com.youku.search.drama;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.youku.search.drama.Drama.Type;
import com.youku.search.drama.db.DramaLoader;
import com.youku.search.sort.json.drama.DramaConverter;

public class DramaMock implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final AtomicInteger year = new AtomicInteger(2008);
    private static final AtomicInteger month = new AtomicInteger(1);
    private static final AtomicInteger seconds = new AtomicInteger(10 * 60);

    public static Drama mock() {
        return mock(Type.DRAMA);
    }

    public static Drama mock(Type type) {
        Drama drama = new Drama();

        drama.setType(type);

        drama.setId(99);
        drama.setName("越狱（这是一个测试数据）");
        drama.setAlias(mockAlias());

        List<Version> versions = mockVersios(drama);
        drama.setRealVersionCount(versions.size());

        return drama;
    }

    private static List<String> mockAlias() {
        List<String> list = new ArrayList<String>();
        list.add("粤语");
        list.add("prision break");
        return list;
    }

    private static List<Version> mockVersios(Drama drama) {
        drama.setVersions(new ArrayList<Version>());

        mockVersion(drama);
        mockVersion(drama);

        return drama.getVersions();
    }

    private static Version mockVersion(Drama drama) {
        Version version = new Version();

        version.setDrama(drama);
        drama.getVersions().add(version);

        version.setId(new Random().nextInt(10000) + "");

        switch (drama.getType()) {
        case DRAMA:
            version.setName("版本 " + version.getId());
            version.setAlias("别名 " + version.getId());
            break;

        case ZONGYI:
            version.setName(year.getAndIncrement() + "");
            version.setAlias("");
            break;

        default:
            throw new RuntimeException("未知类型");
        }

        version.setCate(1);
        version.setSubcate(1);
        version.setFixed(0);
        version.setOrder(0);

        mockEpisodes(version);

        return version;
    }

    private static List<Episode> mockEpisodes(Version version) {

        version.setEpisodes(new ArrayList<Episode>());

        mockEpisode(version);
        mockEpisode(version);
        mockEpisode(version);

        return version.getEpisodes();
    }

    private static Episode mockEpisode(Version version) {
        Episode episode = new Episode();

        episode.setVersion(version);
        version.getEpisodes().add(episode);

        episode.setId(new Random().nextInt(10000));

        switch (version.getDrama().getType()) {
        case DRAMA:
            episode.setName("测试节目 － " + episode.getId());
            break;

        case ZONGYI:
            String name = "0" + month.getAndIncrement();
            episode.setName(name.substring(name.length() - 2));
            break;

        default:
            throw new RuntimeException("未知类型");
        }

        episode.setOrder(1);
        episode.setSourceName("具体不清楚阿");
        episode.setVid(episode.getId() * 10);
        episode.setVidEncoded("encoded 1234 － " + episode.getId());

        if (version.getEpisodes().size() == 1) {
            episode.setSeconds(100);
        } else if (version.getEpisodes().size() == 2) {
            episode.setSeconds(80);
        }

        return episode;
    }

    public static void main(String[] args) throws Exception {
        System.out
                .println(DramaConverter.convert(mock(Type.DRAMA)).toString(4));

        System.out.println("-----------------------------");

        System.out.println(DramaConverter.convert(mock(Type.ZONGYI))
                .toString(4));

        System.out.println("-----------------------------");

        System.out.println(DramaConverter
                .convert(mock(Type.ZONGYI), Type.DRAMA).toString(4));

        System.out.println("-----------------------------");
        List<Drama> list = new LinkedList<Drama>();
        list.add(mock());
        list.add(mock());
        DramaLoader.adjustProperties(list);
        System.out.println(DramaConverter.convert(list.get(0)).toString(4));
        System.out.println(DramaConverter.convert(list.get(1)).toString(4));
    }
}
