/**
 * 
 */
package com.youku.soku.util;

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
	public static Connection getSpider23Connection() throws TorqueException
	{
		return Torque.getConnection("spider23");
	}
	public static Connection getSpiderConnection() throws TorqueException
	{
		return Torque.getConnection("so");
	}
	public static Connection getYoukuConnection() throws TorqueException
	{
		return Torque.getConnection("youku");
	}
	public static Connection getYoukuMappingConnection() throws TorqueException
	{
		return Torque.getConnection("youkumap");
	}
	public static Connection getSearchStatConn() throws TorqueException{
		return Torque.getConnection("search_stat");
	}
	public static Connection getSearchStatSokuConn() throws TorqueException{
		return Torque.getConnection("search_stat_soku");
	}
	public static Connection getFilterConn() throws TorqueException{
		return Torque.getConnection("filter");
	}
	
	public static Connection getSearchRecomendConn() throws TorqueException{
		return Torque.getConnection("searchrecommend");
	}
	
	public static Connection getLibraryConn() throws TorqueException{
		return Torque.getConnection("soku_library");
	}
	
	public static Connection getYoqooConn() throws TorqueException{
		return Torque.getConnection("youku");
	}
	
	public static Connection getTeleplayConn() throws TorqueException{
		return Torque.getConnection("searchteleplay");
	}
	
	public static Connection getSokuConn() throws TorqueException{
		return Torque.getConnection("soku");
	}
	
	public static Connection getSokuTopConn() throws TorqueException{
		return Torque.getConnection("soku_top");
	}
	
	public static Connection getLibDataConn() throws TorqueException{
		return Torque.getConnection("lib_data");
	}
	
	public static Connection getNewSokuTopConn() throws TorqueException{
		return Torque.getConnection("new_soku_top");
	}
}
