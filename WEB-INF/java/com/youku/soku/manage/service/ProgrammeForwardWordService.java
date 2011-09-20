package com.youku.soku.manage.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.ProgrammeForwardWord;
import com.youku.soku.library.load.ProgrammeForwardWordPeer;
import com.youku.soku.manage.bo.ProgrammeForwardWordBo;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.util.SearchParameter;
import com.youku.soku.util.DataBase;

public class ProgrammeForwardWordService {
	
	private static Logger log = Logger.getLogger(ProgrammeForwardWordService.class);

	public static void searchProgrammeInfo(SearchParameter param, PageInfo pageInfo)
			throws Exception {

		log.info("serarch word: " + param.getSearchWord());
		List<ProgrammeForwardWordBo> resultList = new ArrayList<ProgrammeForwardWordBo>();

		String mainSql = "programme p, programme_search_number psn WHERE p.id = psn.fk_programme_id AND p.state = 'normal'";
		String likeSql = "";
		if (param.getSearchWord() != null && !param.getSearchWord().equals("")) {
			likeSql = " AND p.name LIKE ? ";
		}

		if (param.getCategoryId() > 0) {
			mainSql += " AND p.cate = " + param.getCategoryId();
		}
		

		if (param.isAccuratelyMatched()) {
			param.setSearchWord(param.getSearchWord());
		} else {
			param.setSearchWord("%" + param.getSearchWord() + "%");
		}
		
		if(param.getFowardWordStatus() == SearchParameter.FOWARD_WORD_YES) {
			mainSql += " AND p.id in (select fk_programme_id from programme_forward_word where forward_word is not null and forward_word != '')";
		} else if(param.getFowardWordStatus() == SearchParameter.FOWARD_WORD_NO) {
			mainSql += " AND p.id not in (select fk_programme_id from programme_forward_word where forward_word is not null and forward_word != '')";
		}

		String countsql = "SELECT COUNT(*) FROM " + mainSql;
		countsql += likeSql;

		PreparedStatement pstcnt = null;
		ResultSet rscnt = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getLibraryConn();
			pstcnt = conn.prepareStatement(countsql);
			if (!likeSql.equals("")) {
				pstcnt.setString(1, param.getSearchWord());				
			}
			log.info("pstcnt: " + pstcnt.toString());
			rscnt = pstcnt.executeQuery();
			int recordCount = 0;
			while (rscnt.next()) {
				recordCount = rscnt.getInt(1);
			}
			if (recordCount == 0) {
				return;
			}
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);
			String sql = "SELECT p.id, p.name, p.cate FROM " + mainSql + likeSql
					+ " ORDER BY psn.search_number desc LIMIT " + pageInfo.getOffset()
					+ ", " + pageInfo.getPageSize();

			pst = conn.prepareStatement(sql);
			log.info("likeSql " + likeSql);
			if (!likeSql.equals("")) {
				log.info("param.getSearchWord()" + param.getSearchWord());
				pst.setString(1, param.getSearchWord());
				if (param.getStatus() == SearchParameter.WITH_SERIES) {
					pst.setString(2, param.getSearchWord());
				}
			}
			log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				ProgrammeForwardWordBo pfwBo = new ProgrammeForwardWordBo();
				pfwBo.setProgrammeId(rs.getInt("p.id"));
				pfwBo.setProgrammeName(rs.getString("p.name"));
				pfwBo.setCateName(CategoryService.getCategoryMap().get(rs.getInt("p.cate")));
				getProgrammeForwardWord(pfwBo);
				
				resultList.add(pfwBo);
			}
			
			
			
			pageInfo.setResults(resultList);
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
				if (pstcnt != null) {
					pstcnt.close();
				}
				if (rscnt != null) {
					rscnt.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}

	}
	
	public static void getProgrammeForwardWord(ProgrammeForwardWordBo pfwBo) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ProgrammeForwardWordPeer.FK_PROGRAMME_ID, pfwBo.getProgrammeId());
		List<ProgrammeForwardWord> pfwList = ProgrammeForwardWordPeer.doSelect(crit);
		
		StringBuilder builder = new StringBuilder();
		if(pfwList != null) {
			for(ProgrammeForwardWord pfw : pfwList) {
				if(builder.length() > 0) {
					builder.append('|');
				}
				builder.append(pfw.getForwardWord());
				pfwBo.setStartTime(pfw.getStartTime());
				pfwBo.setExpireTime(pfw.getExpireTime());
			}
		}
		
		pfwBo.setForwardWords(builder.toString());
	}

}
