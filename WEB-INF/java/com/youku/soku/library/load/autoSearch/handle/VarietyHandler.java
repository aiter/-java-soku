/**
 * 
 */
package com.youku.soku.library.load.autoSearch.handle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.soku.library.load.form.ProgrammeEpisodeBo;
import com.youku.soku.library.load.form.ProgrammeSiteBo;

/**
 * @author liuyunjian
 * 2011-3-2
 */
public class VarietyHandler extends Handler {

	public VarietyHandler(ProgrammeSiteBo psBo) {
		super(psBo);
	}

	/* (non-Javadoc)
	 * 综艺的自动发现的总集数
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#getEpisodeTotal()
	 */
	@Override
	protected int getEpisodeTotal() {
		int total = super.getEpisodeTotal();
		if(total==0){
			if(psBo.episodeCollected>0){
				//如果已收录一部分，就再最后一集后面再搜索10集
				if(peBoList!=null && peBoList.size()>0){
					total = peBoList.get(peBoList.size()-1).orderId+10;
				}else {
					total+= 10;
				}
			}else {
				total = 40; //没有搜索任何部分，最多搜索40集
			}
		}
		return total;
	}
	
	@Override
	protected void removeExistId(List<Integer> nPeList) {
		if(peBoList!=null){
			for (ProgrammeEpisodeBo pe : peBoList) {
				nPeList.remove(new Integer(pe.orderId));
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#matchName(java.lang.String, int, java.lang.String)
	 */
	@Override
	protected boolean matchName(String title, int order, String version) {
		List<String> tmp = new ArrayList<String>();
		for (String keyword : searchKeys) {
			tmp.add(keyword.replace(" ", "[ -_]*")+getStageOrder(order));
		}
		String s = Utils.parse2Str(tmp);
		String t = Utils.formatTeleplayName(title);
		
//		int stageOrder = buildStage(order);
		
//		System.out.println(order+"::"+stageOrder); //20110301
//		System.out.println(order+"::"+s); //节目名：WTO姐妹会 2011 。组装的搜索词：WTO姐妹会 20110301
//		System.out.println(order+"::"+t); //搜索结果的title：WTO姐妹会20110301
//		System.out.println(order+":"+t+"---->"+s);
		
		if(StringUtils.isBlank(s)||StringUtils.isBlank(t))
			return false;
		
		StringBuilder keyword = new StringBuilder();

		if (!StringUtils.isBlank(s)) {
			keyword.append("(").append(s).append(")");
		} else {
			return false;
		}

		keyword.append("[ ]*");

		if (!StringUtils.isBlank(version)) {
			keyword.append("(").append(version).append("|").append(Utils.analyzer(version)).append(")");
		}

		
		if(StringUtils.isBlank(t)) return false;
		t = Utils.stopWordsFilter(t);
		if(StringUtils.isBlank(t)) return false;
		
		if (t.matches(keyword.toString()))
			return true;
		if (Utils.analyzer(t).matches(keyword.toString()))
			return true;
		return false;
	}
	

	/* (non-Javadoc)
	 * @see com.youku.soku.library.load.autoSearch.handle.Handler#getStageOrder(int)
	 */
	@Override
	protected String getStageOrder(int orderId,boolean haveYear) {
		String tmp = buildStage(orderId)+"";
		if(haveYear){
			return tmp;
		}else {
			return (tmp.length()==8)?tmp.substring(4):tmp;
		}
	}

	/**
	 * 简单实现，最后一期的时间，加一天
	 */
	private int buildStage(int order) {
		int lastOrder = 1;
		int lastStageOrder = date2int(new Date(),-1);
		if(peBoList!=null && peBoList.size()>0){
			lastOrder = peBoList.get(peBoList.size()-1).orderId;
			lastStageOrder = peBoList.get(peBoList.size()-1).orderStage;
		}
//		if(order>lastOrder){
		if((lastStageOrder+"").length()==8){
			return dataIntAddDays(lastStageOrder,order-lastOrder);
		}else {
			return order;
		}
//		}
//		return 0;
	}

	/**
	 * @param lastStageOrder
	 * @param i
	 * @return
	 */
	private int dataIntAddDays(int lastStageOrder, int days) {
		SimpleDateFormat fmtDate = null;
		fmtDate = new SimpleDateFormat("yyyyMMdd");
		
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(fmtDate.parse(lastStageOrder+""));
			calendar.add(Calendar.DATE, days);
			
			return Integer.valueOf(fmtDate.format(calendar.getTime()));
		} catch (ParseException e) {
		}
		return 0;
	}

	/**
	 * 将时间转换为8位的yyyyMMdd整数时间
	 * @param date
	 * @param days
	 */
	private int date2int(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		
		SimpleDateFormat fmtDate = null;
		fmtDate = new SimpleDateFormat("yyyyMMdd");
		return Integer.valueOf(fmtDate.format(calendar.getTime()));
	}
	
	public static void main(String[] args) {
		VarietyHandler handler = new VarietyHandler(null);
		int tmp  = handler.date2int(new Date(),-2);
		System.out.println(tmp);
		tmp = handler.dataIntAddDays(tmp,10);
		System.out.println(tmp);
		System.out.println(handler.date2int(new Date(),1));
	}
}
