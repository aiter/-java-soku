package com.youku.top.paihangbang;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.youku.top.paihangbang.entity.TypeWord;

public final class TypeWordVOMapper implements ParameterizedRowMapper<TypeWord> {

	public static final TypeWordVOMapper twvomapper = new TypeWordVOMapper();
	
    public TypeWord mapRow(ResultSet rs, int rowNum) throws SQLException {
    	TypeWord tw = new TypeWord();
    	tw.setId(rs.getInt("id"));
    	tw.setKeyword(rs.getString("keyword"));
    	tw.setCate(rs.getInt("cate"));
    	tw.setProgramme_id(rs.getInt("programme_id"));
    	tw.setState(rs.getString("state"));
    	tw.setPic(rs.getString("pic"));
    	tw.setChecker(rs.getString("checker"));
    	tw.setCreate_date(rs.getTimestamp("create_date"));
    	tw.setUpdate_date(rs.getTimestamp("update_date"));
    	return tw;
    }
}
