package com.youku.top.topn;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.youku.top.topn.entity.KeywordVO;

public final class KeywordVOMapper implements ParameterizedRowMapper<KeywordVO> {

	public static final KeywordVOMapper kwvomapper = new KeywordVOMapper();
	
    public KeywordVO mapRow(ResultSet rs, int rowNum) throws SQLException {
    	KeywordVO kqo = new KeywordVO();
    	String keyword = rs.getString("keyword");
		if(StringUtils.isBlank(keyword)) return null;
		kqo.setKeyword(keyword);
		kqo.setSearchs(rs.getInt("searchs"));
		kqo.setCate(rs.getInt("cate"));
		kqo.setWeb_searchs(rs.getInt("web_searchs"));
		kqo.setClicks(rs.getInt("clicks"));
        return kqo;
    }
}
