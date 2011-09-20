package com.youku.top.paihangbang;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.youku.soku.top.mapping.TopWords;
import com.youku.top.paihangbang.entity.TopWordsEntity;

public class TopWordsVOMapper implements ParameterizedRowMapper<TopWordsEntity> {

	public static final TopWordsVOMapper twmapper = new TopWordsVOMapper();
	
	@Override
	public TopWordsEntity mapRow(ResultSet rs, int rowNo) throws SQLException {
		TopWordsEntity tw = new TopWordsEntity();
		tw.setTopwords(new TopWords());
		tw.getTopwords().setId(rs.getInt("id"));
		tw.getTopwords().setKeyword(rs.getString("keyword"));
		tw.getTopwords().setUrl(rs.getString("url"));
		tw.getTopwords().setVisible(rs.getInt("visible"));
		tw.getTopwords().setProgrammeId(rs.getInt("programme_id"));
		tw.getTopwords().setPic(rs.getString("pic"));
		tw.getTopwords().setCate(rs.getInt("cate"));
		tw.getTopwords().setQueryCount(rs.getInt("query_count"));
		tw.getTopwords().setTopDate(rs.getDate("top_date"));
		tw.getTopwords().setCreateDate(rs.getTimestamp("create_date"));
		return tw;
	}

}
