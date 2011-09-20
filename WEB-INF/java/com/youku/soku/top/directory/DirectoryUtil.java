package com.youku.soku.top.directory;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.top.util.VideoType;


public class DirectoryUtil {
	static Log logger = LogFactory.getLog(DirectoryUtil.class);
	
	public static int getTotalCount(String sql) {
		try {
			List<Record> records = BasePeer.executeQuery(sql,"new_soku_top");
			if(records!=null && records.size()>0) return new Integer(records.get(0).getValue(1).asInt()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}finally{
		}
		return 0;
	}
	
	public static void main(String[] args){
		String config="E:/workspace/Search/WEB-INF/soku-conf/Torque.properties";
		try {
			Torque.init(config);
			String sql="SELECT count(1) FROM areas a;";
			System.out.println("count:"+getTotalCount(sql));
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("torque初始化失败！");
		}
		System.out.println("over");
	}
	
	public static String cate2ChannelName(int cate){
		switch(cate){
		case 1: return VideoType.teleplay.name();
		case 2: return VideoType.movie.name();
		case 3: return VideoType.variety.name();
		case 4: return VideoType.music.name();
		case 5: return VideoType.anime.name();
		case 6: return VideoType.person.name();
		}
		return null;
	}
	
	public static int channel2Cate(String channel){
		int cate;
		try{
			cate=VideoType.valueOf(channel).getValue();
		}catch(Exception e){
			logger.error("error:"+e.getMessage());
			return 0;
		}
		return cate;
	}
}
