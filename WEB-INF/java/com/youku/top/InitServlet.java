/**
 * 
 */
package com.youku.top;

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.search.log.LogFactory;
import com.youku.search.pool.memcache.MemcachedInit;
import com.youku.top.config.Config;
import com.youku.top.index.analyzer.AnalyzerManager;
import com.youku.top.index.server.ServerManager;
import com.youku.top.recomend.CheckRecomendTask;
import com.youku.top.recomend.RecomendTask;

public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 452412171859422478L;

	/* (non-Javadoc)
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String root = getServletContext().getRealPath("/");

        // 初始化Torque
        try {
            Torque.init(root+config.getInitParameter("torque"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //获取配置文件
        String cfgFilePath = root + config.getInitParameter("config_home");
        try {
			Config.init(cfgFilePath+"config.xml");
			
			//加载索引server列表
			ServerManager.init(cfgFilePath+"index-servers.xml");
//			System.out.println("init index-servers.xml");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("server type = "+Config.getServerType());
        //启动log4j
		try {
			DOMConfigurator.configureAndWatch(root + config.getInitParameter("config_home") + "log4j.xml");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
        
		//配置memcache
        String memcachedPath = root + config.getInitParameter("memcached");
        MemcachedInit.getInstance().init(memcachedPath);
        System.out.println("配置memcache ok!");
		
        AnalyzerManager.init();
        
        //初始化日志配置信息
        LogFactory.init(config);
        
        System.out.println("mina server start");
        ServletContextEvent event = new ServletContextEvent(config.getServletContext());
        //针对服务器类型加载
        switch(Config.getServerType())
        {
        	case Constant.ServerType.INDEX://日志索引服务器
        		//屏蔽词加载
        		new com.youku.soku.shield.DataLoadTimer().start();
        		
            	new RecomendTask().contextInitialized(event);
            	new CheckRecomendTask().contextInitialized(event);
        		break;
        	case Constant.ServerType.SORT:
        		break;
        	case Constant.ServerType.INDEX_MANAGER://索引管理服务器
            	break;
        	case Constant.ServerType.CONSOLE:
        		break;
        	case Constant.ServerType.COMMEND://搜索下拉推荐服务器
            	break;
        }
        
        System.out.println("*****************************************************");
        System.out.println("******search system STARTED & "+(new Date()).toString()+"******");
        System.out.println("*****************************************************");
        
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
