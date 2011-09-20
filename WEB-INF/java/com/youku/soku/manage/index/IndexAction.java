package com.youku.soku.manage.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.service.HotwordService;
import com.youku.soku.manage.service.ItemService;
import com.youku.soku.manage.service.VideoService;
import com.youku.soku.manage.torque.Hotword;
import com.youku.soku.manage.torque.Item;
import com.youku.soku.manage.torque.Video;

public class IndexAction extends BaseActionSupport {
	
	
	public String execute() throws Exception{
		List<Item> itemList = ItemService.findIndexItem();
		List<Video> videoList = VideoService.findIndexVideo();
		List<Hotword> hotwordList = HotwordService.findIndexVideo();
		
		Map<String, List<Video>> videoMap = new HashMap<String, List<Video>>();
		for(Video video : videoList) {
			List<Video> aItemVideoList = videoMap.get(video.getItemId());
			if(aItemVideoList == null) {
				aItemVideoList = new ArrayList<Video>();
				videoMap.put(video.getItemId(), aItemVideoList);
			}
			aItemVideoList.add(video);			
		}
		setVideoMap(videoMap);
		
		Map<Integer, List<Hotword>> hotwordMap = new HashMap<Integer, List<Hotword>>();
		for(Hotword hotword : hotwordList) {
			List<Hotword> aItemHotwordList = hotwordMap.get(hotword.getItemId());
			if(aItemHotwordList == null) {
				aItemHotwordList = new ArrayList<Hotword>();
				hotwordMap.put(hotword.getItemId(), aItemHotwordList);
			}
			aItemHotwordList.add(hotword);
		}
		setHotwordMap(hotwordMap);
		setItemList(itemList);
		
		return SUCCESS;
		
	}
	
	private List<Item> itemList;
	
	private Map<String, List<Video>> videoMap;
	
	private Map<Integer, List<Hotword>> hotwordMap;

	public List<Item> getItemList() {
		return itemList;
	}

	public Map<String, List<Video>> getVideoMap() {
		return videoMap;
	}

	public void setVideoMap(Map<String, List<Video>> videoMap) {
		this.videoMap = videoMap;
	}

	public Map<Integer, List<Hotword>> getHotwordMap() {
		return hotwordMap;
	}

	public void setHotwordMap(Map<Integer, List<Hotword>> hotwordMap) {
		this.hotwordMap = hotwordMap;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}

	
	
	
	

}
