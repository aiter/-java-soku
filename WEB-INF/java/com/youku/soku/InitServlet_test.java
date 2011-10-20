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

import com.youku.soku.config.Config;
import com.youku.soku.config.ExtServerConfig;
import com.youku.soku.index.server.ServerManager;
import com.youku.soku.manage.admin.copyright.util.CopyrightSpiderTimer;
import com.youku.soku.manage.timer.EpisodeProgrammeIdSynTimer;
import com.youku.soku.manage.timer.ProgrammeSearchNumberTimer;
import com.youku.soku.newext.servlet.ExtTimerTask;
import com.youku.soku.newext.servlet.ExtUpdateRelatedShowTimerTask;
import com.youku.soku.newext.servlet.ExtUpdateTimerTask;


/**
 * @author 1verge
 *
 */
public class InitServlet_test extends HttpServlet {

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
		
		ExtServerConfig.init(cfgFilePath+"ext.conf");
        
		//配置memcache
		
		//版权抓取任务
		new CopyrightSpiderTimer().start();
		//剧集的节目Id同步
		new EpisodeProgrammeIdSynTimer().start();
		new ProgrammeSearchNumberTimer().start();
				
	      //加载站点信息
		
		new ExtTimerTask().start();

		new ExtUpdateTimerTask().start();
		new ExtUpdateRelatedShowTimerTask().start();
		
	
		
/*		new Top100ReportMailTimer().start();
	//	new SiteTimer().start();
	//	new DeadLinkCheckTimer().start();
	//	new ProgrammeSiteCompleteMaintainTimer().start();
	//	new KnowledgeDataLoadTimer().start();
		new DataLoadTimer().start();
		new LibraryDataLoadTimer().start();
		new TrieTreeLoaderTimer().start();*/
	/*	new KnowledgeDataLoadTimer().start();
		String memcachedPicPath = root + config.getInitParameter("memcached-pic");
		MemCachedPic.init(memcachedPicPath);*/
	//	new ProgrammeSearchNumberTimer().start();
		//new TrieTreeLoaderTimer().start();
      //  String memcachedPath = root + config.getInitParameter("memcached");
        //MemcachedInit.getInstance().init(memcachedPath);
      //  System.out.println("配置memcache ok!");
		
      /*  AnalyzerManager.init();
        
        //初始化日志配置信息
        LogFactory.init(config);
        
        System.out.println("mina server start");
        new DataLoadTimer().start();
        Filter filter = Filter.getInstance();
		ShieldInfo shiedInfo = filter.isShieldWord("色戒", Source.youku);
		System.out.println(shiedInfo);*/
       
		//new EpisodeAuditLogTimer().start();
	//	new TrieTreeLoaderTimer().start();
		
        //针对服务器类型加载
      /*  switch(Config.getServerType())
        {
        	case Constant.ServerType.INDEX://索引服务器
        		AnalyzerManager.init();
        		VideoQueryManager.getInstance();
                com.youku.soku.sort.server.ServerManager.getInstance();
        		break;
        	case Constant.ServerType.SORT:
        		//start search words filter
        		new RecommendLoaderTimer().start();
        		new LoaderTimer().start();
        		new WordMatchLoaderTask().start();
        		new MajorTermLoaderTimerTask().start();
        		
        		//目录排行榜前台应用
        		new TypeAndAreaTimer().start();
        		new TopChannelDateTimer().start();
        		//加载屏蔽词
        		new DataLoadTimer().start();
        		break;
        	case Constant.ServerType.INDEX_MANAGER://索引管理服务器
            	break;
        	case Constant.ServerType.CONSOLE:
        		ServletContextEvent e = new ServletContextEvent(config.getServletContext());
        		//检测youku站点
        		new VideoExistCheckTimeTask().contextInitialized(e);
//        		new VideoSearchTimeTask().contextInitialized(e);
        		new CollectedAndCompletedCheckTimeTask().contextInitialized(e);
        		new CopySynTimeTask().contextInitialized(e);
        		new JujiSynTimeTask().contextInitialized(e);
        		new FirstlogoUpdateTimeTask().contextInitialized(e);
//        		new SiteOrderTimeTask().contextInitialized(e);
//        		new VideoInfoUpdateTimeTask().contextInitialized(e);
        		//检测综合里的优酷视频
        		new ZongHeCheckTimeTask().contextInitialized(e);
        		new ZongHeMergeTimeTask().contextInitialized(e);
        		new VarietySubTitleTimeTask().contextInitialized(e);
        		new NamesTimer().start();
        		new SiteIdAndTablesTimer().start();
        		new SiteVersionInsertTimer().start();
        		new MusicSpiderTimer().start();
        		//检测其它站点死链
        		new NotYoukuUrlCheckTimer().start();
        		
        		
        		new EpisodeSyncLogFixTimer().start();
        		new EpisodeAuditLogTimer().start();
        		
        		break;
        	case Constant.ServerType.COMMEND://搜索下拉推荐服务器
        		ServletContextEvent event = new ServletContextEvent(config.getServletContext());
        		//加载屏蔽词
        		new DataLoadTimer().start();
            	new RecomendTask().contextInitialized(event);
            	new CheckRecomendTask().contextInitialized(event);
            	break;
        }*/
        
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
