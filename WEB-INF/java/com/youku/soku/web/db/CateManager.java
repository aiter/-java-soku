/**
 * 
 */
package com.youku.soku.web.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.Record;
import com.youku.soku.util.DataBase;

/**
 * @author 1verge
 *
 */
public class CateManager {
	private static HashMap<Integer,String> nameMap = new HashMap<Integer,String>();
	
	
	private static CateManager self = null;

	public static CateManager getInstance(){
		
		if(self == null){
				self = new CateManager();
				self.init();
		}
		return self;
	}
	
	public void init()
	{
		nameMap.clear();
		Connection conn = null;
		try {
			conn = DataBase.getLibraryConn();
			List<Record> list = BasePeer.executeQuery("select * from category",false,conn);
			if (list!=null){
				for (Record record:list){
					nameMap.put(record.getValue("id").asIntegerObj(),record.getValue("name").asString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	public String getName(int cate_id)
	{
		return nameMap.get(cate_id);
	}
	
}
