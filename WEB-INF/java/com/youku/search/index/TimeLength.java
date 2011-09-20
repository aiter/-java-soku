/**
 * 
 */
package com.youku.search.index;

/**
 * @author 1verge
 *
 */
public class TimeLength {
	
	
	public static String getLessIndex(float second)
	{
		StringBuilder builder = new StringBuilder();
		if (second <= 4*60)
			builder.append("1 ");
		else if (second > 4*60 &&  second <= 20*60)
			builder.append("2 ");
		else 
			builder.append("3 ");
		
		if (second <= 15*60)
			builder.append("15 ");
		
		return builder.length() > 0?builder.substring(0,builder.length()-1):null;
	}

	public static int getIndex(int timeless,int timemore)
	{
		if (timeless ==4)
			return 1;
		else if (timemore == 4 && timeless ==20)
			return 2;
		else if (timemore == 20)
			return 3;
		else if (timeless==15)
			return 15;
		return 0;
	}
	
	public static void main(String[] args){
		System.out.println(getLessIndex(200));
	}
}
