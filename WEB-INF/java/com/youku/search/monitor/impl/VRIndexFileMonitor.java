package com.youku.search.monitor.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youku.search.console.util.Wget;
import com.youku.search.monitor.Result;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.index.SmsSender;

public class VRIndexFileMonitor extends CMonitor {

	
	public VRIndexFileMonitor() {
		super();
		super.period = 1000L * 60 * 1; //1小时一次，4点前不检查
	}

	@Override
	public Result check() {
		byte[] bytes;
		Result rs = new Result();
		Calendar calCurrent = Calendar.getInstance();
		if(calCurrent.get(Calendar.HOUR_OF_DAY)<4){
			return rs;
		}
		try {
			bytes = Wget.get("http://10.103.8.225/index/filedownload?date="+DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD));
			String res = new String(bytes,"utf-8");
			if(StringUtils.isBlank(res)){
				rs.setOk(false);
				rs.setMessage("文件下载返回空字符串");
			}
			List<String> list = Utils.parseStr2List(res, "\\\r\\\n");
			if(null==list||list.size()<1){
				rs.setOk(false);
				rs.setMessage("文件下载返回空文件");
			}
		} catch (Exception e) {
			rs.setOk(false);
			rs.setException(e);
		}
		if(!rs.isOk()){
			SmsSender ss = new SmsSender();
			try {
				ss.send("指数文件没有生成,message:"+rs.getMessage(), "13488750198");
			} catch (Exception e) {
			}
			if(calCurrent.get(Calendar.HOUR_OF_DAY)>5){
				
				try {
					Wget.get("http://10.103.8.225/index/log/web/cf.jsp?flag=1");
				} catch (Exception e) {
				}
			}
		}
		return rs;
	}

}
