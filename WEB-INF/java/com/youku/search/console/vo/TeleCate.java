package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.youku.search.console.operate.Channel;

public class TeleCate {
	
	public TeleCate(){
		super();
		setViewcatemap();
		setViewsubcatemap();
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("{catestr:");
		builder.append(catestr);
		builder.append(",");
		builder.append("subcatestr:");
		builder.append(subcatestr);
		builder.append("}");
		return builder.toString();
	}
	
	int cate;
	int subcate;
	String catestr;
	String subcatestr;
	List<View> viewbeancate;
	HashMap<Integer, List<View>> viewbeansubcate;
	// 	
	HashMap<Integer, String> viewcatemap;
	HashMap<Integer, HashMap<Integer, String>> viewsubcatemap;
	
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
	
	public class View {
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
		return this.viewbeancate;
	}

	public void setViewbeancate() {
		this.viewbeancate = new ArrayList<View>();
		View v = null;
		View sv;
		this.viewbeansubcate = new HashMap<Integer, List<View>>();
		for (Entry e : Channel.cateMap.entrySet()) {
			v = new View();
			int k = Integer.parseInt("" + e.getKey());
			v.setViewcatecode(k);
			v.setViewcatename("" + e.getValue());
//			 System.out.println("key="+k+"====value="+e.getValue());
			List<View> lv = new ArrayList<View>();
			for (Entry se : Channel.subcateMap.get(k).entrySet()) {
				sv = new View();
				int key = Integer.parseInt("" + se.getKey());
				sv.setViewcatecode(key);
				sv.setViewcatename("" + se.getValue());
				lv.add(sv);
				// System.out.println("=========key="+key+"====value="+se.getValue());
			}
			this.viewbeansubcate.put(k, lv);
			this.viewbeancate.add(v);
		}

	}
}
