package com.youku.top.topn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.topn.entity.KeywordComVO;
import com.youku.top.topn.entity.KeywordQueryVO;
import com.youku.top.topn.util.KeywordUtil;
import com.youku.top.topn.util.SeclectSort;

public class KeywordWeekPropare {
	
	static Logger logger = Logger.getLogger(KeywordWeekPropare.class);
	
	final String JISHU = " + 集数";
	final String QUANJI = " + 全集";
	final String YUZHONG = " + 语种";
	final String GAOQING = " + 高清/DVD";
	final String RIQI = " + 日期";
	final String ZHUYAN = " + 主演/嘉宾";
	final String DIANYING = " + 电影";
	final String QITA = " + 其它";
	
	final String regexp1 = "[^0-9][0-9]{1,3}([^0-9]|$)";
	final String regexp2 = "全集";
	final String regexp3 = "中文|中字|中文字幕|英文原声|中英文|中英双语|中英双字幕|双语字幕|国英|双语|国语|粤语|日语|韩语|汉语|无字幕|字幕";
	final String regexp4 = "DVD|高清|清晰|TV";
	final String regexp5 = "[0-9]{1,4}-[01]?[0-9]-[0123]?[0-9]|[0-9]{6,8}";
	final String regexp7 = "抢先看|美剧|日剧|韩剧|偶像剧|电影|电视";
	
	Pattern m1 = Pattern.compile(regexp1);
	Pattern m2 = Pattern.compile(regexp2);
	Pattern m3 = Pattern.compile(regexp3);
	Pattern m4 = Pattern.compile(regexp4);
	Pattern m5 = Pattern.compile(regexp5);
	Pattern m7 = Pattern.compile(regexp7);
	
	Set<String> top1WKeywords = null;
	SeclectSort ss = new SeclectSort();
	
	private String getCombination(int type,String keyword){
		switch(type){
		case 0: return keyword;
		case 1: return keyword+JISHU;
		case 2: return keyword+QUANJI;
		case 3: return keyword+YUZHONG;
		case 4: return keyword+GAOQING;
		case 5: return keyword+RIQI;
		case 6: return keyword+ZHUYAN;
		case 7: return keyword+DIANYING;
		case 8: return keyword+QITA;
		}
		return null;
	}
	
	public List<KeywordQueryVO> getUnsortTopKeywords(String uniondate){
		List<KeywordQueryVO> keywordList = Collections.synchronizedList(new LinkedList<KeywordQueryVO>());
		KeywordQueryVO kqo = null;
		
			if(StringUtils.isBlank(uniondate))
				return new ArrayList<KeywordQueryVO>();
			
			//前1w搜索数量最多关键词
			top1WKeywords = getTop1WKeywords(uniondate);
			
			//取得前两个词前800
			Set<String> keyset = getSubKeywords( uniondate);
			//根据前2个词取得所有符合的关键词
			List<String> keywordlist = getComKeywords(uniondate, keyset);
			//System.out.println(Arrays.toString(keywordlist.toArray(new String[]{})));
			
			//取得所有关键词统计数据
			Map<String,KeywordQueryVO> keywords = getAllKeywords(uniondate);
			
			
			int com = 8;
			for(String keyword:keywordlist){
				//System.out.println("keyword:"+keyword);
				int i=0;
				for(String k:keywords.keySet()){
					synchronized (k) {
					if(k.startsWith(keyword)){
						if(i==0){
							kqo = new KeywordQueryVO();
							kqo.setKeyword(keyword);
							keywordList.add(kqo);
						}
						i = i+1;
						if(k.equalsIgnoreCase(keyword)){
							com =0;
						}else if(m1.matcher(k).find()){
							com =1;
						}else if(m2.matcher(k).find()){
							com =2;
						}else if(m3.matcher(k).find()){
							com =3;
						}else if(m4.matcher(k).find()){
							com =4;
						}else if(m5.matcher(k).find()){
							com =5;
						}else if(m7.matcher(k).find()){
							com =7;
						}else {
							com =8;
						}
						mergeKeywordQueryVO(kqo, keywords.get(k),com);
						
						keywords.remove(k);
					}
					}
				}
			}
		return keywordList;
	}
	
