package com.youku.search.index.server;

import java.net.InetSocketAddress;

import com.youku.search.index.server.ChangeServer.AccessControl;
import com.youku.search.sort.Parameter;

/**
 * Video服务器数组的包装类
 * 
 * @author gaosong
 *
 */
public final class ServerWrapper {

	private final boolean isDynamic;
	
	private final InetSocketAddress[] addresses;
	
	private final AccessControl accessControl;
	
	private final int serverNum;
	
	public ServerWrapper(Parameter p, InetSocketAddress[] addresses, AccessControl accessControl) {
		if (null == addresses || addresses.length == 0) {
			throw new IllegalArgumentException("传进来的addresses为空，p.type=" + p.type);
		}
		this.addresses = addresses;
		this.serverNum = this.addresses.length;
		
		this.accessControl = accessControl;
		if (null == accessControl) {
			this.isDynamic = false;
		} else {
			this.isDynamic = true;
		}
	}
	
	public ServerWrapper(Parameter p, InetSocketAddress[] addresses) {
		this(p, addresses, null);
	}

	public InetSocketAddress[] getAddresses() {
		return addresses;
	}
	
	public AccessControl getAccessControl() {
		return accessControl;
	}

	public boolean isDynamic() {
		return isDynamic;
	}

	public int getServerNum() {
		return serverNum;
	}
	
}
