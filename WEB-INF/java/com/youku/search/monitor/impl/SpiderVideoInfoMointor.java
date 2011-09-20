package com.youku.search.monitor.impl;

import com.youku.search.monitor.Monitor;
import com.youku.search.monitor.Person;
import com.youku.search.monitor.Result;
import com.youku.search.util.Request;

public class SpiderVideoInfoMointor extends Monitor {

	public SpiderVideoInfoMointor() {
		super(new Person[]{Person.shifangfang,Person.tanxiuguang});
		super.period = 60 * 60 * 1000;  //one hour
	}

	@Override
	public Result check() {
		Result r = new Result();
		String url = "http://10.12.0.23:8080/spider/getVideoByUrl.jsp?url=http://www.tudou.com/programs/view/lPYq0rZlkRQ/";
		String result = Request.requestGet(url, 500);
		if(result == null) {
			r.setMessage("获取视频信息出错" + url + "\n "+result);
			r.setOk(false);
		}
		return r;
	}
	
	public static void main(String[] args)
	{
		SpiderVideoInfoMointor m = new SpiderVideoInfoMointor();
		System.out.println(m.check().isOk());
	}

}
