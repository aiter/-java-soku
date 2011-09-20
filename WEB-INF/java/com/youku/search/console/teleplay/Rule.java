/**
 * 
 */
package com.youku.search.console.teleplay;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import com.youku.search.util.DataFormat;

/**
 * @author 1verge
 * 电视剧播放日期
 */
public class Rule {
	
	private HashSet<Integer> rule = null;
	private Calendar cal = Calendar.getInstance();
	
	public Rule()
	{
		cal.setFirstDayOfWeek(Calendar.MONDAY);//不清楚为什么失效
	}
	/**
	 * 
	 * @param rules 由星期几组成的数组
	 */
	public Rule(int[] rules)
	{
		if (rules != null ){
			rule = new HashSet<Integer>();
			for (int i:rules)
			{
				if (i == 7) 
					i =0;
				rule.add(i);
			}
		}
	}
	
	/**
	 * 是否合法的日期
	 * @param date
	 * @return
	 */
	public boolean isValid(Date date)
	{
		if (rule == null || date == null)
			return true;
		else
		{
			cal.setTime(date); 
			int week = cal.get(Calendar.DAY_OF_WEEK); 
			return rule.contains(week-1);
		}
	}
	
	/**
	 * 
	 * @param rules 由星期几组成的数组
	 */
	public void setRule(int[] rules)
	{
		if (rules == null){
			rule = null;
			return;
		}
		//清空
		if (rule == null)
			rule = new HashSet<Integer>();
		else
			rule.clear();
		
		//重新赋值
		for (int i:rules)
		{
			if (i == 7) 
				i =0;
			rule.add(i);
		}
	}
	public static void main(String[] args)
	{
		Calendar cal =new GregorianCalendar();
		cal.setTime(DataFormat.getNextDate(new Date(),-2));
		int week = cal.get(Calendar.DAY_OF_WEEK); 
		System.out.println(week-1);
	}
	
	
}
