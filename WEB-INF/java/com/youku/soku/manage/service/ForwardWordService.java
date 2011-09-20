package com.youku.soku.manage.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.ForwardWord;
import com.youku.soku.library.load.ForwardWordPeer;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.util.SearchParameter;
import com.youku.soku.util.DataBase;

public class ForwardWordService {
	
	private static Logger log = Logger.getLogger(ForwardWordService.class);

	public static void searchProgrammeInfo(String searchWord, int cate, PageInfo pageInfo)
			throws Exception {
		
		  Criteria crit = new Criteria();
	        
	        String whereSql = " Where 1 = 1 ";
	        if(searchWord != null && !searchWord.equals("")) {
	        	crit.add(ForwardWordPeer.FORWARD_WORD, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
	        	whereSql += "AND " + ForwardWordPeer.FORWARD_WORD + " LIKE " + ( "'%" + searchWord + "%'");
	        }
	        
	        if(cate > 0) {
	        	crit.add(ForwardWordPeer.CATE, cate);
	        	whereSql += "AND " + ForwardWordPeer.CATE + " = " + cate;
	        }
	        
	        
	        int totalRecord = ((Record) ForwardWordPeer.executeQuery("SELECT count(*) FROM " + ForwardWordPeer.TABLE_NAME + whereSql, ForwardWordPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
	        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
	        pageInfo.setTotalPageNumber(totalPageNumber);
	        pageInfo.initCrit(crit);
	        pageInfo.setTotalRecords(totalRecord);
	        
	        
	        
	        List result = ForwardWordPeer.doSelect(crit);
	        
	        pageInfo.setResults(result);
		
	}
	
/*	public static void getForwardWord(ForwardWordBo pfwBo) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ForwardWordPeer.FK_PROGRAMME_ID, pfwBo.getProgrammeId());
		List<ForwardWord> pfwList = ForwardWordPeer.doSelect(crit);
		
		StringBuilder builder = new StringBuilder();
		if(pfwList != null) {
			for(ForwardWord pfw : pfwList) {
				if(builder.length() > 0) {
					builder.append('|');
				}
				builder.append(pfw.getForwardWord());
				pfwBo.setStartTime(pfw.getStartTime());
				pfwBo.setExpireTime(pfw.getExpireTime());
			}
		}
		
		pfwBo.setForwardWords(builder.toString());
	}*/
	
	public static List<ForwardWord> getForwardWord(String word) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ForwardWordPeer.FORWARD_WORD, word);
		List<ForwardWord> fwList = ForwardWordPeer.doSelect(crit);
		
		return fwList;
	}

}
