package com.youku.top.data_source;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class SpiderDataSource extends BasicDataSource {

    public static final DataSource INSTANCE = new SpiderDataSource();

    private SpiderDataSource() {

        // driver
        setDriverClassName("com.mysql.jdbc.Driver");
        setUrl("jdbc:mysql://10.103.8.10:3306/spider?autoReconnect=true&useUnicode=true&characterEncoding=utf-8");
        setUsername("yoqoo");
        setPassword("yoqoo");

        // pool
        setInitialSize(3);
        setMaxActive(-1);// nolimit
        setMaxIdle(-1);// nolimit
        setMinIdle(3);
        setValidationQuery("select version();");
        setTimeBetweenEvictionRunsMillis(1000 * 60 * 2 );
        setMinEvictableIdleTimeMillis(1000 * 60);
    }
}
