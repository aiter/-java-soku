package com.youku.top.data_source;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class NewSokuLibraryDataSource extends BasicDataSource {

    public static final DataSource INSTANCE = new NewSokuLibraryDataSource();

    private NewSokuLibraryDataSource() {

        // driver
        setDriverClassName("com.mysql.jdbc.Driver");
        setUrl("jdbc:mysql://10.103.8.6:3306/soku_library_online?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
        setUsername("yoqoo");
        setPassword("yoqoo");

        // pool
        setInitialSize(2);
        setMaxActive(-1);// nolimit
        setMaxIdle(-1);// nolimit
        setMinIdle(2);
        setValidationQuery("select version();");
        setTimeBetweenEvictionRunsMillis(1000 * 60 );
        setMinEvictableIdleTimeMillis(1000 * 60);
    }
}
