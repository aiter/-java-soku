package com.youku.search.sort.util.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Test {
    static class MyTask implements Callable {
        long sleep;

        public MyTask(long sleep) {
            this.sleep = sleep;
        }

        public Object call() throws Exception {
            Thread.sleep(sleep);
            return Thread.currentThread().getName();
        }
    }

    public static void main(String[] args) {
        List<Callable> list = new ArrayList<Callable>();
        for (int i = 0; i < 100; i++) {
            list.add(new MyTask(500));
        }

        ConcurrentTask task = new ConcurrentTask();

        while (true) {
            long start = System.currentTimeMillis();
            List result = task.submit(list, 1000);
            long cost = System.currentTimeMillis() - start;

            System.out.println("cost: " + cost);

            if (false) {
                break;
            }
        }

        task.executor.shutdown();

        // for (Object object : result) {
        // System.out.println(object);
        // }

    }

}
