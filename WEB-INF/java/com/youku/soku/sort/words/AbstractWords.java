package com.youku.soku.sort.words;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.config.Config;
import com.youku.soku.index.server.Server;
import com.youku.soku.index.server.ServerManager;
import com.youku.soku.sort.Parameter;

public abstract class AbstractWords<T> {

	protected Log logger = LogFactory.getLog(getClass());

	protected int searchType;

	protected int cache_seconds = 600;

	public String getCacheKey(String keyword) {
		return getClass().getName() + "_" + keyword;
	}

	public abstract T getWord(Parameter p);

	public InetSocketAddress[] getServers() {

		final int currentGroup = Config.getGroupNumber();

		List<Server> list = ServerManager.getWordServers(currentGroup);

		InetSocketAddress[] servers = new InetSocketAddress[list.size()];
		for (int i = 0; i < servers.length; i++) {
			Server server = list.get(i);

			servers[i] = new InetSocketAddress(server.getIp(), server
					.getPoolport());
		}

		return servers;
	}
}
