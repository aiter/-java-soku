package com.youku.search.index.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.config.Config;
import com.youku.search.index.db.PartnerManager;
import com.youku.search.index.db.SynonymManager;
import com.youku.search.index.db.UserManager;
import com.youku.search.index.db.VideoPermissionManager;
import com.youku.search.store.ContainerFactory;
import com.youku.search.util.DataFormat;

public class IndexProcessor {
	
	private static Map<String,String> parseArg(String arg,Map<String,String> map){
		if (arg != null){
			if (arg.startsWith("-type=")){
				map.put("type", arg.substring(6));
			}
			else if (arg.startsWith("-start=")){
				map.put("start", arg.substring(7));
			}
			else if (arg.startsWith("-end=")){
				map.put("end", arg.substring(5));
			}
			else if (arg.startsWith("-path=")){
				map.put("path", arg.substring(6));
			}
		}
		return map;
	}
	
	private static String helpStr = "error:need param! \n" +
									"-type: video / folder \n" +
									"-start: the index start id \n" +
									"-end: the index end id \n" +
									"-path: the outfile path";
	
	private static String getIndexFileName(int start,int end){
		return "index_" + start+"_" + end+ ".txt";
	}
	
	public static void main(String[] args)
	{
		Map<String,String> argMap = new HashMap<String,String>();
		if (args == null){
			System.out.println(helpStr);
			System.exit(1);
			return;
		}
		for (String arg: args){
			System.out.println(arg);
			parseArg(arg,argMap);
		}
		String path = argMap.get("path");
		String type = argMap.get("type");
		int start = DataFormat.parseInt(argMap.get("start"));
		int end = DataFormat.parseInt(argMap.get("end"));
		
		if (path == null || type == null || start <= 0 || end <= 0){
			System.out.println(helpStr);
			System.out.println(path);
			System.out.println(type);
			System.out.println(start);
			System.out.println(end);
			System.exit(1);
		    return;
		}
		
		
        try {
        	Config.init("config/config.xml");
            Torque.init("config/Torque.properties");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
		System.out.println("Torque init ok!");
		
		 //启动log4j
		try {
			DOMConfigurator.configure("config/log4j.xml");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		System.out.println("log4j init ok!");
		
		AnalyzerManager.init();
		//启动定时更新合作方Map
		System.out.println("Partner is reloading.....");
		PartnerManager.getInstance().init();
		System.out.println("Partner load over.....");
		
		//启动定时更新同义词Map
		System.out.println("Synonym is reloading.....");
		SynonymManager.getInstance().init();
		System.out.println("Synonym load over.....");
		
		
		//启动定时更新限制用户
		System.out.println("Limit User is reloading.....");
		UserManager.getInstance().loadLimitUser();
		System.out.println("Limit User load over.....");
		
		//启动定时更新视频屏蔽表
		System.out.println("Video Permission is reloading.....");
		VideoPermissionManager.getInstance().loadPermission();
		System.out.println("Video Permission load over.....");
		
		ContainerFactory.init("config/mem_obj_store.properties");
		
        System.out.println("=============================== \n" +
        		"init ok!");
        
//		try{
//				NewVideoIndexManager m = new NewVideoIndexManager(path,getIndexFileName(start,end));
//				m.createIndex(start,end);
//		}finally{
//			ContainerFactory.getContainer().destroy();
//		}	
		
	}

}
