package com.youku.search.console.action;

import java.util.Date;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.TopQuickView;
import com.youku.search.console.vo.TopQuickVO;
import com.youku.search.util.DataFormat;

public class TopQuickAction {
	TopQuickVO tqv=null;
	String date=null;
	
	public TopQuickVO getTqv() {
		return tqv;
	}

	public void setTqv(TopQuickVO tqv) {
		this.tqv = tqv;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String execute() {
		LogInfoWriter.operate_log.info("上升最快----");
		TopQuickView topview=new TopQuickView();
		if(null==date||date.trim().length()<1){
			date=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYYMMDD);
		}else
			date=date.replaceAll("_", "-");
		tqv=TopQuickView.topmap.get(date);
		if(tqv!=null)
			return Action.SUCCESS;
		else{
			tqv=topview.getTopQuickVO(date);
			if(TopQuickView.topmap.size()>10)
				TopQuickView.topmap.clear();
			if(null!=tqv.getTopviewmap()&&tqv.getTopviewmap().size()>0)
				TopQuickView.topmap.put(date, tqv);
			return Action.SUCCESS;
		}
		
	}
}
