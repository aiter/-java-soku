package com.youku.top.quick;

import java.sql.Types;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.topn.KeywordWeekVOMapper;
import com.youku.top.topn.entity.KeywordWeekVO;
import com.youku.top.topn.util.KeywordUtil;
import com.youku.top.util.LogUtil;

public class QuickWordsParser {
	static Logger logger = Logger.getLogger(QuickWordsParser.class);
	
	private List<KeywordWeekVO> getInsiteUnionQueryKeywords(String date,String type,String order,int offset,int limit){
		String sql="select * from week_query_youku_"+date+ " where query_count>15 and query_type='"+type+"' order by "+order+" desc limit "+offset+","+limit;
		try{
			return JdbcTemplateFactoray.mergeLogYoukuDataSource.query(sql, KeywordWeekVOMapper.kwvomapper);
		}catch(Exception e){
			System.out.println(sql);
			logger.error(e);
		}
		return null;
	}
	
	private void updateWeekQueryKeywords(String date,int id,Set<String> types){
		String type = Utils.parse2Str(types);
		if(StringUtils.isBlank(type)) return;
		String sql="update week_query_youku_"+date+ " set types = ? where id = ?";
		try{
			JdbcTemplateFactoray.mergeLogYoukuDataSource.update(sql, new Object[]{type,id}, new int[]{Types.VARCHAR,Types.INTEGER});
		}catch(Exception e){
			logger.error(e);
		}
	}
	
//	public void quickWordsParse(String date,String type,String order){
//		logger.info("取得直达区的词");
//		List<KeywordWeekVO> list = null;
//		int offset = 0;
//		int limit = 2000;
//		String keyword = null;
//		Set<String> types = null;
//		while(offset<20000){
//			list = getInsiteUnionQueryKeywords(date, type, order, offset, limit);
//			offset =offset + limit;
//			if(null==list||list.size()<1) continue;
//			for(KeywordWeekVO kvo:list){
//				keyword = KeywordUtil.wordFilterTopword(keyword);
//				types = new HashSet<String>();
//				if(!StringUtils.isBlank(keyword)){
//					if(null==types||types.size()<1){
//						types = twp.parse(keyword);
//					}
//				if(null!=types&&types.size()>0){
//					types.remove("topconcern");
//					if(null!=types&&types.size()>0)
//						updateWeekQueryKeywords(date, kvo.getId(), types);
//				}
//				}
//			}
//		}
//	}
	
//	public static void main(String[] args) {
//		String end = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
//		String start = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
//		String date = new StringBuilder(start).append("_").append(end).toString();;
//		String type = "video";
//		String order = "quick_rate";
//		if(null!=args){
//			if(args.length == 1&&args[0].matches("\\d{8}_\\d{8}")){
//				date = args[0];
//			}
//			if(args.length == 2&&args[0].matches("\\d{8}_\\d{8}")){
//				date = args[0];
//				type = args[1];
//			}
//			if(args.length == 3&&args[0].matches("\\d{8}_\\d{8}")){
//				date = args[0];
//				type = args[1];
//				order = args[2];
//			}
//		}
//		try {
//			LogUtil.init(Level.INFO,"/opt/log_analyze/quick/log/log.txt");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		QuickWordsParser qp = new QuickWordsParser();
//		qp.quickWordsParse(date, type, order);
//	}
}
