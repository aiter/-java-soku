package com.youku.top.paihangbang;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.util.DataFormat;
import com.youku.top.paihangbang.entity.TopWordsEntity;

public class RankinfoMgt {
	static Logger logger = Logger.getLogger(RankinfoMgt.class);
	private static RankinfoMgt instance = null;
	
	private RankinfoMgt() {
		super();
	}

	public static synchronized RankinfoMgt getInstance() {
		if(null==instance)
			instance = new RankinfoMgt();
		return instance;
	}
	
	public int rankinfoSave(final TopWordsEntity tw,final int version_no){
		final String date = DataFormat.formatDate(tw.getTopwords().getTopDate(), DataFormat.FMT_DATE_YYYY_MM_DD);
		String sql="insert ignore into rankinfo_"+date+" (fk_cate_id,fk_programme_id,fk_types_id,fk_areas_id,keyword,query_count,version_no,year) values(?,?,?,?,?,?,?,?) ";
		int count = 0;
		for(Integer type:tw.getTypes()){
			for(Integer area:tw.getAreas()){
				count += rankinfoSave(sql,tw.getTopwords().getCate(),tw.getTopwords().getProgrammeId(),type,area,tw.getTopwords().getKeyword(),tw.getTopwords().getQueryCount(),version_no,tw.getYear());
			}
		}
		return count;
	}
	
	public int rankinfoSave(String sql,final int cate,final int id,final int type,final int area,final String keyword,final int query_count,final int version_no,final int year){
		try{
			return TopDateMgt.newSokuTopDataSource.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setInt(1, cate);
					ps.setInt(2, id);
					ps.setInt(3, type);
					ps.setInt(4, area);
					ps.setString(5, keyword);
					ps.setInt(6, query_count);
					ps.setInt(7, version_no);
					ps.setInt(8, year);
				}
			});
		}catch(Exception e){
			logger.error("sql:"+sql+",fk_cate_id:"+cate+",fk_programme_id:"+id+",fk_types_id:"+type+",fk_areas_id:"+area+",keyword:"+keyword+",query_count:"+query_count+",year:"+year+",version_no:"+version_no, e);
		}
		return 0;
	}
	
	public int rankinfoTableVersionGetter(String date) throws Exception{
		String sql = "select max(version_no) from  rankinfo_"+date;
		try{
			return TopDateMgt.newSokuTopDataSource.queryForInt(sql);
		}catch(Exception e){
			logger.error("sql:"+sql, e);
			throw new Exception("sql:"+sql, e);
		}
	}
	
	public int rankinfoTableCreate(String date) throws Exception{
		String sql = "create table if not exists rankinfo_"+date+" like rankinfo";
		try{
			return TopDateMgt.newSokuTopDataSource.update(sql);
		}catch(Exception e){
			logger.error("sql:"+sql, e);
			throw new Exception("sql:"+sql, e);
		}
	}
	
//	public void rankinfoTableTruncate(String date){
//		String sql = "truncate table rankinfo_"+date;
//		try{
//			JdbcTemplateFactoray.newSokuTopDataSource.update(sql);
//		}catch(Exception e){
//			logger.error("sql:"+sql, e);
//		}
//	}
	
	public void rankinfoTableDrop(){
		String date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -5), DataFormat.FMT_DATE_YYYY_MM_DD);
		String sql = "drop table if exists rankinfo_"+date;
		try{
			TopDateMgt.newSokuTopDataSource.update(sql);
		}catch(Exception e){
			logger.error("sql:"+sql, e);
		}
	}
}
