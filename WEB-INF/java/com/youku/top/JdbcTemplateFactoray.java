package com.youku.top;

import org.springframework.jdbc.core.JdbcTemplate;

import com.youku.top.data_source.MergeLogYoukuDataSource;

public class JdbcTemplateFactoray {
	
	public static JdbcTemplate mergeLogYoukuDataSource = new JdbcTemplate(
			MergeLogYoukuDataSource.INSTANCE);
}
