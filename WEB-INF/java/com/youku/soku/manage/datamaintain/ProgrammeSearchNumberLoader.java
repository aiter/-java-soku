package com.youku.soku.manage.datamaintain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.ProgrammeSearchNumber;
import com.youku.soku.library.load.ProgrammeSearchNumberPeer;
import com.youku.soku.suggest.trie.Entity;
import com.youku.soku.util.DataBase;

public class ProgrammeSearchNumberLoader {
	
	private Logger log = Logger.getLogger(this.getClass()); 
	
	public void init() {
		int programmeCount = getProgrammeCount();
		int step = 5000;
		
		for(int i = 0; i <= programmeCount + 5000; i+= step) {
			loadAndSaveProgrammeSearchNumber(i, step);
		}
	}
	
	private void loadAndSaveProgrammeSearchNumber(int offset, int limit) {

		Connection conn = null;
		Connection searchConn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getLibraryConn();
			searchConn = DataBase.getSearchStatConn();
			String sql = "SELECT id, name, alias FROM programme limit ?, ?";

			pst = conn.prepareStatement(sql);
			pst.setInt(1, offset);
			pst.setInt(2, limit);
			rs = pst.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String alias = rs.getString("alias");
				int id = rs.getInt("id");
				
				int searchNumber = getSearchNumber(searchConn, name);
				
				if(alias != null) {
					StringTokenizer st = new StringTokenizer(alias, "|");
					while(st.hasMoreElements()) {
						String aa = st.nextToken();
						int aliasSearchNumber = getSearchNumber(searchConn, aa);
						if(aliasSearchNumber > searchNumber) {
							searchNumber = aliasSearchNumber;
						}
					}
				}
				Criteria crit = new Criteria();
				crit.add(ProgrammeSearchNumberPeer.FK_PROGRAMME_ID, id);
				List<ProgrammeSearchNumber> psnList = ProgrammeSearchNumberPeer.doSelect(crit);
				if(psnList != null && !psnList.isEmpty()) {
					ProgrammeSearchNumber psn = psnList.get(0);
					psn.setSearchNumber(searchNumber);
					ProgrammeSearchNumberPeer.doUpdate(psn);
				} else {
					ProgrammeSearchNumber psn = new ProgrammeSearchNumber();
					psn.setFkProgrammeId(id);
					psn.setSearchNumber(searchNumber);
					psn.save();
				}
			}
			
		
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				JdbcUtil.close(conn);
				JdbcUtil.close(searchConn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}	
	}
	
	private int getProgrammeCount() {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int recordCount = 0;
		
		try {
			conn = DataBase.getLibraryConn();		
			String sql = "SELECT count(*) FROM programme";
			pst = conn.prepareStatement(sql);
		
			rs = pst.executeQuery();
			while (rs.next()) {
				recordCount = rs.getInt(1);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}

		return recordCount;
	}
	
	public int getSearchNumber(Connection conn, String keyword) {
		String date = DataFormat.formatDate(DataFormat.getNextDate(
				new Date(), -2), DataFormat.FMT_DATE_YYYY_MM_DD);
		String table = "query_" + date;
		String sql = "select id, keyword,keyword_py,query_type,query_count,result from "
				+ table + " where keyword like ? order by query_count desc";

		PreparedStatement pt = null;
		ResultSet rs = null;

		Map<String, Integer> result = new HashMap<String, Integer>();

		Map<String, Entity> keywordsMap = new HashMap<String, Entity>();
		try {
			pt = conn.prepareStatement(sql);
			pt.setString(1, keyword);
			rs = pt.executeQuery();

			while (rs.next()) {
				String orginalKeyword = rs.getString("keyword");
				String query_type = rs.getString("query_type");
				int query_count = rs.getInt("query_count");
				int searchNum = rs.getInt("query_count");

				return query_count;
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != pt)
					pt.close();
			
			} catch (SQLException e) {
				log.info("db close error!");
				e.printStackTrace();
			}

		}

		return 0;
	}

}
