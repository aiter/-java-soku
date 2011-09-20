package com.youku.search.sort.benchmark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.config.Config;
import com.youku.search.index.server.CServerManager;
import com.youku.search.index.server.ChangeServer;
import com.youku.search.index.server.ServerManager;
import com.youku.search.pool.memcache.MemcachedInit;
import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.search.LogInfo;
import com.youku.search.sort.search.LogInfo.Item;
import com.youku.search.sort.servlet.search_page.SearchHelper;
import com.youku.search.store.ContainerFactory;
import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil.EncodingSet;

public class MockVideoSearchClient {

	private Log logger = LogFactory.getLog(getClass());

	// ----------------- local config -----------------
	private static final String CONFIG_PATH_PREFIX = "conf" + File.separatorChar;
	private static final String LOG_FILE_PATH_PREFIX = "log" + File.separatorChar + "mockSearch.";

	// ----------------- online config -----------------
//	private static final String CONFIG_PATH_PREFIX = "target" + File.separatorChar + "conf" + File.separatorChar;
//	private static final String LOG_FILE_PATH_PREFIX = "/opt/logs/mockSearch.";

	private static File logFile;
	
	private static final String DEFAULT_QUERY_FILE_PATH = "lib" + File.separatorChar + "queryData2.txt";
	
	public static void main(String[] args) throws ConfigurationException, DocumentException {
		if (args.length == 0) {
			throw new IllegalArgumentException(
					"参数错误，参数格式: maxQueryCount threadCount mockKeyword");
		}

		DOMConfigurator.configure(CONFIG_PATH_PREFIX + "log4j-test.xml");

		int maxQueryCount = Integer.parseInt(args[0]);
		int threadCount = Integer.parseInt(args[1]);
		String mockKeyword = (args.length > 2) ? args[2] : null;
		logFile = new File(LOG_FILE_PATH_PREFIX
				+ DataFormat.formatDate(new Date(),
						DataFormat.FMT_DATE_YYYY_MM_DD_HH) + ".log");

		QueryStat.I.isStat.set(true);

		MockVideoSearchClient client = new MockVideoSearchClient(threadCount);
		client.fire(threadCount, maxQueryCount, mockKeyword);
	}

	private AtomicBoolean isFinished = new AtomicBoolean(false);
	
	private final MockQueryBuilder mockQueryBuilder;

	public MockVideoSearchClient(int threadCount) throws ConfigurationException, DocumentException {
		mockQueryBuilder = new MockQueryBuilder(DEFAULT_QUERY_FILE_PATH, EncodingSet.UTF8);
		
		Config.init(CONFIG_PATH_PREFIX + "config.xml");
		ServerManager.init(CONFIG_PATH_PREFIX + "index-servers.xml");
		
		MemcachedInit.getInstance().init(
				CONFIG_PATH_PREFIX + "memcached.properties");
		ContainerFactory.init(CONFIG_PATH_PREFIX + "mem_obj_store.properties");
		logger.info("------------------- 配置memcache ok!");
		logger.info("前端洗脸的type = " + Config.getResortType());
		
		CServerManager.init(CONFIG_PATH_PREFIX + "c-servers.xml");
		
		logger.info("------------------- 连接池初始化完毕");
		
		ChangeServer.I.init();
		logger.info("初始化切服工作");
		
		logger.info("*****************************************************");
		logger.info("******search system STARTED & " + (new Date()).toString()
				+ "******");
		logger.info("*****************************************************");
	}

	private void fire(int threadCount, int maxQueryCount, String mockKeyword) {
		logger.info("--------- 开始查询 ---------");
		Cost cost = new Cost();
		if (null != mockKeyword) {
			if (threadCount > 1) {
				multiThreadSingleQuery(maxQueryCount, threadCount, mockKeyword);
			} else {
				singleThreadSingleQuery(mockKeyword);
			}
		} else {
			logWriter();
			if (threadCount > 1) {
				multiThreadQuery(maxQueryCount, threadCount);
			} else {
				singleThreadQuery(maxQueryCount);
			}
		}
		cost.updateEnd();

		isFinished.set(true);
		int usedSeconds = (int) (cost.getCost() / 1000);
		int totalQueryCount = -1;
		logger.info("--------- 总请求次数=" + totalQueryCount + "; 成功请求次数="
				+ QueryStat.I.successRequestCount.get() + "; 查询结果总数="
				+ QueryStat.I.totalResultCount.get() + "; 总用时=" + usedSeconds
				+ "秒; 每秒请求数=" + QueryStat.I.successRequestCount.get()
				/ (usedSeconds == 0 ? 1 : usedSeconds));
	}

