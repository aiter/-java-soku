package com.youku.search.console.vo;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class LeftMenuVO {
	
	int id;
	String name;
	String url;
	String module;
	Set<LeftMenuVO> twoMenu=new LinkedHashSet<LeftMenuVO>();
	Set<String> modules=new HashSet<String>();
	
	
	public Set<String> getModules() {
		return modules;
	}
	public void setModules(Set<String> modules) {
		this.modules = modules;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public Set<LeftMenuVO> getTwoMenu() {
		return twoMenu;
	}
	public void setTwoMenu(Set<LeftMenuVO> twoMenu) {
		this.twoMenu = twoMenu;
	}
	
	public boolean equals(LeftMenuVO lmvo){
		if(this.id==lmvo.id)
			return true;
		else return false;
	}
	
	public String toString(){
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("[id:");
		sbuilder.append(id);
		sbuilder.append(",name:");
		sbuilder.append(name);
		sbuilder.append(",url:");
		sbuilder.append(url);
		sbuilder.append(",module:");
		sbuilder.append(module);
		if(null!=twoMenu&&twoMenu.size()>0){
			sbuilder.append(",twoMenu:");
			sbuilder.append(twoMenu.toString());
		}
		sbuilder.append("]");
		return sbuilder.toString();
	}
}
