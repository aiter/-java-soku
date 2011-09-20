package com.youku.soku.suggest.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.suggest.data.LibraryData;
import com.youku.soku.suggest.data.loader.LibraryDataLoader;
import com.youku.soku.suggest.data.serialize.ObjectSaverAndLoader;

public class LibraryDataLoadTimer {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private final long delay = 1000 * 1;
	
	private final long period = 1000 * 60 * 60;  //
	
	
	
	private static int counter = 0;
	
	public void start() {		
		
	
        
		TimerTask task = new TimerTask() {			
			
			@Override
			public void run() {
				
				Calendar calendar = Calendar.getInstance();  
		       
				if(calendar.get(Calendar.HOUR_OF_DAY) == 3 || counter == 0) {
					log.info("开始加载 Libraray Data .....Counter: " + counter);
					log.info("Begin to load Library Data from database");
					LibraryData data = new LibraryData();
					LibraryDataLoader loader = new LibraryDataLoader();
					loader.loadData(data);
					log.info("Complete loading Library Data from database");
					
					log.info("Begin Save data to local disk ...");
					ObjectSaverAndLoader osl = new ObjectSaverAndLoader();
					osl.save(data);
					log.info("Complete saving data to local disk");
					counter++;
				}
								
			}
			
		};
		
		Timer timer = new Timer(true);
		//timer.schedule(task, date);
		timer.schedule(task, delay, period);
	}
		
	

}
