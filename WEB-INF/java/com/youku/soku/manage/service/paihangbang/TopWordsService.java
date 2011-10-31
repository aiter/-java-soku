package com.youku.soku.manage.service.paihangbang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.soku.manage.bo.paihangbang.KeywordTypeVO;
import com.youku.soku.manage.bo.paihangbang.TopWordsVO;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.top.mapping.TopWords;
import com.youku.soku.top.mapping.TopWordsPeer;
import com.youku.soku.top.mapping.TypeWords;
import com.youku.soku.top.mapping.TypeWordsPeer;
import com.youku.top.util.TopWordType;
import com.youku.top.util.TopWordType.WordType;

public class TopWordsService {
	private static Logger logger = Logger.getLogger(TopWordsService.class);
	
	private static TopWordsService instance = null;
	private TopWordsService(){
		super();
	}
	
	public synchronized static TopWordsService getInstance() {
		if(null==instance)
			instance = new TopWordsService();
		return instance;
	}
	
	public int executeSql(String sql){
		try {
			return TopWordsPeer.executeStatement(sql, TopWordsPeer.DATABASE_NAME);
		} catch (TorqueException e) {
			logger.error("sql:"+sql,e);
		}
		return 0;
	}
	
	public TopWords getTopWordsById(int id){
		try {
			return TopWordsPeer.retrieveByPK(id);
		} catch (NoRowsException e) {
			logger.error("id:"+id,e);
		} catch (TooManyRowsException e) {
			logger.error("id:"+id,e);
		} catch (TorqueException e) {
			logger.error("id:"+id,e);
		}
		return null;
	}
	
