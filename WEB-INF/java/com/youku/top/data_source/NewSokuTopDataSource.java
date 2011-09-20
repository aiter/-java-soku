package com.youku.top.data_source;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class NewSokuTopDataSource extends BasicDataSource {

    public static final DataSource INSTANCE = new NewSokuTopDataSource();

    private NewSokuTopDataSource() {

        // driver
        setDriverClassName("com.mysql.jdbc.Driver");
        setUrl("jdbc:mysql://10.103.8.6:3306/new_soku_top?autoReconnect=true&useUnicode=true&characterEncoding=utf-8");
        setUsername("yoqoo");
        setPassword("yoqoo");

        // pool
        setInitialSize(2);
        setMaxActive(-1);// nolimit
        setMaxIdle(-1);// nolimit
        setMinIdle(2);
        setValidationQuery("select version();");
        setTimeBetweenEvictionRunsMillis(1000 * 60 * 2 );
        setMinEvictableIdleTimeMillis(1000 * 60);
    }
}
