package com.youku.soku.pos_analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.soku.pos_analysis.entity.CategoryClickVO;
import com.youku.soku.pos_analysis.entity.KeywordCategoryVO;
import com.youku.top.paihangbang.TypeWordsMgt;
import com.youku.top.paihangbang.entity.TypeWord;
import com.youku.top.topn.util.KeywordUtil;
import com.youku.top.util.TopWordType.WordType;

public class InitAnalysis {
	
	private static Log logger = LogFactory.getLog(InitAnalysis.class);
	private static InitAnalysis instance=null;
	public static synchronized InitAnalysis getInstance(){
		if(null!=instance) return instance;
		else {
			instance = new InitAnalysis();
			instance.getFreshKeywordCategoryMap();
			instance.getPersonDataSet();
			mapPrepare();
			return instance;
		}
	}
	
	public static InitAnalysis getInstanceFromDataBase(){
		InitAnalysis ia = new InitAnalysis();
		ia.getFreshKeywordCategoryMap();
		instance.getPersonDataSet();
		mapPrepare();
		return ia;
	}
	
	public static final String PERSON = "明星";
	
	private static boolean initFromDababase = true;// 从数据库加载分类信息
	private static final long LoadFromDababasePeriod = 1000L * 60 * 60 * 24;// 24小时
	private static final long LoadFromDababaseDelay = Utils.getDelayTime(1000L* 60*60*24, 4,true);// 4点

	private static final long checkLoadFromDababaseDelay = 1000L * 60 * 5;// 5分钟
	private static final long checkLoadFromDababasePeriod = 1000L * 60 * 60; //1小时
	
//	static {
//		if (initFromDababase) {
//			new Timer().schedule(new LoadFromDatabaseTimerTask(),
//					LoadFromDababaseDelay, LoadFromDababasePeriod);
//		}
//		
//		new Timer().schedule(new CheckLoadFromDatabaseTimerTask(),
//				checkLoadFromDababaseDelay, checkLoadFromDababasePeriod);
//	}
	
	private static final int rate1=50;
	private static final int rate2=40;
	private static final int rate3=30;
	private static final int rate1_rate2=10;
	private static final int merge_rate1=60;
	private static final int date_limit = 5;
	private static Set<String> persons = null;
	private static Map<String,KeywordCategoryVO> keywordCategoryMaps = null;
	
	public Set<String> getPersons() {
		return persons;
	}

	public void setPersons(Set<String> persons) {
		InitAnalysis.persons = persons;
	}

	public Map<String, KeywordCategoryVO> getKeywordCategoryMaps() {
		return keywordCategoryMaps;
	}

	public void setKeywordCategoryMaps(
			Map<String, KeywordCategoryVO> keywordCategoryMaps) {
		InitAnalysis.keywordCategoryMaps = keywordCategoryMaps;
	}

	/**
	 * 取得关键词点击数据
	 * @param date yyyy_mm_dd
	 */
	private Map<String,KeywordCategoryVO> getFreshKeywordCategoryMap(String date){
		Map<String,KeywordCategoryVO> map = MergeClickGetter.getInstance().getKeywordCategoryVO(date);
//		KeywordCategoryVO kvo = null;
//		for(String p:persons){
//			kvo = map.get(p);
//			if(null==kvo){
//				kvo = new KeywordCategoryVO();
//			}
//			kvo.setPerson(true);
//			map.put(p, kvo);
		return map;
	}
	
