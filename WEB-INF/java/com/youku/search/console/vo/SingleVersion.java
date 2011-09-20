package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.youku.search.console.operate.Channel;

public class SingleVersion {
	public SingleVersion() {
		super();
		setViewcatemap();
		setViewsubcatemap();
	}

	/** The value for the id field */
	int pid;

	/** The value for the fkTeleplayId field */
	int fkTeleplayId;

	/** The value for the cate field */
	int cate;

	/** The value for the subcate field */
	int subcate;

	/** The value for the name field */
	String versionname;

	/** The value for the alias field */
	String alias;

	/** The value for the orderId field */
	int orderId;

	/** The value for the episodeCount field */
	int total_Count;

	/** The value for the fixed field */
	int fixed = 0;
	
	int episode_count;
	String firstlogo;
	String viewname;
	int reverse = 0;
	String catestr;
	String subcatestr;
	String excludes;
	List<View> viewbeancate;
	HashMap<Integer, List<View>> viewbeansubcate;
	// 	
	HashMap<Integer, String> viewcatemap;
	HashMap<Integer, HashMap<Integer, String>> viewsubcatemap;
	
	public int getReverse() {
		return reverse;
	}

	public void setReverse(int reverse) {
		this.reverse = reverse;
	}

	public String getViewname() {
		return viewname;
	}

	public void setViewname(String viewname) {
		this.viewname = viewname;
	}

	public String getExcludes() {
		return excludes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public int getEpisode_count() {
		return episode_count;
	}

	public void setEpisode_count(int episode_count) {
		this.episode_count = episode_count;
	}

	public HashMap<Integer, List<View>> getViewbeansubcate() {
		return viewbeansubcate;
	}

	public void setViewbeansubcate(HashMap<Integer, List<View>> viewbeansubcate) {
		this.viewbeansubcate = viewbeansubcate;
	}

	public HashMap<Integer, String> getViewcatemap() {
		return viewcatemap;
	}

	public void setViewcatemap() {
		this.viewcatemap = Channel.cateMap;
	}

	public HashMap<Integer, HashMap<Integer, String>> getViewsubcatemap() {
		return viewsubcatemap;
	}

	public void setViewsubcatemap() {
		this.viewsubcatemap = Channel.subcateMap;
	}

	public void setCatestr() {
		this.catestr = Channel.cateMap.get(this.cate);
	}

	public void setSubcatestr() {
		this.subcatestr = Channel.subcateMap.get(this.cate).get(this.subcate);
	}

	public String getCatestr() {
		return this.catestr;
	}

	public String getSubcatestr() {
		return this.subcatestr;
	}

	public String getFirstlogo() {
		return firstlogo;
	}

	public void setFirstlogo(String firstlogo) {
		this.firstlogo = firstlogo;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getFkTeleplayId() {
		return fkTeleplayId;
	}

	public void setFkTeleplayId(int fkTeleplayId) {
		this.fkTeleplayId = fkTeleplayId;
	}

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getSubcate() {
		return subcate;
	}

	public void setSubcate(int subcate) {
		this.subcate = subcate;
	}

	public String getVersionname() {
		return versionname;
	}

	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getTotal_Count() {
		return total_Count;
	}

	public void setTotal_Count(int total_Count) {
		this.total_Count = total_Count;
	}

	public int getFixed() {
		return fixed;
	}

	public void setFixed(int fixed) {
		this.fixed = fixed;
	}

	class View {
		int viewcatecode;
		String viewcatename;

		public int getViewcatecode() {
			return viewcatecode;
		}

		public void setViewcatecode(int viewcatecode) {
			this.viewcatecode = viewcatecode;
		}

		public String getViewcatename() {
			return viewcatename;
		}

		public void setViewcatename(String viewcatename) {
			this.viewcatename = viewcatename;
		}
	}

	public List<View> getViewbeancate() {
		return viewbeancate;
	}

	public void setViewbeancate() {
		viewbeancate = new ArrayList<View>();
		View v = null;
		View sv;
		viewbeansubcate = new HashMap<Integer, List<View>>();
		for (Entry e : Channel.cateMap.entrySet()) {
			v = new View();
			int k = Integer.parseInt("" + e.getKey());
			v.setViewcatecode(k);
			v.setViewcatename("" + e.getValue());
			// System.out.println("key="+k+"====value="+e.getValue());
			List<View> lv = new ArrayList<View>();
			for (Entry se : Channel.subcateMap.get(k).entrySet()) {
				sv = new View();
				int key = Integer.parseInt("" + se.getKey());
				sv.setViewcatecode(key);
				sv.setViewcatename("" + se.getValue());
				lv.add(sv);
				// System.out.println("=========key="+key+"====value="+se.getValue());
			}
			viewbeansubcate.put(k, lv);
			viewbeancate.add(v);
		}

	}
}
