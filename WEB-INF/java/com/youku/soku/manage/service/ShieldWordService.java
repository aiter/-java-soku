package com.youku.soku.manage.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.entity.ShieldWordConstants;
import com.youku.soku.manage.shield.SearchParameter;
import com.youku.soku.manage.torque.ShieldCategory;
import com.youku.soku.manage.torque.ShieldCategoryPeer;
import com.youku.soku.manage.torque.ShieldWords;
import com.youku.soku.util.DataBase;

public class ShieldWordService {
	public static void searchShieldWords(PageInfo pageInfo,
			SearchParameter param, boolean listAll) {

		System.out.println(param);
		String channelCondition = "";
		if (param != null && param.getChannel() >  0) {
			channelCondition = "SELECT fk_word_id FROM shield_word_relation WHERE fk_shield_channel_id = " + param.getChannel();
		}
		
		String siteCondition = "";
		if(param != null && param.getSiteLevel() > 0) {
			siteCondition = "SELECT fk_word_id FROM determiner_word_relation WHERE site_level <= " + param.getSiteLevel();
		}
		
		/*if(param != null && param.getSiteCategory() > -1) {
			siteCondition += " AND site_category = " + param.getSiteCategory();
		}
		
		if(param != null && param.getSiteCategory() < 0 && param.getSiteLevel() < 0) {
			siteCondition = "";
		}*/
		
		String sql = "shield_words sw, shield_category cate WHERE sw.fk_shield_category_id = cate.id";
		
		if(param != null && !StringUtils.isBlank(param.getKeyword())) {
			sql += " AND sw.word like ?"; 
		}
		
		if(param != null && param.getType() > 0 && param.getType() != 3) {
			sql += " AND (sw.type = 3 or sw.type = " + param.getType() + ")";
		}
		
		if(param != null && param.getWordCategory() > 0) {
			sql += " AND cate.id = " + param.getWordCategory();
		}
		
		if(param != null && param.getHitRole() > 0) {
			sql += " AND sw.hit_role = " + param.getHitRole();
		}
		
		if(param != null && !StringUtils.isBlank(param.getExpireDate())){
			sql += " AND sw.create_time > DATE_FORMAT('" + param.getExpireDate() + "', '%Y-%m-%d') AND sw.create_time < DATE_FORMAT(DATE_ADD('" + param.getExpireDate() + "', INTERVAL 1 DAY), '%Y-%m-%d')";
		}
		
		if(param != null && !StringUtils.isBlank(param.getModifier())) {
			sql += " AND sw.modifier = ?";
		}
		
		if(!channelCondition.isEmpty()) {
			sql += " AND sw.id in (" + channelCondition + ")";
		}
		
		if(!siteCondition.isEmpty()) {
			sql += " AND sw.id in (" + siteCondition + ")";
		}
		
		String orderBy = " ORDER BY sw.update_time";
		if(param != null && param.getOrderby() == ShieldWordConstants.UPDATETIMEORDER) {
			orderBy = " ORDER BY sw.update_time";
		} else if(param != null && param.getOrderby() == ShieldWordConstants.KEYWORDORDER) {
			orderBy = " ORDER BY sw.word";
		} else if(param != null && param.getOrderby() == ShieldWordConstants.MODIFIERORDER) {
			orderBy = " ORDER BY sw.modifier";
		}
		
		if(param != null && param.getTrend() == ShieldWordConstants.ASCEDING) {
			orderBy += " ASC";
		} else {
			orderBy += " DESC";
		}

		String countsql = "SELECT COUNT(*) FROM " + sql;
		String normalsql = "SELECT * FROM " + sql + orderBy;


		System.out.println("countsql: " + countsql);
		
		List<ShieldWords> resultList = new ArrayList<ShieldWords>();
		PreparedStatement pstcnt = null;
		ResultSet rscnt = null;

		PreparedStatement pst = null;
		ResultSet rs = null;

		Connection conn = null;
		try {
			conn = DataBase.getSokuConn();
			pstcnt = conn.prepareStatement(countsql);
			if (param != null) {
				if (!StringUtils.isBlank(param.getKeyword())) {
					pstcnt.setString(1, param.getKeyword() + "%");
					if (!StringUtils.isBlank(param.getModifier())) {
						pstcnt.setString(2, param.getModifier());
					}
				} else if (!StringUtils.isBlank(param.getModifier())) {
					pstcnt.setString(1, param.getModifier());
				}
			}
			rscnt = pstcnt.executeQuery();
			int recordCount = 0;
			while (rscnt.next()) {
				recordCount = rscnt.getInt(1);
			}
			System.out.println("Shield Word Service count sql: " + pstcnt.toString());
			System.out.println("!!! Search result of teleplay version is "
					+ recordCount);
			if (recordCount == 0) {
				return;
			}
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);


			if (!listAll) {
				pst = conn.prepareStatement(normalsql + " LIMIT "
						+ pageInfo.getOffset() + ", " + pageInfo.getPageSize());
			} else {
				pst = conn.prepareStatement(normalsql);
			}
			if (param != null) {
				if (!StringUtils.isBlank(param.getKeyword())) {
					pst.setString(1, param.getKeyword()+ "%");
					if (!StringUtils.isBlank(param.getModifier())) {
						pst.setString(2, param.getModifier());
					}
				} else if (!StringUtils.isBlank(param.getModifier())) {
					pst.setString(1, param.getModifier());
				}
			}
			System.out.println("Shield Word Service sql: " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				ShieldWords sw = new ShieldWords();
				sw.setId(rs.getInt("sw.id"));
				sw.setWord(rs.getString("sw.word"));
				sw.setExcluding(rs.getString("sw.excluding"));
				sw.setType(rs.getInt("sw.type"));
				sw.setHitRole(rs.getInt("sw.hit_role"));
				sw.setFkShieldCategoryId(rs.getInt("sw.fk_shield_category_id"));
				sw.setStartTime(rs.getTimestamp("sw.start_time"));
				sw.setExpireTime(rs.getTimestamp("sw.expire_time"));
				sw.setRemark(rs.getString("sw.remark"));
				sw.setCreateTime(rs.getTimestamp("sw.create_time"));
				sw.setUpdateTime(rs.getTimestamp("sw.update_time"));
				sw.setModifier(rs.getString("sw.modifier"));
				resultList.add(sw);
			}
			pageInfo.setResults(resultList);
		} catch (Exception e) {
			System.out
					.println("[ERROR} search teleplay_version union names error");
			e.printStackTrace();
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
				System.out
						.println("[ERROR] close connection error in function searchShieldWords");
				e.printStackTrace();
			}
		}

	}
	
	public static Map<Integer, String> getShieldChannelMap(boolean withall) {
		try {
			Map<Integer, String> channelMap = new LinkedHashMap<Integer, String>();
			List<Category> channelList = CategoryMap.getInstance().videoList;
			if (withall) {
				channelMap.put(-1, "所有");
			}
			for(Category sc : channelList) {
				channelMap.put(sc.getId(), sc.getName());
			}
			
			
			return channelMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, Integer> getShieldChannelNameMap() {
		try {
			Map<String, Integer> channelMap = new HashMap<String, Integer>();
			List<Category> channelList = CategoryMap.getInstance().videoList;
			
			for(Category sc : channelList) {
				channelMap.put( sc.getName(), sc.getId());
			}
			
			
			return channelMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<Integer, String> getShieldCategoryMap(boolean withall) {
		try {
			Map<Integer, String> categoryMap = new LinkedHashMap<Integer, String>();
			List<ShieldCategory> categoryList = ShieldCategoryPeer.doSelect(new Criteria());
			if (withall) {
				categoryMap.put(-1, "所有");
			}
			for(ShieldCategory sc : categoryList) {
				categoryMap.put(sc.getId(), sc.getName());
			}
			
			
			return categoryMap;
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return null;
	}
}
