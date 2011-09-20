/**
 * 
 */
package com.youku.search.monitor;

import java.util.Timer;

import org.apache.commons.configuration.ConfigurationException;

import com.youku.search.config.Config;

/**
 * @author 1verge 监控加载器
 */
public class MonitorTimer {

	private static boolean isRuning = false;

	private static String[] monitors = new String[] { "VideoMonitor", // 视频搜索主站监控
			"AdvanceMonitor", // 高级搜索监控
			"FolderMonitor", // 专辑搜索监控
			"UserMonitor", // 用户搜索监控
			"BarMonitor", // 看吧名称搜索监控
			"BarpostMonitor", // 看吧帖子搜索监控
			"LikeQueryMonitor", // 相关搜索监控
			"CorrectMonitor", // 纠错监控
			"LikeVideoMonitor", // 相似视频监控
			"NewVideoMonitor", // 新视频加载
			"SokuMonitor", // 站外搜索监控
			"RecomendMonitor", // 搜索下拉推荐
			"SpiderMonitor", // 爬虫更新监控
			"LibraryImportMonitor", // 直达区导入监控
			"VRIndexFileMonitor", // 指数文件
			"SpiderVideoInfoMointor", // 直达区获取spider接口监控
			"SokuSuggestMonitor", // soku下拉提示监控
			"SohuRobotsMonitor",

			"TopMonitor" // 榜单后台数据生成监控
	};

	public static void run() {
		if (!isRuning) {
			isRuning = true;
			for (String monitorName : monitors) {
				Monitor monitor = MonitorFactory.getInstance(monitorName);
				if (monitor != null) {
					new Timer().schedule(monitor, 1, monitor.getPeriod());
					System.out.println(monitorName + "已启动,周期："
							+ monitor.getPeriod());
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			Config
					.init(Monitor.class.getResource("/conf/config.xml")
							.getPath());
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run();
	}
}
