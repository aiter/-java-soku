package com.youku.top.index;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SmsSender {
	private static final String DEFAULT_HOST="10.101.11.12";
	private static final int DEFAULT_PORT=20229;
	private SocketAddress socketAddr;
	
	/**
	 * 使用缺省的参数，{@linkplain DEFAULT_HOST}，{@linkplain DEFAULT_PORT}
	 */
	public SmsSender(){
		this(DEFAULT_HOST, DEFAULT_PORT);
	}

	/**
	 * 使用指定的主机和端口
	 * @param host hostname
	 * @param port the port
	 */
	public SmsSender(String host, int port) {
		this(new InetSocketAddress(host, port));
	}
	
	/**
	 * 指定SocketAddress
	 * @param socketAddr
	 */
	public SmsSender(SocketAddress socketAddr) {
		super();
		this.socketAddr = socketAddr;
	}

	/**
	 * 发送短信
	 * @param smsContent 短信内容
	 * @param phoneNumbers 接受短信终端的号码
	 * @throws IOException
	 */
	public void send(String smsContent, String...phoneNumbers) throws IOException{
		send(smsContent, join(",", phoneNumbers));
	}
	
	/**
	 * 发送短信
	 * @param smsContent 短信内容
	 * @param phoneNumbers 多个手机号请用逗号隔开，或者使用{@link #send(String, String...)}函数
	 * @throws IOException
	 */
	public void send(String smsContent, String phoneNumbers) throws IOException{
		DatagramSocket socket = new DatagramSocket();
		String part="m:"+phoneNumbers+" c:"+smsContent;
		String content = String.format("s:%04d %s", part.getBytes("UTF8").length, part);
		//System.out.println(content);
		byte data[] = content.getBytes("UTF8");
		socket.send(new DatagramPacket(data, data.length, this.socketAddr) );
	}
	
	private String join(CharSequence sep, CharSequence...strs){
		if(strs.length == 0){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(strs[0]);
		for (int i = 1; i < strs.length; i++) {
			sb.append(sep).append(strs[i]);
		}
		return sb.toString();
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args){
		SmsSender sms = new SmsSender();
		try {
			sms.send("你好,test", "13488750198");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
