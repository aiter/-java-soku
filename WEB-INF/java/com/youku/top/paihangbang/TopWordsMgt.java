package com.youku.top.paihangbang;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.util.DataFormat;
import com.youku.soku.top.mapping.TopWords;
import com.youku.top.paihangbang.entity.TopWordsEntity;

public class TopWordsMgt {
	
	static Logger logger = Logger.getLogger(TopWordsMgt.class);
	
	private static TopWordsMgt instance = null;
	
	private TopWordsMgt() {
		super();
	}

	public static synchronized TopWordsMgt getInstance() {
		if(null==instance)
			instance = new TopWordsMgt();
		return instance;
	}
	
	public List<TopWordsEntity> doGetTopWords(final Date top_date,final int cate,final int limit){
		String sql="select * from top_words where cate = ? and top_date = ? and visible =1 order by query_count desc limit ?";
		
		final java.sql.Date d = new java.sql.Date(top_date.getTime());
		
		return TopDateMgt.newSokuTopDataSource.query(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, cate);
				ps.setDate(2, d);
				ps.setInt(3,limit);
			}
		}, TopWordsVOMapper.twmapper);
	}
	
	public int topWordSave(final TopWords tw){
		try{
			final java.sql.Date d = new java.sql.Date(tw.getTopDate().getTime());
			String sql = "insert ignore into top_words (keyword,cate,query_count,programme_id,url,visible,pic,top_date,create_date,isTop) values (?,?,?,?,?,?,?,?,?,?)";
			return TopDateMgt.newSokuTopDataSource.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, tw.getKeyword());
					ps.setInt(2,tw.getCate());
					ps.setInt(3, tw.getQueryCount());
					ps.setInt(4, tw.getProgrammeId());
					ps.setString(5,tw.getUrl());
					ps.setInt(6, tw.getVisible());
					ps.setString(7, tw.getPic());
					ps.setDate(8, d);
					ps.setTimestamp(9, new Timestamp(new Date().getTime()));
					if(StringUtils.isBlank(tw.getUrl()))
						ps.setNull(5,Types.VARCHAR,"url");
					ps.setInt(10, tw.getIstop());
				}
			});
		}catch(Exception e){
			logger.error("TopWord:"+tw.toString(),e);
		}
		return 0;
	}
	
	public void topWordsDataDelete(){
		String date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -21), DataFormat.FMT_DATE_YYYYMMDD);
		String sql = "delete from top_words where top_date = '"+date+"'";
		try{
			TopDateMgt.newSokuTopDataSource.update(sql);
		}catch(Exception e){
			logger.error("sql:"+sql, e);
		}
	}
}
