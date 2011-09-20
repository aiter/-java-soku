package com.youku.search.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetUtil {

	static Log logger = LogFactory.getLog(NetUtil.class);

	public static String getFirstLocalIP(){
		String[] ips = getLocalIPs();
		if (null == ips || ips.length == 0) {
			return null;
		}
		
		return ips[0];
	}
	
	public static String[] getLocalIPs() {
		List<String> localIPs = new ArrayList<String>();

		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				if (ni.getName().equalsIgnoreCase("lo")) {
					continue;
				}

				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					String localIP = ips.nextElement().getHostAddress().trim();
					if (localIP.length() >= 7 && localIP.length() <=15) {
						localIPs.add(localIP);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return localIPs.toArray(new String[localIPs.size()]);
	}

}