	private void singleThreadQuery(int maxQueryCount) {
		JSONObject jsonObject = null;
		for (int i = 0; i < maxQueryCount; i++) {
			try {
				jsonObject = query(i + 1);
				QueryStat.I.addTotalResultCount(jsonObject.getInt("total"));
			} catch (Exception e) {
				logger.error("--------- 查询异常", e);
			}
		}
	}
	
	private void multiThreadSingleQuery(int maxQueryCount, int threadCount, String mockKeyword) {
		multiThread(maxQueryCount, threadCount, mockKeyword);
	}

	private void singleThreadSingleQuery(String mockKeyword) {
		JSONObject jsonObject = null;
		try {
			jsonObject = query(mockKeyword);
			QueryStat.I.addTotalResultCount(jsonObject.getInt("total"));
		} catch (Exception e) {
			logger.error("--------- 查询异常", e);
		}
	}
	
	private void multiThread(final int maxQueryCount, final int threadCount, final String queryString){
		// 多线程mock的线程池
		final ThreadPoolExecutor mockExecutor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(threadCount);
		mockExecutor.prestartAllCoreThreads();

		// 结果数据缓存队列
		final BlockingQueue<Future<JSONObject>> resultQueue = new LinkedBlockingQueue<Future<JSONObject>>();

		// 消费单线程，用来消费结果数据
		ExecutorService consumerThread = Executors.newSingleThreadExecutor();
		consumerThread.execute(new Runnable() {
			@Override
			public void run() {
				while (!resultQueue.isEmpty() || !mockExecutor.isTerminated()) {
					Future<JSONObject> producerResult;
					try {
						producerResult = resultQueue.poll(500L,
								TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
						continue;
					}

					JSONObject jsonObject = null;
					try {
						if (null != producerResult) {
							jsonObject = producerResult.get();
							QueryStat.I.addTotalResultCount(jsonObject
									.getInt("total"));
						}
					} catch (Exception e) {
						logger.error("--------- 查询异常", e);
					}
				}
			}
		});
		logger.info("--------- 消费者单线程初始化完毕");

		// 生产者线程池，用来发起查询请求并且生成结果数据
		for (int i = 0; i < maxQueryCount; i++) {
			final int currentQueryCount = i + 1;
			Future<JSONObject> resultFuture = mockExecutor
					.submit(new Callable<JSONObject>() {
						@Override
						public JSONObject call() throws Exception {
							JSONObject jsonObject = null;
							if (null == queryString) {
								jsonObject = query(currentQueryCount);
							} else {
								jsonObject = query(queryString);
							}
							return jsonObject;
						}
					});

			try {
				resultQueue.put(resultFuture);
			} catch (Exception e) {
				logger.error("--------- 查询异常", e);
			}

			// 生产者过快生产会导致内存过大频繁MinorGC，所以等一下
			while (resultQueue.size() > 1000) {
				try {
					Thread.currentThread().sleep(200);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		logger.info("--------- 生产者线程池初始化完毕，线程数：" + threadCount + "，消费队列长度："
				+ resultQueue.size());

		mockExecutor.shutdown();
		while(true){
			if (mockExecutor.isTerminated()) {
				break;
			}
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("--------- 生产者线程池已经Terminated");

		consumerThread.shutdown();
		while (true) {
			if (consumerThread.isTerminated()) {
				break;
			}
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("--------- 消费者单线程已经Terminated");
	}

	private void multiThreadQuery(int maxQueryCount, int threadCount) {
		multiThread(maxQueryCount, threadCount, null);
	}

	private JSONObject query(int queryTimes)
			throws UnsupportedEncodingException, JSONException {
		Parameter p = this.mockQueryBuilder.buildMockParameter(String.valueOf(SearchConstant.VIDEO), "null");
//		Parameter p = this.mockQueryBuilder.buildMockParameter(String.valueOf(SearchConstant.VIDEOTAG), "null");
		JSONObject result = SearchHelper.search(p);

		logger.debug("------------------ 查询结果=\n" + result.toString(4));

		if (queryTimes % 100 == 0) {
			logger.info("--------- 查询次数=" + queryTimes);
		}

		return result;
	}
	
	private JSONObject query(String mockKeyword)
			throws UnsupportedEncodingException, JSONException {
		
		Parameter p = this.mockQueryBuilder.buildMockParameter(mockKeyword, "createtime", 0);
//		Parameter p = this.mockQueryBuilder.buildMockParameter(String.valueOf(SearchConstant.VIDEOTAG), mockKeyword, "null");
//		Parameter p = this.mockQueryBuilder.buildMockParameter(String.valueOf(SearchConstant.VIDEO_MD5), mockKeyword, "null");
//		Parameter p = this.mockQueryBuilder.buildMockParameter(String.valueOf(SearchConstant.VIDEOTAG), mockKeyword, "null");
		JSONObject result = SearchHelper.search(p);
		
		logger.debug("------------------ 查询结果=\n" + result.toString(4));

		return result;
	}

	private void logWriter() {
		Thread logThread = new Thread(new Runnable() {
			@Override
			public void run() {

				PrintWriter p = null;
				try {
					p = new PrintWriter(new FileWriter(logFile, true), true);
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}

				p.println("*****************************************************");
				p.println("****** mock search log STARTED & " + (new Date()).toString() + "******");
				p.println("*****************************************************");

				while (!isFinished.get()) {
					try {
						writeLogInfo(p);
						writeJSONObject(p);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}

				p.println("*****************************************************");
				p.println("****** mock search log FINISHED & " + (new Date()).toString() + "******");
				p.println("*****************************************************");
				p.println("\n\n\n\n");
				IOUtils.closeQuietly(p);
			}

			private void writeLogInfo(PrintWriter p)
					throws InterruptedException {
				LogInfo logInfo = QueryStat.I.logInfoQueue.poll(5L,
						TimeUnit.MILLISECONDS);

				if (null != logInfo) {
					StringBuilder sb = new StringBuilder();

					sb.append(DataFormat.formatDate(new Date(),
							DataFormat.FMT_DATE_YYYYMMDD_HHMMSS));
					sb.append(" LOGINFO - ").append("|");
					sb.append("请求Query=" + logInfo.get(LogInfo.Item.query)).append("|");
					sb.append("是否miss=" + logInfo.get(LogInfo.Item.miss)).append("|");
					sb.append("总搜索结果=" + logInfo.get(LogInfo.Item.total_result)).append("|");
					sb.append("总用时=" + logInfo.get(LogInfo.Item.cost)).append("|");
					sb.append("others=" + logInfo.get(LogInfo.Item.others)).append("|");
					
					p.println(sb.toString());
				}
				
			}
			
			private void writeJSONObject(PrintWriter p)
					throws InterruptedException, JSONException {
				JSONObject json = QueryStat.I.jsonQueue.poll(5L,
						TimeUnit.MILLISECONDS);

				if (null != json) {
					JSONObject items = json.optJSONObject("items");
					if (null != items && items.length() > 0) {
						JSONArray videos = items.toJSONArray(items.names());
						StringBuilder sb = null;
						for (int i = 0; i < videos.length() - 1; i++) {
							JSONObject v1 = (JSONObject) videos.get(i);
							JSONObject v2 = (JSONObject) videos.get(i + 1);
							Date d1 = DataFormat.parseUtilDate(
									v1.getString("createtime"),
									DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
							Date d2 = DataFormat.parseUtilDate(
									v2.getString("createtime"),
									DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
							long timeDiff = d1.getTime() - d2.getTime();
							// 找到 下一个结果的时间 比 上一个结果的时间 要新，并且差值超过1天的数据并记录下来
							if (timeDiff < 0
									&& (int) (-timeDiff)
											/ (24 * 60 * 60 * 1000) > 1) {
								sb = new StringBuilder();

								sb.append(DataFormat.formatDate(new Date(),
										DataFormat.FMT_DATE_YYYYMMDD_HHMMSS));
								sb.append(" CREATETIMEERROR - \n");
								sb.append(v1.toString(4)).append("\n");
								sb.append(v2.toString(4));

								p.println(sb.toString());
							}
						}
					}
				}
			}

		});
		logThread.setDaemon(true);
		logThread.start();
	}

	public static final class QueryStat {
		public static QueryStat I = new QueryStat();

		private QueryStat() {
		}

		public AtomicBoolean isStat = new AtomicBoolean(false);
		private AtomicInteger totalResultCount = new AtomicInteger(0);
		private AtomicInteger successRequestCount = new AtomicInteger(0);
		private BlockingQueue<LogInfo> logInfoQueue = new LinkedBlockingQueue<LogInfo>();
		private BlockingQueue<JSONObject> jsonQueue = new LinkedBlockingQueue<JSONObject>();

		public void addSuccessRequestCount(int addCount) {
			if (isStat.get()) {
				successRequestCount.addAndGet(addCount);
			}
		}

		public void addTotalResultCount(int addCount) {
			if (isStat.get()) {
				totalResultCount.addAndGet(addCount);
			}
		}

		public void addLogInfo(LogInfo logInfo) {
			if (isStat.get()) {
				logInfoQueue.add(logInfo);
			}
		}

		public void addJSONObject(JSONObject jsonObject) {
			if (isStat.get()) {
				jsonQueue.add(jsonObject);
			}
		}
	}

}
