/**
 * 
 */
package com.youku.soku.index.server;

/**
 * @author william
 *
 */
public class Server {
	private String ip ;
	private int group ;
	private int poolport;
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPoolport() {
		return poolport;
	}
	public void setPoolport(int poolport) {
		this.poolport = poolport;
	}
	
	
	
	
}
