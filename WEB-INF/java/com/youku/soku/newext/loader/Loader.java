package com.youku.soku.newext.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.newext.info.ExtInfo;

/**
 * 总加载器
 */
public class Loader {

	Log logger = LogFactory.getLog(getClass());

	AliasInfoLoader aliasLoader=new AliasInfoLoader();
	
	/*TeleplayLoader teleplayLoader = new TeleplayLoader();
	MovieLoader movieLoader = new MovieLoader();
	VarietyLoader varietyLoader = new VarietyLoader();
	AnimeLoader animeLoader = new AnimeLoader();*/
	
	ProgrammeLoader porgrammeLoader = new ProgrammeLoader();

	public void load(ExtInfo extInfo) throws Exception {

//		 1 加载数据; 加载关联数据（part 1）
//		 2 加载关联的人、角色数据
//		 3 整理关联数据（part 2）
		
		
//		填充AliasInfo 中的 Map<Programme,List<ProgrammeSite>>  programme_programmeSite
		
		MiddTierResourceBuilder middTierResourceBuilder = new MiddTierResourceBuilder();
		middTierResourceBuilder.init();

		aliasLoader.load(extInfo.aliasInfo, middTierResourceBuilder);
		porgrammeLoader.load(extInfo.movieInfo,extInfo.personInfo, middTierResourceBuilder);
		porgrammeLoader.load(extInfo.teleplayInfo,extInfo.personInfo, middTierResourceBuilder);
		porgrammeLoader.load(extInfo.varietyInfo,extInfo.personInfo, middTierResourceBuilder);
		porgrammeLoader.load(extInfo.animeInfo,extInfo.personInfo, middTierResourceBuilder);
		
		porgrammeLoader.load(extInfo.documentaryInfo, extInfo.personInfo, middTierResourceBuilder);
		
//		对人物的电影电视剧列表排序
		extInfo.personInfo.sortPersonProgramme(extInfo.aliasInfo);

	}

}
