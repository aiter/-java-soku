package com.youku.top.paihangbang;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.top.data_source.NewSokuTopDataSource;

public class TopDateMgt {
	private static Logger logger = Logger.getLogger(TopDateMgt.class);
	private static TopDateMgt instance = null;
	public static JdbcTemplate newSokuTopDataSource = new JdbcTemplate(
			NewSokuTopDataSource.INSTANCE);
	
	private TopDateMgt() {
		super();
	}

	public static synchronized TopDateMgt getInstance() {
		if(null==instance)
			instance = new TopDateMgt();
		return instance;
	}
	
	public int topDateSave(final String online_date,final int version_no,final String user,final String zhidaqu){
		String sql = "insert into top_date (online_date,version_no,user,zhidaqu) values (?,?,?,?) ON DUPLICATE KEY UPDATE online_date=?,version_no=?,user=?";
		logger.info("保存版本,online_date:"+online_date+",version_no:"+version_no+",user:"+user);
		try{
			return newSokuTopDataSource.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, online_date);
					ps.setInt(2,version_no);
					ps.setString(3,user);
					ps.setString(4, zhidaqu);
					ps.setString(5, online_date);
					ps.setInt(6,version_no);
					ps.setString(7,user);
				}
			});
		}catch(Exception e){
			logger.error("保存版本,sql:"+sql+",online_date:"+online_date+"zhidaqu:"+zhidaqu+",version_no:"+version_no+",user:"+user,e);
		}
		return -1;
	}
	
	public Map<String,String> getTopDate(){
		Map<String,String> map = new HashMap<String, String>();
		try {
			List list = newSokuTopDataSource.queryForList("select * from top_date");
			Iterator iterator = list.iterator();
			Map temp = null;
			while(iterator.hasNext()){
				temp = (Map)iterator.next();
				map.put(String.valueOf(temp.get("zhidaqu")), String.valueOf(temp.get("online_date")));
			}
			return map;
		} catch (Exception e) {
			logger.error(e);
		}
		logger.info("map.size:"+map.size());
		return map;
	}
}
