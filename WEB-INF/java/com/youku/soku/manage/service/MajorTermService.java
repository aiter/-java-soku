package com.youku.soku.manage.service;

import java.util.List;

import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.entity.MajorTerm;
import com.youku.soku.manage.sql.MajorTermPersistence;

public class MajorTermService {
	
	private static MajorTermPersistence mts = new MajorTermPersistence();
	
	public static List<MajorTerm> getMajorTerm() {
		return mts.getMajorTerm();
	}
	
	public static List<MajorTerm> getMajorTerm(int status, String keyword, int offset, int limit) {
		return mts.getMajorTerm(status, keyword, offset, limit);
	}
	
	public static void getMajorTermPagination(int status, String keyword, PageInfo pageInfo) {
		int records = mts.countMajorTerm(status, keyword);
		int totalPageNumber = (int) Math.ceil((double)records / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.setTotalRecords(records);
		if(pageInfo.getCurrentPageNumber() > totalPageNumber) {
            pageInfo.setCurrentPageNumber(totalPageNumber);
        }
		
		List<MajorTerm> result = mts.getMajorTerm(status, keyword, pageInfo.getOffset(), pageInfo.getPageSize());
		pageInfo.setResults(result);
	}
	
	
	public static MajorTerm getMajorTermById(int id) {
		return mts.getMajorTermById(id);
	}
	
	public static List<MajorTerm> getMajorTermByKeyword(String keyword) {
		return mts.getMajorTermByKeyword(keyword);
	}
	
	public static List<MajorTerm> getMajorTermByKeyword(String keyword, int cateId) {
		return mts.getMajorTermByKeyword(keyword, cateId);
	}
	
	public static void insertMajotTerm(MajorTerm mt) {
		mts.insertMajorTerm(mt);
	}
	
	public static void updateMajorTerm(MajorTerm mt) {
		mts.updateMajorTerm(mt);
	}

	public static void deleteMajorTerm(int id) {
		mts.deleteMajorTerm(id);
	}
}
