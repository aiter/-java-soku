/**
 * 
 */
package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.Record;
import com.youku.search.util.Database;

/**
 * @author william
 */
public class VvManager {
	public static VvManager instance = null;
	
	public static VvManager getInstance()
	{
		if ( null == instance ){
			instance = new VvManager();
		}
		return instance;
	}
	
	private VvManager(){
		
	}
	
	public int getVv(int vid)
	{
		Connection conn = null;
		int vv = 0;
		try {
			conn = Database.getVvConnection();
			List list = BasePeer.executeQuery("select vv from t_video_vv where fk_video="+vid,false,conn);
			if (list != null && list.size() > 0)
			{
				vv = ((Record)list.get(0)).getValue("vv").asInt();
			}
		} catch (DataSetException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		finally
		{
			try{
				if (conn != null)conn.close();
			}catch(Exception e)
			{}
		}
		
		return vv;
	}
	
	public int getFolderVv(int pk_folder)
	{
		Connection conn = null;
		int vv = 0;
		try {
			conn = Database.getVvConnection();
			List list = BasePeer.executeQuery("select vv from t_folder_vv where fk_folder="+pk_folder,false,conn);
			if (list != null && list.size() > 0)
			{
				vv = ((Record)list.get(0)).getValue("vv").asInt();
			}
		} catch (DataSetException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		finally
		{
			try{
				if (conn != null)conn.close();
			}catch(Exception e)
			{}
		}
		
		return vv;
	}
}
