/**
 * 
 */
package com.youku.search.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author william
 *
 */
public class DataFormat {


	public static int parseInt ( Object strData )
	{
		return parseInt (  strData ,0);
	}
	public static long parseLong ( Object strData )
	{
		return parseLong (  strData ,0);
	}
	/**
	 * 格式化数字
	 * @param strData
	 *            字符串数据
	 */
	public static float parseFloat ( String strData ,float defaultValue)
	{
		if (strData == null || strData.length ( ) == 0)
		{
			return defaultValue ;
		} else
		{
			try
			{
				return Float.valueOf(strData) ;
			}catch(Exception e)
			{
				return defaultValue;
			}
		}
	}
	/**
	 * 格式化数字
	 * @param strData
	 *            字符串数据
	 */
	public static int parseInt ( Object strData ,int defaultValue)
	{
		if (strData == null || strData.toString().length ( ) == 0)
		{
			return defaultValue ;
		} else
		{
			try
			{
				return Integer.parseInt(strData.toString()) ;
			}catch(Exception e)
			{
				return defaultValue;
			}
		}
	}
	
	public static byte parseByte ( Object strData ,byte defaultValue)
	{
		if (strData == null || strData.toString().length ( ) == 0)
		{
			return defaultValue ;
		} else
		{
			try
			{
				return Byte.parseByte(strData.toString()) ;
			}catch(Exception e)
			{
				return defaultValue;
			}
		}
	}
	
	
	public static long parseLong ( Object strData ,long defaultValue)
	{
		if (strData == null || strData.toString().length ( ) == 0)
		{
			return defaultValue ;
		} else
		{
			try
			{
				return Long.parseLong(strData.toString()) ;
			}catch(Exception e)
			{
				return defaultValue;
			}
		}
	}
	