	/**
	 * 取得关键词点击数据
	 */
	public void getFreshKeywordCategoryMap(){
		String date = null;;
		boolean flag = true;
		for(int i=1;i<date_limit+1;i++){
			date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1*i), DataFormat.FMT_DATE_YYYY_MM_DD);
			Map<String,KeywordCategoryVO> map = getFreshKeywordCategoryMap(date);
			if(flag&&map.size()>50000){
				setKeywordCategoryMaps(map);
				flag = false;
				break;
			}
		}
	}
	
	private List<CategoryClickVO> mergeList(List<CategoryClickVO> list1,List<CategoryClickVO> list2,double right){
		List<CategoryClickVO> list = new ArrayList<CategoryClickVO>();
		CategoryClickVO c = null;
		for(CategoryClickVO ccvo:list1){
			c = new CategoryClickVO();
			list.add(c);
			c.setCategoryId(ccvo.getCategoryId());
			c.setClick_nums(ccvo.getClick_nums());
		}
		
		for(CategoryClickVO ccvo:list2){
			boolean f = false;
			for(CategoryClickVO ccvo1:list){
				if(ccvo1.getCategoryId()==ccvo.getCategoryId()){
					f = true;
					ccvo1.setClick_nums(ccvo1.getClick_nums()+(int)(right*ccvo.getClick_nums()));
					break;
				}
			}
			if(!f){
				c = new CategoryClickVO();
				list.add(c);
				c.setCategoryId(ccvo.getCategoryId());
				c.setClick_nums((int)(right*ccvo.getClick_nums()));
			}
		}
		Collections.sort(list,new CategoryClickVODescComparator());
		return list;
	}
	
	/**
	 * 取得人物数据
	 */
	public void getPersonDataSet(){
		Set<String> persons = PersonGetter.getInstance().getPersonData();
		InitAnalysis.persons = persons;
	}
	
	public static void mapPrepare(){
		if(null!=keywordCategoryMaps){
			
//			Map<Integer,Set<String>>  map = TypeWordsMgt.getInstance().typewordGetterByCate();
			
			KeywordCategoryVO kcvo = null;
			String catename = null;
			for(Entry<String, KeywordCategoryVO> entry:keywordCategoryMaps.entrySet()){
				int sum = 0;
				for(CategoryClickVO cvo:entry.getValue().getCcvo()){
					sum += cvo.getClick_nums();
					catename = CategoryMap.getInstance().getNameById(cvo.getCategoryId());
					if(!StringUtils.isBlank(catename)){
						cvo.setCategory_name(catename);
					}
				}
				int size = entry.getValue().getCcvo().size()>3?4:entry.getValue().getCcvo().size();
				for(int i=0;i<size;i++){
					entry.getValue().getCcvo().get(i).setRate(entry.getValue().getCcvo().get(i).getClick_nums()*100/sum);
					catename = entry.getValue().getCcvo().get(i).getCategory_name();
					if(!entry.getValue().getCategory_name_list().contains(InitAnalysis.PERSON)){
						if(i==0){
							if(entry.getValue().getCcvo().get(i).getRate()>rate1){
								if(!StringUtils.isBlank(catename))
									entry.getValue().getCategory_name_list().add(catename);
							}
						}
						if(i==1){
							if(entry.getValue().getCcvo().get(i).getRate()>rate2){
								if(!StringUtils.isBlank(entry.getValue().getCcvo().get(i-1).getCategory_name())&&!entry.getValue().getCategory_name_list().contains(entry.getValue().getCcvo().get(i-1).getCategory_name())){
									entry.getValue().getCategory_name_list().add(entry.getValue().getCcvo().get(i-1).getCategory_name());
								}
								entry.getValue().getCategory_name_list().add(entry.getValue().getCcvo().get(i).getCategory_name());
							}else{
								if(entry.getValue().getCcvo().get(i-1).getRate()-entry.getValue().getCcvo().get(i).getRate()>rate1_rate2){
									if(!StringUtils.isBlank(entry.getValue().getCcvo().get(i-1).getCategory_name())&&!entry.getValue().getCategory_name_list().contains(entry.getValue().getCcvo().get(i-1).getCategory_name())){
										entry.getValue().getCategory_name_list().add(entry.getValue().getCcvo().get(i-1).getCategory_name());
									}
								}
							}
						}
						if(i==2){
							if(entry.getValue().getCcvo().get(i).getRate()>rate3){
								if(!StringUtils.isBlank(entry.getValue().getCcvo().get(i-2).getCategory_name())&&!entry.getValue().getCategory_name_list().contains(entry.getValue().getCcvo().get(i-2).getCategory_name())){
									entry.getValue().getCategory_name_list().add(entry.getValue().getCcvo().get(i-2).getCategory_name());
								}
								if(!StringUtils.isBlank(entry.getValue().getCcvo().get(i-1).getCategory_name())&&!entry.getValue().getCategory_name_list().contains(entry.getValue().getCcvo().get(i-1).getCategory_name())){
									entry.getValue().getCategory_name_list().add(entry.getValue().getCcvo().get(i-1).getCategory_name());
								}
								if(!StringUtils.isBlank(catename))
									entry.getValue().getCategory_name_list().add(entry.getValue().getCcvo().get(i).getCategory_name());
							}
						}
						if(i==size-1){
							if(!StringUtils.isBlank(entry.getValue().getCcvo().get(0).getCategory_name())&&!entry.getValue().getCategory_name_list().contains(entry.getValue().getCcvo().get(0).getCategory_name())){
								entry.getValue().getCategory_name_list().add(entry.getValue().getCcvo().get(0).getCategory_name());
							}
						}
					}
				}
				if(entry.getValue().getCategory_name_list().size()<1){
					String merge_keyword = KeywordUtil.wordFilterTopword(entry.getKey());
					kcvo = InitAnalysis.getInstance().getKeywordCategoryMaps().get(merge_keyword);
					if(null!=kcvo){
						entry.getValue().setMerge(true);
						entry.getValue().setMerge_keyword(kcvo.getKeyword());
						if(kcvo.getCcvo().size()>1){
							if(kcvo.getCcvo().get(0).getRate()>merge_rate1)
								if(!StringUtils.isBlank(kcvo.getCcvo().get(0).getCategory_name()))
									entry.getValue().getCategory_name_list().add(kcvo.getCcvo().get(0).getCategory_name());
//								else{
//									catename = CategoryMap.getInstance().getNameById(kcvo.getCcvo().get(0).getCategoryId());
//									if(!StringUtils.isBlank(catename))
//										entry.getValue().getCategory_name_list().add(catename);
//								}
						}
					}
				}
				
				
//				if(movies.contains(entry.getKey())||teleplays.contains(entry.getKey())){
//					
//					if(movies.contains(entry.getKey())&&teleplays.contains(entry.getKey())){
//						List<String> l = new ArrayList<String>();
//						if(entry.getValue().getCategory_name_list().contains("电影")){
//							l.add("电影");
//							entry.getValue().setFromBaidu(true);
//						}
//						if(entry.getValue().getCategory_name_list().contains("电视剧")){
//							l.add("电视剧");
//							entry.getValue().setFromBaidu(true);
//						}
//						entry.getValue().getCategory_name_list().clear();
//						entry.getValue().getCategory_name_list().addAll(l);
//					}else{
//						entry.getValue().getCategory_name_list().clear();
//						if(movies.contains(entry.getKey())){
//							entry.getValue().getCategory_name_list().add("电影");
//							entry.getValue().setFromBaidu(true);
//						}
//						
//						if(teleplays.contains(entry.getKey())){
//							entry.getValue().getCategory_name_list().add("电视剧");
//							entry.getValue().setFromBaidu(true);
//						}
//					}
//				}
//				List<String> dblist = new ArrayList<String>();
//				for(Entry<Integer,Set<String>> dbentry:map.entrySet()){
//					if(dbentry.getValue().contains(entry.getKey())){
//						dblist.add(dbentry.getKey());
//					}
//				}
//				
//				if(dblist.size()>0){
//					entry.getValue().getCategory_name_list().clear();
//					entry.getValue().getCategory_name_list().addAll(dblist);
//				}
				
			}
			
			
		}
		
	} 
	
	static class LoadFromDatabaseTimerTask extends TimerTask {
		@Override
		public void run() {
			try {
				String date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
				if(null==instance){
					logger.info("TimerTask从数据库加载关键词点击信息和人物信息: 开始执行...");
					instance = getInstanceFromDataBase();
					logger.info("TimerTask从数据库加载关键词点击信息和人物信息: 执行完毕");
				}else if(MergeClickGetter.getInstance().existMergeClickData(date)){
					Map<String,KeywordCategoryVO> map = instance.getFreshKeywordCategoryMap(date);
					if(map.size()>50000){
						logger.info("TimerTask从数据库加载"+date+"关键词点击信息: 开始执行...");
						if(null!=instance.keywordCategoryMaps)
							instance.keywordCategoryMaps.clear();
						instance.setKeywordCategoryMaps(map);
						mapPrepare();
					}
					logger.info("TimerTask从数据库加载"+date+"关键词点击信息: 执行完毕");
				}else
					logger.info("点击数据太少，不加载");
			} catch (Exception e) {
				logger.error("TimerTask从数据库加载关键词点击信息: 执行发生异常", e);
			}
		}
	}
	
	static class CheckLoadFromDatabaseTimerTask extends TimerTask {
		@Override
		public void run() {
			try {
				if(null==instance){
					logger.info("TimerTask从数据库加载关键词点击信息和人物信息: 开始执行...");
					instance = getInstanceFromDataBase();
					logger.info("TimerTask从数据库加载关键词点击信息和人物信息: 执行完毕");
				}
			} catch (Exception e) {
				logger.error("TimerTask从数据库加载关键词点击信息: 执行发生异常", e);
			}
		}
	}
	
	static class LoadPersonFromDatabaseTimerTask extends TimerTask {
		@Override
		public void run() {
			try {
				if(null!=instance&&(null==instance.persons||instance.persons.size()<1)){
					logger.info("TimerTask从数据库加载人物信息: 开始执行...");
					instance.getPersonDataSet();
				}
				logger.info("TimerTask从数据库加载人物信息: 执行完毕");
			} catch (Exception e) {
				logger.error("TimerTask从数据库加载人物信息: 执行发生异常", e);
			}
		}
	}
}
