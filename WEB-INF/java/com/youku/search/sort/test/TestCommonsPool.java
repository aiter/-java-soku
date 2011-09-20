package com.youku.search.sort.test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool.Config;

import com.youku.search.pool.net.util.Cost;

public class TestCommonsPool {

    static class Param {
        public int count;
        public int tests;

        static Param parse(String[] args) {
            Param param = new Param();
            param.count = Integer.parseInt(args[0]);
            param.tests = Integer.parseInt(args[1]);
            return param;
        }

        private static void printUsage() {
            System.out.println("usage: count");
        }
    }

    static class Worker implements Runnable {

        private String key;

        public Worker(String key) {
            this.key = key;
        }

        public void run() {

            System.out.println(Thread.currentThread().getName() + " with key <"
                    + key + "> running");

            while (true) {
                try {

                    Cost cost = new Cost();
                    Object object = pool.borrowObject(key);
                    cost.updateEnd();

                    if (cost.getCost() > 10) {
                        timeoutCount.incrementAndGet();
                        System.out.println("key: " + key + ": " + cost);
                    }

                    Thread.sleep(10);
                    pool.returnObject(key, object);

                    if (completeRequest.incrementAndGet() >= param.tests) {
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class StatusReporter extends TimerTask {
        @Override
        public void run() {
            StringBuilder builder = new StringBuilder();
            builder.append("thread: " + param.count + "\t");
            builder.append("completeRequest: " + completeRequest.get() + "\t");
            builder.append(pool.getNumActive() + "\t");
            builder.append(pool.getNumIdle() + "\t");
            builder.append(1.0 * timeoutCount.get() / completeRequest.get()
                    + "\t");

            System.out.println(builder.toString());
        }
    }

    static Param param;

    static AtomicLong completeRequest = new AtomicLong();
    static AtomicLong timeoutCount = new AtomicLong();

    static String[] keys = { "key-1", "key-2", "key-3", "key-4", "key-5",
            "key-6", "key-7", "key-8", "key-9" };

    static KeyedObjectPool pool = new GenericKeyedObjectPool(createFactory(),
            createConfig());

    // static KeyedObjectPool pool = new InnerKeyedObjectPool(createConfig(),
    // createFactory());

    private static KeyedPoolableObjectFactory createFactory() {
        return new KeyedPoolableObjectFactory() {
            public void activateObject(Object key, Object obj) throws Exception {
            }

            public void destroyObject(Object key, Object obj) throws Exception {
            }

            public Object makeObject(Object key) throws Exception {
                return new Object();
            }

            public void passivateObject(Object key, Object obj)
                    throws Exception {
            }

            public boolean validateObject(Object key, Object obj) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }

                return true;
            }
        };
    }

    private static Config createConfig() {
        Config config = new Config();

        config.maxTotal = -1;
        config.maxActive = -1;
        config.maxIdle = -1;

        config.testOnBorrow = true;
        config.testOnReturn = false;
        config.testWhileIdle = false;

        config.whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_GROW;

        config.timeBetweenEvictionRunsMillis = 10 * 1000;
        config.numTestsPerEvictionRun = 100;
        config.minIdle = 10;
        config.minEvictableIdleTimeMillis = 1;

        return config;
    }

    public static void main(String[] args) throws Exception {

        Param.printUsage();

        param = Param.parse(args);
        // param = Param.parse(new String[] { "1" });

        for (int i = 0; i < param.count; i++) {
            int index = i % keys.length;
            new Thread(new Worker(keys[index])).start();
        }

        //
        new Timer().schedule(new StatusReporter(), 1000, 1000);
    }
}
