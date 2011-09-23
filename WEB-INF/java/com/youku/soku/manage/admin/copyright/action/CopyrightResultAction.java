package com.youku.soku.manage.admin.copyright.action;

import com.opensymphony.xwork2.Action;
import com.youku.soku.manage.admin.copyright.load.CopyrightConf;
import com.youku.soku.manage.admin.copyright.load.CopyrightConfPeer;
import com.youku.soku.manage.admin.copyright.util.CopyrightSpider;
import com.youku.soku.manage.common.BaseActionSupport;

public class CopyrightResultAction extends BaseActionSupport {
	private int cid;
	
	//根据指定的版权节目配置信息搜索
	public String spider() {
		try {
			CopyrightConf conf = CopyrightConfPeer.retrieveByPK(cid);
			if (null != conf)
				CopyrightSpider.spider(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取指定的版权节目的搜索结果信息
	public String info(){
		
		return Action.SUCCESS;
	}
	
	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

}
