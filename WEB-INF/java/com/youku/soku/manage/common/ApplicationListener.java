package com.youku.soku.manage.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.torque.Torque;


public class ApplicationListener implements ServletContextListener {

	 /**
     * <p>Logging output for this plug in instance.</p>
     */
	private Logger log = Logger.getLogger(this.getClass());
    
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
     * <p>Initialize torque
     * storage.</p>
     *
     * @param event The context initialization event
     */
	public void contextInitialized(ServletContextEvent arg0) {
		
		try {
			Torque.init("conf/torque.properties");
			//constuctPermissonMap();
		} catch (Exception e) {
			log.error("Initialize torque error");
			throw new IllegalStateException("Cannot Initialize torque '" +
                      e);
		}
		
	}
	


}
