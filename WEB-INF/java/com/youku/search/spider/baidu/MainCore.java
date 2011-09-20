package com.youku.search.spider.baidu;

import java.util.List;

import com.youku.search.spider.util.Reader;

public class MainCore {

    public static List<String> parse(String url, boolean print)
            throws Exception {

        String s = Reader.read(url, "gbk");
        List<String> list = Parser.parse(s);

        if (print) {
            for (int i = 0; i < list.size(); i++) {
                System.out.printf("%d, %s\n", i, list.get(i));
            }
        }

        return list;
    }
}