	private void mergeKeywordQueryVO(KeywordQueryVO kqo,KeywordQueryVO temp,int com){
		
		kqo.setInsearchs(kqo.getInsearchs()+temp.getInsearchs());
		kqo.setOutsearchs(kqo.getOutsearchs()+temp.getOutsearchs());
		kqo.setOutclicks(kqo.getOutclicks()+temp.getOutclicks());
		kqo.setInsum(kqo.getInsum()+temp.getInsum());
		kqo.setOutsum(kqo.getOutsum()+temp.getOutsum());
		kqo.setMaxsearchs(Math.max(kqo.getInsearchs(), kqo.getOutsearchs()));
		KeywordComVO kc = kqo.getKeywords().get(com);
		if(null ==kc){
			kc = new KeywordComVO();
			kc.setKeyword(getCombination(com, kqo.getKeyword()));
			kqo.getKeywords().put(com, kc);
		}
		
		kc.setInsearchs(kc.getInsearchs()+temp.getInsearchs());
		kc.setOutsearchs(kc.getOutsearchs()+temp.getOutsearchs());
		kc.setOutclicks(kc.getOutclicks()+temp.getOutclicks());
		kc.setInsum(kc.getInsum()+temp.getInsum());
		kc.setOutsum(kc.getOutsum()+temp.getOutsum());
		kc.setMaxsearchs(Math.max(kc.getInsearchs(), kc.getOutsearchs()));
		
		if(0!=com){
			KeywordComVO kcvo =new KeywordComVO();
			kcvo.setKeyword(temp.getKeyword());
			kcvo.setInsearchs(temp.getInsearchs());
			kcvo.setOutsearchs(temp.getOutsearchs());
			kcvo.setOutclicks(temp.getOutclicks());
			kcvo.setInsum(temp.getInsum());
			kcvo.setOutsum(temp.getOutsum());
			kcvo.setMaxsearchs(Math.max(kcvo.getInsearchs(), kcvo.getOutsearchs()));
			if(temp.getInsearchs()>0)
				kcvo.setInresults((int)(temp.getInsum()/temp.getInsearchs()));
			
			if(kc.getInsearchs()!=0)
				kcvo.setUisrate(String.format("%.2f",100.0*kcvo.getInsearchs()/kc.getInsearchs()));
			else kcvo.setUisrate("-");
			if(kc.getOutsearchs()!=0)
				kcvo.setUosrate(String.format("%.2f",100.0*kcvo.getOutsearchs()/kc.getOutsearchs()));
			else kcvo.setUosrate("-");
			if(kc.getOutclicks()!=0)
				kcvo.setUocrate(String.format("%.2f",100.0*kcvo.getOutclicks()/kc.getOutclicks()));
			else kcvo.setUocrate("-");
			
			kc.getKeywords().add(kcvo);
			kc.setKeywords(ss.sortBySearchs(kc.getKeywords(), 3));
		}
	}
	
