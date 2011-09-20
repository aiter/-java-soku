package com.youku.top.index.entity;

import java.util.HashSet;
import java.util.Set;

public class Item {
	String type="" ;
	String version_name="";
	boolean hasUrl = false;
	public Set<String> areas = new HashSet<String>();
	public Set<String> sub_types = new HashSet<String>();
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVersion_name() {
		return version_name;
	}
	public void setVersion_name(String versionName) {
		version_name = versionName;
	}
	public boolean isHasUrl() {
		return hasUrl;
	}
	public void setHasUrl(boolean hasUrl) {
		this.hasUrl = hasUrl;
	}
	public Set<String> getAreas() {
		return areas;
	}
	public void setAreas(Set<String> areas) {
		this.areas = areas;
	}
	public Set<String> getSub_types() {
		return sub_types;
	}
	public void setSub_types(Set<String> subTypes) {
		sub_types = subTypes;
	}
	
}
