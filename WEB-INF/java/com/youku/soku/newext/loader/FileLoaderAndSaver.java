package com.youku.soku.newext.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.ExtInfo;
import com.youku.soku.newext.info.ExtInfoHolder;

/**
 * 加载器
 */
public class FileLoaderAndSaver {

	Log logger = LogFactory.getLog(getClass());

	public final File dir;
	public final File file;

	public static final String DEFAULT_FILE_NAME = "soku_extinfo.bin";

	public FileLoaderAndSaver() {
		this(System.getProperty("java.io.tmpdir"), DEFAULT_FILE_NAME);
	}

	public FileLoaderAndSaver(String dir) {
		this(dir, DEFAULT_FILE_NAME);
	}

	public FileLoaderAndSaver(String dir, String file) {
		this.dir = new File(dir);
		this.file = new File(this.dir, file);
	}

	/**
	 * 加载直达区文件信息到内存
	 * 如果没找到，或者加载错误，就返回null
	 */
	public void load() {
		logger.info("开始加载直达区文件");
		if (!file.exists()) {
			logger.info("数据文件不存在: " + file.getAbsolutePath());
			
		}

		logger.info("从这个数据文件中加载信息: " + file.getAbsolutePath() + "; "
				+ file.length() + "bytes");
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new FileInputStream(file));
			ExtInfo extInfo = (ExtInfo) inputStream.readObject();
			if (extInfo != null) {
				logger.info("节目数量:"+extInfo.aliasInfo.id_programme.size());
				ExtInfo oldInfo = ExtInfoHolder.getCurrent();
				ExtInfoHolder.setCurrent(extInfo);
				destroy(oldInfo);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception x) {
					logger.error(x.getMessage(), x);
				}
			}
		}
		
		logger.info("加载直达区文件结束");

	}

	/**
	 * @param oldInfo
	 */
	private void destroy(ExtInfo oldInfo) {
		if(oldInfo==null)
			return;
		
		oldInfo.destroy();
	}

	/**
	 * 如果找到，或者加载错误，就返回null
	 */
	public boolean save(ExtInfo extInfo) {

		logger.info("保存数据到这个数据文件中: " + file.getAbsolutePath());

		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(file));
			outputStream.writeObject(extInfo);

			logger.info("保存数据到这个数据文件完毕: " + file.getAbsolutePath() + "; "
					+ file.length() + "bytes");
			return true;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception x) {
					logger.error(x.getMessage(), x);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		FileLoaderAndSaver loaderAndSaver = new FileLoaderAndSaver();
		ExtInfo extInfo = new ExtInfo();
		loaderAndSaver.save(extInfo);
		System.out.println(extInfo);

		System.out.println("/////////////////////////////");
	}
}
