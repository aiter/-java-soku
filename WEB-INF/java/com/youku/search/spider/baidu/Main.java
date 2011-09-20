package com.youku.search.spider.baidu;

import java.util.LinkedList;
import java.util.List;

public class Main {

    static class Info {
        public String name;
        public int toal;
        public String baseurl;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("name: " + name + ", ");
            builder.append("toal: " + toal + ", ");
            builder.append("baseurl: " + baseurl);
            return builder.toString();
        }
    }

    static String mainUrl = "http://tieba.baidu.com/f?ct=536870912&rn=200&pn=0&cm=1101&tn=simpleCategory&sn=%D3%B0%CA%D3";

    static String[] baseInfo = {
            "内地电视剧",
            "http://tieba.baidu.com/f?ct=536870912&sn=%D3%B0%CA%D3&bs=%C4%DA%B5%D8%B5%E7%CA%D3%BE%E7&rn=200&cm=1101&tn=simpleCategory&pn=",

            "港台电视剧",
            "http://tieba.baidu.com/f?ct=536870912&sn=%D3%B0%CA%D3&bs=%B8%DB%CC%A8%B5%E7%CA%D3%BE%E7&rn=200&cm=1101&tn=simpleCategory&pn=",

            "美剧",
            "http://tieba.baidu.com/f?ct=536870912&sn=%D3%B0%CA%D3&bs=%C3%C0%BE%E7&rn=200&pn=0&cm=1101&tn=simpleCategory&pn=",

            "韩剧",
            "http://tieba.baidu.com/f?ct=536870912&sn=%D3%B0%CA%D3&bs=%BA%AB%BE%E7&rn=200&cm=1101&tn=simpleCategory&pn=",

            "日剧",
            "http://tieba.baidu.com/f?ct=536870912&sn=%D3%B0%CA%D3&bs=%C8%D5%BE%E7&rn=200&cm=1101&tn=simpleCategory&pn=",

            "怀旧电视剧",
            "http://tieba.baidu.com/f?ct=536870912&sn=%D3%B0%CA%D3&bs=%BB%B3%BE%C9%B5%E7%CA%D3%BE%E7&rn=200&pn=0&cm=1101&tn=simpleCategory&pn=",

    };

    static List<Info> infoList = new LinkedList<Info>();

    static {
        for (int i = 0; i < baseInfo.length; i += 2) {
            Info info = new Info();
            info.name = baseInfo[i];
            info.baseurl = baseInfo[i + 1];

            infoList.add(info);
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> result = get();
        System.out.println(result);
    }

    public static List<String> get() throws Exception {
        getTotal();
        List<String> result = getData();
        return result;
    }

    private static void getTotal() throws Exception {
        TotalParser.parse(mainUrl, infoList);
    }

    private static List<String> getData() throws Exception {

        List<String> result = new LinkedList<String>();

        for (int i = 0; i < infoList.size(); i++) {

            final Info info = infoList.get(i);
            List<String> pageResult = new LinkedList<String>();

            for (int page = 1; page <= Integer.MAX_VALUE; page++) {
                final String url = info.baseurl + page;

                List<String> list = MainCore.parse(url, false);
                pageResult.addAll(list);

                System.out.printf("    期望总数：%d, 本次抓取：%d, 抓取类别：%s, URL：%s\n",
                        info.toal, list.size(), info.name, url);

                if (list.size() == 0 || pageResult.size() >= info.toal) {
                    break;
                }
            }

            result.addAll(pageResult);
            System.out.printf("期望总数：%d, 总共抓取：%d, 抓取类别：%s, baseURL：%s\n",
                    info.toal, pageResult.size(), info.name, info.baseurl);
        }

        return result;
    }
}
