package com.youku.soku.manage.admin.copyright.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.manage.admin.copyright.load.ActConfig;
import com.youku.soku.manage.admin.copyright.load.ActConfigPeer;
import com.youku.soku.manage.admin.copyright.load.ActSearch;
import com.youku.soku.manage.admin.copyright.load.ActSearchPeer;
import com.youku.soku.manage.admin.copyright.util.CopyrightSpider;
import com.youku.soku.manage.common.PageInfo;
import common.Logger;

public class ActConfigService {

	public static PageInfo getAllActConfig(PageInfo pageInfo, String keyword) {
		Criteria crit = new Criteria();
		if (null != keyword && !keyword.isEmpty())
			crit.add(ActConfigPeer.ACT_TITLE, (Object) ("'%" + keyword + "%'"),
					Criteria.LIKE);
		List<ActConfig> all = null;
		try {
			all = ActConfigPeer.doSelect(crit);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		int totalRecord = 0;
		if (null != all)
			totalRecord = all.size();
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);
		pageInfo.setResults(all);
		return pageInfo;
	}

	public static String spider(int cid, Logger log) {
		try {
			ActConfig conf = ActConfigPeer.retrieveByPK(cid);
			if (null == conf)
				return "";
			List<ActSearch> result = CopyrightSpider.spider(conf);
			if (null == result || result.size() == 0)
				return "";
			int add = 0;
			int update = 0;
			Map<String, Integer> updateMap = new HashMap<String, Integer>();
			for (ActSearch as : result) {
				String id = as.getId();
				Criteria crit = new Criteria();
				crit.add(ActSearchPeer.ID, id);
				List<ActSearch> ass = ActSearchPeer.doSelect(crit);
				if (null == ass || ass.size() == 0) {// 抓取到新的视频
					as.save();
					add++;
				} else {// 更新该视频 数据库中无 create update time
					if (updateMap.get(as.getId()) == null)
						update++;
					as.setFirstCollectTime(ass.get(0).getFirstCollectTime());
					as.setLastCollectTime(new Date());
					ActSearchPeer.doUpdate(as);
					updateMap.put(as.getId(), 0);
				}
			}
			return add + "-" + update;
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			e.printStackTrace();
			return "";
		}

	}

}
