package com.youku.soku.manage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.Correction;
import com.youku.soku.manage.torque.CorrectionPeer;

public class CorrectionService {
	private static Logger logger = Logger.getLogger(CorrectionService.class);
	
	public static List<String> parseStr2List(String str, String split) {
		List<String> alias = new ArrayList<String>();
		String[] aliasArr = str.split(split);
		if (null != aliasArr) {
			for (String a : aliasArr) {
				if (!StringUtils.isBlank(a))
					alias.add(a.trim());
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}
	
	public static Correction findCorrectionByKeyword(String keyword) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(CorrectionPeer.KEYWORD, (Object) ("%" + keyword.trim() + "%"),
				Criteria.LIKE);
		List<Correction> corrections = CorrectionPeer.doSelect(criteria);
		if(null!=corrections&&corrections.size()>0) return corrections.get(0);
		return null;
	}
	
	public static Correction findCorrectionByCorrectKeyword(String correct_keyword) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(CorrectionPeer.CORRECT_KEYWORD, correct_keyword);
		List<Correction> corrections = CorrectionPeer.doSelect(criteria);
		if(null!=corrections&&corrections.size()>0) return corrections.get(0);
		return null;
	}
	
	public static void update(int id,int status,String keyword) throws Exception{
		Correction correction = CorrectionPeer.retrieveByPK(id);
		if(null==correction) throw new Exception("纠错词已经不存在");
		logger.info("before update correction:"+correction.toString());
		if(-1!=status){
			correction.setStatus(status);
			correction.setUpdateTime(new Date());
			correction.setModified(true);
			correction.setNew(false);
			correction.save();
			logger.info("after update correction:"+correction.toString());
		}else if(!StringUtils.isBlank(keyword)){
			keyword = keyword.trim();
			if(correction.getKeyword().trim().equalsIgnoreCase(keyword))
				return;
			correction.setKeyword(keyword);
			correction.setUpdateTime(new Date());
			correction.setModified(true);
			correction.setNew(false);
			correction.save();
			logger.info("after update correction:"+correction.toString());
		}
	}
	
	public static void findCorrectionPagination(PageInfo pageInfo,
			String searchWord, int status) throws Exception {

		Criteria crit = new Criteria();

		String whereSql = " Where 1 = 1 ";
		if (!StringUtils.isBlank(searchWord)) {
			crit.add(CorrectionPeer.CORRECT_KEYWORD, (Object) ("%" + searchWord.trim() + "%"),
					Criteria.LIKE);
			whereSql += "AND " + CorrectionPeer.CORRECT_KEYWORD + " LIKE "
					+ ("'%" + searchWord.trim() + "%'");
		}
		if (-1 != status) {
			crit.add(CorrectionPeer.STATUS, status);
			whereSql += " AND " + CorrectionPeer.STATUS + " = " + status;
		}
		crit.addDescendingOrderByColumn(CorrectionPeer.UPDATE_TIME);
		
		int totalRecord = ((Record) CorrectionPeer.executeQuery(
				"SELECT count(*) FROM " + CorrectionPeer.TABLE_NAME + whereSql,
				CorrectionPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);
		List<Correction> result = CorrectionPeer.doSelect(crit);
		pageInfo.setResults(result);
	}
}
