package com.youku.search.index.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.entity.Query;
import com.youku.search.monitor.Person;
import com.youku.search.monitor.SmsSender;
import com.youku.search.pool.net.ClientManager;
import com.youku.search.pool.net.Lock;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.ResultHolder;
import com.youku.search.pool.net.ResultHolderConstant;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.core.entity.Page;
import com.youku.search.util.Constant;

public class ServerHealthCheck {

	private final Log logger = LogFactory.getLog(Constant.LogCategory.ServerStateLog);

	public static final ServerHealthCheck I = new ServerHealthCheck();

	private final ExecutorService executor;
	
	/**
	 * 健康检查许可，如果其它线程正在进行健康检查，则不能取到许可
	 */
	private final Semaphore checkPermit = new Semaphore(1);
	
	private ServerHealthCheck() {
		executor = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * 异步健康检查，如果某台server尝试访问的失败次数等于checkTimes，则将它的isFall状态置为true，并且报警 <br>
	 * 
	 * @param servers
	 * @param checkTimes
	 */
	public void asyncHealthCheck(final List<CServer> servers, final int checkTimes){
		if (checkPermit.tryAcquire()) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						boolean healthCheckResult = healthCheck(servers, checkTimes);
						if (!healthCheckResult) {
							throw new ServerStateException("健康检查发现有服务器宕机！！！");
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						
						// 短信报警
						try {
							SmsSender.I.send(e.getMessage(), Person.gaosong.getPhone(), Person.zhenghailong.getPhone());
						} catch (IOException e1) {
							logger.error(e1.getMessage(), e1);
						}
					} finally {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						
						checkPermit.release();
					}
				}
			});
		}
	}
	
	/**
	 * 服务器健康检查，如果某台server尝试访问的失败次数等于checkTimes，则将它的isFall状态置为true，并且此方法返回false <br>
	 * 如果所有的server都通过健康检查，则返回true <br>
	 * 
	 * @param servers
	 * @param checkTimes
	 * @return
	 */
	public boolean healthCheck(List<CServer> servers, int checkTimes) {
		logger.info("---- 开始健康检查");
		LockQuery lockQuery = mockHealthCheckQuery(servers);
		int[] missArray = new int[servers.size()];
		for (int i = 0; i < missArray.length; i++) {
			missArray[i] = 0;
		}
		
		for (int i = 0; i < checkTimes; i++) {
			Lock lock = ClientManager.getInstance().query(lockQuery);
			MergedResult mergedResult = lock.getResultMerged();
			if (mergedResult.miss) {
				List<ResultHolder> resultList = lock.getResultHolderList();
				for (int j = 0; j < resultList.size(); j++) {
					com.youku.search.index.entity.Result r = resultList.get(j).result;
					if (ResultHolderConstant.isPrivateResult(r)) {
						missArray[j]++;
					}
				}
			}
		}
		
		int missCount = 0;
		for (int i = 0; i < missArray.length; i++) {
			CServer server = servers.get(i);
			if (missArray[i] == checkTimes) {
				server.setIsFall(true);
				logger.error("--- 健康检查serverIsFall,server=" + server.toString());
				missCount++;
			}
			// 以前健康检查发现宕机的，这次检查发现正常了，所以需要重置isFall状态
			else if (missArray[i] == 0 && server.getIsFall()){
				server.setIsFall(false);
				logger.info("--- 健康检查serverResume,server=" + server.toString());
			}
		}
		logger.info("--- 健康检查完毕，missCount="+missCount+", missArray=" + Arrays.toString(missArray));
		
		if (missCount == 0) {
			return true;
		} else {
			return false;
		}
	}

	private LockQuery mockHealthCheckQuery(List<CServer> servers) {
		Query query = new Query();
		query.start = 0;
		query.end = 48;
		query.field = SearchConstant.VIDEO;
		query.highlight = true;
		query.indexPage = new Page(1, 48);
		query.keywords = "SOKU TEST";
		query.orderFieldStr = "null";

		InetSocketAddress[] addresses = new InetSocketAddress[servers.size()];
		for (int i = 0; i < addresses.length; i++) {
			addresses[i] = CServerManager.getAddress(servers.get(i));
		}
		
		LockQuery lockQuery = new LockQuery(addresses, query, SearchConstant.VIDEO);
		
		return lockQuery;
	}
}
