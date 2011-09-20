package com.youku.soku.newext.loader;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import java.util.Date;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.newext.info.ExtInfo;
import com.youku.soku.newext.loader.Loader;
import com.youku.soku.newext.loader.FileLoaderAndSaver;

/**
 * 从数据库load数据，填充精准检索Map 并序列化为文件
 * @author User
 *
 */
public class GenFile {

	private static Log logger = LogFactory.getLog(GenFile.class);

	String info_dir;
	String info_file_name;

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GenFile fileSaver = init(args);
		logger.info("////////////开始创建直达区文件 ///////////////"+sdf.format(new Date()));
		
		boolean success = fileSaver.run();

		if (success) {
			logger.info("\\\\\\\\\\\\直达区文件创建成功\\\\\\\\\\\\\\"+sdf.format(new Date()));
			System.exit(0);
		} else {
			logger.info("\\\\\\\\\\\\直达区文件创建失败\\\\\\\\\\\\\\"+sdf.format(new Date()));
			System.exit(1);
		}
	}

	private static GenFile init(String[] args) throws Exception {
//				 初始化logger
		String log4j = args[0];
		DOMConfigurator.configure(log4j);
		logger.info("初始化log4j: " + log4j);
		
		// usage
		logger.info("usage: log4j torque info_dir info_file_name");

//		 初始化torque
		String torque = args[1];
		logger.info("初始化torque: " + torque);
		Torque.init(torque);

//		 file saver
		String info_dir = args[2];
		String info_file_name = args[3];

		GenFile fileSaver = new GenFile();
		fileSaver.info_dir = info_dir;
		fileSaver.info_file_name = info_file_name;

		return fileSaver;
	}

	public boolean run() {
		try {
//			 从数据库加载数据
			logger.info("开始从数据库加载数据...");
			Cost cost = new Cost();

			ExtInfo extInfo = new ExtInfo();
			new Loader().load(extInfo);

			cost.updateEnd();
			logger.info("从数据库加载信息完毕; cost: " + cost.getCost() + "; extInfo: "
					+ extInfo.info());

			// 将加载到的信息保存到本地文件
			logger.info("保存extInfo到本地文件...");
			cost.updateStart();

			FileLoaderAndSaver loaderAndSaver = new FileLoaderAndSaver(
					info_dir, info_file_name);
			boolean success = loaderAndSaver.save(extInfo);

			cost.updateEnd();
			logger.info("保存extInfo到本地文件完毕; cost: " + cost.getCost()
					+ "; extInfo: " + extInfo.info() + "; 是否成功: " + success);

			return success;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			logger.error("加载信息发生异常", e);
			return false;
		}
	}
}
