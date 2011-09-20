package com.youku.soku.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExtServerConfig {
	
	private static Log logger=LogFactory.getLog(ExtServerConfig.class);
	private static Configuration config=null;
	private ExtServerConfig(){};
	
	public static Configuration getInstance(){
		
		return config;
	}
	
	public static String getRandomServer(){
		try{
			String[] serverArr=config.getStringArray("EXTSERVER");
			if(serverArr==null || serverArr.length<=0){
				logger.error("EXTSERVER");
				return "";
			}
			return serverArr[new Random().nextInt(serverArr.length)];
			
			
		}catch(Exception e){
			logger.error("获取ext服务器失败");
			return "";
		}
		
		
	}
	
	public static void main(String[] args){
		try {
//			getInstance("WEB-INF/local-conf/");
//			File count=new File("count");
			BufferedWriter bw=new BufferedWriter(new FileWriter("count"));
			
			for(int i=0;i<100000;i++){
				bw.write(getRandomServer());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
		System.out.println("over");
	}

	public static void init(String path) {
		if(path==null || StringUtils.trimToEmpty(path).length()<=0){
			logger.error("直达区服务器初始化失败，请输入ext.conf文件名");
			return;
		}
		if(config==null){
			File file=new File(path);
			try {
				config= new PropertiesConfiguration(file);
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				logger.error("直达区服务器初始化失败");
				e.printStackTrace();
			}
		
		}
		
	}
  

}
