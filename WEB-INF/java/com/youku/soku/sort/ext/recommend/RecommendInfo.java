package com.youku.soku.sort.ext.recommend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.soku.index.entity.Video;

public class RecommendInfo {
	public int keywordCount;
	public int videoCount;
	public Map<String, List<Video>> info = new HashMap<String, List<Video>>();

	@Override
	public String toString() {
		return "关键词数: " + keywordCount + "; 实际有效关键词数: " + info.size()
				+ "; 视频数: " + videoCount;
	}
}
