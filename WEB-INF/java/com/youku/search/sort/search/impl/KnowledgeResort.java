package com.youku.search.sort.search.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.core.entity.Page;

public class KnowledgeResort<T extends Serializable> {
	
	private List<T> result = null;
	
	private List<T> sourceList = null ;
	private Page frontPage = null;
	
	public KnowledgeResort(List<T> list,Page frontPage){
		this.sourceList = list;
		this.frontPage = frontPage;
	}
	
	public List<T> getResult(){
		if (result == null)
			result = resort();
		
		return result;
	}
	
	private  List<T> resort() {
		
		HashMap<Integer, LinkedList<T>> map = groupByCate(sourceList);
		
		PageList<T> pageList = new PageList<T>(map,frontPage);
		
		return pageList.getList();
	}
	
	private HashMap<Integer,LinkedList<T>> groupByCate(List<T> list){
		HashMap<Integer,LinkedList<T>> map = new HashMap<Integer,LinkedList<T>>();
		
		for (Iterator<T> it = list.iterator();it.hasNext();)
		{
			Video video = (Video)it.next();
			if (CateScaleRule.isCollected(video.cate_ids))
			{
				LinkedList<T> cateList = map.get(video.cate_ids);
				if (cateList != null){
					cateList.add((T)video);
				}
				else{
					cateList = new LinkedList<T>();
					cateList.add((T)video);
					map.put(video.cate_ids, cateList);
				}
				
				it.remove();
			}
		}
		
		return map;
	}
	

	
}
class PageList<T>{
	int totalPage = 0;
	int pageSize = 0;
	
	HashMap<Integer,LinkedList<T>> groupsMap = null;
	
	
	PageList(HashMap<Integer,LinkedList<T>> groupsMap,Page frontPage){
		totalPage = frontPage.page_no;
		pageSize = frontPage.page_size;
		this.groupsMap = groupsMap;
	}
	
	public List<T> getList(){
		List<T> result = new LinkedList<T>();
		for (int i = 0 ; i < totalPage ; i++){
			CurPage<T> curPage = new CurPage<T>(groupsMap,pageSize);
			
			List<T> list = curPage.getList();
			
			result.addAll(list);
		}
		
		return result;
	}
	
}

class CurPage<T>{
	protected Log logger = LogFactory.getLog(getClass());
	
	int pageSize = 0;
	
	LinkedHashMap<Integer,List<T>> map = new LinkedHashMap<Integer,List<T>>(pageSize);
	CurPage(HashMap<Integer,LinkedList<T>> groupsMap,int pageSize){
		this.pageSize = pageSize;
		collect(groupsMap);
	}
	
	
	void collect(HashMap<Integer,LinkedList<T>> groupsMap){
		Set<Integer> cates = CateScaleRule.cateMap.keySet();
		
			for (Iterator<Integer> it = cates.iterator();it.hasNext();){
				int cate = it.next();
				
				int count = CateScaleRule.cateMap.get(cate);
				logger.debug("开始查找分类:"+cate + "\t 个数:"+count);
				
				LinkedList<T> videos = groupsMap.get(cate);
				
				List<T> newList = map.get(cate);
				if (newList == null){
					newList = new ArrayList<T>(); 
					map.put(cate, newList);
				}
				
				for (int i = 0 ;i < count ;i ++)
				{
					if (videos != null && videos.size() > 0){
						T v = videos.pop();
						logger.debug("添加分类:"+((Video)v).cate_ids);
						newList.add(v);
					}
					else{
						for (Iterator<Integer> itc = cates.iterator();itc.hasNext();){
							LinkedList<T> lastVideos  = groupsMap.get(itc.next());
							if (lastVideos != null && lastVideos .size() > 0){
								T v = lastVideos.pop();
								logger.debug("添加分类:"+((Video)v).cate_ids);
								newList.add(v);
								break;
								
							}
						}
					}
				}
				
			}
		
	}
	
	public List<T> getList(){
		List<T> result = new ArrayList<T>();
		for (Iterator<Integer> it = map.keySet().iterator();it.hasNext();){
			int cate = it.next();
			
			result.addAll(map.get(cate));
		}
		return result;
	}
}

 class CateScaleRule{
	static LinkedHashMap<Integer,Integer> cateMap = new LinkedHashMap<Integer,Integer>();
	static {
		cateMap.put(87, 8);//教育
		cateMap.put(90, 3);//母婴
		cateMap.put(92, 3);//原创
		cateMap.put(103, 3);//生活
		cateMap.put(105, 3);//科技
	}
	
	static int getMaxCount(int cate){
		return cateMap.get(cate);
	}
	
	static boolean isCollected(int cate){
		 return cateMap.containsKey(cate);
	 }
	
}

