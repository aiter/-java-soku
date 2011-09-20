/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import com.youku.search.util.DataFormat;
import com.youku.soku.index.om.Movielog;
import com.youku.soku.index.om.MovielogPeer;

/**
 * @author 1verge
 *
 */
public class MovielogManager {

	private static MovielogManager self = null;

	public static MovielogManager getInstance(){
		
		if(self == null){
			self = new MovielogManager();
		}
		return self;
	}
	
	@SuppressWarnings("unchecked")
	public List<Movielog> getDeleteVideos()
	{
		Date start = DataFormat.getNextDate(new Date(), -1);
		Criteria c = new Criteria();
		c.add(MovielogPeer.STATE,"delete");
		c.add(MovielogPeer.UPDATETIME,(Object)DataFormat.formatDate(start, DataFormat.FMT_DATE_YYYYMMDD),Criteria.GREATER_THAN);
		try {
			return MovielogPeer.doSelect(c);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Movielog> getUpdateVideos()
	{
		Criteria c = new Criteria();
		c.add(MovielogPeer.STATE,"update");
		try {
			return MovielogPeer.doSelect(c);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void clean()
	{
		Connection conn = null;
		try {
			String sql = "delete from movieLog where updateTime < '"+ DataFormat.formatDate(DataFormat.getNextDate(new java.util.Date(),-1), DataFormat.FMT_DATE_YYYYMMDD) +"'";
			BasePeer.executeStatement(sql);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		finally
		{
			if (conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	/**
//	 * 获取丢失的视频,规则认为1天前
//	 * 此表中数据被操作后即删除，如果删除不了，说明索引中漏掉此视频，此方法用来查询这些漏掉的视频供后续处理
//	 * @return
//	 */
//	public List<Movielog> getLostVideos()
//	{
//		Date date = DataFormat.getNextDate(new Date(),-1);
//		
//		Criteria c = new Criteria();
//		c.add(MovielogPeer.STATE,"update");
//		c.add(MovielogPeer.UPDATETIME,date,Criteria.LESS_THAN);
//		try {
//			return MovielogPeer.doSelect(c);
//		} catch (TorqueException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public void delete(int id)
	{
		try {
			Criteria c = new Criteria();
			c.add(MovielogPeer.ID,id);
			MovielogPeer.doDelete(c);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}
	
	public void create(String table,int vid,String state)
	{
		Movielog log=  new Movielog();
		log.setTablename(table);
		log.setVid(vid);
		log.setState(state);
		try {
			log.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
