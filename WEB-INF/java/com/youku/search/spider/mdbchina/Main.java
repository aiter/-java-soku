package com.youku.search.spider.mdbchina;

import java.util.List;

import com.youku.search.spider.mdbchina.entity.SimpleTVDrama;
import com.youku.search.spider.mdbchina.entity.TVDrama;
import com.youku.search.spider.util.Reader;

public class Main {

    static String baseURL = "http://www.mdbchina.cn/sections/genres/tv/default2.html?page=2&prefix=default&pc=5403&ps=90";

    public static void main(String[] args) throws Exception {

        String url = baseURL;

        String response = Reader.read(url, "gb2312");
        TVDrama drama = Parser.parseTVDrama(response);
        List<SimpleTVDrama> list = Parser.parseTVId(response);

        System.out.println(response);
        System.out.println(drama);
        System.out.println(list);
    }

}
