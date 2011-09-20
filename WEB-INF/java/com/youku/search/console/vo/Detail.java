package com.youku.search.console.vo;

import java.util.HashMap;
import java.util.Map;

public class Detail {
	private int port;
	private String ip;
	private Map<String,Integer> status=new HashMap<String, Integer>();
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Map<String, Integer> getStatus() {
		return status;
	}
	public void setStatus(Map<String, Integer> status) {
		this.status = status;
	}
	
	
}
