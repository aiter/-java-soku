package com.youku.search.drama;

import java.io.Serializable;
import java.util.List;

public class Version implements Serializable {

	private static final long serialVersionUID = 1L;

	private Drama drama;

	private String id; // 对集数过多的分段后也使用此字段，所以为string类型
	private String name;
	private String alias;
	private int cate;
	private int subcate;
	private int fixed;
	private int order;
	private String logo;
	private boolean reverse;
	private List<Episode> episodes;

	private int is_show;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getEpisodeCount() {
		return episodes == null ? 0 : episodes.size();
	}

	public List<Episode> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(List<Episode> episodes) {
		this.episodes = episodes;
	}

	public int getFixed() {
		return fixed;
	}

	public void setFixed(int fixed) {
		this.fixed = fixed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getSubcate() {
		return subcate;
	}

	public void setSubcate(int subcate) {
		this.subcate = subcate;
	}

	public int getIs_show() {
		return is_show;
	}

	public void setIs_show(int is_show) {
		this.is_show = is_show;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Drama getDrama() {
		return drama;
	}

	public void setDrama(Drama drama) {
		this.drama = drama;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

}
