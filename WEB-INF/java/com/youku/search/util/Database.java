/**
 * 
 */
package com.youku.search.util;

import java.sql.Connection;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

/**
 * @author william
 *
 */
public class Database {

	
	/**
	 * 获取视频,专辑等主数据库Connection
	 */
	public static Connection getYoqooConnection() throws TorqueException
	{
		return Torque.getConnection("yoqoo");
	}
	public static Connection getFolderConnection() throws TorqueException
	{
		return Torque.getConnection("folder");
	}
	
	/**
	 * 获取用户,专辑等主数据库Connection
	 */
	public static Connection getYoqooUserConnection() throws TorqueException
	{
		return Torque.getConnection("youkuuser");
	}
	
	/**
	 * 获取mapping数据库Connection
	 */
	public static Connection getMappingConnection() throws TorqueException
	{
		return Torque.getConnection("yoqoomap");
	}
	
	/**
	 * 获取看吧数据库Connection
	 */
	public static Connection getBarConnection() throws TorqueException
	{
		return Torque.getConnection("youkubar");
	}
	
	/**
	 * 获取统计数据库Connection
	 */
	public static Connection getStatConnection() throws TorqueException
	{
		return Torque.getConnection("search_stat");
	}
	
	/**
	 * 获取删除记录数据库 Connection
	 */
	public static Connection getAffectConnection() throws TorqueException
	{
		return Torque.getConnection("affect");
	}
	
	/**
	 * 获取铃声数据库 Connection
	 */
	public static Connection getRingConnection() throws TorqueException
	{
		return Torque.getConnection("affect");
	}
	
	/**
	 * 获取vv数据库 Connection
	 */
	public static Connection getVvConnection() throws TorqueException
	{
		return Torque.getConnection("vv");
	}
	
	/**
	 * 获取partner数据库 Connection
	 */
	public static Connection getPartnerConnection() throws TorqueException
	{
		return Torque.getConnection("partner");
	}
	
	/**
	 * 获取禁忌词 Connection
	 */
	public static Connection getFilterConnection() throws TorqueException
	{
		return Torque.getConnection("filter");
	}
	
	/**
	 * 获取禁忌词 Connection
	 */
	public static Connection getConsoleConnection() throws TorqueException
	{
		return Torque.getConnection("console");
	}
	
	/**
	 * 获取最后15天vv数据库Connection
	 */
	public static Connection getLastVvConnection() throws TorqueException
	{
		return Torque.getConnection("lastvv");
	}
	
	/**
	 * 获取soku Connection 
	 */
	public static Connection getSokuConn() throws TorqueException{
		return Torque.getConnection("soku");
	}
	
}
