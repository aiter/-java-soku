/**
 * 
 */
package com.youku.soku.log;


/**
 * @author 1verge
 *
 */
public class AccessLog {
	
	private long time;
	private String cookie;
	private String ip;
	private String referer;
	private String pageurl;
	
	
	public long getTime() {
		return time;
	}


	public void setTime(long time) {
		this.time = time;
	}


	public String getCookie() {
		return cookie;
	}


	public void setCookie(String cookie) {
		this.cookie = cookie;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getReferer() {
		return referer;
	}


	public void setReferer(String referer) {
		this.referer = referer;
	}


	public String getPageurl() {
		return pageurl;
	}


	public void setPageurl(String pageurl) {
		this.pageurl = pageurl;
	}


	public String getContent() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(time).append("\t");
		builder.append(cookie).append("\t");
		builder.append(ip).append("\t");
		builder.append(referer).append("\t");
		builder.append(pageurl).append("\n");
		
		return builder.toString();
	}
	
	
	
}
