/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.util.Date;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.index.om.Indexlog;
import com.youku.soku.index.om.IndexlogPeer;

/**
 * @author 1verge
 *
 */
public class IndexLogManager {

	
	private static IndexLogManager self = null;
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	public static IndexLogManager getInstance(){
		
		if(self == null){
				self = new IndexLogManager();
		}
		return self;
	}
	
	private IndexLogManager(){
		
	}
	
	public Indexlog getLastAddTime()
	{
		Criteria c = new Criteria();
		c.add(IndexlogPeer.TYPE,"ADD");
		
		try {
			List list = IndexlogPeer.doSelect(c);
			if(list != null && !list.isEmpty())
			{
				Indexlog log = (Indexlog)list.get(0);
				return log;
			}
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public void updateLastAddTime(int id,Date start,Date end)
	{
		_log.info("update last add Time id:"+id+"\tstart="+start+"\tend:"+end);
		Criteria c = new Criteria();
		c.add(IndexlogPeer.ID,id);
		c.add(IndexlogPeer.START_TIME,start);
		c.add(IndexlogPeer.END_TIME,end);
		try {
			IndexlogPeer.doUpdate(c);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}
	public int insertLastAddTime(Date start,Date end)
	{
		Indexlog log = new Indexlog();
		log.setStartTime(start);
		log.setEndTime(end);
		log.setType("ADD");
		try {
			log.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return log.getId();
	}
}
