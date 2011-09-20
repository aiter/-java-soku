package com.youku.search.pool.net;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.apache.mina.common.IoSession;

import com.youku.search.index.entity.Result;
import com.youku.search.pool.net.util.Cost;

/**
 * ServerHandler 的处理逻辑
 * 
 */
public class ServerHandler_test extends ServerHandler_base {

    /**
     * 收到来自客户端的消息
     */
    public void messageReceived(IoSession session, Object message)
            throws Exception {

        QueryHolder queryHolder = (QueryHolder) message;

        ResultHolder resultHolder = new ResultHolder();
        resultHolder.c_start = queryHolder.c_start;
        resultHolder.s_received = System.currentTimeMillis();

        try {
            Random random = new Random();

            long start = System.currentTimeMillis();
            long life = 10 + random.nextInt(40);

            synchronized (random) {
                random.wait(life);
            }

            long cost = System.currentTimeMillis() - start;

            resultHolder.result = new FooResult();
            resultHolder.result.timecost = (int) cost;

        } catch (Exception e) {
            logger.error("'searchManager.processQuery(msg)' failed!", e);
        }

        resultHolder.s_send = System.currentTimeMillis();

        Cost cost = new Cost();
        session.write(resultHolder);
        cost.updateEnd();

        if (cost.getCost() > 200) {
            logger.info("write cost: " + cost);
        }
    }

    static class FooResult extends Result {
        private static final long serialVersionUID = 1L;
        public String mockString = mock();

        FooResult() {
            totalCount = 1;
            results = null;
            hasNext = false;
        }
    }

    private static String mock() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            builder.append("012345678");
        }
        return builder.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new FooResult().mockString.length());

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                "/home/jiabaozhen/xxxxx"));

        out.writeObject(new FooResult());
        out.close();
    }
}