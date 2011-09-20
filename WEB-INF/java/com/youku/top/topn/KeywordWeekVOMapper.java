package com.youku.top.topn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.youku.soku.library.Utils;
import com.youku.top.topn.entity.KeywordWeekVO;
import com.youku.top.topn.util.KeywordUtil;

public final class KeywordWeekVOMapper implements RowMapper {

	public static final KeywordWeekVOMapper kwvomapper = new KeywordWeekVOMapper();
	
    public KeywordWeekVO mapRow(ResultSet rs, int rowNum) throws SQLException {
    	KeywordWeekVO kwvo = new KeywordWeekVO();
    	String keyword = rs.getString("keyword");
		if(StringUtils.isBlank(keyword)) return null;
		keyword = KeywordUtil.stopWordsFilter(keyword);
		if(StringUtils.isBlank(keyword)) return null;
		kwvo = new KeywordWeekVO();
		kwvo.setKeyword(keyword);
		if(ResultSetUtil.isNotNull(rs, "id"))
			kwvo.setId(rs.getInt("id"));
		if(ResultSetUtil.isNotNull(rs, "query_count"))
			kwvo.setQuery_count(rs.getInt("query_count"));
		if(ResultSetUtil.isNotNull(rs, "c"))
			kwvo.setQuery_count(rs.getInt("c"));
		if(ResultSetUtil.isNotNull(rs, "result"))
			kwvo.setResult(rs.getInt("result"));
		if(ResultSetUtil.isNotNull(rs, "query_type"))
			kwvo.setQuery_type(rs.getString("query_type"));
		if(ResultSetUtil.isNotNull(rs, "type"))
			kwvo.setQuery_type(rs.getString("type"));
		if(ResultSetUtil.isNotNull(rs, "quick_rate"))
			kwvo.setRate(rs.getFloat("quick_rate"));
		if(ResultSetUtil.isNotNull(rs, "types")){
			String t = rs.getString("types");
			if(!StringUtils.isBlank(t)){
				List<String> ts = Utils.parseStr2List(t, "\\|");
				if(null!=ts&&ts.size()>0)
					kwvo.getTypes().addAll(ts);
			}
		}
        return kwvo;
    }
}
