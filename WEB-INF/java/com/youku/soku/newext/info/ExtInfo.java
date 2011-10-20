package com.youku.soku.newext.info;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 电影、电视剧等内存Map信息
 */
public class ExtInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	Log logger = LogFactory.getLog(getClass());

	public AliasInfo aliasInfo=new AliasInfo();

	public TeleplayInfo teleplayInfo = new TeleplayInfo(aliasInfo);
	public MovieInfo movieInfo = new MovieInfo(aliasInfo);
	public VarietyInfo varietyInfo = new VarietyInfo(aliasInfo);
	public AnimeInfo animeInfo = new AnimeInfo(aliasInfo);
	public PersonInfo personInfo = new PersonInfo();
	
	public DocumentaryInfo documentaryInfo = new DocumentaryInfo(aliasInfo);
	
	public boolean isDestroy = false;
	

	public String info() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(aliasInfo.info());
		list.add(teleplayInfo.info());
		list.add(movieInfo.info());
		list.add(varietyInfo.info());
		list.add(animeInfo.info());
		list.add(personInfo.info());
		list.add(documentaryInfo.info());

		return list.toString();
	}
	
	public void destroy(){
		aliasInfo.destroy();
		movieInfo.destroy();
		teleplayInfo.destroy();
		varietyInfo.destroy();
		animeInfo.destroy();
		personInfo.destroy();
		isDestroy = true;
	}
}
