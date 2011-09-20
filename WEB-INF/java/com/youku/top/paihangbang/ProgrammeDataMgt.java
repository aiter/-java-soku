package com.youku.top.paihangbang;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.top.paihangbang.entity.TypeWord;
import com.youku.top.util.LogUtil;
import com.youku.top.util.TopWordType.WordType;

public class ProgrammeDataMgt {
	
	static Logger logger = Logger.getLogger(ProgrammeDataMgt.class);
	
	public static void main(String[] args){
		try {
			LogUtil.init(Level.INFO,"/opt/log_analyze/soku_top/log/log.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<TypeWord> typewords = TypeWordsMgt.getInstance().typewordGet();
		int proid = 0;
		logger.info("typewords size:"+(null!=typewords?typewords.size():0));
		Map<Integer,Map<String,Integer>> promap = ZhiDaQuGetter.getInstance().programmeIdNoVarietyGetter();
		logger.info("promap size:"+(null!=promap?promap.size():0));
		Map<Integer,Map<String,Integer>> seriesmap = ZhiDaQuGetter.getInstance().seriesIdGetter();
		logger.info("seriesmap size:"+(null!=seriesmap?seriesmap.size():0));
		for(TypeWord tw:typewords){
			proid = 0;
			if(tw.getCate() == WordType.综艺.getValue()){
				if(null!=seriesmap.get(tw.getCate()))
					proid = DataFormat.parseInt(seriesmap.get(tw.getCate()).get(tw.getKeyword().trim()));
			}else{
				if(null!=promap.get(tw.getCate()))
					proid = DataFormat.parseInt(promap.get(tw.getCate()).get(tw.getKeyword().trim()));
			}
			if(proid>0){
				TypeWordsMgt.getInstance().typeWordUpdateProgrammeId(tw.getId(), proid);
			}
		}
	}
	
}
