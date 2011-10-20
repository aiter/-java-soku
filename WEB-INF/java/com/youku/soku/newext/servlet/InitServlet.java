package com.youku.soku.newext.servlet;

/**
 * 系统初始化servlet
 */

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;

import com.youku.soku.config.Config;
import com.youku.soku.index.server.ServerManager;
import com.youku.soku.shield.DataLoadTimer;
import com.youku.soku.suggest.timer.LibraryDataLoadTimer;
import com.youku.soku.suggest.timer.TrieTreeLoaderTimer;

/**
 * @author 1verge
 * 
 */
public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 452412171859422478L;
	private static Log logger = LogFactory.getLog(InitServlet.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String root = getServletContext().getRealPath("/");
		
		 //获取配置文件
        String cfgFilePath = root + config.getInitParameter("config_home");
        try {
			Config.init(cfgFilePath+"config.xml");
			
			//加载索引server列表
			ServerManager.init(cfgFilePath+"index-servers.xml");
			System.out.println("init index-servers.xml");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化Torque
		try {
			Torque.init(root + config.getInitParameter("torque"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("torque 初始化失败！");
		}

		// 获取配置文件
//		String cfgFilePath = root + config.getInitParameter("config_home");

		// 配置log4j
		try {
			DOMConfigurator.configureAndWatch(root+ config.getInitParameter("log4j"));
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			logger.error("log4j 初始化失败！");
		}

//		 加载Ext文件到内存中
//		new FileLoaderAndSaver().load();
		
		new ExtTimerTask().start();
		
//		 启动定时更新任务
		new ExtUpdateTimerTask().start();
		
		
		new ExtUpdateRelatedShowTimerTask().start();

		System.out
				.println("*****************************************************");
		System.out.println("******Ext Search system STARTED & "
				+ (new Date()).toString() + "******");
		System.out
				.println("*****************************************************");

	}

	public void destroy() {
		super.destroy();
		try {
			Torque.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("destroy 失败！");

		}
	}

}
