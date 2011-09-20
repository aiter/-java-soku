package com.youku.search.spider.baidu;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.youku.search.spider.baidu.Main.Info;
import com.youku.search.spider.util.Reader;
import com.youku.search.util.StringUtil;

public class TotalParser {

    // <span class="style5" style="margin-right:4px;">内地电视剧</span>(1597)

    // <a
    // href="/f?ct=536870912&sn=%D3%B0%CA%D3&bs=%B8%DB%CC%A8%B5%E7%CA%D3%BE%E7&rn=200&pn=0&cm=1101&tn=simpleCategory"
    // target="_self">港台电视剧</a>&nbsp;(1018)

    static String regexNolink_1 = "<span[^>]*>";
    static String regexNolink_2 = "</span>\\s*\\((\\d+)\\)";

    static String regexLink_1 = "<a href=\"/f\\?ct=536870912[^>]*>";
    static String regexLink_2 = "</a>[^(]*\\((\\d+)\\)";

    public static void parse(String url, List<Info> infoList) {

        String s;
        try {
            s = Reader.read(url, "gbk");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < infoList.size(); i++) {
            final Info info = infoList.get(i);

            {
                String regex = regexNolink_1 + info.name + regexNolink_2;
                Matcher matcher = Pattern.compile(regex).matcher(s);
                if (matcher.find()) {
                    info.toal = StringUtil.parseInt(matcher.group(1), 0);
                    continue;
                }
            }

            {
                String regex = regexLink_1 + info.name + regexLink_2;
                Matcher matcher = Pattern.compile(regex).matcher(s);
                if (matcher.find()) {
                    info.toal = StringUtil.parseInt(matcher.group(1), 0);
                    continue;
                }
            }
        }
    }

    public static void main(String[] args) {

        System.out.println(1);

        parse(Main.mainUrl, Main.infoList);
        for (int i = 0; i < Main.infoList.size(); i++) {
            System.out.println(Main.infoList.get(i));
        }
    }
}
