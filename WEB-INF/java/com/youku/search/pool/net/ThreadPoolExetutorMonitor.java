package com.youku.search.pool.net;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadPoolExetutorMonitor {

    static Log logger = LogFactory.getLog(ThreadPoolExetutorMonitor.class);

    public static synchronized void monitor(String name,
            ThreadPoolExecutor executor, long period) {

        Timer timer = new Timer(true);
        timer.schedule(new MonitorTask(name, executor), period, period);
    }

    private static class MonitorTask extends TimerTask {

        private String name;
        private ThreadPoolExecutor executor;

        private int maxActive = 0;

        public MonitorTask(String name, ThreadPoolExecutor executor) {
            this.name = name;
            this.executor = executor;

        }

        @Override
        public void run() {

            int active = executor.getActiveCount();
            maxActive = Math.max(active, maxActive);

            StringBuilder builder = new StringBuilder();
            builder.append(name + ": ");

            builder.append("active: " + active + "/" + maxActive + ", ");

            builder.append("size: " + executor.getPoolSize() + "/"
                    + executor.getLargestPoolSize());

            logger.info(builder.toString());

        }
    }
}
