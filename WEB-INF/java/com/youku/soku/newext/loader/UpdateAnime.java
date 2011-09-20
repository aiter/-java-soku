package com.youku.soku.newext.loader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;

public class UpdateAnime {
	private static Log logger = LogFactory.getLog(UpdateAnime.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void doUpdate(Programme programme, AnimeInfo animeInfo, PersonInfo personInfo) {

		logger.debug("更新animeInfo ...添加programme:" + programme.getName());
		
		Set<String> programmeNameSet = new HashSet<String>();
		programmeNameSet.add(StringUtils.trimToEmpty(programme.getName().toLowerCase()));
		if(programme.getAlias()!=null && StringUtils.trimToEmpty(programme.getAlias()).length()>0){
			String[] tmpArr=StringUtils.trimToEmpty(programme.getAlias()).split("\\|");
			if(tmpArr!=null && tmpArr.length>0){
				for(String aliasName: tmpArr)
					programmeNameSet.add(StringUtils.trimToEmpty(aliasName.toLowerCase()));
			}
		}
		
		
        if(programmeNameSet==null || programmeNameSet.size()==0 ) return;
		//构造节目名/节目别名-->节目列表（列表中含一个或多个节目）  结构
		load(programme, animeInfo,programmeNameSet);

		//人物头像 和 人物-->节目列表（列表中含一个或多个节目）
		personInfo.addPerson(programme, animeInfo);

		//对name--programme_list的list排序
		for (String proName : programmeNameSet) {
			List<Programme> programmeList=animeInfo.name_programme.get(proName);
			if(programmeList==null || programmeList.size()<=0) continue;
			SortUtil.sortProgrammeByDate(programmeList, animeInfo.middMap);
		}
		logger.debug("更新animeInfo。。。 添加programme:" + programme.getName()+"  结束");
	}
	
	// 将某个programme 对应的List<ProgrammeSite>加入到 name_programme
	private static void load(Programme programme, AnimeInfo info, Set<String> programmeNameSet)
{
		if(programme==null || info==null) return;
		
		if(programmeNameSet==null || programmeNameSet.size()==0) return;
		
		for (String proName : programmeNameSet) {
			MiscUtil.putIfAbsent(info.name_programme, StringUtils
					.trimToEmpty(proName), new ArrayList<Programme>());
			
			if(!info.name_programme.get(StringUtils.trimToEmpty(proName)).contains(programme)){
				info.name_programme.get(StringUtils.trimToEmpty(proName))
				.add(programme);
				
				if(logger.isDebugEnabled()){
					logger.info("name_programme 加载："+proName);
				}
			}
			
			
			
		}
		

	}
}
