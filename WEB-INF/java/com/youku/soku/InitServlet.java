/**
 * 
 */
package com.youku.soku;

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.search.log.LogFactory;
import com.youku.search.pool.memcache.MemcachedInit;
import com.youku.search.store.pic.MemCachedPic;
import com.youku.soku.config.Config;
import com.youku.soku.config.ExtServerConfig;
import com.youku.soku.index.analyzer.AnalyzerManager;
import com.youku.soku.index.query.VideoQueryManager;
import com.youku.soku.index.server.ServerManager;
import com.youku.soku.index.timer.SiteTimer;
import com.youku.soku.library.load.timer.ForwardWordTimer;
import com.youku.soku.manage.admin.copyright.util.CopyrightSpiderTimer;
import com.youku.soku.manage.timer.DeadLinkCheckTimer;
import com.youku.soku.manage.timer.EpisodeProgrammeIdSynTimer;
import com.youku.soku.manage.timer.IndexPicGeneratorTimer;
import com.youku.soku.manage.timer.IndexPicOnlineTimer;
import com.youku.soku.manage.timer.ProgrammeAuditFlagFixTimer;
import com.youku.soku.manage.timer.ProgrammeSearchNumberTimer;
import com.youku.soku.manage.timer.ProgrammeSiteCompleteMaintainTimer;
import com.youku.soku.manage.timer.ProgrammeSiteHdSynTimer;
import com.youku.soku.manage.timer.ShieldMailTimer;
import com.youku.soku.manage.timer.Top100ReportMailTimer;
import com.youku.soku.netspeed.NetSpeedTask;
import com.youku.soku.shield.DataLoadTimer;
import com.youku.soku.sort.ext.recommend.RecommendLoaderTimer;
import com.youku.soku.sort.word_match.timer.WordMatchLoaderTask;
import com.youku.soku.suggest.timer.LibraryDataLoadTimer;
import com.youku.soku.suggest.timer.TrieTreeLoaderTimer;
import com.youku.soku.top.directory.TopDateManager;
import com.youku.soku.util.Constant;

/**
 * @author 1verge
 *
 */
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
			System.out.println("init index-servers.xml");
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
        
      //加载站点信息
        new SiteTimer().start();
		
		
        //针对服务器类型加载
        switch(Config.getServerType())
        {
        	case Constant.ServerType.INDEX://索引服务器
        		AnalyzerManager.init();
        		VideoQueryManager.getInstance();
                com.youku.soku.sort.server.ServerManager.getInstance();
                new LibraryDataLoadTimer().start();
                new TrieTreeLoaderTimer().start();
        		break;
        	case Constant.ServerType.SORT:
        		//TODO  pic-memcached
        		String memcachedPicPath = root + config.getInitParameter("memcached-pic");
        		MemCachedPic.init(memcachedPicPath);
    	        System.out.println("配置memcache-pic ok!");
    	        
    	        //2011.5.9 luwei 先注释掉知识
//    	        new KnowledgeDataLoadTimer().start();
    	        
    	        //跳转词定时加载
    	        new ForwardWordTimer().start();
    	        
        		//start search words filter
        		new RecommendLoaderTimer().start();
        		
//        		直达区修改为从远程服务器提取
        		ExtServerConfig.init(cfgFilePath+"ext.conf");
        		
        		//加载模糊匹配
        		new WordMatchLoaderTask().start();
        		
        		//加载大词  2011.5.13 先注释掉
//        		new MajorTermLoaderTimerTask().start();
        		
        		//加载屏蔽词
        		new DataLoadTimer().start();
        		
        		//网速任务
        		new NetSpeedTask(cfgFilePath).start();
        		
        		//获取排行榜日期
        		TopDateManager.start();
        		
        		break;
        	case Constant.ServerType.INDEX_MANAGER://索引管理服务器
            	break;
        	case Constant.ServerType.CONSOLE:
        		new DeadLinkCheckTimer().start();
        		new ProgrammeSiteCompleteMaintainTimer().start();
        		new ProgrammeSearchNumberTimer().start();
        		new Top100ReportMailTimer().start();
        		new IndexPicGeneratorTimer().start();
        		new IndexPicOnlineTimer().start();
        		new ProgrammeAuditFlagFixTimer().start();
        		new ShieldMailTimer().start();
        		new ProgrammeSiteHdSynTimer().start();
        		new CopyrightSpiderTimer().start();
        		new EpisodeProgrammeIdSynTimer().start();
        		break;
        	case Constant.ServerType.COMMEND://搜索下拉推荐服务器
        		//加载屏蔽词
        		new DataLoadTimer().start();
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
