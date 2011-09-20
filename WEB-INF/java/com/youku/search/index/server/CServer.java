package com.youku.search.index.server;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 后端搜索服务器的配置信息
 * 
 * @author gaosong
 */
public final class CServer {

	private final int group;

	private final String id;

	private final String ip;

	private final int port;

	private final AtomicBoolean isFall = new AtomicBoolean(false);

	public CServer(int group, String id, String ip, int port) {
		this.group = group;
		this.id = id;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.group).append(this.id).append(this.ip).append(this.port).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		CServer rhs = (CServer) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(this.group, rhs.group).append(this.id, rhs.id)
				.append(this.ip, rhs.ip).append(this.port, rhs.port).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append("id=").append(id);
		sb.append(", address=").append(ip).append(':').append(port);
		sb.append(']');
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public int getGroup() {
		return group;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public boolean getIsFall() {
		return isFall.get();
	}

	public void setIsFall(boolean isFall) {
		this.isFall.getAndSet(isFall);
	}
}
