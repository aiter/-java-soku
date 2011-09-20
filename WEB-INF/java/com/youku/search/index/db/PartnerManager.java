/**
 * 
 */
package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import com.youku.search.util.Database;

/**
 * @author william
 *
 */
public class PartnerManager {
	
	private HashMap<Integer,Integer> partnerMap = null;
	public static PartnerManager instance = null;
	
	public static PartnerManager getInstance()
	{
		if ( null == instance ){
			instance = new PartnerManager();
		}
		return instance;
	}
	
	private PartnerManager(){
		
	} 
	
	public synchronized void init()
	{
		if (partnerMap == null){
			partnerMap = new HashMap<Integer,Integer>();
		}
		else{
			partnerMap.clear();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			 conn = Database.getPartnerConnection();
			 st = conn.createStatement();
			 rs = st.executeQuery("select * from t_video_partner");
			 
			 if (rs != null)
			 {
				 while (rs.next())
				 {
					 partnerMap.put(rs.getInt("videoid"),rs.getInt("partnerid"));
				 }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try{
				if (rs != null){ 
					rs.close();
					rs = null;
				}
				if (st != null){ 
					st.close();
					st = null;
				}
				if (conn != null){ 
					conn.close();
					conn = null;
				}
			}catch(Exception e){}
		}
	}
	
	public int getPartnerId(int videoId)
	{
		if (partnerMap == null)
		{
			init();
		}
		Integer partner = partnerMap.get(videoId);
		return null == partner? 0 :partner.intValue();
	}
}
