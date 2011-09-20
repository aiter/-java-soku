package com.youku.search.index.entity;

import com.youku.search.util.MyUtil;

public class StreamType {
	int stream ;
	String value;
	public StreamType(int stream){
		if (stream > 0)
		{
			StringBuffer sb = new StringBuffer();
			
			
			if (MyUtil.contains(0x08,stream))//mp4
			{
				sb.append("1,");
			}
			if (MyUtil.contains(0x10,stream))//3gp
			{
				sb.append("2,");
			}
			if (MyUtil.contains(0x04,stream))//FLV
			{
				sb.append("3,");
			}
			if (MyUtil.contains(0x20,stream))//3GPHD  
			{
				sb.append("4,");
			}
			
			if (sb.length() > 0)
				value = sb.substring(0,sb.length()-1);
		}
	}
	
	public String getValue()
	{
		return value;
	}
}
