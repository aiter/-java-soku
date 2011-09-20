package com.youku.soku.pos_analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.soku.pos_analysis.entity.CategoryClickVO;
import com.youku.soku.pos_analysis.entity.KeywordCategoryVO;
public class MergeClickGetter {
	
	private static Log logger = LogFactory.getLog(MergeClickGetter.class);
	private static MergeClickGetter instance=null;
	public static synchronized MergeClickGetter getInstance(){
		if(null!=instance) return instance;
		else {
			instance = new MergeClickGetter();
			return instance;
		} 
	}
	
	private boolean getMergeClickData(Map<String,List<CategoryClickVO>> ccvos,String sql){
		boolean flag = false;
		try {
			List<Record> records = BasePeer.executeQuery(sql,"youkuLog");
			List<CategoryClickVO> cos = null;
			CategoryClickVO co = null;
			String keyword = null;
			logger.info("取得点击日志数据,sql:"+sql);
			for(Record r:records){
				flag =true;
				keyword = r.getValue("keyword").asString();
				if(StringUtils.isBlank(keyword)) continue;
				keyword = keyword.toLowerCase();
				cos = ccvos.get(keyword);
				if(null==cos){
					cos = new ArrayList<CategoryClickVO>();
					ccvos.put(keyword, cos);
				}
				co = new CategoryClickVO();
				co.setCategoryId(r.getValue("category").asInt());
				co.setClick_nums(r.getValue("click_num").asInt());
				cos.add(co);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}
	
	public Map<String,List<CategoryClickVO>> getMergeClickData(String date){
		Map<String,List<CategoryClickVO>> ccvos = new HashMap<String, List<CategoryClickVO>>();
		int start = 0;
		int step = 50000;
		boolean hasnext = true;
		while(true){
			hasnext = getMergeClickData(ccvos,"select * from click_merge_"+date+" limit "+start+","+step);
			if(!hasnext)
				break;
			start +=step;
		}
		return ccvos;
	}
	
	public Map<String,KeywordCategoryVO> getKeywordCategoryVO(String date){
		Map<String,KeywordCategoryVO> map =new HashMap<String, KeywordCategoryVO>();
		Map<String,List<CategoryClickVO>> ccvos = getMergeClickData(date);
		KeywordCategoryVO kcvo = null;
		List<CategoryClickVO> cclist = null;
		for(Entry<String, List<CategoryClickVO>> entry:ccvos.entrySet()){
			kcvo = new KeywordCategoryVO();
			kcvo.setClick_date(date);
			kcvo.setKeyword(entry.getKey());
			cclist = entry.getValue();
			Collections.sort(cclist,new CategoryClickVODescComparator());
			kcvo.setCcvo(cclist);
			map.put(entry.getKey(), kcvo);
		}
		return map;
	}
	
	public boolean existMergeClickData(String date){
		boolean flag = false;
		try {
			List<Record> records = BasePeer.executeQuery("select count(*) as c from click_merge_"+date,"youkuLog");
			int c = 0;
			for(Record r:records){
				c = r.getValue("c").asInt();
				if(c>50000)
					flag =true;
			}
		} catch (Exception e) {
			logger.error("click_merge_"+date+" 的数据小于5万",e);
		}
		return flag;
	}
}
