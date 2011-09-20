package com.youku.top.quick;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.youku.top.data_source.SokuTopDataSource;

public class QuickTopDataMgt {
	static Logger logger = Logger.getLogger(QuickTopDataMgt.class);
	public static JdbcTemplate sokuTopDataSource = new JdbcTemplate(
			SokuTopDataSource.INSTANCE);
	
	public static int getCounts(String sql) {
		try {
			return sokuTopDataSource.queryForInt(sql);
		} catch (Exception e) {
			logger.debug(sql,e);
		}
		return 0;
	}
	
	public static int execute(String sql) {
		try {
			sokuTopDataSource.execute(sql);
		} catch (Exception e) {
			logger.debug(sql,e);
		}
		return 0;
	}
	
	public static List<QuickVO> getQuickVOs(String sql) {
		try {
			return sokuTopDataSource.query(sql, QuickVOMapper.qvomapper);
		} catch (Exception e) {
			logger.debug(sql,e);
			e.printStackTrace();
		}
		return new ArrayList<QuickVO>();
	}
}
