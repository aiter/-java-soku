package com.youku.search.pool.memcache;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedInit {

    private static MemcachedInit self = null;

    private MemcachedInit() {
    }

    public synchronized static MemcachedInit getInstance() {
        if (self == null) {
            self = new MemcachedInit();
        }
        return self;
    }

    public void init(String file) {

        try {
            Configuration p = new PropertiesConfiguration(new File(file));

            // server=127.0.0.1:11211
            // initconn=5
            // minconn=3
            // maxconn=50
            // maxidle=21600000
            // maintsleep=30
            // nagle=0
            // socketreadtimeout=2
            // socketconntimeout=0
            // compress=0

            String[] servers = p.getStringArray("server");
            int initConn = p.getInt("initconn");
            int minconn = p.getInt("minconn");
            int maxconn = p.getInt("maxconn");
            int maxidle = p.getInt("maxidle");
            int maintsleep = p.getInt("maintsleep");
            boolean nagle = p.getBoolean("nagle");

            int socketreadtimeout = p.getInt("socketreadtimeout");
            int socketconntimeout = p.getInt("socketconntimeout");
            boolean compress = p.getBoolean("compress");

            SockIOPool pool = SockIOPool.getInstance();
            pool.setServers(servers);
            pool.setInitConn(initConn);
            pool.setMinConn(minconn);
            pool.setMaxConn(maxconn);
            pool.setMaxIdle(maxidle);
            pool.setMaintSleep(maintsleep);
            pool.setNagle(nagle);
            pool.setSocketTO(socketreadtimeout);
            pool.setSocketConnectTO(socketconntimeout);
            pool.initialize();

            MemCachedClient mcc = new MemCachedClient();
            mcc.setCompressEnable(compress);

        } catch (Exception e) {
            System.out.println(e + "file " + file + " not found");
            e.printStackTrace();
        } finally {

        }

    }

    public static void main(String[] args) throws Exception {
        String file = "D:\\work\\svn\\youku.search\\src\\WEB-INF\\conf\\memcached.properties";

        Configuration p = new PropertiesConfiguration(new File(file));
        
        String[] server = p.getStringArray("server");
        System.out.println(server.length);
        System.out.println(Arrays.asList(server));
    }
}
