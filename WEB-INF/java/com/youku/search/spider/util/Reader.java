package com.youku.search.spider.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.json.util.JSONUtil.ParseResult;

public class Reader {

    public static String read(String url, String encoding) throws Exception {
        return read(url, encoding, null);
    }

    public static String read(String url, String encoding,
            Map<String, String> headers) throws Exception {

        StringWriter writer = new StringWriter();
        read(url, encoding, headers, writer);

        return writer.toString();
    }

    private static void read(String url, String encoding,
            Map<String, String> headers, StringWriter writer) throws Exception {

        BufferedReader reader = null;

        try {
            URL theUrl = new URL(url);
            URLConnection connection = theUrl.openConnection();
            if (headers != null) {
                for (Map.Entry<String, String> i : headers.entrySet()) {
                    connection.setRequestProperty(i.getKey(), i.getValue());
                }
            }

            reader = new BufferedReader(new InputStreamReader(connection
                    .getInputStream(), encoding));

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                writer.write(line);
                writer.write("\n");
            }

        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        String response = read(
                "http://v.ku6.com/fetchVideo4Player/1/EdJihB7s8aJP6A0P.html",
                "UTF-8");

        System.out.println(response);
        System.out.println(response.length());

        ParseResult parseResult = JSONUtil.tryParse(response);
        System.out.println(parseResult.object.toString(4));
    }
}
