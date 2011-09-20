package com.youku.search.console.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.search.config.Config;
import com.youku.search.console.config.time.BlacklistTimeTask;
import com.youku.search.console.config.time.CopyrightMusicTimeTask;
import com.youku.search.console.config.time.EpisodeTimeTask;
import com.youku.search.console.config.time.EpisodeVideoInfoMailTimer;
import com.youku.search.console.config.time.EpisodeVideoStatusTimeTask;
import com.youku.search.console.config.time.EpisodeVideoTimeTask;
import com.youku.search.console.config.time.IndexOperateTask;
import com.youku.search.console.config.time.LogoEpisodeCollectionFixTimer;
import com.youku.search.console.config.time.ReverseLatestTimeTask;
import com.youku.search.console.config.time.ReverseTimeTask;
import com.youku.search.console.config.time.TaskOfInsertCache;
import com.youku.search.console.config.time.TeleplaySpideTimeTask;
import com.youku.search.console.config.time.TeleplayTimeTask;
import com.youku.search.console.config.time.TeleplayValidTimeTask;
import com.youku.search.console.config.time.DailyUnionTimeTask;
import com.youku.search.console.config.time.TeleplayVersionTimeTask;
import com.youku.search.console.config.time.WebSiteTimeTask;
import com.youku.search.console.operate.Channel;
import com.youku.search.index.server.ServerManager;
import com.youku.search.log.LogFactory;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		String root = getServletContext().getRealPath("/");
		String cfgFilePath = root + config.getInitParameter("config_home");

		String configPath = root + config.getInitParameter("torque");
		
		// 初始化Torque
		try {
			Torque.init(configPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 //获取配置文件
        try {
			Config.init(cfgFilePath+"config.xml");
			//com.youku.search.index.server.ServerManager.init(cfgFilePath+"index-servers.xml");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 初始化index
		try {
			ServerManager.init(cfgFilePath + "index-servers.xml");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		try {
			Constants.init(config);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		MemcachedInit.getInstance().init("memcached.properties");
		
		//启动log4j
		try {
			DOMConfigurator.configure(root + config.getInitParameter("config_home") + "log4j.xml");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
        
        //初始化日志配置信息
        LogFactory.init(config);
        
        //
        Channel.listInit();
        
      //启动定时器
		ServletContextEvent event = new ServletContextEvent(config
				.getServletContext());
		//连接池和memcache状态检测
//		new TaskManager().contextInitialized(event);
		//定时推荐
		new TaskOfInsertCache().contextInitialized(event);
		//索引操作结果检测并重复
		new IndexOperateTask().contextInitialized(event);
		//连续剧定时剧集分析
		new TeleplayTimeTask().contextInitialized(event);//0.4点
		//定时单个剧集搜索并入库
		new EpisodeVideoTimeTask().contextInitialized(event);//1点
		//定时单个剧集状态更新
		new EpisodeTimeTask().contextInitialized(event);//3.2点
		//连续剧定时状态更新
		new TeleplayValidTimeTask().contextInitialized(event);//0.2点
		//每日各种搜索统计数据
		new DailyUnionTimeTask().contextInitialized(event);//3点
		//删除不正确的blacklist,feedback,episode_log,teleplay_spide数据
		new BlacklistTimeTask().contextInitialized(event);//4点
		//定时单个剧集接口调用
		new EpisodeVideoStatusTimeTask().contextInitialized(event);//4点
		//电视剧抓取
		new TeleplaySpideTimeTask().contextInitialized(event);//0点
		
		//每周电视剧版本抓取
		new TeleplayVersionTimeTask().contextInitialized(event);//每周一5点
		
		//根据file_id添加episode_video数据
		new ReverseTimeTask().contextInitialized(event);//4点
		
		//根据file_id添加episode_video数据
		new ReverseLatestTimeTask().contextInitialized(event);//5点
		
		new WebSiteTimeTask().contextInitialized(event);//3点
		
//		new KeywordTimeTask().contextInitialized(event);//5点
		
		new LogoEpisodeCollectionFixTimer().start();  //每2小时
		
		new EpisodeVideoInfoMailTimer().start();  //5点
		
		new CopyrightMusicTimeTask().contextInitialized(event);
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
