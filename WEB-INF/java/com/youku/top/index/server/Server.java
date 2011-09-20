/**
 * 
 */
package com.youku.top.index.server;

/**
 * @author william
 *
 */
public class Server {
	private String ip ;
	private int group ;
	private int order;
	private int start;
	private int end;
	private int bak;
	private int poolport;
	private int manager;
	
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getPoolport() {
		return poolport;
	}
	public void setPoolport(int poolport) {
		this.poolport = poolport;
	}
	public int getManager() {
		return manager;
	}
	public void setManager(int manager) {
		this.manager = manager;
	}
	public int getBak() {
		return bak;
	}
	public void setBak(int bak) {
		this.bak = bak;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	
	
}
