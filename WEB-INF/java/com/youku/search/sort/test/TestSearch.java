package com.youku.search.sort.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.youku.search.spider.util.Reader;

public class TestSearch {

    static class Param {
        public File file;
        public int count;
        public String server;

        static Param parse(String[] args) {
            Param param = new Param();
            param.file = new File(args[0]);
            param.count = Integer.parseInt(args[1]);
            param.server = args[2];
            return param;
        }

        private static void printUsage() {
            System.out.println("usage: inputfile count server");
        }
    }

    static class Worker implements Runnable {

        public void run() {

            System.out.println(Thread.currentThread().getName() + " running");

            while (true) {
                try {
                    String url = queue.poll(1, TimeUnit.SECONDS);
                    if (url != null) {
                        read(url);
                        completeRequest.incrementAndGet();

                    } else if (readComplete) {
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }

            System.out.println(Thread.currentThread().getName() + " run over");
        }

        private void read(String url) {
            try {
                Reader.read(param.server + url, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class StatusReporter extends TimerTask {
        @Override
        public void run() {
            StringBuilder builder = new StringBuilder();
            builder.append("thread: " + param.count + "\t");
            builder.append("readLines: " + readLines.get() + "\t");
            builder.append("completeRequest: " + completeRequest.get() + "\t");

            System.out.println(builder.toString());
        }
    }

    static Param param;

    static final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(
            100 * 10000);

    static AtomicLong readLines = new AtomicLong();
    static boolean readComplete = false;
    static AtomicLong completeRequest = new AtomicLong();

    public static void main(String[] args) throws Exception {

        Param.printUsage();

        param = Param.parse(args);

        for (int i = 0; i < param.count; i++) {
            new Thread(new Worker()).start();
        }

        //
        new Timer().schedule(new StatusReporter(), 1000, 1000);

        // 
        BufferedReader reader = null;
        while (true) {
            try {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(param.file), "utf-8"));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    readLines.incrementAndGet();
                    queue.put(line);
                }

            } catch (Throwable e) {
                e.printStackTrace(System.out);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }
}
