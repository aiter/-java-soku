/**
 * 
 */
package com.youku.soku.library.load.autoSearch.handle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.library.Utils;
import com.youku.soku.library.load.form.ProgrammeSiteBo;

/**
 * @author liuyunjian
 * 2011-3-2
 */
public class AnimeHandler extends Handler {

	public AnimeHandler(ProgrammeSiteBo psBo) {
		super(psBo);
	}

	/* (non-Javadoc)
	 * 动漫的自动发现的总集数
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#getEpisodeTotal()
	 */
	@Override
	protected int getEpisodeTotal() {
		int total = super.getEpisodeTotal();
		if(total==0){
			if(psBo.episodeCollected>0){
				//如果已收录一部分，就再最后一集后面再搜索10集
				if(peBoList!=null && peBoList.size()>0){
//					total = peBoList.get(peBoList.size()-1).orderId+10;
					total = peBoList.get(peBoList.size()-1).orderStage+10;
				}else {
					total+= 10;
				}
			}else {
				total = 40; //没有搜索任何部分，最多搜索40集
			}
		}
		return total;
	}

	/* (non-Javadoc)
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#matchName(java.lang.String, int, java.lang.String)
	 */
	@Override
	protected boolean matchName(String title, int order, String version) {
		//处理。搜索词为：蓝龙 第一部    搜索结果名称为：(2个结果){蓝龙第一部25, 蓝龙 第一季 第25集}
		//处理。搜索词为：我的邻居山田君 1    搜索结果名称为：(2个结果){宫崎骏经典全集——我的邻居山田君01,我的邻居山田君1会飞的猪Q彡}
		String s = Utils.parse2Str(searchKeys);
		// String v = Utils.parse2Str(p.versions);
		StringBuilder keyword = new StringBuilder();

		if (!StringUtils.isBlank(s)) {
			keyword.append("[\\S\\s]*(").append(s).append(")");
		} else {
			return false;
		}

		keyword.append("[ ]*");

		if (!StringUtils.isBlank(version)) {
			keyword.append("(").append(version).append("|").append(Utils.analyzer(version)).append(")");
		}

		//动漫的集数后面，要么为空，要么后面有一个非数字
		keyword.append("[ ]*[第]*[ ]*[0]*" + order + "[ ]*[集话回]*(|[\\D]{1}[\\S\\s]*)");

		String t = Utils.formatTeleplayName(title);
		
		if(StringUtils.isBlank(t)) return false;
		t = Utils.stopWordsFilter(t);
		if(StringUtils.isBlank(t)) return false;
		
		if (t.matches(keyword.toString()))
			return true;
		if (Utils.analyzer(t).matches(keyword.toString()))
			return true;
		return false;
	}
	
	public boolean test(List<String> searchKeys,String title, int order, String version){
		this.searchKeys = searchKeys;
		return matchName(title, order, version);
	}
	
	public static void main(String[] args) {
		AnimeHandler handler = new AnimeHandler(null);
		List<String> searchKeys = new ArrayList<String>();
//		searchKeys.add("我的邻居山田君");
//		searchKeys.add("蓝龙 第一部");
		searchKeys.add("樱桃小丸子");
		String version = null;
		
		int order = 1;
//		int order = 25;
//		String [] titles = new String[]{"宫崎骏经典全集——我的邻居山田君01","我的邻居山田君1会飞的猪Q彡"};
//		String [] titles = new String[]{"蓝龙第一部25","蓝龙 第一季 第25集"};
		String [] titles = new String[]{"樱桃小丸子真人版01","蓝龙 第一季 第25集"};
		
		for (String title : titles) {
			boolean matched = handler.test(searchKeys, title, order, version);
			System.out.println(matched);
		}
		
//		System.out.println("宫崎骏经典全集——我的邻居山田君1dddd".matches("[\\S\\s]*(我的邻居山田君)[ ]*[ ]*[第]*[ ]*[0]*1[ ]*[集话回]*[\\D]?[\\S\\s]*"));
	}
}