	public void findTopWordsPagination(PageInfo pageInfo,
			String words, int cate,int visible,String date) throws Exception {

		Criteria crit = new Criteria();

		String whereSql = " Where isTop = 1 ";
		if(cate==WordType.搞笑.getValue())
			whereSql = " Where 1 = 1 ";
		if (!StringUtils.isBlank(words)) {
			crit.add(TopWordsPeer.KEYWORD, (Object) ("%" + words.trim() + "%"),
					Criteria.LIKE);
			whereSql += "AND " + TopWordsPeer.KEYWORD + " LIKE "
					+ ("'%" + words.trim() + "%'");
		}
		if (0 != cate) {
			if(cate == 100 )
				cate = 0;
			crit.add(TopWordsPeer.CATE, cate);
			whereSql += " AND " + TopWordsPeer.CATE + " = " + cate;
		}
		
		if(cate!=WordType.搞笑.getValue())
			crit.add(TopWordsPeer.ISTOP, 1);
		
		if (-1 != visible) {
			crit.add(TopWordsPeer.VISIBLE, visible);
			whereSql += " AND " + TopWordsPeer.VISIBLE + " = " + visible;
		}
		
		if (!StringUtils.isBlank(date)) {
			crit.add(TopWordsPeer.TOP_DATE, date);
			whereSql += " AND " + TopWordsPeer.TOP_DATE + " = '" + date + "'";
		}
		
		crit.addDescendingOrderByColumn(TopWordsPeer.QUERY_COUNT);
		crit.addDescendingOrderByColumn(TopWordsPeer.KEYWORD);
		
		int totalRecord = ((Record) TopWordsPeer.executeQuery(
				"SELECT count(*) FROM " + TopWordsPeer.TABLE_NAME + whereSql,
				TopWordsPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);
		List<TopWords> result = TopWordsPeer.doSelect(crit);
		
		Set<Integer> muluidset = new HashSet<Integer>();
		//if(cate==WordType.电视剧.getValue()||cate==WordType.电影.getValue()||cate==WordType.不限.getValue()){
			Map<String,Integer> muludate = TopDateService.getTopDateMulu();
			String onlineDate = null;
			int version_no = 0;
			for(Entry<String, Integer> entry:muludate.entrySet()){
				onlineDate = entry.getKey();
				version_no = entry.getValue();
			}
			muluidset = RankInfoService.getInstance().getMuluProgrammeId(onlineDate, version_no);
		//}
		List<TopWordsVO> topwords = new ArrayList<TopWordsVO>();
		TopWordsVO twvo = new TopWordsVO();
		if(null!=result){
			for(TopWords tw:result){
				if(tw.getKeyword().equalsIgnoreCase(twvo.getKeyword())){
					twvo.getTops().add(topWords2KeywordTypeVO(tw,muluidset));
				}else{
					twvo = new TopWordsVO();
					if(!StringUtils.isBlank(tw.getKeyword())){
						topwords.add(twvo);
					}
					twvo.setKeyword(tw.getKeyword());
					twvo.getTops().add(topWords2KeywordTypeVO(tw,muluidset));
				}
			}
		}
		pageInfo.setResults(topwords);
	}
	
	private KeywordTypeVO topWords2KeywordTypeVO(TopWords tw,Set<Integer> muluidset){
		KeywordTypeVO ktvo = new KeywordTypeVO();
		ktvo.setId(tw.getId());
		ktvo.setCate(tw.getCate());
		if(tw.getCate()>0)
			ktvo.setCname(TopWordType.wordTypeMap.get(tw.getCate()));
		ktvo.setKeyword(tw.getKeyword());
		ktvo.setQueryCount(tw.getQueryCount());
		if(!StringUtils.isBlank(tw.getPic())&&!tw.getPic().startsWith("http://"))
			ktvo.setPic("http://g"+(new Random().nextInt(4)+1)+".ykimg.com/"+tw.getPic());
		else
			ktvo.setPic(tw.getPic());
		if(tw.getProgrammeId()>0)
			ktvo.setProgrammeId(tw.getProgrammeId());
		else{
			Criteria crit = new Criteria();
			crit.add(TypeWordsPeer.KEYWORD, tw.getKeyword());
			crit.add(TypeWordsPeer.CATE,tw.getCate());
			try {
				List<TypeWords> result = TypeWordsPeer.doSelect(crit);
				if(null!=result && result.size()>0){
					ktvo.setProgrammeId(result.get(0).getProgrammeId());
					tw.setProgrammeId(ktvo.getProgrammeId());
				}
			} catch (TorqueException e) {
				e.printStackTrace();
			}
		}
		//if(tw.getProgrammeId()>0&&(tw.getCate()==WordType.电视剧.getValue()||tw.getCate()==WordType.电影.getValue()||tw.getCate()==WordType.综艺.getValue())){
			ktvo.setProgrammeName(ProgrammeMgt.getInstance().getProgrammeNameById(tw.getProgrammeId(),tw.getCate()));
			if(muluidset.contains(tw.getProgrammeId()))
				ktvo.setIsMulu(1);
		//}
		ktvo.setVisible(tw.getVisible());
		ktvo.setCreateTime(tw.getCreateDate());
		ktvo.setUrl(tw.getUrl());
		return ktvo;
	}
	
	public List<TopWords> getTopWords(String date){
		Criteria criteria = new Criteria();
		criteria.add(TopWordsPeer.VISIBLE,1);
		criteria.add(TopWordsPeer.TOP_DATE,date);
		//Criterion crit1 = criteria.getNewCriterion(TopWordsPeer.CATE,WordType.电视剧.getValue(),SqlEnum.EQUAL);
		//Criterion crit2 = criteria.getNewCriterion(TopWordsPeer.CATE,WordType.电影.getValue(),SqlEnum.EQUAL);
		//crit1.or(crit2);
		//criteria.add(crit1);
		try {
			return TopWordsPeer.doSelect(criteria);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//查询指定数量的热门搜索 并生存xls
	public static WritableWorkbook exportXls(int num,int cate,String sheetName,String topdate,WritableWorkbook wwb){
		logger.debug("--------- start export --------");
		WritableSheet ws = wwb.createSheet(sheetName, 0);
		ws.setColumnView(0, 30);
		ws.setColumnView(2, 15);
		Label keyWordCell = new Label(0, 0, "搜索词");  
		Label cateCell = new Label(1, 0, "类型");  
		Label countCell = new Label(2, 0, "搜索量");  
        try {
			ws.addCell(keyWordCell);
			ws.addCell(cateCell);
			ws.addCell(countCell);
		} catch (RowsExceededException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (WriteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		Criteria crit = new Criteria();
		crit.add(TopWordsPeer.VISIBLE,1);
		crit.addDescendingOrderByColumn(TopWordsPeer.QUERY_COUNT);
		crit.add(TopWordsPeer.TOP_DATE,topdate);
		if(num > 0)
			crit.setLimit(num);
		if(cate>0){
			if(cate>9)
				cate=0;
			crit.add(TopWordsPeer.CATE,cate);
		}
		List<TopWords> twList = null;
		try {
			twList = TopWordsPeer.doSelect(crit);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("--------- get "+sheetName+" top:"+twList.size()+"  --------");
		if(null!=twList && twList.size()>0){
			int row = 0;
			for(TopWords tw:twList){
				row ++;
				String cateName = "无类型";
				if(tw.getCate()>0)
					cateName = getCateNameForXls(tw.getCate());
				Label _keyWordCell = new Label(0, row, tw.getKeyword());  
				Label _cateCell = new Label(1, row, cateName);  
				Label _countCell = new Label(2, row, tw.getQueryCount()+"");  
				try {
					ws.addCell(_keyWordCell);
					ws.addCell(_cateCell);
					ws.addCell(_countCell);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return wwb;
	} 
	
	public static String getCateNameForXls(int cate){
		if(cate>10)
			return "无类型";
		else if(cate>0)
			return TopWordType.wordTypeMap.get(cate);
		else
			return "全部";
	}
	
	//获取指定数量的搜索词  默认按搜索数量排序
	public static List<TopWordsVO> getTopKeyWords(int top,int cate){
		List<TopWordsVO> result = new ArrayList<TopWordsVO>();
		Calendar c= Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		String top_date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		
		Criteria crit = new Criteria();
		if(cate>0 && cate <10)
			crit.add(TopWordsPeer.CATE,cate);
		else if(cate==100)
			crit.add(TopWordsPeer.CATE,0);
		crit.setLimit(top);
		crit.add(TopWordsPeer.TOP_DATE,top_date);
		if(cate!=WordType.搞笑.getValue())
			crit.add(TopWordsPeer.ISTOP,1);
		crit.addDescendingOrderByColumn(TopWordsPeer.QUERY_COUNT);
		crit.addDescendingOrderByColumn(TopWordsPeer.KEYWORD);
		try {
			List<TopWords> twList = TopWordsPeer.doSelect(crit);
			for(TopWords tw:twList){
				TopWordsVO twv = new TopWordsVO();
				twv.setKeyword(tw.getKeyword());
				result.add(twv);
			}
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
