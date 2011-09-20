/**
 * 
 */
package com.youku.search.console.teleplay;

import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 *
 */
public class QueryResult {

	private int version;	//版本数
	private List<String> versionNames = new ArrayList<String>();  //版本名称
	private List<List<Video>> content = new ArrayList<List<Video>>(); //各版本内容集合
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public List<List<Video>> getContent() {
		return content;
	}
	public void putOneVersion(List<Video> oneVersion) {
		content.add(oneVersion);
	}
	public List<String> getVersionNames() {
		return versionNames;
	}
	public void putVersionName(String name) {
		versionNames.add(name);
	}
	
	
	

}
