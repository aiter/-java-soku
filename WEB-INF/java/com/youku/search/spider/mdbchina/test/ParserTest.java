package com.youku.search.spider.mdbchina.test;

import java.util.List;

import com.youku.search.spider.mdbchina.Parser;
import com.youku.search.spider.mdbchina.entity.SimpleTVDrama;
import com.youku.search.spider.util.Reader;

public class ParserTest {

    static String url = "http://www.mdbchina.cn/sections/genres/tv/default2.html?page=2&prefix=default&pc=5403&ps=90";

    public void testParseTVId() throws Exception {
        String s = Reader.read(url, "gb2312");
        List<SimpleTVDrama> list = Parser.parseTVId(s);
        System.out.println(list);
    }
}
