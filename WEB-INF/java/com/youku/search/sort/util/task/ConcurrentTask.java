package com.youku.search.sort.util.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrentTask {

    public static class Failed {
    }

    public static class FailedWithException extends Failed {
        public Throwable failed;

        public FailedWithException(Throwable failed) {
            this.failed = failed;
        }
    }

    public static class FailedWithTimeout extends Failed {
    }

    ExecutorService executor = Executors.newCachedThreadPool();

    public Object submit(Callable task, long timeout) {

        Object result = null;

        try {
            Future future = executor.submit(task);
            result = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable t) {
            result = new FailedWithException(t);
        }

        return result;
    }

    public List submit(List<Callable> tasks, long timeout) {

        final long deadline = System.currentTimeMillis() + timeout;

        List<Future> list = new ArrayList<Future>(tasks.size());
        for (Callable task : tasks) {
            list.add(executor.submit(task));
        }

        while (true) {
            if (System.currentTimeMillis() >= deadline || isDone(list)) {
                break;
            }

            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List result = new ArrayList(tasks.size());
        for (Future future : list) {
            if (future.isDone()) {
                try {
                    result.add(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                result.add(new FailedWithTimeout());
                if (!future.isCancelled()) {
                    future.cancel(true);
                }
            }
        }

        return result;
    }

    private boolean isDone(List<Future> list) {

        for (Future future : list) {
            if (!future.isDone() && !future.isCancelled()) {
                return false;
            }
        }
        
        return true;
    }
}
