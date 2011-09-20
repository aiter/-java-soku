package com.youku.search.log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;


/**
 * Log类
 * @author william
 *
 */
public class Logger {

	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("STDOUTLOG");
	
	private String remoteIp = null;  //接受LOG服务器IP
	private int remotePort = 0;		//接受LOG服务器UDP端口
	private int localPort = 0;		//发送LOG服务器本机UDP端口
	
	private DatagramSocket sourceSocket = null;
	private SocketAddress targetSocket = null;
	
	public Logger(String remoteIp,int remotePort,int localPort) throws SocketException
	{
		this.remoteIp = remoteIp;
		this.remotePort = remotePort;
		this.localPort = localPort;
		
		try
		{
			sourceSocket = new DatagramSocket(localPort);
		}
		catch(Exception e)
		{
			sourceSocket = new DatagramSocket();
			System.out.println(localPort + ",DatagramSocket init error,use new port=" + sourceSocket.getPort());
			e.printStackTrace();
		}
		try
		{
			targetSocket = new InetSocketAddress(remoteIp,remotePort);
		}
		catch(Exception e)
		{
			System.out.println(remoteIp+":"+remotePort + ",InetSocketAddress init error");
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送日志详细方法
	 * @param content  发送日志内容
	 */
	public void log(String content)
	{
		if (content == null)
			return;
		
		//发送log
		try {
			byte[] buf = content.getBytes();
			DatagramPacket pac = new DatagramPacket(buf, buf.length,targetSocket );
			sourceSocket.send(pac);
		} catch (Exception e) {
			if (sourceSocket != null)
			{
				synchronized(sourceSocket) {
					if (sourceSocket.isConnected()){
						sourceSocket.close();
						sourceSocket.disconnect();
					}
					try {
						sourceSocket = new DatagramSocket();
					} catch (SocketException e1) {
						e1.printStackTrace();
					}
				}
			}
			
			//发送日志出现异常,存在本地log
//			_log.error("send log error:" + remoteIp + ":" + remotePort + "  localPort="+localPort + ",error:"+e.getMessage());
			_log.error("localPort:"+localPort);
		} 
	}
	
	/**
	 * 垃圾回收关闭socket
	 */
	public void finalize()
	{
		if (sourceSocket != null)
		{
			sourceSocket.close();
		}
	}
	
}
