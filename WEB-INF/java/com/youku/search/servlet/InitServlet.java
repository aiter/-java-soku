package com.youku.search.servlet;

import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.dom4j.DocumentException;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.config.Config;
import com.youku.search.drama.timer.DramaAllInfoLoadTimer;
import com.youku.search.index.SearchManager;
import com.youku.search.index.schedule.IndexManagerTimer;
import com.youku.search.index.server.CServerManager;
import com.youku.search.index.server.ChangeServer;
import com.youku.search.log.LogFactory;
import com.youku.search.monitor.impl.ChangeServerMonitor;
import com.youku.search.pool.memcache.MemcachedInit;
import com.youku.search.pool.net.ServerManager;
import com.youku.search.recomend.CheckRecomendTask;
import com.youku.search.recomend.RecomendTask;
import com.youku.search.store.ContainerFactory;
import com.youku.soku.shield.DataLoadTimer;
import com.youku.soku.sort.word_match.timer.WordMatchLoaderTask;
import com.youku.soku.sort.word_match.timer.WordMatchLoaderYoukuTask;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContextEvent s = null;
		String root = getServletContext().getRealPath("/");
		String configPath = root + config.getInitParameter("torque");
		// System.setProperty ( "file.encoding", "utf-8" );
		// Constant.Home_Path = root;

		ServletContextEvent event = new ServletContextEvent(
				config.getServletContext());

		// 获取配置文件
		String cfgFilePath = root + config.getInitParameter("config_home");
		try {
			Config.init(cfgFilePath + "config.xml");
			com.youku.search.index.server.ServerManager.init(cfgFilePath + "index-servers.xml");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化Torque
		try {
			Torque.init(configPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Torque init ok!");

		// 配置memcache
		String memcachedPath = root + config.getInitParameter("memcached");
		MemcachedInit.getInstance().init(memcachedPath);
		System.out.println("配置memcache ok!");

		// 启动log4j
		try {
			DOMConfigurator.configure(root + config.getInitParameter("config_home") + "log4j.xml");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

		// 初始化日志配置信息
		LogFactory.init(config);

		System.out.println("server type = " + Config.getServerType());

		// 针对服务器类型加载
		switch (Config.getServerType()) {
		case 1:// 索引服务器
				// 配置分词
			AnalyzerManager.init();

			System.out.println("mina server start");
			ServerManager.getInstance();

			SearchManager.getInstance();
			break;
		case 2:
			// 屏蔽词加载
			new com.youku.soku.shield.DataLoadTimer().start();
			//字典加载
    		new WordMatchLoaderTask().start();
    		new WordMatchLoaderYoukuTask().start();
    		//加载大词文件到内? 2011.5.13 先注释掉
//    		new MajorTermLoaderTimerTask().start();

			// 加载存储StoreVideo的Memcached
			// 2011-06-01 gaosong
			ContainerFactory.init(cfgFilePath + "mem_obj_store.properties");
			
			System.out.println("前端洗脸的type = " + Config.getResortType());
			
			// 加载CServer配置
			try {
				CServerManager.init(cfgFilePath + "c-servers.xml");
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			System.out.println("配置c-servers.xml OK!");
			
			// 初始化切服类
			ChangeServer.I.init();
			
			// 初始化切服监听类
			ChangeServerMonitor changeServerMonitor = new ChangeServerMonitor();
			new Timer().schedule(changeServerMonitor, ChangeServerMonitor.TASK_DELAY, ChangeServerMonitor.TASK_PERIOD);
			
			break;
		case 3:// 索引管理服务器
				// 配置分词
			AnalyzerManager.init();

			new IndexManagerTimer().start();

			break;
		case 5:// 推荐服务器
				// 屏蔽词加载
				// 加载屏蔽词
			new DataLoadTimer().start();
			new RecomendTask().contextInitialized(event);
			new CheckRecomendTask().contextInitialized(event);
			break;
		case 6:// 剧集信息本机内存加载、memcahce加载
			new DramaAllInfoLoadTimer().start();
			break;
		}

		System.out
				.println("*****************************************************");
		System.out.println("******search system STARTED & "
				+ (new Date()).toString() + "******");
		System.out
				.println("*****************************************************");

	}

	public void destroy() {
		super.destroy();
		try {
			Torque.shutdown();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}
}
