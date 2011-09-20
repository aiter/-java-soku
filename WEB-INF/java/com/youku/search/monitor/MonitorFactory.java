/**
 * 
 */
package com.youku.search.monitor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * @author 1verge
 *
 */
public class MonitorFactory {
	
	protected static Log logger = LogFactory.getLog(MonitorFactory.class);
	
	private static Map<String,Monitor> maps = new HashMap<String,Monitor>();
	
	public static Monitor getInstance(String className)
	{
		Monitor monitor = maps.get(className);
		if (monitor == null)
		{
			try {
				//动态转载类
				Class<?> c = Class.forName(MonitorFactory.class.getPackage().getName()+".impl."+className);
				monitor = (Monitor)c.newInstance();
				maps.put(className,monitor);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage(),e);
			}
		}
		return monitor;
	}
}
