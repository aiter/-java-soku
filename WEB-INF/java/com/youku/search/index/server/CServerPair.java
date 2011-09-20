package com.youku.search.index.server;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 后端一对搜索服务器的配置信息（一主一从）
 * 
 * @author gaosong
 */
public final class CServerPair {

	private static Log logger = LogFactory.getLog(CServerPair.class);

	private final int group;

	private final String id;

	private final CServer[] subServers;

	public CServerPair(int group, String id, CServer majorServer,
			CServer minorServer) {
		this.group = group;
		this.id = id;
		this.subServers = new CServer[] { majorServer, minorServer };
	}

	public String toStringForLog() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append('\t');

		sb.append(getMajorServer().getIp());
		sb.append(':').append(getMajorServer().getPort());
		boolean majorIsFall = getMajorServer().getIsFall();
		sb.append('(').append((majorIsFall ? "OFF" : "on")).append(')').append('\t');

		sb.append(getMinorServer().getIp());
		sb.append(':').append(getMinorServer().getPort());
		boolean minorIsFall = getMinorServer().getIsFall();
		sb.append('(').append((minorIsFall ? "OFF" : "on")).append(')');

		return sb.toString();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getId() {
		return id;
	}

	public CServer getMajorServer() {
		return subServers[0];
	}

	public CServer getMinorServer() {
		return subServers[1];
	}

	public int getGroup() {
		return group;
	}
	
	/**
	 * 随机得到Pair中的一台服务器
	 * 
	 * @param random
	 * @return
	 */
	public CServer getRandomServer(int random) {
		return subServers[random];
	}
	
}
