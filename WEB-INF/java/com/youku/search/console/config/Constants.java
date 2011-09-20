package com.youku.search.console.config;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.youku.search.index.server.ServerManager;

public class Constants {
	public static final int VIDEO = 1; // 视频
	public static final int FOLDER = 2; // 专辑
	public static final int MEMBER = 3; // 会员
	public static final int BARPOST_SUBJECT = 5; // 看吧帖子按标题搜索
	public static final int PK = 7; // pk擂台

	public static String DIRPATH = "/opt/log_info/logs";
	
	// error
	public static String PWDERR;
	public static String NAMEERR;
	public static String REMOVEERR;
	public static String USRPWDERR;
	public static String SESERR;
	// connpool
	public static int CONN_SERVER_NUM;
	public static String[] iparr;
	// memcached
	public static int SERVER_NUM;
	public static String[] cacheiparr;

	public static int VIDEOORDER=ServerManager.getVideoServers(1).size();
	public static int FOLDERORDER=ServerManager.getFolderServers(1).size();
	public static int BARORDER=ServerManager.getBarServers(1).size();
	public static int USERORDER=ServerManager.getUserServers(1).size();
	public static int PKORDER=ServerManager.getPkServers(1).size();
	
	public static String ROOT;
	public static String CONFIGHOME;
	
	public static String MAIL_HOST;
	public static int MAIL_PORT=25;
	public static String MAIL_FROMADDR;
	public static String MAIL_TOADDR;
	public static String MAIL_USERNAME;
	public static String MAIL_PASSWORD;
	public static String[] MAIL_CC;
	
	public static final int PAGESIZE=10;
	
	public static void init(ServletConfig config) throws ConfigurationException {
		ROOT = config.getServletContext().getRealPath("/");
		CONFIGHOME = config.getInitParameter("config_home");
		Configuration c = new PropertiesConfiguration(ROOT + CONFIGHOME
				+ "error.properties");
		PWDERR = c.getString("pwderr");
		NAMEERR = c.getString("nameerr");
		REMOVEERR = c.getString("removeerr");
		USRPWDERR = c.getString("usrpwderr");
		SESERR = c.getString("seserr");
		c = new PropertiesConfiguration(ROOT + CONFIGHOME
				+ "memcached.properties");
		cacheiparr = c.getStringArray("server");
		SERVER_NUM = cacheiparr.length;
		c = new PropertiesConfiguration(ROOT + CONFIGHOME
				+ "conn-server.properties");
		iparr = c.getStringArray("connpool_ip");
		CONN_SERVER_NUM = iparr.length;
		c = new PropertiesConfiguration(ROOT + CONFIGHOME
				+ "mail.properties");
		MAIL_HOST=c.getString("host");
		MAIL_PORT=c.getInt("port");
		MAIL_FROMADDR=c.getString("from_addr");
		MAIL_TOADDR=c.getString("to_addr");
		MAIL_USERNAME=c.getString("username");
		MAIL_PASSWORD=c.getString("password");
		MAIL_CC=c.getStringArray("cc_addr");
	}
}
