package com.youku.search.console.config.time;

import java.util.Calendar;

public class DelayTime {
	
	public static long getDelayTime(long d,double hour,boolean f){
		Calendar calCurrent = Calendar.getInstance();
		long now;
		if(f)
			now = calCurrent.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 + calCurrent.get(Calendar.MINUTE) * 60 * 1000 + calCurrent.get(Calendar.SECOND) * 1000;
		else now = calCurrent.get(Calendar.HOUR) * 60 * 60 * 1000 + calCurrent.get(Calendar.MINUTE) * 60 * 1000 + calCurrent.get(Calendar.SECOND) * 1000;
		long run = (long) (hour * 60 * 60 * 1000);
		
		if (run < now){
			run = d-(now - run) ;
		}
		else{
			run = run - now;
		}
		return run;
	}
}