	public static final int FMT_DATE_YYYY				= 0;
	public static final int	FMT_DATE_YYYYMMDD			= 1 ;
	public static final int	FMT_DATE_YYYYMMDD_HHMMSS	= 2 ;
	public static final int	FMT_DATE_HHMMSS				= 3 ;
	public static final int	FMT_DATE_HHMM				= 4 ;
	public static final int	FMT_DATE_SPECIAL    		= 5 ;
	public static final int	FMT_DATE_MMDD    			= 6 ;
	public static final int	FMT_DATE_YYYYMMDDHHMM    	= 7 ;
	public static final int	FMT_DATE_MMDD_HHMM 			= 8;
	public static final int	FMT_DATE_MMMDDD    			= 9 ;
	public static final int	FMT_DATE_YYYY_MM_DD    		= 10 ;
	public static final int	FMT_DATE_YYYY_MM_DD_HH    	= 11 ;
	public static final int	FMT_DATE_YYYYMMDDHHMMSS   		= 12 ;
	public static final int	FMT_DATE_YYMMDD			= 13 ;
	public static String formatDate ( Date date , int nFmt )
	{
		SimpleDateFormat fmtDate = new SimpleDateFormat ( ) ;
		switch (nFmt)
		{
			default :
			case DataFormat.FMT_DATE_YYYY:
				fmtDate.applyLocalizedPattern("yyyy");
				break;
			case DataFormat.FMT_DATE_YYYYMMDD :
				fmtDate.applyPattern ( "yyyy-MM-dd" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYYMMDD_HHMMSS :
				fmtDate.applyPattern ( "yyyy-MM-dd HH:mm:ss" ) ;
				break ;
			case DataFormat.FMT_DATE_HHMM :
				fmtDate.applyPattern ( "HH:mm" ) ;
				break ;
			case DataFormat.FMT_DATE_HHMMSS :
				fmtDate.applyPattern ( "HH:mm:ss" ) ;
				break ;
			case DataFormat.FMT_DATE_SPECIAL :
				fmtDate.applyPattern ( "yyyyMMdd" ) ;
				break ;
			case DataFormat.FMT_DATE_MMDD :
				fmtDate.applyPattern ( "MM-dd" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYYMMDDHHMM :
				fmtDate.applyPattern ( "yyyy-MM-dd HH:mm" ) ;
				break ;
			case DataFormat.FMT_DATE_MMDD_HHMM :
				fmtDate.applyPattern ( "MM-dd HH:mm" ) ;
				break ;
			case DataFormat.FMT_DATE_MMMDDD :
				fmtDate.applyPattern ( "MM月dd日" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYY_MM_DD :
				fmtDate.applyPattern ( "yyyy_MM_dd" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYY_MM_DD_HH :
				fmtDate.applyPattern ( "yyyy_MM_dd_HH" ) ;
				break ;
			case DataFormat.FMT_DATE_YYMMDD :
				fmtDate.applyPattern ( "yyMMdd" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYYMMDDHHMMSS :
				fmtDate.applyPattern ( "yyyyMMddHHmmss" ) ;
				break ;
		}
		return fmtDate.format ( date ) ;
	}
	
	public static Date parseUtilDate ( String strDate , int nFmtDate )
	{
		if (strDate == null || strDate.trim().length()==0) return null;
		SimpleDateFormat fmtDate = null ;
		switch (nFmtDate)
		{
			default :
			case DataFormat.FMT_DATE_YYYYMMDD :
				fmtDate = new SimpleDateFormat ( "yyyy-MM-dd" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYYMMDD_HHMMSS :
				fmtDate = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" ) ;
				break ;
			case DataFormat.FMT_DATE_HHMM :
				fmtDate = new SimpleDateFormat ( "HH:mm" ) ;
				break ;
			case DataFormat.FMT_DATE_HHMMSS :
				fmtDate = new SimpleDateFormat ( "HH:mm:ss" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYY_MM_DD :
				fmtDate = new SimpleDateFormat ( "yyyy_MM_dd" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYY_MM_DD_HH :
				fmtDate = new SimpleDateFormat ( "yyyy_MM_dd_HH" ) ;
				break ;
			case DataFormat.FMT_DATE_SPECIAL :
				fmtDate = new SimpleDateFormat( "yyyyMMdd" ) ;
				break ;
			case DataFormat.FMT_DATE_YYYYMMDDHHMMSS :
				fmtDate = new SimpleDateFormat( "yyyyMMddHHmmss" ) ;
				break ;
		}
		try {
			return fmtDate.parse ( strDate ) ;
		} catch (ParseException e) {
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static Date formatDate(String strDate)
	{
		SimpleDateFormat fmtDate = null;
		fmtDate = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" ) ;
		try {
			return fmtDate.parse ( strDate ) ;
		} catch (ParseException e) {
		}
		fmtDate = new SimpleDateFormat ( "yyyy/MM/dd HH:mm:ss" ) ;
		try {
			return fmtDate.parse ( strDate ) ;
		} catch (ParseException e) {
		}
		fmtDate = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss" ) ;
		try {
			return fmtDate.parse ( strDate ) ;
		} catch (ParseException e) {
		}
		fmtDate = new SimpleDateFormat ( "yyyy年MM月dd日 HH时mm分ss秒" ) ;
		try {
			return fmtDate.parse ( strDate ) ;
		} catch (ParseException e) {
		}
		try {
			return new Date ( strDate ) ;
		} catch (Exception e) {
		}
		System.out.println("日期格式化错误："+strDate);
		return new Date();
	}
	
	
	/**
	 * 获取某天的日期
	 * @param date 基准
	 * @param number 1 明天 2后天 -1 昨天
	 * @return
	 */
	public static Date getNextDate(Date date ,int number)
	{
		Calendar c = Calendar.getInstance();
        c.setTime(date);
        int td = c.get(Calendar.DATE);
        c.set(Calendar.DATE, td+number);  
        return c.getTime();
	}
	
	/**
	 * 获取下1小时
	 * @param date 基准
	 * @param number 可为负数 
	 * @return
	 */
	public static Date getNextHour(Date date ,int number)
	{
		Calendar c = Calendar.getInstance();
        c.setTime(date);
        int td = c.get(Calendar.HOUR_OF_DAY);
        c.set(Calendar.HOUR_OF_DAY, td+number);  
        return c.getTime();
	}
	
	public static Date getNextMinute(Date date ,int number)
	{
		Calendar c = Calendar.getInstance();
        c.setTime(date);
        int td = c.get(Calendar.MINUTE);
        c.set(Calendar.MINUTE, td+number);  
        return c.getTime();
	}
	public static Date getDate(Date date ,int seconds)
	{
		Calendar c = Calendar.getInstance();
        c.setTime(date);
        int td = c.get(Calendar.SECOND);
        c.set(Calendar.SECOND, td+seconds);  
        return c.getTime();
	}
	
	public static int getYear(Date date)
	{
		Calendar c =  Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	public static int getMonth(Date date)
	{
		Calendar c =  Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH)+1;
	}
	public static int getDay(Date date)
	{
		Calendar c =  Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}
	public static int getHour(Date date)
	{
		Calendar c =  Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY);
	}
	public static int getWeek(Date date)
	{
		Calendar c =  Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);
	}
	
	public static String formatNumber(int num)
	{
		if (num < 10)
			return "0"+num;
		return String.valueOf(num);
	}
	
	/**
	 * 两个时间间隔天数
	 * @param dtBegin
	 * @param dtEnd
	 * @return
	 */
	public static int getDay ( java.util.Date dtBegin , java.util.Date dtEnd )
	{
		return (int)(dtEnd.getTime() - dtBegin.getTime()) / (24 * 60 * 60 * 1000) ;
	}
	
	/**
	 * Encode string into HTML
	 */
	public final static String htmlEncode(String plainText,int limit) 
	{
		if (plainText == null || plainText.length() == 0)
		{
			return null;
		}
		if (limit > plainText.length()) 
			limit = plainText.length();
		StringBuffer result = new StringBuffer(limit);

		for (int index=0; index<limit; index++) 
		{
			char ch = plainText.charAt(index);

			switch (ch) 
			{
			case '"':
				result.append("&quot;");
				break;

			case '&':
				result.append("&amp;");
				break;

			case '<':
				result.append("&lt;");
				break;

			case '>':
				result.append("&gt;");
				break;

			default:
				result.append(ch);
			}
		}

		return result.toString();
	}
	
	
}
