package com.youku.soku.manage.service.paihangbang;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.top.mapping.TypeWords;
import com.youku.soku.top.mapping.TypeWordsPeer;

public class TypeWordsService {
	private static Logger logger = Logger.getLogger(TypeWordsService.class);

	private static TypeWordsService instance = null;

	private TypeWordsService() {
		super();
	}

	public synchronized static TypeWordsService getInstance() {
		if (null == instance)
			instance = new TypeWordsService();
		return instance;
	}

	public TypeWords getTypeWordsByKeywordAndCate(String keyword, int cate) {
		Criteria criteria = new Criteria();
		criteria.add(TypeWordsPeer.KEYWORD, keyword);
		criteria.add(TypeWordsPeer.CATE, cate);
		try {
			List<TypeWords> list = TypeWordsPeer.doSelect(criteria);
			if (null != list && list.size() > 0) {
				return list.get(0);
			}
		} catch (TorqueException e) {
			logger.error("keyword:" + keyword + ",cate:" + cate, e);
		}
		return null;
	}

	public List<TypeWords> getTypeWords(String keyword, int cate, String state,
			String checker, int programme_id) {
		Criteria criteria = new Criteria();
		criteria.add(TypeWordsPeer.KEYWORD, keyword);
		criteria.add(TypeWordsPeer.CATE, cate);
		criteria.add(TypeWordsPeer.STATE, state);
		criteria.add(TypeWordsPeer.CHECKER, checker);
		criteria.add(TypeWordsPeer.PROGRAMME_ID, programme_id);
		try {
			return TypeWordsPeer.doSelect(criteria);
		} catch (TorqueException e) {
			logger.error(e);
		}
		return null;
	}

	public void findTypeWordsPagination(PageInfo pageInfo, String keyword,
			int cate, String state, String checker) throws Exception {

		Criteria crit = new Criteria();

		String whereSql = " Where 1 = 1 ";
		if (!StringUtils.isBlank(keyword)) {
			crit.add(TypeWordsPeer.KEYWORD,
					(Object) ("%" + keyword.trim() + "%"), Criteria.LIKE);
			whereSql += "AND " + TypeWordsPeer.KEYWORD + " LIKE "
					+ ("'%" + keyword.trim() + "%'");
		}
		if (0 != cate) {
			crit.add(TypeWordsPeer.CATE, cate);
			whereSql += " AND " + TypeWordsPeer.CATE + " = " + cate;
		}

		if (!StringUtils.isBlank(state)) {
			crit.add(TypeWordsPeer.STATE, state);
			whereSql += " AND " + TypeWordsPeer.STATE + " = '" + state + "'";
		}

		if (!StringUtils.isBlank(checker)) {
			crit.add(TypeWordsPeer.CHECKER, checker);
			whereSql += " AND " + TypeWordsPeer.CHECKER + " = '" + checker
					+ "'";
		}

		crit.addAscendingOrderByColumn(TypeWordsPeer.CATE);

		int totalRecord = ((Record) TypeWordsPeer.executeQuery(
				"SELECT count(*) FROM " + TypeWordsPeer.TABLE_NAME + whereSql,
				TypeWordsPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());

		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.setTotalRecords(totalRecord);
		pageInfo.initCrit(crit);
		List<TypeWords> result = TypeWordsPeer.doSelect(crit);
		pageInfo.setResults(result);
	}

	public int executeSql(String sql) {
		try {
			return TypeWordsPeer.executeStatement(sql,
					TypeWordsPeer.DATABASE_NAME);
		} catch (TorqueException e) {
			logger.error("sql:" + sql, e);
		}
		return 0;
	}

	public TypeWords getTypeWordsById(int id) {
		try {
			return TypeWordsPeer.retrieveByPK(id);
		} catch (Exception e) {
			logger.error("id:" + id, e);
		}
		return null;
	}
	
	public PageInfo getTypeList(PageInfo pageInfo,int cate){
		Criteria crit = new Criteria();
		if(cate > -1)
			crit.add(TypeWordsPeer.CATE,cate);
		try {
			List<TypeWords> result = TypeWordsPeer.doSelect(crit);
			int totalRecord = result.size();
			int totalPageNumber = (int) Math.ceil((double) totalRecord
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(totalRecord);
			pageInfo.initCrit(crit);
			pageInfo.setResults(result);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pageInfo;
	}
}
