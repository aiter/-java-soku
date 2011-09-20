package com.youku.top.quick;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class QuickVOMapper implements ParameterizedRowMapper<QuickVO>{

	public static final QuickVOMapper qvomapper = new QuickVOMapper();
	
	@Override
	public QuickVO mapRow(ResultSet rs, int arg1) throws SQLException {
		QuickVO kvo = new QuickVO();
    	String keyword = rs.getString("keyword");
		if(StringUtils.isBlank(keyword)) return kvo;
		kvo.setKeyword(keyword);
		kvo.setCate(rs.getInt("cate"));
		kvo.setQuery_count1(rs.getInt("query_count1"));
		kvo.setQuery_count2(rs.getInt("query_count2"));
		kvo.setQuery_count_sub(rs.getInt("query_count_sub"));
		kvo.setOrder_number(rs.getInt("order_number"));
		kvo.setVisible(rs.getInt("visible"));
		kvo.setUrl(rs.getString("url"));
		kvo.setHas_directory(rs.getInt("has_directory"));
        return kvo;
	}

}
