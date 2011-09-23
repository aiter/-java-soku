package com.youku.soku.manage.admin.copyright.service;

import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.manage.admin.copyright.load.CopyrightConf;
import com.youku.soku.manage.admin.copyright.load.CopyrightConfPeer;
import com.youku.soku.manage.common.PageInfo;

public class CopyrightConfService {

	public static PageInfo getAllCopyrightConf(PageInfo pageInfo, String keyword) {
		Criteria crit = new Criteria();
		if (null != keyword && !keyword.isEmpty())
			crit.add(CopyrightConfPeer.NAME, (Object) ("'%" + keyword + "%'"),
					Criteria.LIKE);
		pageInfo.initCrit(crit);
		List<CopyrightConf> all = null;
		try {
			all = CopyrightConfPeer.doSelect(crit);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		int totalRecord = 0;
		if (null != all)
			totalRecord = all.size();
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.setResults(all);
		return pageInfo;
	}

}
