package com.youku.search.index.server;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.dom4j.DocumentException;

import com.youku.search.config.Config;
import com.youku.search.index.server.ChangeServer.AccessControl;
import com.youku.search.pool.net.util.Cost;

public class TestChangeServer {
	
	private static final String CONFIG_PATH_PREFIX = "conf" + File.separatorChar;
	
	public static void main(String[] args) throws InterruptedException, ConfigurationException, DocumentException {
		Config.init(CONFIG_PATH_PREFIX + "config.xml");
		ServerManager.init(CONFIG_PATH_PREFIX + "index-servers.xml");
		CServerManager.init(CONFIG_PATH_PREFIX + "c-servers.xml");
		
//		final AccessControl ac = new AccessControl(CServerManager.getInitOnlineServers());
//		
//		waitAndPrintResult(ac);
//
//		Thread t1 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				for (int i = 1; i <= 10000; i++) {
//					ac.startRequest();
//					if (i % 2500 == 0) {
//						waitAndPrintResult(ac);
//					}
//				}
//			}
//		});
//		t1.start();
//
//		Thread.currentThread().sleep(10000);
//
//		Thread t2 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				for (int i = 1; i <= 10000; i++) {
//					ac.endRequest();
//					if (i % 2500 == 0) {
//						waitAndPrintResult(ac);
//					}
//				}
//			}
//		});
//		t2.start();

	}

	/**
	 * for test
	 * 
	 * @param ac
	 */
	private static void waitAndPrintResult(final AccessControl ac) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"------------- currentThread=" + Thread.currentThread().getId()
						+ " -----------------").append('\n');
		sb.append("concurrentAccessCount=" + ac.getConcurrentAccessCount()).append('\n');
		Cost cost = new Cost();
		
		try {
			sb.append("wait for drain result=" + ac.waitDrain(1000)).append('\n');
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cost.updateEnd();
		sb.append("wait time=" + cost.getCost()).append('\n');
		System.out.println(sb.toString());
	}
}
