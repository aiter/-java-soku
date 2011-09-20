package com.youku.top.paihangbang;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.top.util.LogUtil;

public class Main {
	public static void main(String[] args) {
		try {
			LogUtil.init(Level.INFO,"/opt/log_analyze/soku_top/log/log.txt");
			if(null!=args&&args.length==1&&args[0].matches("\\d{4}_\\d{2}_\\d{2}"))
				TopWordsDataBuilder.getInstance().topwordBuild(args[0]);
			else TopWordsDataBuilder.getInstance().topwordBuild();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
