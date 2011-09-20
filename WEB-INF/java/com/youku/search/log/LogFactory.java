package com.youku.search.log;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

/**
 * 日志配置封装类
 * 
 * @author william
 * 
 */
public class LogFactory {

	private static Map<String, Logger> loggerMap = null;

	/**
	 * 获取封装好的Logger
	 * 
	 * @param key
	 * @return
	 */
	public static Logger getLogger(String key) {
		if (loggerMap != null)
			return loggerMap.get(key);
		else {
			System.out.print("error:log configuration has't been initialized!");
			return null;
		}
	}

	/**
	 * 将配置初始化成Logger并用key,进行关联封装到Map中
	 * 
	 * @param ServletConfig
	 */
	public synchronized static void init(ServletConfig servletConfig) {
		String start_logger = servletConfig.getInitParameter("start_logger");
		if (start_logger != null) {
			loggerMap = new HashMap<String, Logger>();
			String[] loggernames = start_logger.split(",");
			for (int i = 0; i < loggernames.length; i++) {
				String name = loggernames[i];
				String value = servletConfig.getInitParameter(name);
				if (value != null) {
					String[] array = value.split(":");
					if (array != null && array.length == 3) {
						try {
							System.out.println("name="+name + ",localPort="+array[2]);
							loggerMap.put(name, new Logger(array[0], Integer
									.parseInt(array[1]), Integer
									.parseInt(array[2])));
						} catch (Throwable e) {
							System.out.println("error:logfactory init error");
							e.printStackTrace();
						}
					}
				}

			}

		}
	}
}