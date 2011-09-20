package com.youku.search.monitor.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.server.CServer;
import com.youku.search.index.server.CServerManager;
import com.youku.search.index.server.CServerPair;
import com.youku.search.index.server.ChangeServer;
import com.youku.search.index.server.ChangeServerException;
import com.youku.search.index.server.ServerHealthCheck;
import com.youku.search.index.server.ServerStateException;
import com.youku.search.monitor.Person;
import com.youku.search.monitor.SmsSender;
import com.youku.search.pool.net.util.Cost;
import com.youku.search.util.Constant;

/**
 * 监控StartUpdateService.dat文件，如果存在则读取WorkServer_0.dat文件进行切换 <br>
 * 监控内部的ChangeServer，如果切服cost过期则报警 <br>
 * 
 * @author gaosong
 */
public class ChangeServerMonitor extends TimerTask {

	private Log logger = LogFactory.getLog(Constant.LogCategory.ServerStateLog);
	
	private static long MAX_CHANGE_WAIT = 60 * 1000;	// 一分钟
	
	public static long TASK_PERIOD = 5 * 1000;	// 5 seconds
	
	public static long TASK_DELAY = 50 * 1000;	// 50 seconds
	
	private static final int HEALTH_CHECK_TIMES = 20;
	
	@Override
	public void run() {
		try {
			change();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void change(){
		boolean isExistStartFlag = CServerManager.startChangeFlagFile.exists();
		if (!isExistStartFlag) {
			logger.info("--- 监控切服，没有发现startFlag...");
			return;
		}
		
		Cost changeCost = new Cost();
		logger.info("--- 开始切服流程...");
		try {
			boolean isExistEndFlag = CServerManager.endChangeFlagFile.exists();
			if (isExistEndFlag && isExistStartFlag) {
				throw new ChangeServerException("Start标记和End标记不能同时存在！");
			}
			
			// 加载WorkServer_0.dat文件
			Collection<CServerPair> changeServerPairList = loadChangeList(CServerManager.changeListFile);
			
			// 健康检查
			List<CServer> serverList = CServerManager.transformServerPairList(changeServerPairList);
			boolean healthCheckResult = ServerHealthCheck.I.healthCheck(serverList, HEALTH_CHECK_TIMES);
			if (!healthCheckResult) {
				throw new ServerStateException("切服前健康检查发现有服务器宕机！！！");
			}
			
			// 切服
			ChangeServer.I.change(MAX_CHANGE_WAIT, changeServerPairList);
			
			// 切服成功，持久化xml文件
			CServerManager.save(changeServerPairList);
			
			// 写End文件
			boolean deleteOk = CServerManager.startChangeFlagFile.delete();
			if (deleteOk) {
				FileUtils.touch(CServerManager.endChangeFlagFile);
			} else {
				throw new ChangeServerException("无法删除endFlag文件");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
			// 短信报警
			try {
				SmsSender.I.send(e.getMessage(), Person.gaosong.getPhone(), Person.zhenghailong.getPhone());
			} catch (IOException e1) {
				logger.error(e1.getMessage(), e1);
			}
		}
		
		changeCost.updateEnd();
		logger.info("--- 完成切服流程，整个切服过程使用 " + (changeCost.getCost()/1000) + " 秒，"+
				"ChangeServer.change()使用 " + ChangeServer.I.getChangeCost() + " 毫秒");
	}
	
	/**
	 * 从WorkServer_0.dat文件中加载新的CServerPair，并且和当前服务的CServerPair进行合并，合并后的结果为新的切服服务器
	 * 
	 * @param changeListFile 切服文件
	 * @return 与线上服务合并后的切服列表（只读）
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ServerStateException 健康检查没有通过
	 */
	private Collection<CServerPair> loadChangeList(File changeListFile) throws FileNotFoundException, IOException, ServerStateException {
		
		Collection<CServerPair> onLineServers = ChangeServer.I.getCurrentAccessControl().getOnLineServerPairList();
		Map<String, CServerPair> serverIdMapForChange = new HashMap<String, CServerPair>();	// 和线上服务列表合并后的切服Map
		
		logger.info("--- 记录切服前的服务状态：\n" + CServerManager.getOnlineServerDescription(onLineServers));
		logger.info("--- 记录切服文件WorkServer_0.dat：\n" + IOUtils.toString(new FileReader(changeListFile)));
		
		List<String> changeList = IOUtils.readLines(new FileReader(changeListFile));
		for (String line : changeList) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			if (line.equalsIgnoreCase("end")) {
				break;
			}
			
			String[] splitLine = StringUtils.split(line, '\t');
			String serverId = splitLine[0];
			String majorIp = splitLine[1];
			String minorIp = splitLine[2];
			
			CServerPair serverPair = CServerManager.getServerPair(serverId, majorIp, minorIp);
			serverIdMapForChange.put(serverId, serverPair);
		}
		
		for (CServerPair serverPair : onLineServers) {
			// 如果包含此id则说明此ID是要被切换下来的
			if (serverIdMapForChange.containsKey(serverPair.getId())) {
				continue;
			}
			serverIdMapForChange.put(serverPair.getId(), serverPair);
		}
		
		return serverIdMapForChange.values();
	}

}