	private Map<String,KeywordQueryVO> getAllKeywords(String uniondate){
		KeywordQueryVO kqo=null;
		Map<String,KeywordQueryVO> keywordMap = new ConcurrentHashMap<String, KeywordQueryVO>();
		String sql="select keyword,insearchs,outsearchs,outclicks,insearchs*inresults insum,outsearchs*outresults outsum from  keyword_week_"+uniondate+ " ";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql);
		Iterator it = list.iterator();
		Map map = null;
		String keyword = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			keyword = (String) map.get("keyword");
			if(StringUtils.isBlank(keyword))continue;
			keyword = keyword.trim();
			kqo = keywordMap.get(keyword);
			if(null==kqo){
				kqo = new KeywordQueryVO();
				keywordMap.put(keyword, kqo);
				kqo.setKeyword(keyword);
				kqo.setInsearchs(DataFormat.parseInt(map.get("insearchs")));
				kqo.setOutsearchs(DataFormat.parseInt(map.get("outsearchs")));
				kqo.setOutclicks(DataFormat.parseInt(map.get("outclicks")));
				kqo.setInsum(DataFormat.parseLong(map.get("insum")));
				kqo.setOutsum(DataFormat.parseLong(map.get("outsum")));
				keywordMap.put(keyword, kqo);
			}else{
				kqo.setInsearchs(kqo.getInsearchs()+DataFormat.parseInt(map.get("insearchs")));
				kqo.setOutsearchs(kqo.getOutsearchs()+DataFormat.parseInt(map.get("outsearchs")));
				kqo.setOutclicks(kqo.getOutclicks()+DataFormat.parseInt(map.get("outclicks")));
				kqo.setInsum(kqo.getInsum()+DataFormat.parseLong(map.get("insum")));
				kqo.setOutsum(kqo.getOutsum()+DataFormat.parseLong(map.get("outsum")));
			}
		}
		return keywordMap;
	}
	
	private Set<String> getSubKeywords(String uniondate){
		Set<String> ks = new HashSet<String>();
		String sql="select substring(keyword,1,2) k from (select * from keyword_week_"+uniondate+ " order by maxsearchs desc) temp group by substring(keyword,1,2) order by sum(maxsearchs) desc limit 800";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql);
		Iterator it = list.iterator();
		Map map = null;
		String keyword = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			keyword = (String) map.get("k");
			if(StringUtils.isBlank(keyword))continue;
			keyword = KeywordUtil.filter(keyword);
			if(StringUtils.isBlank(keyword))continue;
			ks.add(keyword);
		}
		return ks;
	}
	
	private Set<String> getTop1WKeywords(String uniondate){
		Set<String> ks = new HashSet<String>();
		String sql="select keyword from keyword_week_"+uniondate+ " order by maxsearchs desc limit 10000";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql);
		Iterator it = list.iterator();
		Map map = null;
		String keyword = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			keyword = (String) map.get("keyword");
			if(StringUtils.isBlank(keyword))continue;
			keyword = KeywordUtil.filter(keyword);
			if(StringUtils.isBlank(keyword))continue;
			ks.add(keyword);
		}
		return ks;
	}
	
	private List<String> getComKeywords(String uniondate,Set<String> keyset){
		Set<String> kset = null;
		List<String> klist = new LinkedList<String>();
		Set<String> keywordset = new HashSet<String>();
		for(String k:keyset){
			kset = getComSingleKeywords(uniondate, k);
			for(String key:kset)
				keywordset.add(key);
		}
		for(String k:keywordset){
			if(k.length()>1)
				klist.add(k);
		}
		ss.sortByLength(klist);
		return klist;
	}
	
	private Set<String> getComSingleKeywords(String uniondate,String k){
		Set<String> ks = new HashSet<String>();
		String sql="select * from keyword_week_"+uniondate+ " where keyword like ? order by maxsearchs desc limit 700";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql, new Object[]{k+"%"}, new int[]{Types.VARCHAR});
		Iterator it = list.iterator();
		Map map = null;
		String keyword=null;
		String srcKeyword = null;
		int i=0;
		while(it.hasNext()){
			map = (Map)(it.next());
			srcKeyword = (String) map.get("keyword");
			if(StringUtils.isBlank(srcKeyword))continue;
			keyword = KeywordUtil.filter(srcKeyword);
			if(StringUtils.isBlank(keyword))continue;
			if(i!=0){
				if(null!=top1WKeywords&&top1WKeywords.contains(keyword))
					break;
			}
			ks.add(keyword);
			i = i+1;
		}
		return ks;
	}
	
}
