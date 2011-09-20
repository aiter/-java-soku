package com.youku.search.sort.benchmark;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import com.youku.search.util.StringUtil;
import com.youku.search.util.StringUtil.EncodingSet;

/**
 * 根据用户QueryLog[]生成QueryData文件，然后再根据QueryData生成LoadBenchUrl文件
 * 
 * @author gaosong
 */
public class LoadBenchUrlsGenerator {
	private static final Log logger = LogFactory.getLog(LoadBenchUrlsGenerator.class);

	private static final String CONFIG_PATH_PREFIX = "conf"
			+ File.separatorChar;	

	public static void main(String[] args) {
		DOMConfigurator.configure(CONFIG_PATH_PREFIX + "log4j-test.xml");

		if (args.length == 0) {
			throw new IllegalArgumentException(
					"参数错误，参数格式: userQueryDirPath queryDataFilePath loadBenchFilePath loadBenchUrlPrefix benchCount");
		}

		String userQueryDirPath = args[0];
		String queryDataFilePath = args[1];
		String loadBenchFilePath = args[2];
		String loadBenchUrlPrefix = args[3];
		int benchCount = Integer.parseInt(args[4]);
		
		System.out.println("params, [userQueryDirPath]=" + userQueryDirPath
				+ ", [queryDataFilePath]=" + queryDataFilePath
				+ ", [loadBenchFilePath]=" + loadBenchFilePath
				+ ", [loadBenchUrlPrefix]=" + loadBenchUrlPrefix + ", [benchCount]=" + benchCount);
		
		try {
			LoadBenchUrlsGenerator generator = new LoadBenchUrlsGenerator(userQueryDirPath, queryDataFilePath, loadBenchFilePath, loadBenchUrlPrefix, benchCount);
			generator.buildQueryData(generator.userQueryFiles);
			generator.buildLoadBenchURL(generator.benchCount, generator.urlPrefix);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	private final File userQueryDir;
	private final File[] userQueryFiles;
	private final File queryDataFile;
	private final File loadBenchFile;
	private final String urlPrefix;
	private final int benchCount;
	private final static byte lineSeparator = (byte)'\n';
	
	public LoadBenchUrlsGenerator(String userQueryDirPath, String queryDataFilePath, String loadBenchFilePath, String loadBenchUrlPrefix, int benchCount) {
		this.userQueryDir = new File(userQueryDirPath);
		if (!userQueryDir.exists() || !userQueryDir.isDirectory()) {
			throw new IllegalStateException("userQueryDir目录已经存在，或userQueryDir不是一个目录：" + userQueryDir.getAbsolutePath());
		}
		this.userQueryFiles = userQueryDir.listFiles();
		for (File userQueryFile : userQueryFiles) {
			if (!userQueryFile.isFile()) {
				throw new IllegalStateException("userQueryFile解析错误，不是文件：" + userQueryFile.getAbsolutePath());
			}
		}
		
		this.queryDataFile = new File(queryDataFilePath);
		if (queryDataFile.exists()) {
			throw new IllegalStateException("queryDataFile文件已经存在：" + queryDataFile.getAbsolutePath());
		}
		
		this.loadBenchFile = new File(loadBenchFilePath);
		if (loadBenchFile.exists()) {
			throw new IllegalStateException("loadBenchFile文件已经存在：" + loadBenchFile.getAbsolutePath());
		}
		
		this.urlPrefix = loadBenchUrlPrefix;
		this.benchCount = benchCount;
	}
	
	/**
	 * 生成urls测试文件为http_load工具使用
	 * 
	 * @param maxLineNum 需要生成的行数
	 * @param urlPrefix 生成的每个URL的前缀
	 * @param loadBenchFilePath 生成的文件路径
	 * @throws IOException
	 */
	private void buildLoadBenchURL(int maxLineNum, String urlPrefix) throws IOException{
		logger.info("开始生成urls文件");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(loadBenchFile, true), 1024 * 512);
		MockQueryBuilder queryDataMapping = new MockQueryBuilder(queryDataFile.getAbsolutePath(), EncodingSet.UTF8);
		
		String line = null;
		String url = null;
		int lineNum = 0;
		for (; lineNum < maxLineNum; lineNum++) {
			line = queryDataMapping.readRandomLine(EncodingSet.UTF8);
			url = urlPrefix + StringUtil.urlEncode(line, EncodingSet.UTF8.getEncode());
			
			out.write(url.getBytes());
			out.write(lineSeparator);
			
			if (lineNum % 1000 == 0) {
				logger.info("LoadBenchURL LineNum=" + lineNum);
			}
		}
		logger.info("LoadBenchURL LineNum=" + lineNum);
		
		out.flush();
		out.close();
		logger.info("--- 生成文件完毕：" + loadBenchFile.getAbsolutePath());
	}

	/**
	 * 根据用户搜索词生成QueryData文件
	 * 
	 * @param userQueryFiles 用户搜索词QueryLog，可以传递多个文件
	 * @throws IOException 
	 */
	private void buildQueryData(File... userQueryFiles) throws IOException{
		if (userQueryFiles.length == 0) {
			throw new IllegalArgumentException("userQueryFiles参数不存在");
		}
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(queryDataFile, true), 1024 * 512);
		
		int totalQueryCount = 0;
		for (File userQueryFile : userQueryFiles) {
			if (userQueryFile.isDirectory()) {
				throw new IllegalStateException("不能包含目录：" + userQueryFile.getAbsolutePath());
			}
			
			logger.info("--- 开始读取文件：" + userQueryFile.getAbsolutePath());
			List<String> lines = FileUtils.readLines(userQueryFile, EncodingSet.UTF8.getEncode());
			for (String line : lines) {
				if (null == line || line.trim().length() == 0) {
					continue;
				}
				
				line = line.trim();
				if (!StringUtils.contains(line, "	video")) {
					continue;
				}
				
				String word = StringUtils.substringBeforeLast(line, "	video");
				out.write(word.getBytes(EncodingSet.UTF8.getEncode()));
				out.write(lineSeparator);
				
				totalQueryCount++;
				if (totalQueryCount % 1000 == 0) {
					logger.info("totalQueryCount=" + totalQueryCount);
				}
			}
			logger.info("totalQueryCount=" + totalQueryCount);
			logger.info("--- 读取文件完毕：" + userQueryFile.getAbsolutePath());
		}
		
		out.flush();
		out.close();
		logger.info("--- 生成文件完毕：" + queryDataFile.getAbsolutePath());
	}
}
