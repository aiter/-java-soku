/**
 * 
 */
package com.youku.soku.index.query;

import java.io.IOException;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author william
 *
 */
public class UpdateIndexSchedule extends TimerTask{

	static Log logger = LogFactory.getLog(UpdateIndexSchedule.class);
	BaseQuery query = null;
	public UpdateIndexSchedule(BaseQuery q)
	{
		this.query = q;
		System.err.println("type:" + query.getClass() + "===关闭临时索引 timer 已经启动");
	}
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		logger.info("正在关闭临时索引");
		try {
			query.closeTmpReader();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		logger.info("临时索引关闭完成");
	}

}
