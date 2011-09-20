package com.youku.search.monitor.impl;

import com.youku.search.monitor.Monitor;
import com.youku.search.monitor.Person;

public abstract class EMonitor extends Monitor {

	public EMonitor() {
		super(new Person[]{Person.gaosong,Person.zhenghailong});
		super.period = 10 * 1000;	// 10 seconds
	}
	
}
