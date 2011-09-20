package com.youku.search.spider.baidu;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Writer {

    public static void write(List<String> list, String file) throws Exception {

        Set<String> set = new HashSet<String>();
        set.addAll(list);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf8"));
            for (String s : set) {
                writer.write(s);
                writer.newLine();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<String>();
        list.add("大学");
        list.add("美女");
        list.add("大学");

        write(list, "/home/jiabaozhen/xxxxxxxxxxxxxxxxxxxx.txt");
    }
}
