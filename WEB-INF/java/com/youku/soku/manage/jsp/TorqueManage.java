package com.youku.soku.manage.jsp;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

public class TorqueManage {
	
	public static void initTorque() {
		try {
			Torque.init("/opt/search/WEB-INF/soku-conf/Torque.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void closeTorque() {
		try {
			Torque.shutdown();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}

}
