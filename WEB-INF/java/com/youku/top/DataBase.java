/**
 * 
 */
package com.youku.top;

import java.sql.Connection;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

/**
 * @author 1verge
 *
 */
public class DataBase {

	/**
	 * 获取主数据库Connection
	 */
	public static Connection getSearchStatConn() throws TorqueException{
		return Torque.getConnection("search_stat");
	}
	
	public static Connection getLibraryConn() throws TorqueException{
		return Torque.getConnection("soku_library");
	}
}
