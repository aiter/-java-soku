package com.youku.search.pool.api;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.youku.search.pool.net.Pool;

public class PoolSatusUtil {

    public static String buildPoolStatusJsonString() {

        // build total
        TotalStatus total = new TotalStatus();
        total.setMax_total(Pool.getMaxTotal());
        total.setActive(Pool.getNumActive());
        total.setIdle(Pool.getNumIdle());

        // build per remote-address
        Map<InetSocketAddress, SessionStatus> detail = new HashMap<InetSocketAddress, SessionStatus>();
        Set<InetSocketAddress> remoteSocketSet = Pool.getRemoteSocketSet();
        for (InetSocketAddress remote : remoteSocketSet) {
            SessionStatus status = new SessionStatus();
            status.setMax_active(Pool.getMaxActive());
            status.setActive(Pool.getNumActive(remote));
            status.setIdle(Pool.getNumIdle(remote));

            detail.put(remote, status);
        }

        // OK!
        PoolStatus poolStatus = new PoolStatus(total, detail);
        return poolStatus.toJsonString();
    }

    public static void main(String[] args) {
        System.out.println("start...");

        System.out.println(buildPoolStatusJsonString());
    }
}
