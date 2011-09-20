package com.youku.search.spider.baidu;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    // <a href="/f?kw=%D0%C2%B0%E6%BA%EC%C2%A5%C3%CE" target=_blank>新版红楼梦</a>

    static String regex = "<a href=\"/f\\?kw=[^\"]+\"[^>]*>([^<]+)</a>";;

    public static List<String> parse(String s) {

        List<String> list = new ArrayList<String>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }

        return list;
    }

    public static void main(String[] args) {
        String s = "<a href=\"/f?kw=%D0%C2%B0%E6%BA%EC%C2%A5%C3%CE\" target=_blank>新版红楼梦</a>"
                + "<td width=\"20%\"><a href=\"/f?kw=%CC%EC%CF%C2%B5%DA%D2%BB\" target=_blank>天下第一</a></td>"
                + "<a href=\"dfgdfg\">22</a>"
                + "\n"
                + "\n"
                + "<a href=\"/f?kw=%D0%C2%B0%E6%BA%EC%C2%A5%C3%CE\"  target=_blank>新版红楼梦</a>"
                + "<td width=\"20%\"><a href=\"/f?kw=%CC%EC%CF%C2%B5%DA%D2%BB\" target=_blank>天下第一</a></td>";

        System.out.println(s);
        System.out.println("--------------------");
        System.out.println(parse(s));
    }
}
